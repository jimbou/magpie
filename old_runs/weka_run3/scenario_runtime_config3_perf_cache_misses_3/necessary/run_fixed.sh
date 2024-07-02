#!/bin/sh

java -cp build/classes/ weka.classifiers.trees.RandomForest -t data/diabetes.arff $@
java -cp build/classes/ weka.classifiers.trees.RandomForest -t data/segment.arff $@
java -cp build/classes/ weka.classifiers.trees.RandomForest -t data/soybean.arff $@
exit 0
