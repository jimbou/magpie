import os
import json
import numpy as np

# Function to calculate coefficient of variation
def coefficient_of_variation(data):
    mean = np.mean(data)
    std_dev = np.std(data)
    return (std_dev / mean) * 100

# Set the directory where the benchmark subdirectories are located
root_directory = 'examples'

# Dictionary to store all factor values for each measure
factors = {}

# Traverse through each subdirectory to access factors.json
for subdir, dirs, files in os.walk(root_directory):
    for file in files:
        if file == 'factors.json':
            file_path = os.path.join(subdir, file)
            with open(file_path, 'r') as json_file:
                data = json.load(json_file)
                # Collect each measure's data across all benchmarks
                for key, value in data.items():
                    if key in factors:
                        factors[key].append(value)
                    else:
                        factors[key] = [value]

# Calculate the coefficient of variation for each measure
cv_results = {}
for measure, values in factors.items():
    cv = coefficient_of_variation(values)
    cv_results[measure] = cv

# Sort measures by CV in ascending order (lower CV means better stability)
sorted_cv_results = sorted(cv_results.items(), key=lambda item: item[1])

# Print the sorted results
print("Measures ranked by their Coefficient of Variation (CV):")
for measure, cv in sorted_cv_results:
    print(f"{measure}: {cv:.2f}%")

# Optional: Write CV results to a JSON file in sorted order
with open('sorted_cv_results.json', 'w') as outfile:
    json.dump(dict(sorted_cv_results), outfile, indent=4)
