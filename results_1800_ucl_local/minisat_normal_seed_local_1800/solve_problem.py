import os
import shutil
import statistics

import time
import json
import subprocess

def update_performance_data(results):
    # Read the existing JSON data
    with open('performance_data.json', 'r') as file:
        data = json.load(file)

    # Update the median_execution_time in the items list
    for item in data['items']:
        name1 = item.get('item_name')
        name2 = item.get('number_of_retries')
        # Check if this combination exists in the results
        if f'{name1}_{name2}' in results:
            item['median_execution_time'] = results[f'{name1}_{name2}']
        
        else:
            print(f'The {name1}_{name2} does not exist')

    # Write the updated JSON data to a new file
    with open('performance_data_updated.json', 'w') as file:
        json.dump(data, file, indent=4)

def run_command(command, directory=None):
    """ Helper function to run a shell command and return its output. """
    return subprocess.run(command, shell=True, text=True, capture_output=True, cwd=directory)

def main():
    base_path = os.getcwd()
    source_folder = "../../examples/minisat/necessary"

    results = {}

    # Traverse subdirectories in the current directory
    for folder in os.listdir(base_path):
        if os.path.isdir(folder) and folder.startswith("scenario_runtime_config3_"):
            name = folder.replace("scenario_runtime_config3_", "")
            name1, name2 = name.rsplit('_', 1)
            print(name1, name2)
            subfolder_path = os.path.join(base_path, folder)

            # Copy the necessary folder into the current subfolder
            dest_folder = os.path.join(subfolder_path, "necessary")
            shutil.copytree(source_folder, dest_folder, dirs_exist_ok=True)

            # Perform the commands inside the copied folder
            os.chdir(dest_folder)
            run_command("patch core/Solver.cc ../*.diff")
            shutil.copy("core/Solver.cc", "../")

            # Compile the software
            run_command("bash compile.sh")

            # Run 'bash run_fixed.sh' 21 times and record the execution times
            durations = []
            for _ in range(3):
                start_time = time.perf_counter()
                run_command("bash run_fixed.sh")
                end_time = time.perf_counter()
                duration = (end_time - start_time)  # convert seconds to nanoseconds
                durations.append(duration)

            median_duration = statistics.median(durations)
            results[f'{name1}_{name2}'] = median_duration
            print(f'{name1}_{name2} : {median_duration}')
            #print the results dict temporarily in a json file overwrriting it every time
            
            # Remove the necessary folder
            os.chdir(subfolder_path)  # Move back to the subdirectory
            shutil.rmtree(dest_folder)

            # Move back to the base directory
            os.chdir(base_path)
            with open('results1.json', 'w') as json_file:
                print("Writing to results1.json")
                #print the path of the 
                json.dump(results, json_file, indent=4)

    # Store results in a YAML file
    with open('results_final.json', 'w') as json_file:
        json.dump(results, json_file, indent=4)

    # Print results
    for key, value in results.items():
        print(f"{key}: Median Duration = {value} seconds")
    
    update_performance_data(results)

if __name__ == "__main__":
    main()