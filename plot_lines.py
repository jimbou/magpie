import os
import json
import matplotlib.pyplot as plt
import sys

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
    
    # Process each item in the 'items' list
    for item in data.get('items', []):
        item_name = item['item_name']
        retries = item['number_of_retries']
        fitness_values = item.get('fitness_values', [])
        
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
