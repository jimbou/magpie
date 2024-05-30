#!/bin/bash

# Ensure the script is run with the necessary arguments
if [ "$#" -ne 1 ]; then
    echo "Usage: $0 <command> "
    exit 1
fi

# Assign arguments to variables
command=$1

taskset -c 0 perf record  -g -F 100000  -e cycles:u -- $command

perf report  --stdio > report1.txt
perf annotate --stdio > report2.txt
#echo "All files have been created."
python3 read_total.py report1.txt report2.txt