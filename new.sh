#!/bin/bash
cd examples/scipy
./total.sh "bash run_fixed.sh"

cd ../
cd zlib
./total.sh "bash run_fixed.sh level=6 wbits=15 memLevel=8 strategy=0"