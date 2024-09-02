#!/bin/sh
# Script to run Python scripts with error checking

# Function to run a Python script and check for errors
run_python_script() {
    # echo python3 run_updated.py  $@
    /home/jim/magpie/.venv/bin/python run_updated.py  $@
    if [ $? -ne 0 ]; then
        echo "Error: Failed to execute $1" >&2
        exit 1
    fi
}
echo $@
# Pass all arguments to the function calls
run_python_script "data/complex_function_3.py" $@
run_python_script "data/complex_function_4.py" $@
run_python_script "data/complex_function_6.py" $@
run_python_script "data/complex_function_7.py" $@
run_python_script "data/complex_function_8.py" $@
run_python_script "data/complex_function_9.py" $@
run_python_script "data/complex_function_0.py" $@

echo "All scripts executed successfully."
exit 0
