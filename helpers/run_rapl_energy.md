# run_rapl_energy.sh Script Documentation

## Introduction

This Bash script, `run_rapl_energy.sh`, is designed to measure the energy consumption of a specified command on Linux systems equipped with Intel CPUs that support the Running Average Power Limit (RAPL) feature. It provides a straightforward method to track energy usage in microjoules (µJ) before and after command execution, making it particularly useful for energy profiling in performance optimization tasks.

## Prerequisites

- **Intel CPU with RAPL Support**: The script is specifically intended for Intel processors that provide RAPL energy counters.
- **Linux Operating System**: The RAPL interface is available through specific file paths in the Linux file system.
- **Access Permissions**: The script requires root or appropriate permissions to read from the `/sys/class/powercap/intel-rapl` directory.

## File Path

The energy measurement is conducted using the RAPL energy counter for the first domain, typically the CPU package, located at:

/sys/class/powercap/intel-rapl/intel-rapl:0/intel-rapl:0:0/energy_uj (if your system supports you can change the last 0 to 1 for uncore and 2 for ram)


This file stores the energy consumed by the CPU in microjoules.

## Script Execution Flow

1. **Verification of Energy File**: The script starts by checking if the RAPL energy file exists. If the file is not found, it exits with an error message stating "Energy file does not exist."
2. **Command Validation**: The script checks if a command has been provided as an argument. If no command is supplied, it prints the usage syntax and exits.
3. **Initial Energy Measurement**: Before executing the user command, the script records the energy value from the RAPL file.
4. **Command Execution**: Executes the provided command using `taskset` to pin the command to a specific CPU core, minimizing measurement disturbances.
5. **Final Energy Measurement**: After command execution, the script records the energy value again.
6. **Energy Calculation**: Calculates the energy consumed during the execution by subtracting the initial energy value from the final value.
7. **Result Output**: Prints the calculated energy consumption to stderr, facilitating integration with other scripts or tools.

## Usage

The script is intended to be invoked from the command line with a single argument, the command whose energy usage you wish to measure:

```bash
bash run_rapl_energy.sh "<command>"
