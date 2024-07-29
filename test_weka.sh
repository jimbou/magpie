#!/bin/sh

ARGV=$@

my_test() {
    FILENAME=$1
    EXPECTED=$2
    # Redirect standard error to standard output and capture it
    OUTPUT=$(java -cp build/classes/ weka.classifiers.trees.RandomForest -t $FILENAME $ARGV 2>&1)
    RETURN=$?
    echo "RETURN:" $RETURN
    # Check for the presence of "Weka exception" in the output
    if echo "$OUTPUT" | grep -q "Weka exception"; then
        echo "FAILED ON FILE:" $FILENAME
        echo "REASON: Weka exception detected"
        echo "OUTPUT:" $OUTPUT
        exit -1
    fi
    #check if the output contains "Confusion Matrix" and if it does not fail
    if ! echo "$OUTPUT" | grep -q "Confusion Matrix"; then
        echo "FAILED ON FILE:" $FILENAME
        echo "REASON: No Confusion Matrix detected"
        echo "OUTPUT:" $OUTPUT
        exit -1
    fi
    # Continue with existing return code checks
    if [ $RETURN -ne $((EXPECTED)) ]; then
        echo "FAILED ON FILE:" $FILENAME
        echo "GOT:" $RETURN
        echo "EXPECTED:" $EXPECTED
        exit -1
    fi
}

my_test data/glass.arff 0
my_test data/iris.arff 0
# Uncomment to enable other tests
# my_test data/cpu.arff 0
# my_test dexter/test.arff 0
# my_test dexter/train.arff 0
# my_test data/diabetes.arff $@
# my_test data/segment.arff $@
# my_test data/soybean.arff $@
