
# Fitness Value Analysis

## Overview

This script processes fitness values from a genetic algorithm to analyze the pattern of decrease in fitness values over time. It generates plots for the original and filtered fitness values and computes various metrics to quantify the decrease pattern.

## How It Works

1. **Filtering Decreasing Values**: The script filters the input list to keep only values that are in strictly decreasing order.
2. **Metrics Calculation**: It calculates several metrics to analyze the pattern of decrease:
    - **Total Steps**: The total number of elements in the filtered list.
    - **Average Decrease Per Step**: The average decrease in fitness value per step.
    - **Average Decrease Percentage Per Step**: The average decrease per step as a percentage of the initial fitness value.
    - **Standard Deviation of Decreases**: The standard deviation of the differences between consecutive values in the filtered list.
    - **Median Decrease**: The median of the differences between consecutive values.
    - **Number of Large Decreases**: The count of decreases that are larger than 1.5 times the average decrease per step.
    - **Proportion of Large Decreases**: The proportion of large decreases relative to the total number of decreases.sa

3. **Plot Generation**: The script generates and saves plots for both the original fitness values and the filtered fitness values.

4. **Results Storage**: The results are saved to a CSV file for further analysis.

## CSV File Explanation

The CSV file contains the following columns:

- **number_of_retries**: The number of retries for the given item.
- **item_name**: The name of the item.
- **total_decreases**: The total number of elements in the filtered decreasing sequence.
  - Higher value indicates a more gradual decrease.
  - Lower value indicates a more abrupt decrease.

- **average_decrease_per_step**: The average decrease in fitness value per step.
  - Higher value indicates a steeper decrease.
  - Lower value indicates a slower and steadier decrease.

- **average_decrease_percentage_per_step**: The average decrease per step as a percentage of the initial fitness value.
  - Higher value indicates a steeper decrease in percentage terms.
  - Lower value indicates a slower and steadier decrease in percentage terms.

- **std_deviation_of_decreases**: The standard deviation of the differences between consecutive values.
  - Higher value indicates more variability and irregularity in decreases.
  - Lower value indicates more uniform and steady decreases.

- **median_decrease**: The median of the differences between consecutive values.
  - Higher value indicates a generally steeper decrease.
  - Lower value indicates a generally more gradual decrease.

- **number_of_large_decreases**: The count of decreases that are larger than 1.5 times the average decrease per step.
  - Higher value indicates more significant drops in fitness value.
  - Lower value indicates fewer significant drops and a steadier decrease.

- **proportion_of_large_decreases**: The proportion of large decreases relative to the total number of decreases.
  - Higher value indicates a larger proportion of significant drops.
  - Lower value indicates a more steady decrease with fewer significant drops.

## Usage

1. **Install Dependencies**:
    - Ensure you have Python installed on your system.
    - Install the required packages using pip:
      ```bash
      pip install numpy pandas matplotlib
      ```

2. **Prepare Your Data**:
    - Place your JSON file containing the fitness values in a directory.

3. **Run the Script**:
    - Execute the script with the directory path and JSON filename as arguments:
      ```bash
      python script.py <directory_path> <json_filename>
      ```
      Replace `<directory_path>` with the path to your directory and `<json_filename>` with the name of your JSON file.

4. **View Results**:
    - The results will be saved in a CSV file located in the `data` subdirectory within your specified directory.
    - Plots for the fitness values will be saved in the `data_lines` subdirectory within your specified directory.

## Example

```bash
python script.py /path/to/your/data fitness_values.json
```

This command will process the `fitness_values.json` file located in `/path/to/your/data` and save the results accordingly.

## License

This project is licensed under the MIT License.