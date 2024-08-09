
# Find Median Across Benchmarks

## Overview

The `find_median_across_benchmarks.py` script is designed to process multiple CSV files across a directory structure, finding CSV files with a specified name. It computes the median of numerical values for each unique identifier found in the first column of these CSV files, consolidating the results into a single CSV file.

## How It Works

The script operates as follows:

1. **Directory and File Search**: It searches recursively through a given directory and all its subdirectories to find CSV files matching a specific filename.

2. **Data Aggregation**: It reads each found CSV file, assuming the first column as a unique identifier and subsequent columns as numerical data. All these CSV files are then appended into a single DataFrame.

3. **Median Calculation**: For each unique identifier (index), the script calculates the median of each column from all entries matching that identifier across different CSV files.

4. **Output**: The median values for each identifier are saved into a new CSV file in the specified directory, appending `_median` to the original filename to distinguish it.

## Prerequisites

Before running this script, ensure that you have Python installed on your system along with the `pandas` and `numpy` libraries. These can be installed via pip if not already available:

```bash
pip install pandas numpy
```

## Usage

To use the script, you need to specify the path to the directory containing the CSV files and the name of the CSV files to process. Run the script from the command line as follows:

```bash
python find_median_across_benchmarks.py <directory_path> <csv_filename>
```

### Parameters:

- `<directory_path>`: The path to the directory where the script will begin searching for CSV files.
- `<csv_filename>`: The name of the CSV files to search for in the directory and its subdirectories.

### Example:

```bash
python3 find_median_across_benchmarks.py /home/user/data benchmarks.csv
```

This command will process all files named `benchmarks.csv` found within `/home/user/data` and its subdirectories, and it will output the medians to a file named `benchmarks_median.csv` in `/home/user/data`.

## Output

The output CSV file will be placed in the root of the specified directory and will contain two columns:
- The first column will list the unique identifiers.
- The second column will contain the computed median values for each identifier across the specified CSV files.

For detailed questions or troubleshooting, refer to the script's inline comments or consider raising an issue in the repository where this script is maintained.