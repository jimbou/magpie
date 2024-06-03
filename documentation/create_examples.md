
# Documentation for `create_examples.sh` Script
## this script must be in the same file with your template scenario

## Example template scenario 

'''bash
[software]
path = examples/triangle-c
target_files =
    triangle.c.xml
fitness = placeholder1

init_cmd = bash init_slow.sh
compile_cmd = make test_triangle placeholder2
test_cmd = ./test_triangle
run_cmd = perf stat -e placeholder3 ./placeholder2
run_timeout = 1

[search]
max_steps = 100
max_time = 60
possible_edits =
    SrcmlStmtReplacement
    SrcmlStmtInsertion
    SrcmlStmtDeletion

```

## Overview

The `create_examples.sh` script is designed to automate the creation of multiple configuration files for performance profiling using `perf`. It includes custom cases for specific metrics such as `energy`, `weight`, and now `time2`, each tailored to different profiling requirements.

## Usage

```bash
./create_examples.sh <scenario filename> <executable>
```

**Parameters:**
- `<scenario filename>`: The name of the template file containing placeholders.
- `<executable>`: The executable or command you want to profile.

## Script Description

The script operates by iterating over a predefined list of performance metrics and modifying a template file to replace placeholders with specific values relevant to each metric. It generates new files tailored to different profiling needs:

### Standard Metrics

For standard performance metrics like `instructions`, `cycles`, etc., the script modifies the template to include appropriate `perf stat -e` event identifiers.

### Special Cases

- **perf_time**: Handles the `perf_time` metric specially by removing extra flags.
- **energy**: Replaces the standard `perf` command with `./run_rapl_energy.sh` to measure energy using RAPL.
- **weight**: Uses `./run_custom.sh` to calculate a custom metric based on the weighted sum of assembly instructions.
- **time**: Similar to `time` but removes the `perf stat -e` completely and uses `time` as the metric identifier.

## File Creation

Each metric results in a separate configuration file with the metric-specific modifications applied. The script outputs the name of each created file for tracking.

## Error Handling

The script checks for the correct number of arguments and ensures that the specified template file exists before proceeding. If any requirements are not met, it outputs an appropriate error message and exits.

## Example Command

To create profiling configurations for an executable called `my_program` using a template named `config_template.txt`, you would run:

```bash
./create_examples.sh config_template.txt my_program
```

This would generate multiple configuration files each tailored to a specific metric for detailed performance analysis.

## Conclusion

The `create_examples.sh` script simplifies the process of generating multiple performance profiling configurations, allowing for detailed and specific performance analysis across various metrics.