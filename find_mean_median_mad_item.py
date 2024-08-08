import pandas as pd
import sys

def process_csv(input_file):
    # Read the CSV file
    data = pd.read_csv(input_file)

    # Remove the last underscore and number from the Item_Retry column
    data['Item_Name'] = data['Item_Retry'].str.rsplit('_', 1).str[0]

    # Group the data by the new Item_Name column
    grouped = data.groupby('Item_Name')

    # Calculate the mean and median for each group and for each column
    results = grouped.agg({
        'Normalized_MAD_median': ['mean', 'median'],
        'Normalized_MAD_IQR': ['mean', 'median']
    })

    # Rename the columns as required
    results.columns = ['_'.join(col).strip() for col in results.columns.values]
    results.reset_index(inplace=True)

    # Rename columns to match the output specification
    results.rename(columns={
        'Normalized_MAD_median_mean': 'Normalized_MAD_median_mean',
        'Normalized_MAD_median_median': 'Normalized_MAD_median_median',
        'Normalized_MAD_IQR_mean': 'Normalized_MAD_IQR_mean',
        'Normalized_MAD_IQR_median': 'Normalized_MAD_IQR_median'
    }, inplace=True)

    # Output the results to a new CSV file
    output_file = input_file.rsplit('/', 1)[0] + '/mean_median_mad_per_item.csv'
    print(f"Saving processed file to {output_file}")
    results.to_csv(output_file, index=False)
    print(f"Processed file saved to {output_file}")

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python script.py <path_to_input_csv>")
    else:
        process_csv(sys.argv[1])
