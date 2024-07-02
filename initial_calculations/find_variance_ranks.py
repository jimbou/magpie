import os
import json
import numpy as np
import sys
from statistics import mean, median

def find_variance_files(directory):
    """ Recursively find all 'variance.json' files in given directory. """
    variance_files = []
    for root, dirs, files in os.walk(directory):
        for file in files:
            if file == 'variance.json':
                variance_files.append(os.path.join(root, file))
    return variance_files

def calculate_metric_ranks(files):
    """ Calculate the rank of each metric per file and then find the average and median rank for each metric. """
    metric_ranks = {}
    # Read each file and rank metrics
    for file_path in files:
        with open(file_path, 'r') as file:
            data = json.load(file)
            # Rank metrics in the current file
            sorted_metrics = sorted(data.items(), key=lambda item: item[1])
            for rank, (metric, value) in enumerate(sorted_metrics, start=1):
                if metric in metric_ranks:
                    metric_ranks[metric].append(rank)
                else:
                    metric_ranks[metric] = [rank]
    
    # Calculate average and median rank for each metric
    average_ranks = {metric: mean(ranks) for metric, ranks in metric_ranks.items()}
    median_ranks = {metric: median(ranks) for metric, ranks in metric_ranks.items()}
    
    return average_ranks, median_ranks

def save_sorted_dict_to_json(data_dict, json_file_path):
    # Sort the dictionary by its values
    sorted_data = {k: v for k, v in sorted(data_dict.items(), key=lambda item: item[1])}
    
    with open(json_file_path, 'w') as jsonfile:
        json.dump(sorted_data, jsonfile, indent=4)

def main(directory):
    variance_files = find_variance_files(directory)
    if not variance_files:
        print("No variance.json files found.")
        return
    
    average_ranks, median_ranks = calculate_metric_ranks(variance_files)
    
    # Get directory of the JSON file
    json_dir = os.path.dirname(directory)
    
    # Create file paths for the JSON files
    avg_output_file_path = os.path.join(json_dir, 'average_variance_ranks.json')
    med_output_file_path = os.path.join(json_dir, 'median_variance_ranks.json')
    
    # Save sorted dictionaries to JSON files
    save_sorted_dict_to_json(average_ranks, avg_output_file_path)
    save_sorted_dict_to_json(median_ranks, med_output_file_path)
    
    print(f"Average ranks have been written to {avg_output_file_path}.")
    print(f"Median ranks have been written to {med_output_file_path}.")

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python script.py <directory>")
        sys.exit(1)
    
    input_directory = sys.argv[1]
    main(input_directory)
