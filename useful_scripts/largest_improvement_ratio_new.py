import json
import numpy as np
import sys
import os
import csv
from statistics import median

def find_largest_improvement_ratio(fitness_values):
    if not fitness_values:
        return 0, 0, 0, 0  # Return 0 if the list is empty

    current_best = fitness_values[0]
    max_improvement = 0
    max_improvement_index = 1
    max_improvement_perc = 0
    max_improvement_perc_index = 0

    for i in range(1, len(fitness_values)):
        value = fitness_values[i]
        improvement = current_best - value if current_best > value else 0
        if improvement > max_improvement:
            max_improvement = improvement
            max_improvement_index = i
        improvement_perc = (current_best - value) / current_best if current_best > value else 0
        if improvement_perc > max_improvement_perc:
            max_improvement_perc = improvement_perc
            max_improvement_perc_index = i   
        current_best = min(current_best, value)
    
    ratio = max_improvement_index / len(fitness_values)
    ratio_perc = max_improvement_perc_index / len(fitness_values)
    return ratio, max_improvement_index, ratio_perc, max_improvement_perc_index

def process_json(json_file_path):
    with open(json_file_path, 'r') as file:
        data = json.load(file)

    category_dicts = {
        'item_name': {}, 'retries': {}, 'item_name_num': {}, 'retries_num': {},
        'item_name_perc': {}, 'retries_perc': {}, 'item_name_perc_num': {}, 'retries_perc_num': {}
    }

    for element in data['items']:
        item_name = element['item_name']
        retries = element['number_of_retries']
        fitness_values = element['fitness_values']
        
        improvement_ratio, improvement_num, improvement_perc_ratio, improvement_perc_num = find_largest_improvement_ratio(fitness_values)
        
        categories = [
            ('item_name', item_name, improvement_ratio),
            ('retries', retries, improvement_ratio),
            ('item_name_num', item_name, improvement_num),
            ('retries_num', retries, improvement_num),
            ('item_name_perc', item_name, improvement_perc_ratio),
            ('retries_perc', retries, improvement_perc_ratio),
            ('item_name_perc_num', item_name, improvement_perc_num),
            ('retries_perc_num', retries, improvement_perc_num)
        ]

        for category_name, key, value in categories:
            if key not in category_dicts[category_name]:
                category_dicts[category_name][key] = []
            category_dicts[category_name][key].append(value)

    result = {}
    for name, cat_dict in category_dicts.items():
        result[name] = {key: (median(values), np.percentile(values, 75) - np.percentile(values, 25)) for key, values in cat_dict.items()}
    
    return result

def save_dict_to_csv(data_dict, csv_file_path, header):
    sorted_data = sorted(data_dict.items(), key=lambda x: x[1][0])  # Sort by median
    
    with open(csv_file_path, 'w', newline='') as csvfile:
        writer = csv.writer(csvfile)
        writer.writerow(header)
        for key, value in sorted_data:
            writer.writerow([key, value[0], value[1]])

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python script.py <json_file_path>")
        sys.exit(1)
    
    json_file_path = sys.argv[1]
    results = process_json(json_file_path)
    
    json_dir = os.path.dirname(json_file_path)
    json_dir = os.path.join(json_dir, 'largest_improvement_ratio')
    os.makedirs(json_dir, exist_ok=True)
    
    file_paths = {
        'item_name': 'largest_improvement_ratio_per_metric.csv',
        'retries': 'largest_improvement_ratio_per_retry.csv',
        'item_name_num': 'largest_improvement_per_metric.csv',
        'retries_num': 'largest_improvement_per_retry.csv',
        'item_name_perc': 'largest_improvement_percentage_ratio_per_metric.csv',
        'retries_perc': 'largest_improvement_percentage_ratio_per_retry.csv',
        'item_name_perc_num': 'largest_improvement_percentage_per_metric.csv',
        'retries_perc_num': 'largest_improvement_percentage_per_retry.csv'
    }

    for key, data in results.items():
        csv_path = os.path.join(json_dir, file_paths[key])
        header = ['Key', 'Median', 'IQR']
        save_dict_to_csv(data, csv_path, header)
        
        print(f"CSV file for {key} has been saved to {csv_path}")
