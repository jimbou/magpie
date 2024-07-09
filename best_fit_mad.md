# Normalized Mean Absolute Deviation Analysis Script

## Overview
This Python script is designed to analyze a series of fitness values provided in a JSON format. For each item in the dataset, the script fits a linear regression line, calculates the absolute deviations from this line, and then computes the Mean Absolute Deviation (MAD). The MAD is further normalized by dividing it by the mean of the observed values, making it a robust and scale-independent metric suitable for comparing datasets with different units or scales (e.g., time, number of instructions).

## Purpose
The script's purpose is to provide a clear, normalized measure of how much, on average, each data point deviates from a best-fit line. This normalized metric allows for meaningful comparisons across different types of data, which is particularly useful in scenarios where the datasets vary significantly in their range or unit of measure.

## Mathematical Background
- **Linear Regression**: A statistical method used to model the relationship between a dependent variable and one or more independent variables. The line of best fit is determined by minimizing the squared difference between the predicted and actual values.
- **Mean Absolute Deviation (MAD)**: This is calculated as the average of the absolute differences between the observed values and the values predicted by the linear regression. It measures the average magnitude of deviations from the fitted line, providing a sense of typical deviation size.
- **Normalization**: By dividing the MAD by the mean of the observed values, the result is a dimensionless ratio that provides a scale-independent measure of variability relative to the size of the data itself.

## Requirements
To run this script, you need Python installed on your system along with the following libraries:
- `numpy`
- `pandas`
- `scipy`

These can be installed via pip:
pip install numpy pandas scipy

vbnet
Αντιγραφή κώδικα

## Usage
1. Prepare your JSON file with the following structure:
    ```json
    {
        "items": [
            {
                "item_name": "SampleItem1",
                "number_of_retries": 1,
                "fitness_values": [10, 20, 30, 40]
            },
            {
                "item_name": "SampleItem2",
                "number_of_retries": 2,
                "fitness_values": [15, 25, 35, 45]
            }
        ]
    }
    ```

2. Save the script in a file, for example, `analyze_data.py`.

3. Run the script from the command line by navigating to the directory where the script is saved and executing:
    ```
    python analyze_data.py path_to_your_file.json
    ```

    Replace `path_to_your_file.json` with the actual path to your JSON file.

4. After running, check the directory of your JSON file for the `results.csv` file containing the normalized MAD for each item.

## Output
The CSV file will have two columns:
- `Item_Retry`: Combines the item name and number of retries.
- `Normalized_MAD`: The normalized mean absolute deviation of the residuals from the linear regression fit.

This analysis tool is particularly useful for researchers and data analysts looking to c