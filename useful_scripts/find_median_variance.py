import os
import json
import numpy as np
import sys

def find_variance_files(directory):
    """ Recursively find all 'variance.json' files in given directory. """
    variance_files = []
    for root, dirs, files in os.walk(directory):
        for file in files:
            if file == 'variance.json':
                variance_files.append(os.path.join(root, file))
    return variance_files

def calculate_median_variance(files):
    """ Calculate the median of variance values for each metric from multiple JSON files. """
    metric_data = {}
    # Read each file and aggregate data
    for file_path in files:
        with open(file_path, 'r') as file:
            data = json.load(file)
            for key, value in data.items():
                if key in metric_data:
                    metric_data[key].append(value)
                else:
                    metric_data[key] = [value]
    
    # Calculate median for each metric
    median_variances = {}
    for key, values in metric_data.items():
        if values:
            median_variances[key] = np.median(values)
    
    return median_variances

def main(directory):
    variance_files = find_variance_files(directory)
    if not variance_files:
        print("No variance.json files found.")
        return
    
    median_variances = calculate_median_variance(variance_files)
    
    # Write the median variances to a new JSON file
    output_file_path = os.path.join(directory, 'median_variance.json')
    with open(output_file_path, 'w') as file:
        json.dump(median_variances, file, indent=4)
    
    print(f"Median variance values have been written to {output_file_path}.")

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python script.py <directory>")
        sys.exit(1)
    
    input_directory = sys.argv[1]
    main(input_directory)
