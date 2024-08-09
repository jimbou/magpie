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

def calculate_statistics(combined_df):
    """Calculate medians, means, standard deviations, and IQRs across rows grouped by the index."""
    # Calculating the median for each group
    median_df = combined_df.groupby(combined_df.index).median()
    median_df.rename(columns=lambda x: x + '_median', inplace=True)

    # Calculating the mean for each group
    mean_df = combined_df.groupby(combined_df.index).mean()
    mean_df.rename(columns=lambda x: x + '_mean', inplace=True)

    # Calculating the standard deviation for each group
    std_df = combined_df.groupby(combined_df.index).std()
    std_df.rename(columns=lambda x: x + '_std', inplace=True)

    # Calculating the IQR for each group
    Q1 = combined_df.groupby(combined_df.index).quantile(0.25)
    Q3 = combined_df.groupby(combined_df.index).quantile(0.75)
    IQR_df = Q3 - Q1
    IQR_df.rename(columns=lambda x: x + '_IQR', inplace=True)
    
    # Combine the median, mean, std, and IQR DataFrames
    stats_df = pd.concat([median_df, mean_df, std_df, IQR_df], axis=1)
    return stats_df

def save_stats_csv(stats_df, output_file):
    """Save the statistics DataFrame (medians, means, stds, and IQRs) to a CSV file."""
    stats_df.to_csv(output_file)

def process_csvs(root_dir, csv_name):
    """Main function to process CSVs, calculating medians, means, stds, and IQRs."""
    csv_files = find_csv_files(root_dir, csv_name)
    combined_df = read_and_combine_csv(csv_files)
    stats_df = calculate_statistics(combined_df)
    output_file = os.path.join(root_dir, csv_name.replace('.csv', '_stats.csv'))
    save_stats_csv(stats_df, output_file)
    print(f"Statistics CSV (medians, means, stds, and IQRs) has been saved to {output_file}")

if __name__ == "__main__":
    if len(sys.argv) != 3:
        print("Usage: python script_name.py <directory_path> <csv_filename>")
    else:
        directory_path = sys.argv[1]
        csv_filename = sys.argv[2]
        process_csvs(directory_path, csv_filename)
