#!/bin/sh

ARGV=$@

my_test() {
    FILENAME1=$1
    FILENAME2=$2
    EXPECTED=$3
   python3 compress_decompress.py $FILENAME1 compressed4.gz $FILENAME2 -o $ARGV method=8  > /dev/null
    RETURN=$?
    echo "RETURN1:" $RETURN
    if [ $RETURN -ne $((EXPECTED)) ]; then
        echo "FAILED ON FILE:" $FILENAME1
        echo "GOT:" $RETURN
        echo "EXPECTED:" $EXPECTED
        exit -1
    fi
}

my_test data/random1.png data/random1_d.png 0
my_test data/random2.txt data/random2_d.txt 0


