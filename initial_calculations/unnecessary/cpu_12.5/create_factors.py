import json
import sys

def process_metrics(input_filename):
    # Load data from the provided JSON file
    try:
        with open(input_filename, 'r') as file:
            data = json.load(file)
    except FileNotFoundError:
        print("The specified file was not found.")
        return
    except json.JSONDecodeError:
        print("Failed to decode JSON. Please check the file format.")
        return

    # Extract the time value
    time_value = data['time']

    # Check for division by zero
    if time_value == 0:
        result = {key: None for key in data}
        print("Time value is zero, resulting in division by zero. All values set to None.")
    else:
        # Calculate the result for each metric
        result = {key: (data[key] / time_value ) for key in data}

    # Save the result in a new JSON file
    with open('factors.json', 'w') as output_file:
        json.dump(result, output_file, indent=4)

    print("The file 'factors.json' has been created with the updated metrics.")

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python script.py <input_file.json>")
    else:
        input_file = sys.argv[1]
        process_metrics(input_file)
