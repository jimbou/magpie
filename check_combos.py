import json
import argparse

def check_item_retries(json_data):
    # Extract the items list from the JSON data
    items = json_data.get('items', [])

    # Dictionary to store the item_name and retry combination
    item_retry_dict = {}

    # Loop through each item in the items list
    for item in items:
        item_name = item.get('item_name')
        number_of_retries = item.get('number_of_retries')

        if item_name and number_of_retries:
            item_retry_dict[(item_name, number_of_retries)] = True

    # List of item_names to check
    perf_items = [
        'time', 'perf_time', 'perf_cycles', 'perf_cache_references', 
        'perf_cache_misses', 'perf_branch_misses', 'energy', 
        'perf_instructions', 'perf_branches', 'weights', 
        'perf_L1_dcache_loads', 'perf_faults', 'perf_cpu_clock', 
        'perf_task_clock'
    ]

    # Check for every combination of item_name and retry number from 1 to 5
    for item_name in perf_items:
        for retry in range(1, 6):
            if (item_name, retry) not in item_retry_dict:
                print(f"Missing combination: item_name={item_name}, number_of_retries={retry}")
           

if __name__ == "__main__":
    # Set up argument parsing
    parser = argparse.ArgumentParser(description="Check item_name and number_of_retries in JSON file.")
    parser.add_argument("json_file", help="Path to the JSON file to be checked")

    args = parser.parse_args()

    # Load the JSON data from the file provided as an argument
    with open(args.json_file, 'r') as file:
        data = json.load(file)
        check_item_retries(data)
