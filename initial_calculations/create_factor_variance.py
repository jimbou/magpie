import os
import json
import numpy as np

def find_json_files(directory):
    """Find all 'factors.json' files in the given directory and its subdirectories."""
    json_files = []
    for root, dirs, files in os.walk(directory):
        for file in files:
            if file == 'factors.json':
                json_files.append(os.path.join(root, file))
    return json_files

def calculate_variance(json_files):
    """Calculate the variance percentage for each metric across all found JSON files."""
    metric_data = {}
    for file_path in json_files:
        with open(file_path, 'r') as file:
            data = json.load(file)
            for key, value in data.items():
                if key not in metric_data:
                    metric_data[key] = []
                metric_data[key].append(value)

    variances = {}
    for key, values in metric_data.items():
        if len(values) > 1:
            mean_value = np.mean(values)
            variance_value = np.var(values)
            variance_percent = (np.sqrt(variance_value) / mean_value) * 100 if mean_value != 0 else 0
            variances[key] = variance_percent
        else:
            variances[key] = 0.0  # No variance if there's only one value

    return variances

def main(directory):
    json_files = find_json_files(directory)
    variances = calculate_variance(json_files)

    # # Save the variance results to a JSON file
    # with open('factor_variance.json', 'w') as file:
    #     json.dump(variances, file, indent=4)
    output_file_path = os.path.join(directory, 'factor_variance.json')
    with open(output_file_path, 'w') as file:
        json.dump(variances, file, indent=4)

    print("Variance data saved to 'variance.json'.")

if __name__ == "__main__":
    import sys
    if len(sys.argv) != 2:
        print("Usage: python script.py <directory>")
    else:
        main(sys.argv[1])
