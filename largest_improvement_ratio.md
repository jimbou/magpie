
# JSON Data Processing Script

## Overview

This Python script processes a JSON file to evaluate and compute statistical metrics related to performance improvements across multiple retries and different metrics. It outputs the median best improvement ratio and the median improvement index into four separate CSV files.

## Features

- **Improvement Ratio Calculation**: Finds the largest improvement ratio across fitness values in the JSON data.
- **Improvement Index Calculation**: Determines the index at which the largest improvement occurs.
- **Median Aggregation**: Calculates the median of these improvement metrics across different item names and retry counts.

## Prerequisites

Ensure you have Python installed on your system along with the necessary libraries:

- numpy
- statistics

You can install the required libraries using pip:

```bash
pip install numpy
```

## Input Format

The input JSON should be structured as follows:

```json
{
    "items": [
        {
            "item_name": "Metric1",
            "number_of_retries": 5,
            "fitness_values": [0.1, 0.2, 0.1, 0.05, 0.03]
        },
        ...
    ]
}
```

## Usage

Run the script from the command line by specifying the path to your JSON file:

```bash
python script.py <json_file_path>
```

## Output

The script generates four CSV files:

1. **largest_improvement_ratio_per_metric.csv** - Median largest improvement ratio per item.
2. **largest_improvement_ratio_per_retry.csv** - Median largest improvement ratio per retry count.
3. **largest_improvement_per_metric.csv** - Median index of largest improvement per item.
4. **largest_improvement_per_retry.csv** - Median index of largest improvement per retry count.

Each CSV is saved in the same directory as the input JSON file.

## Troubleshooting

Ensure your JSON file is correctly formatted and accessible at the specified path. Misformatted data or incorrect paths will result in errors during execution.

For more detailed inquiries or to report issues, review the script comments and ensure all input data is properly formatted and complete.