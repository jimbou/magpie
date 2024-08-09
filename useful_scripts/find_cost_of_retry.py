import os
import json
import pandas as pd
import sys
from scipy.stats import iqr

def gather_performance_data(base_dir):
    # Initialize an empty DataFrame
    columns = ['item_name', 'number_of_steps', 'number_of_retries', 'percentage_decrease']
    df = pd.DataFrame(columns=columns)

    # Walk through the directory structure
    for root, dirs, files in os.walk(base_dir):
        if 'performance_data.json' in files:
            # Construct the full file path
            file_path = os.path.join(root, 'performance_data.json')
            # Read and process the JSON file
            with open(file_path, 'r') as file:
                data = json.load(file)
                items_data = data.get('items', [])

                # First, collect the baseline number of steps for number_of_retries = 1
                baseline_steps = {}
                for item in items_data:
                    if item.get('number_of_retries') == 1:
                        baseline_steps[item.get('item_name')] = item.get('number_of_steps')
                
                # Process each item to calculate the percentage decrease
                for item in items_data:
                    item_name = item.get('item_name')
                    number_of_steps = item.get('number_of_steps')
                    number_of_retries = item.get('number_of_retries')
                    
                    if number_of_retries == 1:
                        percentage_decrease = 0  # Baseline case
                    else:
                        # Calculate percentage decrease only if the baseline exists
                        if item_name in baseline_steps and baseline_steps[item_name] > 0:
                            baseline = baseline_steps[item_name]
                            percentage_decrease = ((baseline - number_of_steps) / baseline) * 100
                        else:
                            percentage_decrease = 0  # Either no baseline or division by zero
                        
                    # Append the data to the DataFrame
                    df = df.append({
                        'item_name': item_name,
                        'number_of_steps': number_of_steps,
                        'number_of_retries': number_of_retries,
                        'percentage_decrease': percentage_decrease
                    }, ignore_index=True)

    return df

def analyze_decrease(df):
    df = df[df['percentage_decrease'].notna()]
    stats_df = df.groupby('number_of_retries')['percentage_decrease'].agg(
        mean_percentage_decrease='mean',
        median_percentage_decrease='median',
        std_deviation='std',
        iqr=(lambda x: iqr(x, rng=(25, 75)))  # Corrected keyword for calculating IQR
    ).reset_index()

    return stats_df


if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python script.py <path_to_base_directory>")
    else:
        result_df = gather_performance_data(sys.argv[1])
        stats_df = analyze_decrease(result_df)
        print(stats_df)
        stats_df.to_csv(sys.stdout, index=False)
        # Optionally, save the DataFrame to a CSV file
        stats_df.to_csv(os.path.join(sys.argv[1], 'performance_decrease_stats.csv'), index=False)
