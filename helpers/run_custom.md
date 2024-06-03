
# Documentation for Custom Metric Calculation Scripts (run_custom.sh, run_total.py)

## Introduction

This documentation provides a comprehensive guide to the `run_custom.sh` and `run_total.py` scripts. These tools are designed to calculate a custom metric based on the weighted sum of assembly commands executed by an application. The metric reflects the computational cost of operations and can be used as a fitness function in optimization frameworks like Magpie.

### Both scripts should be in the directory of the example you are running for magpie

## run_custom.sh Script

### Description

The `run_custom.sh` script orchestrates the profiling of an executable to collect data on its assembly-level execution. It uses the `perf` tool to record and annotate the performance, which is then processed by a Python script.

### Usage

```bash
./run_custom.sh <executable_command>
```

**Parameters:**
- `<executable_command>`: The command to execute and analyze.

### Execution Steps

1. **Check for Arguments**: Ensures the script is called with the necessary executable command.
2. **Record Performance**: Utilizes `perf record` to trace the assembly instructions executed by the command.
3. **Annotate Data**: Uses `perf annotate` to generate detailed reports of the assembly instructions execution.
4. **Invoke Python Script**: Calls `run_total.py` to analyze the annotated data and compute the custom metric based on pre-defined weights.

## run_total.py Script

### Description

The `run_total.py` script processes the output from `run_custom.sh` to calculate a custom metric. It parses data from performance reports, assigns weights to assembly instructions based on their types and operand usage, and sums up these values to provide a comprehensive performance metric.

### Usage

```bash
python3 run_total.py report1.txt report2.txt
```

**Parameters:**
- `report1.txt`: First performance report file.
- `report2.txt`: Second performance report file with detailed command execution.

### Processing Steps

1. **Parse Files**: Reads input files to gather execution frequency and assembly details.
2. **Assign Weights**: Looks up weights for each assembly instruction from a predefined dictionary.
3. **Compute Metric**: Calculates the total metric by multiplying execution frequencies with the corresponding weights.
4. **Output**: Prints and logs the computed metric in a format suitable for further analysis or optimization.

## Conclusion

These scripts are essential for developers and researchers interested in optimizing software performance through detailed assembly-level analysis. They provide a robust framework for quantifying the efficiency of different code paths within an application.