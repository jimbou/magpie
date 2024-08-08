# Performance Data Processor

This Python script traverses a given directory and its subdirectories to find JSON files named `performance_data.json`, extracts performance metrics, and calculates statistics on the number of retries per item.

## Description

The script processes each `performance_data.json` found within the specified directory structure. From each JSON file, it collects items and determines the item with the lowest `median_execution_time` for each unique `item_name`. Then, it aggregates these items to compute various statistics including the mean, median, IQR (Interquartile Range), and standard deviation of the `number_of_retries` for each `item_name`. The resulting statistics are saved in a CSV file named `best_retry_per_item.csv` within the same directory specified.

## Features

- **Directory Traversal**: Recursively searches for `performance_data.json` files in the specified directory and all subdirectories.
- **Data Extraction**: Identifies the item with the lowest median execution time from each JSON file.
- **Statistical Analysis**: Calculates mean, median, IQR, and standard deviation for the `number_of_retries` of each unique `item_name`.
- **CSV Output**: Outputs the computed statistics into a CSV file in the base directory.

## Usage

To use this script, you must have Python installed on your system along with the `pandas` and `scipy` libraries. You can install these libraries using pip if they are not already installed:

```bash
pip install pandas scipy
