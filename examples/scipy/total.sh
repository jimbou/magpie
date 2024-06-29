#!/bin/bash
COMMAND="$*"

# Check for input command
if [ $# -eq 0 ]; then
    echo "No command provided."
    exit 1
fi

# Gather the full command as a single string


./run.sh $COMMAND
python3 create_factors.py medians.json
python3 create_variance.py medians_all.json
# sudo ./total.sh "bash run_fixed.sh level=6 wbits=15 memLevel=8 strategy=0"