import os
import json
import matplotlib.pyplot as plt
import sys
import numpy as np
import pandas as pd

def process_list(values, item_name, retries, output_dir):
        # Filter to keep only decreasing values
        filtered_values = []
        current_best = float('inf')
        for value in values:
            if value < current_best:
                filtered_values.append(value)
                current_best = value
        
        plt.figure()
        plt.scatter(range(len(filtered_values)), filtered_values, label='Fitness Values cleaned', marker='o')
        plt.title(f"{item_name} - Fitness Points cleaned")
        plt.xlabel('Step')
        plt.ylabel('Fitness Value')
        plt.grid(True)
        plt.legend()
        
        # Save the plot
        graph_name = f"{item_name}_{retries}_cleaned.png"
        plt.savefig(os.path.join(output_dir, graph_name))
        plt.close()  # Close the plot to free up memory



        # Calculate metrics
        total_steps = len(filtered_values)
        if total_steps > 1:
            average_decrease_per_step = (filtered_values[0] - filtered_values[-1]) / (total_steps - 1)
        else:
            average_decrease_per_step = 0
        average_decrease_percentage_per_step=average_decrease_per_step/filtered_values[0]
        decreases = np.diff(filtered_values)
        
        #make decreases the absolut value of the decreases
        decreases = np.abs(decreases)
        std_deviation_of_decreases = np.std(decreases)
        median_decrease = np.median(decreases) if len(decreases) > 0 else 0
        decr_perc = len(filtered_values)/len(values)
        if len(decreases) > 0:
            Q1 = np.percentile(decreases, 25)  # 25th percentile
            Q3 = np.percentile(decreases, 75)  # 75th percentile
            IQR_decrease = Q3 - Q1
        else:
            IQR_decrease = 0
        
        threshold = average_decrease_per_step * 1.5  # Example threshold
        #print(average_decrease_per_step, threshold,decreases)
        number_of_large_decreases = np.sum(decreases > threshold)
        proportion_of_large_decreases = number_of_large_decreases / len(decreases) if len(decreases) > 0 else 0
        
        return {
            "total_decreases": total_steps,
            "proportion_of_decreases": decr_perc,
            "average_decrease_per_step": average_decrease_per_step,
            "average_decrease_percentage_per_step": average_decrease_percentage_per_step,
            "std_deviation_of_decreases": std_deviation_of_decreases,
            "median_decrease": median_decrease,
            "iqr_decrease": IQR_decrease,
            "number_of_large_decreases": number_of_large_decreases,
            "proportion_of_large_decreases": proportion_of_large_decreases
        }

def process_json_data(directory, json_filename):
    # Construct the full path to the JSON file
    json_path = os.path.join(directory, json_filename)
    
    # Check if the JSON file exists
    if not os.path.isfile(json_path):
        raise FileNotFoundError(f"The file {json_filename} does not exist in {directory}")
    
    # Read JSON data
    with open(json_path, 'r') as file:
        data = json.load(file)
    
    # Ensure the 'data_lines' directory exists
    output_dir = os.path.join(directory, 'data_lines')
    if not os.path.exists(output_dir):
        os.makedirs(output_dir)
    results = []
    # Process each item in the 'items' list
    for item in data.get('items', []):
        item_name = item['item_name']
        retries = item['number_of_retries']
        reference= item['reference_fitness']
        fitness_values = item.get('fitness_values', [])
        fitness_values_new = [reference] + fitness_values
        
            
        metrics = process_list(fitness_values_new, item_name, retries, output_dir)
        metrics['number_of_retries'] = retries
        metrics['item_name'] = item_name
        
        results.append(metrics)

        plt.figure()
        plt.scatter(range(len(fitness_values)), fitness_values, label='Fitness Values', marker='o')
        plt.title(f"{item_name} - Fitness Points")
        plt.xlabel('Step')
        plt.ylabel('Fitness Value')
        plt.grid(True)
        plt.legend()
        
        # Save the plot
        graph_name = f"{item_name}_{retries}.png"
        plt.savefig(os.path.join(output_dir, graph_name))
        plt.close()  # Close the plot to free up memory

        
    
    df = pd.DataFrame(results)
    csv_filename = 'fitness_analysis_results.csv'
    #save the csv file to the directory directory in the data folder
    csv_filename = os.path.join(directory, 'data', csv_filename)
    df.to_csv(csv_filename, index=False)
    print(f"Results have been saved to {csv_filename}")

def main():
    # Command-line argument validation
    if len(sys.argv) != 3:
        print("Usage: python script.py <directory_path> <json_filename>")
        sys.exit(1)
    
    directory = sys.argv[1]
    json_filename = sys.argv[2]
    process_json_data(directory, json_filename)

if __name__ == "__main__":
    main()
