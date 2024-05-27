#!/bin/bash

# List of events to monitor
events=(
    "instructions"
    "cache-references"
    "cache-misses"
    "branch-instructions"
    "branches"
    "branch-misses"
    "bus-cycles"
    "ref-cycles"
    "cpu-clock"
    "task-clock"
    "page-faults"
    "faults"
)

# Output file
output_file="perf_results.txt"

# Clear the output file
> "$output_file"

# Run perf for each event and store the output
for event in "${events[@]}"; do
    echo "Running perf stat for event: $event" | tee -a "$output_file"
    perf stat -e "$event" python3 ../../test.py &>> "$output_file"
    echo -e "\n\n" | tee -a "$output_file"
done

echo "All perf stat commands have been executed. Results are stored in $output_file."

