
# Writing New Scenario Files for MAGPIE

Scenario files in MAGPIE configure the settings for genetic improvement experiments. Below are templates and instructions for creating scenario files for different performance metrics such as execution time, instruction count, energy, and the custom weight metric.

## General Structure of Scenario Files

A scenario file in MAGPIE is structured into several sections, detailing everything from timeouts to the specific commands needed to run and test the software. Here’s how you can configure various performance metrics:

### Example for Time Metric
```ini
[magpie]
default_timeout = 100
default_lengthout = 1e6
local_original_copy = True
run_timeout = 100

[software]
path = examples/minisat
target_files = core/Solver.cc.xml
fitness = time

init_cmd = bash init.sh
setup_cmd = bash compile.sh
compile_cmd = bash compile.sh
test_cmd = bash test.sh
run_cmd = bash run_fixed.sh
retries = 1

[search]
max_steps = 10000
max_time = 700
possible_edits =
    SrcmlStmtReplacement
    SrcmlStmtInsertion
    SrcmlStmtDeletion
```

### Example for Performance Metrics (e.g., perf_instructions)
```ini
[magpie]
default_timeout = 100
default_lengthout = 1e6
local_original_copy = True
run_timeout = 100

[software]
path = examples/minisat
target_files = core/Solver.cc.xml
fitness = perf_instructions

init_cmd = bash init.sh
setup_cmd = bash compile.sh
compile_cmd = bash compile.sh
test_cmd = bash test.sh
run_cmd = perf stat -e instructions bash run_fixed.sh
retries = 1

[search]
max_steps = 10000
max_time = 700
possible_edits =
    SrcmlStmtReplacement
    SrcmlStmtInsertion
    SrcmlStmtDeletion
```

### Example for Energy
```ini
[magpie]
default_timeout = 100
default_lengthout = 1e6
local_original_copy = True
run_timeout = 100

[software]
path = examples/minisat
target_files = core/Solver.cc.xml
fitness = energy

init_cmd = bash init.sh
setup_cmd = bash compile.sh
compile_cmd = bash compile.sh
test_cmd = bash test.sh
run_cmd = ./run_rapl_energy.sh "bash run_fixed.sh"
retries = 1

[search]
max_steps = 10000
max_time = 700
possible_edits =
    SrcmlStmtReplacement
    SrcmlStmtInsertion
    SrcmlStmtDeletion
```

### Example for Custom Weights
```ini
[magpie]
default_timeout = 100
default_lengthout = 1e6
local_original_copy = True
run_timeout = 100

[software]
path = examples/minisat
target_files = core/Solver.cc.xml
fitness = weights

init_cmd = bash init.sh
setup_cmd = bash compile.sh
compile_cmd = bash compile.sh
test_cmd = bash test.sh
run_cmd = ./run_custom.sh "bash run_fixed.sh"
retries = 1

[search]
max_steps = 10000
max_time = 700
possible_edits =
    SrcmlStmtReplacement
    SrcmlStmtInsertion
    SrcmlStmtDeletion
```

### Automated Scenario File Creation
Use the `create_examples.sh` script from the `useful_scripts` directory to automatically generate scenario files for all measures. It uses a template and can differentiate between source code optimization and parameter tuning based on the position of the argument.

## Adjusting the retry parameter
If you want to run only a scenario file by yourself the adjust the retry parameter to your likign, if you want to test different configurations through the run_examples.py script, then leave it as 1 and the script will automatically spawn new scenario files with the different retry numbers.