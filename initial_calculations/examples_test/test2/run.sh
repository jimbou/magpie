#!/bin/bash

# Check for input command
if [ $# -eq 0 ]; then
    echo "No command provided."
    exit 1
fi

# Gather the full command as a single string
COMMAND="$*"

# Define the prefixes for perf commands and custom scripts
PREFIXES=(
    'time'
    "perf stat"
    "perf stat -e instructions"
    "perf stat -e cycles"
    "perf stat -e cache-references"
    "perf stat -e cache-misses"
    "perf stat -e branches"
    "perf stat -e branch-misses"
    "perf stat -e cpu-clock"
    "perf stat -e task-clock"
    "perf stat -e faults"
    "perf stat -e minor-faults"
    "perf stat -e major-faults"
    "perf stat -e cs"
    "perf stat -e migrations"
    "perf stat -e L1-dcache-loads"
    "perf stat -e L1-dcache-load-misses"
    "perf stat -e dTLB-loads"
    "perf stat -e dTLB-load-misses"
    "./run_custom.sh"
    "./run_rapl_energy.sh"
)
# )

METRICS=(
    "time"
    "perf_time"
    "instructions"
    "cycles"
    "cache-references"
    "cache-misses"
    "branches"
    "branch-misses"
    "cpu-clock"
    "task-clock"
    "faults"
    "minor-faults"
    "major-faults"
    "cs"
    "migrations"
    "L1-dcache-loads"
    "L1-dcache-load-misses"
    "dTLB-loads"
    "dTLB-load-misses"
    "weights"
    "energy"
)
#     ".weights"
#     ".energy"
# )

# Regex list to match the metric in the output from each prefix
REGEXES=(
    'real\s+(\d+m)?(\d+,\d+)s'
    '\s*\K[0-9,]+(?=\s+seconds time elapsed)' # General, modify for different metrics
    '\s*\K[0-9,]+(?=\s+instructions)'
    '\s*\K[0-9,]+(?=\s+cycles)'
    '\s*\K[0-9,]+(?=\s+cache-references)'
    '\s*\K[0-9,]+(?=\s+cache-misses)'
    '\s*\K[0-9,]+(?=\s+branches)'
    '\s*\K[0-9,]+(?=\s+branch-misses)'
    '\s*\K[0-9,]+(?=\s+seconds time elapsed)'
    '\s*\K[0-9,]+(?=\s+seconds time elapsed)'
    '\s*\K[0-9,]+(?=\s+faults)'
    '\s*\K[0-9,]+(?=\s+minor-faults)'
    '\s*\K[0-9,]+(?=\s+major-faults)'
    '\s*\K[0-9,]+(?=\s+cs)' # Assuming 'cs' stands for context switches
    '\s*\K[0-9,]+(?=\s+migrations)'
    '\s*\K[0-9,]+(?=\s+L1-dcache-loads)'
    '\s*\K[0-9,]+(?=\s+L1-dcache-load-misses)'
    '\s*\K[0-9,]+(?=\s+dTLB-loads)'
    '\s*\K[0-9,]+(?=\s+dTLB-load-misses)'
    '(?<=Custom Metric: )[\d\.]+'
    "(?<=Energy consumed: )\d+"
)
#     '' # For custom scripts, define or leave empty if output format varies or is unknown
#     '' # Same as above
# )

# Create a temporary file to hold outputs
temp_file=$(mktemp)

# JSON file to store the medians
json_file="medians_all.json"
echo "{" > "$json_file"

json_file2="medians.json"
echo "{" > "$json_file2"

# Run each command 20 times and capture the output
for i in "${!PREFIXES[@]}"; do
    prefix="${PREFIXES[i]}"
    regex="${REGEXES[i]}"
    metric_name="${METRICS[i]}"
    results=()
    for j in {1..21}; do

        #if prefix == time
        if [ "$prefix" == "time" ]; then
           /usr/bin/time -f "%e" $COMMAND >"$temp_file" 2>&1
            #make the metric the contents of the tmp file
            metric=$(cat "$temp_file")
            echo $metric
            
        else
            $prefix $COMMAND >"$temp_file" 2>&1
            
            metric=$(grep -oP "$regex" "$temp_file")
            metric=$(echo $metric | tr -d ' ' | awk '{printf "%.8f", $1}')
            metric=$(echo $metric | tr , .)

        fi
        # Run the command, redirect both stdout and stderr to the temp file
        
        # Extract the metric using the specific regex
        
        
        #transform commas to dots
        # echo $metric
        #remove and spaces and make them float  with 8 decimal points
        
        # metric=${metric//,/} # Remove commas if present
        results+=($metric)
    done

    # Calculate median
    IFS=$'\n' sorted=($(sort -n <<<"${results[*]}"))
    unset IFS
    median=${sorted[10]} # Fetching the 11th item in a sorted 20 item list

    results_string=$(IFS=,; echo "${results[*]}")
    echo "\"$metric_name\": [$results_string]," >> "$json_file"
    # Save to JSON
    echo "\"$metric_name\": $median," >> "$json_file2"
done

# Properly close JSON file
sed -i '$ s/,$//' "$json_file" # Remove trailing comma
echo "}" >> "$json_file"

sed -i '$ s/,$//' "$json_file2" # Remove trailing comma
echo "}" >> "$json_file2"
# Clean up temporary file
rm "$temp_file"
rm perf.data
rm perf.data.old
rm report1.txt
rm report2.txt


echo "Medians stored in $json_file"
