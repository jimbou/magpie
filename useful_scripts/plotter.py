import sys
import os
import pandas as pd
import matplotlib.pyplot as plt
import json

def load_data(json_path):
    # Load the JSON data from the specified path
    with open(json_path, "r") as file:
        return json.load(file)


def prepare_dataframes(data):
    # Convert the items to a DataFrame
    df_items = pd.DataFrame(data['items'])
    # Create a unique identifier for each item
    df_items['item_name_orig'] = df_items['item_name']
    df_items['item_name'] = df_items['item_name'] + "_" + df_items['number_of_retries'].astype(str)
    
    # Prepare the original row as a DataFrame
    original_row = pd.DataFrame([{'item_name': 'original',
                                  'median_execution_time': data['original']['median_execution_time'],
                                  'params': None,
                                  'new_highs': None,
                                  'item_name_orig':'original'}])
    
    # Combine the original row with the items
    df_complete = pd.concat([df_items, original_row], ignore_index=True)
    
    # Create two separate dataframes based on the 'params' attribute
    df_false = df_complete[df_complete['params'] == False].copy()
    df_true = df_complete[df_complete['params'] == True].copy()
    
    # Include the original row in both dataframes
    df_false = pd.concat([df_false, original_row], ignore_index=True)
    df_true = pd.concat([df_true, original_row], ignore_index=True)
    
    return df_false, df_true

def rank_and_visualize(df, save_path,params):
    name =""
    if params:
        name = "_params"
    
    # Temporarily add the original value for plotting
    df['rank_execution_time'] = df['median_execution_time'].rank(method='dense', ascending=True)
    df_sorted_execution = df.sort_values(by='median_execution_time')

    plt.figure(figsize=(10, 6))
    plt.barh(df_sorted_execution['item_name'], df_sorted_execution['median_execution_time'], color='skyblue')
    plt.xlabel('Median Execution Time')
    plt.title('Ranking by Median Execution Time')
    plt.gca().invert_yaxis()
    plt.savefig(os.path.join(save_path, f'execution_time_ranking{name}.png'))
    plt.close()
    
    ranking_column = f'rank_median_execution_time'
    df.loc[:, ranking_column] = df['median_execution_time'].rank(method='dense', ascending=True)

    df_sorted = df.sort_values(by=ranking_column)

    # Saving data: Only saving the ranked and item_name columns
    df_sorted[['item_name', ranking_column, 'median_execution_time']].to_csv(os.path.join(save_path, f'median_execution_time_data{name}.csv'), index=False)


    # Remove the original value from the dataframe after plotting
    df = df[df['item_name'] != 'original']

    # Continue with other analyses without the original item
    df_new_highs = df.dropna(subset=['new_highs']).copy()
    df_new_highs.loc[:, 'rank_new_highs'] = df_new_highs['new_highs'].rank(method='dense', ascending=False)
    df_sorted_new_highs = df_new_highs.sort_values(by='new_highs', ascending=False)

    plt.figure(figsize=(10, 4))
    plt.barh(df_sorted_new_highs['item_name'], df_sorted_new_highs['new_highs'], color='lightgreen')
    plt.xlabel('Number of New Highs')
    plt.title('Ranking by Number of New Highs')
    plt.gca().invert_yaxis()
    plt.savefig(os.path.join(save_path, f'new_highs_ranking{name}.png'))
    plt.close()


    ranking_column = f'rank_new_highs'
    df_new_highs.loc[:, ranking_column] = df_new_highs['new_highs'].rank(method='dense', ascending=True)
    df_new_highs_sorted = df_new_highs.sort_values(by=ranking_column)

    # Saving data: Only saving the ranked and item_name columns
    df_new_highs_sorted[['item_name', ranking_column, 'new_highs']].to_csv(os.path.join(save_path, f'new_highs_data{name}.csv'), index=False)

    return df
    

