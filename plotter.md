
# Data Analysis Documentation

## Overview
This documentation covers the data analysis processes implemented in a Python script designed to handle and analyze JSON data from a specified path. The script includes multiple functionalities like loading data, preparing dataframes, ranking, visualizing, and saving data insights.

## Functions Description

### `load_data(json_path)`
- **Purpose**: Loads JSON data from a specified file path.
- **Input**: `json_path` (str) - The path to the JSON file.
- **Output**: Dictionary object containing the loaded JSON data.

### `prepare_dataframes(data)`
- **Purpose**: Converts JSON items to a DataFrame and splits data based on a boolean parameter 'params'.
- **Input**: `data` (dict) - The loaded JSON data.
- **Output**: Two DataFrames (`df_false` and `df_true`) based on the 'params' attribute.

### `rank_and_visualize(df, save_path, params)`
- **Purpose**: Ranks items by median execution time and number of new highs; visualizes and saves these rankings.
- **Input**: 
  - `df` (DataFrame) - The DataFrame to process.
  - `save_path` (str) - Directory path to save outputs.
  - `params` (bool) - Parameter to modify output file names.
- **Output**: Modified DataFrame with rankings. Saves visualizations and CSV files.

### `analyze_retry_ranks(df, save_path, params)`
- **Purpose**: Analyzes and ranks retries within each item group based on execution time and calculates the average rank for each retry number.
- **Input**: Similar to `rank_and_visualize`.
- **Output**: Saves plots and CSV of average rank per retry number.

### `analyze_retries(df, save_path, params)`
- **Purpose**: Analyzes average rankings of execution times and retries across all items.
- **Input**: Similar to `rank_and_visualize`.
- **Output**: Saves plots and CSV of average rankings.

### `analyze_and_plot_means(df, save_path, params)`
- **Purpose**: Calculates and plots the mean of median execution times for groups defined by original item names.
- **Input**: Similar to `rank_and_visualize`.
- **Output**: Saves plots and CSV of mean values.

## Visualizations and Files Produced
Each function capable of producing outputs saves both a graphical visualization (PNG) and a CSV file containing the relevant data:
- **Rankings by Median Execution Time and New Highs**:
  - Files: `execution_time_ranking[name].png`, `median_execution_time_data[name].csv`, `new_highs_ranking[name].png`, `new_highs_data[name].csv`
- **Average Rank of Execution Time by Number of Retries**:
  - Files: `average_rank_per_retry[name].png`, `average_rank_per_retry[name].csv`
- **Mean Median Execution Times by Item Name**:
  - Files: `mean_median_execution_times[name].png`, `mean_median_execution_times[name].csv`

## Usage
To run the main script:
```bash
python plotter.py <json path>
```
Replace `<json path>` with the actual path to your JSON data file.