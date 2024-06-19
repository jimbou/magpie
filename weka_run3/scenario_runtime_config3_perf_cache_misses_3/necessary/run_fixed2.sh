#!/bin/sh

java -cp build/classes/ weka.classifiers.trees.RandomTree -t data/diabetes.arff $@
java -cp build/classes/ weka.classifiers.trees.RandomTree -t data/segment.arff $@
java -cp build/classes/ weka.classifiers.trees.RandomTree -t data/test.arff $@

exit 0
