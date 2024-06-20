#!/bin/bash

# Path to the RAPL energy file
energy_file="/sys/class/powercap/intel-rapl/intel-rapl:0/intel-rapl:0:2/energy_uj"

# Check if the file exists
if [ ! -f "$energy_file" ]; then
  echo "Energy file does not exist."
  exit 1
fi

if [ $# -eq 0 ]; then
  echo "Usage: $0 \"<command>\""
  exit 1
fi

# The command to execute is the first argument
user_command=$@
sleep 1
energy_value1=$(cat "$energy_file")
# Read the energy value
eval "taskset -c 0  $user_command"

# Output the energy value
energy_value2=$(cat "$energy_file")
sleep 1
printf "\nEnergy uncore consumed: $((energy_value2 - energy_value1))\n" 1>&2

