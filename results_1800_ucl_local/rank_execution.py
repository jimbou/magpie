import pandas as pd

# Read the CSV file
csv_file = 'median_execution_time_data_stats.csv'  # Replace with your actual file path
df = pd.read_csv(csv_file)

# Rank the DataFrame based on 'rank_median_execution_time_median'
df_ranked = df.sort_values(by='median_execution_time_mean')

# Write the ranked DataFrame to a new CSV file
output_file = 'ranked_total_output.csv'
df_ranked.to_csv(output_file, index=False)

print(f"Ranked data has been written to {output_file}")

#split the first column at the last underscore and the first part is the measure the second is the retry
df_ranked[['measure','retry']] = df_ranked['item_name'].str.rsplit('_', 1, expand=True)
#group them by measure keeping mean for all values
df_grouped = df_ranked.groupby('measure').median()
#store the results in a new csv
output_file_grouped = 'total_output_measures.csv'
df_grouped.to_csv(output_file_grouped, index=True)
#group them by retry keeping mean for all values
df_grouped = df_ranked.groupby('retry').median()
#store the results in a new csv
output_file_grouped = 'total_output_retry.csv'
df_grouped.to_csv(output_file_grouped, index=True)
