import os
import shutil
import statistics
import yaml
import time
import json

def update_performance_data(results):
    # Read the existing JSON data
    with open('performance_data.json', 'r') as file:
        data = json.load(file)

    # Update the median_execution_time in the items list
    for item in data['items']:
        name1 = item.get('item_name')
        name2 = item.get('number_of_retries')
        # Check if this combination exists in the results
        if (name1, name2) in results:
            item['median_execution_time'] = results[(name1, name2)]
        
        else:
            print(f'The {name1}, {name2} does not exist')

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
        if os.path.isdir(folder) and folder.startswith("scenario_runtime_config1_"):
            name = folder.replace("scenario_runtime_config1_", "")
            name1, name2 = name.rsplit('_', 1)
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
            for _ in range(21):
                start_time = time.perf_counter()
                run_command("bash run_fixed.sh")
                end_time = time.perf_counter()
                duration = (end_time - start_time) * 1e9  # convert seconds to nanoseconds
                durations.append(duration)

            median_duration = statistics.median(durations)
            results[(name1, name2)] = median_duration

            # Remove the necessary folder
            os.chdir(subfolder_path)  # Move back to the subdirectory
            shutil.rmtree(dest_folder)

            # Move back to the base directory
            os.chdir(base_path)

    # Store results in a YAML file
    with open('results.yml', 'w') as yaml_file:
        yaml.dump(results, yaml_file, default_flow_style=False)

    # Print results
    for key, value in results.items():
        print(f"{key}: Median Duration = {value} nanoseconds")
    
    update_performance_data(results)

if __name__ == "__main__":
    main()
