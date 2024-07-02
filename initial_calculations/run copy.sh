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
    "perf stat"
    "perf stat -e instructions"
    "perf stat -e cycles"
    "perf stat -e cache_references"
    "perf stat -e cache_misses"
    "perf stat -e branches"
    "perf stat -e branch_misses"
    "perf stat -e cpu_clock"
    "perf stat -e task_clock"
    "perf stat -e faults"
    "perf stat -e minor_faults"
    "perf stat -e major_faults"
    "perf stat -e cs"
    "perf stat -e migrations"
    "perf stat -e L1_dcache_loads"
    "perf stat -e L1_dcache_load_misses"
    "perf stat -e dTLB_loads"
    "perf stat -e dTLB_load_misses"
    "./run_custom.sh"
    "./run_rapl_energy.sh"
)

# Regex list to match the metric in the output from each prefix
REGEXES=(
    '\s*\K[0-9,]+(?=\s+instructions)' # General, modify for different metrics
    '\s*\K[0-9,]+(?=\s+instructions)'
    '\s*\K[0-9,]+(?=\s+cycles)'
    '\s*\K[0-9,]+(?=\s+cache_references)'
    '\s*\K[0-9,]+(?=\s+cache_misses)'
    '\s*\K[0-9,]+(?=\s+branches)'
    '\s*\K[0-9,]+(?=\s+branch_misses)'
    '\s*\K[0-9,]+(?=\s+cpu_clock)'
    '\s*\K[0-9,]+(?=\s+task_clock)'
    '\s*\K[0-9,]+(?=\s+faults)'
    '\s*\K[0-9,]+(?=\s+minor_faults)'
    '\s*\K[0-9,]+(?=\s+major_faults)'
    '\s*\K[0-9,]+(?=\s+context_switches)' # Assuming 'cs' stands for context switches
    '\s*\K[0-9,]+(?=\s+migrations)'
    '\s*\K[0-9,]+(?=\s+L1_dcache_loads)'
    '\s*\K[0-9,]+(?=\s+L1_dcache_load_misses)'
    '\s*\K[0-9,]+(?=\s+dTLB_loads)'
    '\s*\K[0-9,]+(?=\s+dTLB_load_misses)'
    '' # For custom scripts, define or leave empty if output format varies or is unknown
    '' # Same as above
)

# Create a temporary file to hold outputs
temp_file=$(mktemp)

# JSON file to store the medians
json_file="medians.json"
echo "{" > "$json_file"

# Run each command 20 times and capture the output
for i in "${!PREFIXES[@]}"; do
    prefix="${PREFIXES[i]}"
    regex="${REGEXES[i]}"
    results=()
    for j in {1..20}; do
        # Run the command, redirect both stdout and stderr to the temp file
        $prefix $COMMAND >"$temp_file" 2>&1
        # Extract the metric using the specific regex
        metric=$(grep -oP "$regex" "$temp_file")
        metric=${metric//,/} # Remove commas if present
        results+=($metric)
    done

    # Calculate median
    IFS=$'\n' sorted=($(sort -n <<<"${results[*]}"))
    unset IFS
    median=${sorted[10]} # Fetching the 11th item in a sorted 20 item list

    # Save to JSON
    echo "\"$prefix\": $median," >> "$json_file"
done

# Properly close JSON file
sed -i '$ s/,$//' "$json_file" # Remove trailing comma
echo "}" >> "$json_file"

# Clean up temporary file
rm "$temp_file"

echo "Medians stored in $json_file"