def analyze_retry_ranks(df, save_path, params):
    # Extract the base item_name by removing the retry count
    name=""
    if params:
        name = "_params"
    
    # Rank retries within each item_name_orig based on execution time
    df['execution_rank_per_item'] = df.groupby('item_name_orig')['median_execution_time'].rank(method='dense', ascending=True)
    # Calculate the average rank for each number_of_retries across all item_name_orig
    average_rank_per_retry = df.groupby('number_of_retries').agg({
        'execution_rank_per_item': 'mean'
    }).reset_index()

    # Visualize the average rank per retry number
    plt.figure(figsize=(10, 5))
    plt.plot(average_rank_per_retry['number_of_retries'], average_rank_per_retry['execution_rank_per_item'], marker='o', linestyle='-')
    plt.xlabel('Number of Retries')
    plt.ylabel('Average Rank of Execution Time')
    plt.title('Average Rank of Execution Time by Number of Retries')
    plt.grid(True)
    plt.savefig(os.path.join(save_path, f'average_rank_per_retry{name}.png'))
    plt.close()

    # Save the average ranking data to a CSV
    average_rank_per_retry.to_csv(os.path.join(save_path, f'average_rank_per_retry{name}.csv'), index=False)

    return average_rank_per_retry

def analyze_retries(df, save_path, params):
    # Rank items within each item_name based on execution time and retries
    name =""
    if params:
        name = "_params"
    
    df['execution_rank_per_item'] = df.groupby('item_name_orig')['median_execution_time'].rank(method='dense')
    df['retries_rank_per_item'] = df.groupby('item_name_orig')['number_of_retries'].rank(method='dense')

    # Average ranking across all item names
    average_placement = df.groupby('number_of_retries').agg({
        'execution_rank_per_item': 'mean',
        'retries_rank_per_item': 'mean'
    }).reset_index()

    # Visualize the average placement
    plt.figure(figsize=(10, 5))
    plt.plot(average_placement['number_of_retries'], average_placement['execution_rank_per_item'], marker='o', label='Avg. Execution Time Rank')
    plt.plot(average_placement['number_of_retries'], average_placement['retries_rank_per_item'], marker='x', label='Avg. Retries Rank')
    plt.xlabel('Number of Retries')
    plt.ylabel('Average Rank')
    plt.title('Average Placement for Number of Retries')
    plt.legend()
    plt.grid(True)
    plt.savefig(os.path.join(save_path, f'average_ranking{name}.png'))
    plt.close()

    # Save the average placement data to a CSV
    average_placement.to_csv(os.path.join(save_path, f'average_placement{name}.csv'), index=False)


def analyze_and_plot_means(df, save_path, params):
    name =""
    if params:
        name = "_params"
    # Extract the base item_name by removing the retry count
    
    
    # Group by the original item name and calculate the mean of the median execution times
    means = df.groupby('item_name_orig')['median_execution_time'].mean().reset_index()
    means['mean_median_execution_time'] = means['median_execution_time']
    
    # Rank the means
    means['rank'] = means['mean_median_execution_time'].rank(method='dense', ascending=True)
    
    # Sorting by rank for better visualization
    means_sorted = means.sort_values('rank')
    
    # Plotting
    plt.figure(figsize=(10, 6))
    plt.bar(means_sorted['item_name_orig'], means_sorted['mean_median_execution_time'], color='lightblue')
    plt.xlabel('Item Name')
    plt.ylabel('Mean of Median Execution Times')
    plt.title('Mean Median Execution Times by Item Name')
    plt.xticks(rotation=45)
    plt.tight_layout()
    plt.savefig(f"{save_path}/mean_median_execution_times{name}.png")
    plt.close()

    # Save the data to a CSV file
    means_sorted[['item_name_orig', 'mean_median_execution_time', 'rank']].to_csv(f"{save_path}/mean_median_execution_times{name}.csv", index=False)

    return means_sorted

def main(json_path):
    # Load and prepare data
    data = load_data(json_path)
    save_path = os.path.dirname(json_path)
    #create directory data in the save path
    os.makedirs(os.path.join(save_path, 'data'), exist_ok=True)

    save_path = os.path.join(save_path, 'data')

    # Load and prepare data
    data = load_data(json_path)
    df_false, df_true = prepare_dataframes(data)
    #if df_false has more than one row
    if df_false.shape[0] > 1:
        analyze_and_plot_means(df_false, save_path, False)
        df_false = rank_and_visualize(df_false, save_path, False)
        analyze_retry_ranks(df_false, save_path, False)
    else :
        print("No data for params=False")
    if df_true.shape[0] > 1:
        analyze_and_plot_means(df_true, save_path, True)
        df_true = rank_and_visualize(df_true, save_path,True )
        analyze_retry_ranks(df_true, save_path, True)
    else:
        print("No data for params=True")
    



    
if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python plotter.py <json path>")

    json_path = sys.argv[1] 
    main(json_path)

