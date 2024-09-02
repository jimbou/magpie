import os
import pandas as pd
import json
import sys
def find_files(directory, filename):
    """Find all files with the given filename in the specified directory and subdirectories."""
    matches = []
    for root, dirs, files in os.walk(directory):
        for name in files:
            if name == filename:
                matches.append(os.path.join(root, name))
    return matches

def process_json_file(filepath):
    """Read the JSON file and extract relevant data."""
    with open(filepath, 'r') as file:
        data = json.load(file)
    
    records = []
    for item in data.get('items', []):
        item_name = item.get('item_name')
        # number_of_retries = item.get('number_of_retries')
        fitness_values_length = len(item.get('fitness_values', []))
        records.append((item_name, fitness_values_length))
    
    return records

def create_dataframe(directory, filename):
    """Create a pandas DataFrame from all JSON files found in the directory."""
    files = find_files(directory, filename)
    all_records = []
    
    for file in files:
        records = process_json_file(file)
        all_records.extend(records)
    
    df = pd.DataFrame(all_records, columns=['item_name', 'fitness_values_length'])
    return df

# Example usage
if __name__ == "__main__":
    if len(sys.argv) != 3:
        print("Usage: python script_name.py <directory_path> <csv_filename>")
    else:
        directory_path = sys.argv[1]
        csv_filename = sys.argv[2]
        df = create_dataframe(directory_path, csv_filename)
        print(df)

        #group the dataframe by item_name using  mean
        grouped_df = df.groupby('item_name').mean()
        #i want the standard deviation of the fitness_values_length
        print(grouped_df)
        #print the results ranked in a csv
        grouped_df.to_csv('epochs_per_measure.csv')