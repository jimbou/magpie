#!/bin/bash

# Ensure the script is run with the necessary arguments
if [ "$#" -ne 2 ]; then
    echo "Usage: $0 <filename> <filename_params>"
    exit 1
fi

# Assign arguments to variables
filename=$1
filename_params=$2


# Remove the '.txt' extension for manipulation
base_filename="${filename%.txt}"
base_filename_params="${filename_params%.txt}"

# List of standard perf values for which to generate modified files
values=(instructions cycles cache_references cache_misses branches branch_misses cpu_clock task_clock faults minor_faults major_faults cs migrations L1_dcache_loads L1_dcache_load_misses dTLB_loads dTLB_load_misses)

# Loop over each standard value in the list
for value in "${values[@]}"; do
    if [ "$filename" != "" ]; then
        new_filename="${base_filename}_perf_${value}.txt"
        # Replace placeholders in the template file
        sed -e "s|placeholder1|perf_${value}|g" \
            -e "s|placeholder3|$(echo ${value} | tr '_' '-')|g" \
            "$filename" > "$new_filename"
        echo "File created: $new_filename"
    fi
    #if filename_params is not "" then create a new file with the same name
    if [ "$filename_params" != "" ]; then
        new_filename_params="${base_filename_params}_perf_${value}.txt"
        sed -e "s|placeholder1|perf_${value}|g" \
            -e "s|placeholder3|$(echo ${value} | tr '_' '-')|g" \
            "$filename_params" > "$new_filename_params"
        echo "File created: $new_filename_params"
    fi
done

if [ "$filename" != "" ]; then
# Handle 'time', 'energy', and 'weight' individually
# For 'time'
time_filename="${base_filename}_perf_time.txt"
sed -e "s|placeholder1|perf_time|g" \
    -e "s|placeholder3||g" \
    -e "s|perf stat -e |perf stat |g" \
    "$filename" > "$time_filename"
echo "File created: $time_filename"

# For 'energy'
energy_filename="${base_filename}_energy.txt"
sed -e "s|placeholder1|energy|g" \
    -e "s|placeholder3|./run_rapl_energy.sh|g" \
    -e "s|perf stat -e | |g" \
    "$filename" > "$energy_filename"
echo "File created: $energy_filename"

# For 'weight'
weights_filename="${base_filename}_weights.txt"
sed -e "s|placeholder1|weights|g" \
    -e "s|placeholder3|./run_custom.sh|g" \
    -e "s|perf stat -e | |g" \
    "$filename" > "$weights_filename"
echo "File created: $weights_filename"

# Handle 'time'
time2_filename="${base_filename}_time.txt"
sed -e "s|placeholder1|time|g" \
    -e "s|perf stat -e placeholder3 ||g" \
    "$filename" > "$time2_filename"
echo "File created: $time2_filename"
fi

if [ "$filename_params" != "" ]; then
    # Handle 'time', 'energy', and 'weight' individually
    # For 'time'
    time_filename_params="${base_filename_params}_perf_time.txt"
    sed -e "s|placeholder1|perf_time|g" \
        -e "s|placeholder3||g" \
        -e "s|perf stat -e |perf stat |g" \
        "$filename_params" > "$time_filename_params"
    echo "File created: $time_filename_params"

    # For 'energy'
    energy_filename_params="${base_filename_params}_energy.txt"
    sed -e "s|placeholder1|energy|g" \
        -e "s|placeholder3|./run_rapl_energy.sh|g" \
        -e "s|perf stat -e | |g" \
        "$filename_params" > "$energy_filename_params"
    echo "File created: $energy_filename_params"

    # For 'weight'
    weights_filename_params="${base_filename_params}_weights.txt"
    sed -e "s|placeholder1|weights|g" \
        -e "s|placeholder3|./run_custom.sh|g" \
        -e "s|perf stat -e | |g" \
        "$filename_params" > "$weights_filename_params"
    echo "File created: $weights_filename_params"

    # Handle 'time'
    time2_filename_params="${base_filename_params}_time.txt"
    sed -e "s|placeholder1|time|g" \
        -e "s|perf stat -e placeholder3 ||g" \
        "$filename_params" > "$time2_filename_params"
    echo "File created: $time2_filename_params"  
    fi

echo "All files have been created."
