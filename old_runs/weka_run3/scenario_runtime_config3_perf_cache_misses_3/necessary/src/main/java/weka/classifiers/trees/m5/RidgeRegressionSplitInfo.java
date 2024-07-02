/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * RidgeRegressionSplitInfo.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 *
 */

package weka.classifiers.trees.m5;

import no.uib.cipr.matrix.*;
import no.uib.cipr.matrix.Matrix;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.functions.LinearRegression;
import weka.core.*;

import java.io.Serializable;

/**
 * Finds split points using ridge regression.
 *
 * @author Eibe Frank (eibe@cs.waikato.ac.nz)
 * @version $Revision: 10169 $
 */
public final class RidgeRegressionSplitInfo implements Cloneable, Serializable,
        SplitEvaluate, RevisionHandler {

  /** for serialization */
  private static final long serialVersionUID = 3112734895125452171L;

  private int m_position;

  /**
   * the maximum impurity reduction
   */
  private double m_maxImpurity;

  /**
   * the attribute being tested
   */
  private int m_splitAttr;

  /**
   * the best value on which to split
   */
  private double m_splitValue;

  /**
   * the number of instances
   */
  private int m_number;

  /**
   * Constructs an object which contains the split information
   *
   * @param low the index of the first instance
   * @param high the index of the last instance
   * @param attr an attribute
   */
  public RidgeRegressionSplitInfo(int low, int high, int attr) {
    initialize(low, high, attr);
  }

  /**
   * Makes a copy of this RidgeRegressionSplitInfo object
   */
  @Override
  public final SplitEvaluate copy() throws Exception {
    RidgeRegressionSplitInfo s = (RidgeRegressionSplitInfo) this.clone();

    return s;
  }

  /**
   * Resets the object of split information
   *
   * @param low the index of the first instance
   * @param high the index of the last instance
   * @param attr the attribute
   */
  public final void initialize(int low, int high, int attr) {
    m_number = high - low + 1;
    m_position = -1;
    m_maxImpurity = -Double.MAX_VALUE;
    m_splitAttr = attr;
    m_splitValue = 0.0;
  }

  /**
   * Computes the inverse of the A matrix for the G matrix
   *
   * @param G the G matrix
   * @param ridge the value of the ridge parameter
   * @exception Exception if something goes wrong
   */
  public final Matrix getAInverse(Matrix G, double ridge) throws Exception {

    Matrix A = G.copy();
    for (int j = 0; j < A.numRows() - 1; j++) { // -1 is important here!
      A.add(j, j, ridge);
    }
    DenseMatrix I = Matrices.identity(A.numRows());
    DenseMatrix AI = I.copy();
    A.solve(I, AI);
    return AI;
  }

  /**
   * Updates the A inverse matrix based on the given instance
   *
   * @param AI the matrix to update
   * @param inst the instance
   * @param add whether to add and not subtract
   * @exception Exception if something goes wrong
   */
  public final void updateAInverse(Matrix AI, Instance inst, boolean add) throws Exception {

    int classIndex = inst.classIndex();
    int numAttributes = inst.numAttributes();

    Vector m = new DenseVector(numAttributes);
    int index = 0;
    for (int j = 0; j < numAttributes; j++) {
      if (j != classIndex) {
        m.set(index++, inst.value(j));
      }
    }
    m.set(numAttributes - 1, 1.0);
    Vector z = AI.mult(m, new DenseVector(numAttributes));
    AI.rank1(add ? -1.0 / (1 + z.dot(m)) : 1.0 / (1 - z.dot(m)), z);
  }

  /**
   * Computes the G matrix for the given set of instances
   *
   * @param insts the instances
   * @exception Exception if something goes wrong
   */
  public final Matrix getG(Instances insts) throws Exception {

    int classIndex = insts.classIndex();
    int numAttributes = insts.numAttributes();
    int numInstances = insts.numInstances();

    Matrix independentTransposed = new DenseMatrix(numAttributes, numInstances);

    for (int i = 0; i < numInstances; i++) {
      int index = 0;
      for (int j = 0; j < numAttributes; j++) {
        if (j != classIndex) {
          independentTransposed.set(index++, i, insts.instance(i).value(j));
        }
      }
      independentTransposed.set(numAttributes - 1, i, 1.0);
    }
    return new DenseMatrix(numAttributes, numAttributes).rank1(independentTransposed);
  }

  /**
   * Updates the G matrix based on the given instance
   *
   * @param G the matrix to update
   * @param inst the instance
   * @param add whether to add and not subtract
   * @exception Exception if something goes wrong
   */
  public final void updateG(Matrix G, Instance inst, boolean add) throws Exception {

    int classIndex = inst.classIndex();
    int numAttributes = inst.numAttributes();

    Vector vals = new DenseVector(numAttributes);
    int index = 0;
    for (int j = 0; j < numAttributes; j++) {
      if (j != classIndex) {
        vals.set(index++, inst.value(j));
      }
    }
    vals.set(numAttributes - 1, 1.0);
    G.rank1(add ? 1 : -1, vals);
  }

  /**
   * Computes the S vector for the given set of instances
   *
   * @param insts the instances
   * @exception Exception if something goes wrong
   */
  public final Vector getS(Instances insts) throws Exception {

    int classIndex = insts.classIndex();
    int numAttributes = insts.numAttributes();
    int numInstances = insts.numInstances();

    Vector S = new DenseVector(numAttributes);
    for (int i = 0; i < numInstances; i++) {
      int index = 0;
      double classValue = insts.instance(i).classValue();
      for (int j = 0; j < numAttributes; j++) {
        if (j != classIndex) {
          S.add(index++, insts.instance(i).value(j) * classValue);
        }
      }
      S.add(S.size() - 1, classValue);
    }
    return S;
  }

  /**
   * Updates the S vector based on the given instance
   *
   * @param S the vector to update
   * @param inst the instance
   * @param add whether to add and not subtract
   * @exception Exception if something goes wrong
   */
  public final void updateS(Vector S, Instance inst, boolean add) throws Exception {

    int classIndex = inst.classIndex();
    int numAttributes = inst.numAttributes();

    int index = 0;
    double classValue = add ? inst.classValue() : -inst.classValue();
    for (int j = 0; j < numAttributes; j++) {
      if (j != classIndex) {
        S.add(index++, inst.value(j) * classValue);
      }
    }
    S.add(S.size() - 1, classValue);
  }

  /**
   * Computes the S vector for the given set of instances
   *
   * @param G the G matrix
   * @param AI the inverse of the A matrix
   * @param S the vector S
   * @exception Exception if something goes wrong
   */
  public final double getRSS(Matrix G, Matrix AI, Vector S) throws Exception {

    Vector AIS = AI.mult(S, new DenseVector(S.size()));
    Vector GAIS = G.mult(AIS, new DenseVector(AIS.size()));
    Vector AIGAIS = AI.mult(GAIS, new DenseVector(GAIS.size()));

    double RSS = 0;
    for (int i = 0; i < S.size(); i++) {
      RSS += S.get(i) * AIGAIS.get(i);
    }
    for (int i = 0; i < S.size(); i++) {
      RSS -= 2 * S.get(i) * AIS.get(i);
    }
    return RSS;
  }

  /**
   * Finds the best splitting point for an attribute in the instances
   *
   * @param attr the splitting attribute
   * @param insts the instances
   * @exception Exception if something goes wrong
   */
  @Override
  public final void attrSplit(int attr, Instances insts) throws Exception {

    int low = 0;
    int high = insts.numInstances() - 1;
    double ridge = 0.01;

    initialize(low, high, attr);

    if (m_number < 4) {
      return;
    }

    int len = (m_number < 5) ? 1 : m_number / 5;

    m_position = low;

    Instances leftSubset = new Instances(insts, low, len);
    Instances rightSubset = new Instances(insts, len, m_number - len);

    Matrix GL = getG(leftSubset);
    Matrix GR = getG(rightSubset);
    Matrix AIL = getAInverse(GL, ridge);
    Matrix AIR = getAInverse(GR, ridge);
    Vector SL = getS(leftSubset);
    Vector SR = getS(rightSubset);

    for (int i = low + len; i <= high - len - 1; i++) {

      Instance currentInstance = insts.instance(i);
      Instance nextInstance = insts.instance(i + 1);

      updateS(SL, currentInstance, true);
      updateS(SR, currentInstance, false);
      updateG(GL, currentInstance, true);
      updateG(GR, currentInstance, false);
      updateAInverse(AIL, currentInstance, true);
      updateAInverse(AIR, currentInstance, false);

      double splitCandidate = (currentInstance.value(attr) + nextInstance.value(attr)) * 0.5;
      if (splitCandidate < nextInstance.value(attr)) {
        double currentRSS = getRSS(GL, AIL, SL) + getRSS(GR, AIR, SR);

        if (-currentRSS > m_maxImpurity) {
          m_maxImpurity = -currentRSS;
          m_splitValue = splitCandidate;
          m_position = i;
        }
      }
    }
  }


  /**
   * Returns the impurity of this split
   *
   * @return the impurity of this split
   */
  @Override
  public double maxImpurity() {
    return m_maxImpurity;
  }

  /**
   * Returns the attribute used in this split
   *
   * @return the attribute used in this split
   */
  @Override
  public int splitAttr() {
    return m_splitAttr;
  }

  /**
   * Returns the position of the split in the sorted values. -1 indicates that a
   * split could not be found.
   *
   * @return an <code>int</code> value
   */
  @Override
  public int position() {
    return m_position;
  }

  /**
   * Returns the split value
   *
   * @return the split value
   */
  @Override
  public double splitValue() {
    return m_splitValue;
  }

  /**
   * Returns the revision string.
   *
   * @return the revision
   */
  @Override
  public String getRevision() {
    return RevisionUtils.extract("$Revision: 10169 $");
  }
}
