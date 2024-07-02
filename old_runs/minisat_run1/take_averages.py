import json
import os
import subprocess
import time

def main(json_path, path, compile_command, run_command):
    # Load JSON data
    with open(json_path, 'r') as file:
        data = json.load(file)
    
    os.chdir("../../examples/triangle-c/necessary")
    start_time = time.perf_counter()
    for _ in range(20):
        subprocess.run(run_command, shell=True)
    end_time = time.perf_counter()
    # Calculate the median execution time
    median_time = (end_time - start_time) / 20
    data['original']['median_execution_time'] = median_time
    
    os.chdir(f"../../../final/triangle-c-600")
    subprocess.run("pwd", shell=True)
    time.sleep(2)
    items = data['items']
    
    for item in items:
        # Create the directory name
        dir_name = f"{path}_{item['item_name']}_{item['number_of_retries']}"
        
        # Change to the directory
        os.chdir(f"{dir_name}/necessary")
        
        # Execute the compile command
        subprocess.run(compile_command, shell=True)
        
        # Execute the run command multiple times and record execution times
        execution_times = []
        start_time = time.perf_counter()
        for _ in range(20):
            subprocess.run(run_command, shell=True)
            end_time = time.perf_counter()
        
        # Calculate the median execution time
        median_time = (end_time - start_time) / 20
        
        # Update the item's median execution time
        item['median_execution_time'] = median_time

        # Change back to the original directory if needed
        os.chdir('../../')
    
    # Write the updated dictionary back to a new JSON file
    new_json_path = json_path.replace('.json', '_new.json')
    with open(new_json_path, 'w') as file:
        json.dump(data, file, indent=4)

if __name__ == '__main__':
    import sys
    if len(sys.argv) != 4:
        print("Usage: python script.py <path> <compile_command> <run_command>")
    else:
        main("performance_data.json", sys.argv[1], sys.argv[2], sys.argv[3])
