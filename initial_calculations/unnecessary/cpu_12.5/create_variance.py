import json
import numpy as np
import sys

def calculate_variance_percentages(input_file):
    # Read the JSON data from the file
    try:
        with open(input_file, 'r') as file:
            data = json.load(file)
    except FileNotFoundError:
        print("File not found.")
        return
    except json.JSONDecodeError:
        print("Error decoding JSON.")
        return

    variance_data = {}
    
    # Calculate variance percentage for each metric
    for key, values in data.items():
        if len(values) > 1:
            # Calculate mean and variance
            array = np.array(values)
            mean_value = np.mean(array)
            variance = np.var(array)
            # Calculate standard deviation as a percentage of the mean
            if mean_value != 0:
                variance_percentage = (np.sqrt(variance) / mean_value) * 100
            else:
                variance_percentage = 0  # Avoid division by zero if mean is zero
            variance_data[key] = variance_percentage
        else:
            variance_data[key] = 0.0  # No variance if only one value or zero

    # Write the variance data to a new JSON file
    with open('variance.json', 'w') as outfile:
        json.dump(variance_data, outfile, indent=4)
    
    print("Variance percentages written to variance.json.")

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python script.py <input_file.json>")
    else:
        input_file_path = sys.argv[1]
        calculate_variance_percentages(input_file_path)
