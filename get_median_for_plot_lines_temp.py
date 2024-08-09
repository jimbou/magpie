import os
import pandas as pd
import numpy as np
import sys

def process_files(root_dir):
    all_data = []

    # Traverse the directory structure
    for subdir,dir, files in os.walk(root_dir):
        #read the file data/fitness_analysis_results.csv in the subdir if it exists
        for file in os.listdir(subdir):
            if file == 'fitness_analysis_results.csv':
                file_path = os.path.join(subdir, file)
                print(f"Reading {file_path}...")
                try:
                    # Read each CSV file
                    data = pd.read_csv(file_path)
                    all_data.append(data)
                except Exception as e:
                    print(f"Failed to read {file_path}: {str(e)}")

    # Combine all data into a single DataFrame
    combined_data = pd.concat(all_data, ignore_index=True)

    # Group by item_name and calculate medians
    grouped_by_item = combined_data.groupby('item_name').median()
    grouped_by_item[['total_decreases','average_decrease_percentage_per_step', 'proportion_of_decreases']].to_csv(f'{root_dir}/fitness_analysis_grouped_by_item.csv')

    # Group by number_of_retries and calculate medians
    grouped_by_retries = combined_data.groupby('number_of_retries').median()
    grouped_by_retries[['total_decreases','average_decrease_percentage_per_step', 'proportion_of_decreases']].to_csv(f'{root_dir}/fitness_analysis_grouped_by_retries.csv')

    print(f"Files have been processed and saved.{root_dir}fitness_analysis_grouped_by_retries.csv")

# Get the directory input from user
#if the user has not given one argument
if len(sys.argv) != 2:
    print("Please provide the root directory path as an argument.")
    sys.exit(1)
root_directory = sys.argv[1]
print(f"Processing files in {root_directory}...")
process_files(root_directory)
