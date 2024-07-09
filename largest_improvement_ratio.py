import json
import numpy as np
import sys
import os
import csv
from statistics import median

def find_largest_improvement_ratio(fitness_values):
    if not fitness_values:
        return 0  # Return 0 if the list is empty
    
    current_best = fitness_values[0]  # Initialize current best as the first value
    max_improvement = 0
    max_improvement_index = 1

    for i in range(1, len(fitness_values)):  # Start from the second element
        value = fitness_values[i]
        improvement = current_best - value if current_best > value else 0
        if improvement > max_improvement:
            max_improvement = improvement
            max_improvement_index = i
        current_best = min(current_best, value)  # Update the current best
    
    if max_improvement_index == -1:
        return 0  # No improvement found
    
    # Calculate the ratio of the index with the largest improvement
    ratio = max_improvement_index / len(fitness_values)
    return (ratio, max_improvement_index)

def process_json(json_file_path):
    with open(json_file_path, 'r') as file:
        data = json.load(file)
    
    item_name_dict = {}
    retries_dict = {}

    item_name_dict_num = {}
    retries_dict_num = {}
    i=0

    for element in data['items']:
        item_name = element['item_name']
        retries = element['number_of_retries']
        fitness_values = element['fitness_values']
        # print(item_name,retries) 
        (improvement_ratio, improvement_num) = find_largest_improvement_ratio(fitness_values)
        
        if item_name not in item_name_dict:
            item_name_dict[item_name] = []
        item_name_dict[item_name].append(improvement_ratio)
        
        if retries not in retries_dict:
            retries_dict[retries] = []
        retries_dict[retries].append(improvement_ratio)

        if item_name not in item_name_dict_num:
            item_name_dict_num[item_name] = []
        item_name_dict_num[item_name].append(improvement_num)
        
        if retries not in retries_dict_num:
            retries_dict_num[retries] = []
        retries_dict_num[retries].append(improvement_num)
    
    # Calculate median for each item_name
    item_name_median_dict_num = {item: median(nums) for item, nums in item_name_dict_num.items()}
    # Calculate median for each retries
    retries_median_dict_num = {retry: median(nums) for retry, nums in retries_dict_num.items()}

    item_name_median_dict = {item: median(ratios) for item, ratios in item_name_dict.items()}
    # Calculate median for each retries
    retries_median_dict = {retry: median(ratios) for retry, ratios in retries_dict.items()}
    
    return item_name_median_dict, retries_median_dict , item_name_median_dict_num, retries_median_dict_num

def save_dict_to_csv(data_dict, csv_file_path):
    # Sort the dictionary by its values (median values)
    sorted_data = sorted(data_dict.items(), key=lambda x: x[1])
    
    with open(csv_file_path, 'w', newline='') as csvfile:
        writer = csv.writer(csvfile)
        writer.writerow(['Key', 'Median_Largest_Improvement_Ratio'])
        for key, value in sorted_data:
            writer.writerow([key, value])

def save_dict_to_csv_num(data_dict, csv_file_path):
    # Sort the dictionary by its values (median values)
    sorted_data = sorted(data_dict.items(), key=lambda x: x[1])
    
    with open(csv_file_path, 'w', newline='') as csvfile:
        writer = csv.writer(csvfile)
        writer.writerow(['Key', 'Median_Largest_Improvement'])
        for key, value in sorted_data:
            writer.writerow([key, value])

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python script.py <json_file_path>")
        sys.exit(1)
    
    json_file_path = sys.argv[1]
    item_name_median_dict, retries_median_dict, item_name_median_dict_num, retries_median_dict_num = process_json(json_file_path)
    
    # Get directory of the JSON file
    json_dir = os.path.dirname(json_file_path)
    
    # Create file paths for the CSV files
    item_name_csv_path = os.path.join(json_dir, 'largest_improvement_ratio_per_metric.csv')
    retries_csv_path = os.path.join(json_dir, 'largest_improvement_ratio_per_retry.csv')
    item_name_csv_path_num = os.path.join(json_dir, 'largest_improvement_per_metric.csv')
    retries_csv_path_num = os.path.join(json_dir, 'largest_improvement_per_retry.csv')
    
    
    # Save dictionaries to CSV files
    save_dict_to_csv(item_name_median_dict, item_name_csv_path)
    save_dict_to_csv(retries_median_dict, retries_csv_path)
    save_dict_to_csv_num(item_name_median_dict_num, item_name_csv_path_num)
    save_dict_to_csv_num(retries_median_dict_num, retries_csv_path_num)
    
    print("CSV files have been saved:")
    print(f"Item Name Median CSV: {item_name_csv_path}")
    print(f"Retries Median CSV: {retries_csv_path}")
    print(f"Item Name  CSV: {item_name_csv_path_num}")
    print(f"Retries CSV: {retries_csv_path_num}")