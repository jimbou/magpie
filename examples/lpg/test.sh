#!/bin/sh

ARGV=$@

my_test() {
    FILENAME1=$1
    FILENAME2=$2
    NUM=$3
    EXPECTED=$4
    EXPECTED_OUTPUT="Solution found:"
    
    COMMAND="./lpg-td -f $FILENAME1 -o blocksworld/domain.pddl -speed -out $FILENAME2 -seed $NUM $ARGV"
    OUTPUT=$($COMMAND)
    RETURN=$?
    echo "RETURN1:" $RETURN
    if [ $RETURN -ne $((EXPECTED)) ]; then
        echo "FAILED ON FILE:" $FILENAME1
        echo "GOT:" $RETURN
        echo "EXPECTED:" $EXPECTED
        exit -1
    fi

    if echo "$OUTPUT" | grep -q "$EXPECTED_OUTPUT"; then
        echo "SUCCESS: Command executed successfully and expected output found."
    else
        echo "FAILED ON FILE: $FILENAME1"
        echo "EXPECTED OUTPUT NOT FOUND"
        echo "OUTPUT:"
        echo "$OUTPUT"
        exit 1
    fi


    ./Val-20211204.1-Linux/bin/Validate -v blocksworld/domain.pddl $FILENAME1  $FILENAME2.SOL > /dev/null

    RETURN2=$?
    echo "RETURN2:" $RETURN2
    if [ $RETURN2 -ne $((EXPECTED)) ]; then
        echo "FAILED ON VALIDATION OF FILE:" $FILENAME1
        echo "GOT:" $RETURN2
        echo "EXPECTED:" $EXPECTED
        exit -1
    fi
}

my_test blocksworld/problems/pfile01-004.pddl foo4 111000 0
rm foo4 foo4.SOL
# my_test blocksworld/problems/pfile01-002.pddl foo2 110001 0
# rm foo2 foo2.SOL

