import json
import os
import subprocess
import statistics
import time

def main(json_path, path, compile_command, run_command):
    # Load JSON data
    with open(json_path, 'r') as file:
        data = json.load(file)
    



    os.chdir(f"../../examples/triangle-c/necessary")
    start_time = time.perf_counter()
    for _ in range(20):
        
        subprocess.run(run_command, shell=True)
        end_time = time.perf_counter()
        
    end_time = time.perf_counter()
    # Calculate the median execution time
    median_time = (end_time - start_time)/20
    print(f"triangle-c original : {median_time}")
    os.chdir(f"../../../final/triangle-c-600")
    items = data['items']
    for item in items:
        # Create the directory name
        dir_name = f"{path}_{item['item_name']}_{item['number_of_retries']}"
        
        # Change to the directory
        os.chdir(dir_name)
        
        # Execute the compile command
        subprocess.run(compile_command, shell=True)
        
        # Execute the run command multiple times and record execution times
        execution_times = []
        start_time = time.perf_counter()
        for _ in range(20):
            
            subprocess.run(run_command, shell=True)
            end_time = time.perf_counter()
            
        end_time = time.perf_counter()
        # Calculate the median execution time
        median_time = (end_time - start_time)/20
        
        # Print the item name and its median execution time
        print(f"{item['item_name']}: {median_time}")

        # Change back to the original directory if needed
        os.chdir('..')

if __name__ == '__main__':
    import sys
    if len(sys.argv) != 4:
        print("Usage: python script.py <path> <compile_command> <run_command>")
    else:
        main("performance_data.json",sys.argv[1], sys.argv[2], sys.argv[3])
