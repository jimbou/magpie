import pandas as pd
import sys

def process_csv(file_path):
    # Load the CSV data into a DataFrame
    df = pd.read_csv(file_path)

    # Extract item name and retry number
    df['Item_Name'] = df['Item_Retry'].apply(lambda x: '_'.join(x.split('_')[:-1]))
    df['Retry_Number'] = df['Item_Retry'].apply(lambda x: x.split('_')[-1])

    # Convert Retry_Number to integer
    df['Retry_Number'] = df['Retry_Number'].astype(int)

    # Group by Item_Name and Retry_Number, then calculate mean and median
    grouped_by_item = df.groupby(['Item_Name', 'Retry_Number']).agg(['mean', 'median']).reset_index()
    grouped_by_item.columns = ['_'.join(col).strip() if col[1] else col[0] for col in grouped_by_item.columns.values]

    # Save the grouped data to CSV files
    # grouped_by_item.to_csv('mean_median_mad_per_item.csv', index=False)

    # Group by Retry_Number, then calculate mean and median
    grouped_by_retry = df.groupby('Retry_Number').agg(['mean', 'median']).reset_index()
    grouped_by_retry.columns = ['_'.join(col).strip() if col[1] else col[0] for col in grouped_by_retry.columns.values]

    # Save the grouped data to CSV files
    grouped_by_retry.to_csv('mean_median_mad_per_retries.csv', index=False)
    print(f'saved to mean_median_mad_per_retries.csv')

if __name__ == "__main__":
    file_path = sys.argv[1]  # File path provided as the first command-line argument
    process_csv(file_path)
