import os
import sys
import pandas as pd
import numpy as np

def find_csv_files(root_dir, filename):
    """Recursively find all CSV files with the specified name in the directory and its subdirectories."""
    csv_files = []
    for subdir, dirs, files in os.walk(root_dir):
        for file in files:
            if file == filename:
                csv_files.append(os.path.join(subdir, file))
    return csv_files

def read_and_combine_csv(files):
    """Read multiple CSV files, append them into a single DataFrame, indexed by the first column."""
    data_frames = []
    for file in files:
        df = pd.read_csv(file, index_col=0)
        data_frames.append(df)
    combined_df = pd.concat(data_frames)
    return combined_df

def calculate_medians(combined_df):
    """Calculate medians across rows grouped by the index (first column in original CSV)."""
    median_df = combined_df.groupby(combined_df.index).median()
    return median_df

def save_median_csv(median_df, output_file):
    """Save the median DataFrame to a CSV file."""
    median_df.to_csv(output_file)

def process_csvs(root_dir, csv_name):
    """Main function to process CSVs."""
    csv_files = find_csv_files(root_dir, csv_name)
    combined_df = read_and_combine_csv(csv_files)
    median_df = calculate_medians(combined_df)
    output_file = os.path.join(root_dir, csv_name.replace('.csv', '_median.csv'))
    save_median_csv(median_df, output_file)
    print(f"Median CSV has been saved to {output_file}")

if __name__ == "__main__":
    if len(sys.argv) != 3:
        print("Usage: python script_name.py <directory_path> <csv_filename>")
    else:
        directory_path = sys.argv[1]
        csv_filename = sys.argv[2]
        process_csvs(directory_path, csv_filename)