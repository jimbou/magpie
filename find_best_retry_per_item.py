import os
import json
import pandas as pd
import numpy as np
import sys
from scipy.stats import iqr

def gather_performance_data(base_dir):
    # Initialize an empty DataFrame
    columns = ['item_name', 'median_execution_time', 'number_of_retries']
    df = pd.DataFrame(columns=columns)

    # Walk through the directory structure
    for root, dirs, files in os.walk(base_dir):
        if 'performance_data.json' in files:
            # Construct the full file path
            file_path = os.path.join(root, 'performance_data.json')
            # Read the JSON file
            with open(file_path, 'r') as file:
                data = json.load(file)
                # Process items to find the one with the lowest execution time per item name
                min_time_items = {}
                for item in data.get('items', []):
                    item_name = item.get('item_name')
                    median_execution_time = item.get('median_execution_time')
                    number_of_retries = item.get('number_of_retries')
                    if item_name not in min_time_items or min_time_items[item_name]['median_execution_time'] > median_execution_time:
                        # Update the record if this item has a lower median execution time
                        min_time_items[item_name] = {
                            'item_name': item_name,
                            'median_execution_time': median_execution_time,
                            'number_of_retries': number_of_retries
                        }
                
                # Append the items with the minimum median execution time from this JSON to the DataFrame
                for item in min_time_items.values():
                    df = df.append(item, ignore_index=True)

    # Group by item_name and calculate stats
    retry_stats = df.groupby('item_name')['number_of_retries'].agg([
        ('retry_mean', 'mean'),
        ('retry_median', 'median'),
        ('retry_iqr', lambda x: iqr(x, rng=(25, 75))),  # IQR using scipy.stats.iqr
        ('retry_std_dev', 'std')  # Standard deviation
    ]).reset_index()

    # Save the retry statistics to a new CSV file in the base directory
    output_path = os.path.join(base_dir, 'best_retry_per_item.csv')
    retry_stats.to_csv(output_path, index=False)

    return df, retry_stats

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python script.py <path_to_base_directory>")
    else:
        _, retry_stats_df = gather_performance_data(sys.argv[1])
        print(f"Retry statistics saved to {os.path.join(sys.argv[1], 'best_retry_per_item.csv')}")
