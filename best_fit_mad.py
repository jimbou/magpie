import json
import numpy as np
import pandas as pd
from scipy.stats import linregress
import sys
import os

def process_json_data(json_file):
    # Load JSON data
    with open(json_file, 'r') as file:
        data = json.load(file)

    # Dictionary to store results
    results = {}

    # Process each item in the JSON data
    for item in data['items']:
        fitness_values = item['fitness_values']
        item_name = item['item_name']
        number_of_retries = item['number_of_retries']
        
        # Ensure there is enough data to perform regression
        if len(fitness_values) < 2:
            print(f"Not enough data points for {item_name} to perform regression.")
            continue

        # Linear regression on the fitness values
        x = np.arange(len(fitness_values))
        y = np.array(fitness_values)
        slope, intercept, r_value, p_value, std_err = linregress(x, y)

        # Predict values using the fitted line
        y_pred = slope * x + intercept

        # Calculate residuals
        residuals = np.abs(y - y_pred)

        # Calculate the mean absolute deviation (MAD)
        mean_absolute_deviation = np.mean(residuals)

        # Normalize the MAD by the mean of y
        if np.mean(y) != 0:
            normalized_mad = mean_absolute_deviation / np.abs(np.mean(y))
        else:
            normalized_mad = float('inf')  # Handle division by zero if mean is zero

        # Store the result
        key = f"{item_name}_{number_of_retries}"
        results[key] = normalized_mad

    # Define the output CSV file path
    output_file = os.path.join(os.path.dirname(json_file), 'best_fit_mad.csv')

    # Convert the results dictionary to a DataFrame
    results_df = pd.DataFrame(list(results.items()), columns=['Item_Retry', 'Normalized_MAD'])

    # Save the results to a CSV file in the same directory as the JSON file
    results_df.to_csv(output_file, index=False)
    print(f"Results saved to '{output_file}'.")

if __name__ == '__main__':
    if len(sys.argv) != 2:
        print("Usage: python script_name.py path_to_your_file.json")
    else:
        json_file = sys.argv[1]
        process_json_data(json_file)
