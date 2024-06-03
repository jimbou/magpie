#!/bin/bash

# Ensure the script is run with the necessary arguments
if [ "$#" -ne 2 ]; then
    echo "Usage: $0 <filename> <PROGRAM>"
    exit 1
fi

# Assign arguments to variables
filename=$1
program=$2

# Remove the '.txt' extension for manipulation
base_filename="${filename%.txt}"

# List of values for which to generate modified files
values=(time instructions cycles cache_references cache_misses branches branch_misses cpu_clock task_clock faults minor_faults major_faults cs migrations L1_dcache_loads L1_dcache_load_misses dTLB_loads dTLB_load_misses)

# Loop over each value in the list
for value in "${values[@]}"; do
    # Create a new filename for each value, re-appending the .txt extension correctly
    new_filename="${base_filename}_perf_${value}.txt"

    # Check if the current value is 'time'
    if [ "$value" == "time" ]; then
        # Read the original file and replace placeholders accordingly
        # For 'time', replace placeholder3 with nothing and remove any '-e ' from perf stat command
        sed -e "s/placeholder1/perf_${value}/g" \
            -e "s/placeholder2/${program}/g" \
            -e "s/placeholder3//g" \
            -e "/perf stat -e /s/-e //g" \
            "$filename" > "$new_filename"
    else
        # Read the original file and replace placeholders for other values
        sed -e "s/placeholder1/perf_${value}/g" \
            -e "s/placeholder2/${program}/g" \
            -e "s/placeholder3/$(echo ${value} | tr '_' '-')/g" \
            "$filename" > "$new_filename"
    fi
    
    echo "File created: $new_filename"
done

echo "All files have been created."
