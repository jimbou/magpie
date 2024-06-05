#!/bin/bash

# Check if an argument is provided
if [ $# -eq 0 ]; then
    echo "Usage: $0 <argument>"
    exit 1
fi

chmod 777 "$1"
# Execute the Python script with the provided argument
python3 plotter.py "$1/performance_data.json"
