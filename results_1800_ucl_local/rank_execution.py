import pandas as pd

# Read the CSV file
csv_file = 'median_execution_time_data_stats.csv'  # Replace with your actual file path
df = pd.read_csv(csv_file)

# Rank the DataFrame based on 'rank_median_execution_time_median'
df_ranked = df.sort_values(by='rank_median_execution_time_median')

# Write the ranked DataFrame to a new CSV file
output_file = 'ranked_output.csv'
df_ranked.to_csv(output_file, index=False)

print(f"Ranked data has been written to {output_file}")
