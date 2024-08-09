
# Fitness Analysis Processor

## Overview
This script is designed to automate the process of analyzing fitness performance data across multiple directories. It reads `fitness_analysis_results.csv` files from each subdirectory within a specified root directory, aggregates this data, and calculates the median values for several metrics grouped by `item_name` and `number_of_retries`.

## Prerequisites
- Python 3.x
- Pandas library
- Numpy library

You can install the required Python libraries using pip:
```
pip install pandas numpy
```

## Usage
The script is executed from the command line, where the root directory path must be provided as an argument.

```bash
python fitness_analysis_processor.py /path/to/your/directory
```

### What the Script Does
1. **Traverses Subdirectories**: The script walks through each subdirectory of the provided root directory.
2. **Reads CSV Files**: It looks for `fitness_analysis_results.csv` in each subdirectory and reads it if found.
3. **Aggregates Data**: All the CSV files are aggregated into a single dataset.
4. **Calculates Medians**:
    - By `item_name`: It computes the median of `total_decreases`, `average_decrease_percentage_per_step`, `std_deviation_of_decreases`, and `proportion_of_large_decreases`.
    - By `number_of_retries`: It computes the median of `average_decrease_percentage_per_step`, `std_deviation_of_decreases`, and `proportion_of_large_decreases`.
5. **Outputs CSV Files**:
    - `fitness_analysis_grouped_by_item.csv`: Contains aggregated data by item name.
    - `fitness_analysis_grouped_by_retries.csv`: Contains aggregated data by number of retries.

The output files are saved in the specified root directory.

## Error Handling
The script includes error handling to manage issues during file reading. If a file cannot be read, it will output an error message specifying the file and the error encountered.

## Conclusion
This script provides a fast and automated approach to process and analyze large sets of fitness analysis data, making it easier to derive insights across multiple directories.