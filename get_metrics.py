import subprocess
import os
import shutil
import statistics
import sys
import time
import json
import re
import glob
import magpie.settings
#python3.11 run_examples.py triangle-c scenario scenario_params run_triangle "make run_triangle" triangle.c
data = {
    "original": {
        "median_execution_time": 0
    },
    "items": []
}


def count_new_best(lst):
    count = 0
    current_high = float('+inf')
    for num in lst:
        if num < current_high:
            count += 1
            current_high = num
    return count-1

def update_retries(filepath, new_retry_value):
    # Read the original file
    with open(filepath, 'r') as file:
        lines = file.readlines()

    # Update the specified line
    for i in range(len(lines)):
        if lines[i].strip().startswith("retries ="):
            lines[i] = f"retries = {new_retry_value}\n"
            break

    # Generate a new file name based on the original name and new_retry_value
    base_name, ext = os.path.splitext(filepath)
    new_filepath = f"{base_name}_{new_retry_value}{ext}"

    # Write to the new file
    with open(new_filepath, 'w') as file:
        file.writelines(lines)

    
    return new_filepath


def extract_data_from_log(file_path):
    # Regular expression to find the number of retries
    retries_pattern = re.compile(r"Retries:\s+(\d+)")
    # Regular expression to find all occurrences of "patch(xxx)="
    patch_numbers_pattern = re.compile(r"patch\((\d+)\)=")
    #fitness if float
    best_fitness_pattern = re.compile(r"Best fitness:\s+(\d+(?:\.\d+)?)")


    ref_fitness_pattern = re.compile(r"Reference fitness:\s+(\d+(?:\.\d+)?)")

    fitness_values_pattern = re.compile(r"\[INFO\]\s+\d+\s+SUCCESS\s+\*?\+?(\d+(?:\.\d+)?)")   



    retries_number = None
    max_patch_number = None

    try:
        with open(file_path, 'r') as file:
            file_content = file.read()

            # Search for retries
            retries_match = retries_pattern.search(file_content)
            if retries_match:
                retries_number = int(retries_match.group(1))

            
            
            # Search for best fitness
            best_fitness_match = best_fitness_pattern.search(file_content)
            if best_fitness_match:
                best_fitness = float(best_fitness_match.group(1))

            # Search for reference fitness
            ref_fitness_match = ref_fitness_pattern.search(file_content)
            if ref_fitness_match:
                ref_fitness = float(ref_fitness_match.group(1))

            # Find all patch numbers and determine the maximum
            patch_numbers = [int(num) for num in patch_numbers_pattern.findall(file_content)]
            if patch_numbers:
                max_patch_number = max(patch_numbers)
            
            fitness_values = []
            fitness_values = [float(num) for num in fitness_values_pattern.findall(file_content)]
            #transform the strings in the list to floats
            # print(fitness_values)
    except FileNotFoundError:
        print("The file does not exist.")
    except Exception as e:
        print(f"An error occurred: {e}")

    return retries_number, max_patch_number, best_fitness, ref_fitness, fitness_values




def run_command(command, directory =None):
    """ Helper function to run a shell command and return its output. """
    
    return subprocess.run(command, shell=True, text=True, capture_output=True, cwd=directory)
# 
    
def create_directory_with_suffix(base_path):
    """ Create a directory with an incrementing number suffix if it already exists. """
    counter = 1
    new_path = base_path
    while os.path.exists(new_path):
        new_path = f"{base_path}{counter}"
        counter += 1
    os.makedirs(new_path)
    return new_path

def load_parameters(param_file):
    """ Load parameters from a .params file and return them as a dictionary. """
    params = {}
    with open(param_file, 'r') as file:
        for line in file:
            key, value = line.split(' := ')
            params[key.strip()] = value.strip().strip("'")
    return params

def build_command(params, cmd):
    """ Build the command based on the loaded parameters. """
    cmd = [cmd]
    # Define mappings from parameter names to command line flags
    flag_mappings = {
        'luby': '-luby',
        'rnd-init': '-no-rnd-init',
        'gc-frac': '-gc-frac',
        'rinc': '-rinc',
        'var-decay': '-var-decay',
        'cla-decay': '-cla-decay',
        'rnd-freq': '-rnd-freq',
        'rnd-seed': '-rnd-seed',
        'phase-saving': '-phase-saving',
        'ccmin-mode': '-ccmin-mode',
        'rfirst': '-rfirst',
        'pre': '-pre',
        'verb': '-verb',
        'rcheck': '-no-rcheck',
        'asymm': '-no-asymm',
        'elim': '-elim',
        'simp-gc-frac': '-simp-gc-frac',
        'sub-lim': '-sub-lim',
        'cl-lim': '-cl-lim',
        'grow': '-grow'
    }

    for key, value in params.items():
        if key in flag_mappings:
            if value.lower() == 'true':
                cmd.append(flag_mappings[key])
            elif value.lower() == 'false':
                if "no-" not in flag_mappings[key]:
                    cmd.append('-no-' + flag_mappings[key][1:])
            else:
                cmd.append(flag_mappings[key] + '=' + value)
    return cmd

def main(name1, scenario ,name3, compile_command, improved_file, main_directory, params_file):
    # perf_items = [
    #     'time', 'perf_time', 'perf_instructions', 'perf_cycles',
    #     "perf_cache_references", "perf_cache_misses", "perf_branches",
    #     "perf_branch_misses", "perf_cpu_clock", "perf_task_clock", "perf_faults",
    #     "perf_minor_faults", "perf_major_faults", "perf_cs", "perf_migrations",
    #     "perf_L1_dcache_loads", "perf_L1_dcache_load_misses", "perf_dTLB_loads",
    #     "perf_dTLB_load_misses", "weights", "energy"
    # ]
    # "energy_ram", "energy_uncore"
    perf_items = ['time','perf_time','perf_instructions', 'perf_cycles',
        "perf_cache_references", "perf_cache_misses", "perf_branches",
        "perf_branch_misses", "perf_cpu_clock", "perf_task_clock", "perf_faults"]
    # perf_items = [ 'time','perf_time',"weights", "energy"]
    erroneous=[]
    execution_times = []
    run_com =name3
    if params_file != "":
        params = load_parameters(f'examples/{name1}/necessary/{params_file}')
        run_com = build_command(params, name3)
        run_com= " ".join(run_com)
        print(run_com)

    result = run_command(compile_command, f"examples/{name1}/necessary")
    for _ in range(20):
        start = time.time()
        result = run_command(run_com, f'examples/{name1}/necessary')
        end = time.time()
        duration = end - start  
        execution_times.append(float(duration))
    
    median_time_orig = statistics.median(execution_times)
    data["original"]["median_execution_time"] = median_time_orig
    print(f'Median execution time: {median_time_orig}')
    
    with open(f'{main_directory}/performance_data_temp.json', 'w') as file:
        json.dump(data, file, indent=4)
    # Main directory for this run
    
    orig_path=f"examples/{name1}/necessary"
    for item in perf_items:
        #get time before execution
        try:
            for retries_num  in range(1,6):

                new_string = f"{scenario }_{item}.txt"
                print(f"Running {new_string} for retry {retries_num}")
                scenario_path_base = f"examples/{name1}/_magpie/{new_string}"
                scenario_path="minisat_run16"
                #command = f"python3.11 magpie local_search --scenario {scenario_path}"
                duration_magpie = 0
                # if item != "weights":
                #     start_mag_w = time.perf_counter()
                #     result1 = run_command(command)
                #     end_mag_w = time.perf_counter()
                #     duration_magpie = end_mag_w - start_mag_w  
                #     print(f"Duration: {duration_magpie}")
                #     result1 =result1.stderr
                # else: 
                #     print(command)
                #     start_mag = time.perf_counter()
                #     process = subprocess.Popen(command, shell=True,stderr=subprocess.PIPE)
                #     stdout, result1 = process.communicate()
                #     result1 =result1.decode(magpie.settings.output_encoding)
                #     end_mag = time.perf_counter()
                #     duration_magpie = end_mag - start_mag  
                #     print(f"Duration magpie: {duration_magpie}")
                    

                # Parse output for file paths
                #os.remove(scenario_path)
                # log_path = None
                # patch_path = None
                # diff_path = None
                # for line in result1.splitlines():
                #     if "Log file:" in line:
                #         log_path = line.split()[-1]
                #     elif "Patch file:" in line:
                #         patch_path = line.split()[-1]
                #     elif "Diff file:" in line:
                #         diff_path = line.split()[-1]

                # if not log_path or not patch_path or not diff_path:
                #     print(f"ERROR : Files not found for {item}")
                #     print (result1)
                #     erroneous.append(item)
                #     continue
                # # Subdirectory for the current item
                item_directory = os.path.join(main_directory, scenario+"_"+item)
                item_directory = item_directory +"_"+str(retries_num)
                # os.makedirs(item_directory, exist_ok=True)
                final_destination = os.path.join(item_directory, 'necessary')
                # # Copy files
                # shutil.copytree(orig_path, final_destination)
                # shutil.copy(log_path, item_directory)
                # shutil.copy(patch_path, item_directory)
                # shutil.copy(diff_path,item_directory )

                #get in  a string the contents of the patch file
                #in the scenario path get the path of the file that ends in .diff and store it in diff_path
                diff_files = glob.glob(os.path.join(item_directory, "*.diff"))

                # Check if there is at least one .diff file
                if diff_files:
                    # If there are multiple .diff files, this will take the first one
                    diff_path = diff_files[0]
                else:
                    diff_path = None
                    print("No .diff file found in the scenario path.")
                #in the scenario path get the path of the file that ends in .log and store it in log_path
                log_files = glob.glob(os.path.join(item_directory, "*.log"))

                # Check if there is at least one .log file
                if log_files:
                    # If there are multiple .log files, this will take the first one
                    log_path = log_files[0]
                else:
                    log_path = None
                    print("No .log file found in the scenario path.")
                #in the scenario path get the path of the file that ends in .patch and store it in patch_path
                patch_files = glob.glob(os.path.join(item_directory, "*.patch"))

                # Check if there is at least one .patch file
                if patch_files:
                    # If there are multiple .patch files, this will take the first one
                    patch_path = patch_files[0]
                else:
                    patch_path = None
                    print("No .patch file found in the scenario path.")

                
                with open(patch_path, 'r') as file:
                    patch_contents = file.read()
                diff_name = os.path.basename(diff_path)
                retries, max_patch, best_fitness, ref_fitness, fitness_values = extract_data_from_log(log_path)
                if retries is not None and max_patch is not None:
                    print("Number of Retries:", retries)
                    print("Highest Patch Number:", max_patch)
                else:
                    print("Failed to extract data.")

            

                
                # patch_command= f"patch {improved_file} ../{diff_name}"
                # result = run_command(patch_command, f"{item_directory}/necessary")
                # #print(result.stderr)
                # result = run_command(compile_command, f"{item_directory}/necessary")
                print(f"Files for {item} saved in {item_directory}")
                if params_file != "":
                    params = load_parameters(f'{item_directory}/necessary/{params_file}')
                    run_com2 = build_command(params, name3)
                    run_com2= " ".join(run_com2)
                    print(run_com2)
                execution_times = []
                for _ in range(20):
                    start = time.time()
                    result = run_command(run_com2, f"{item_directory}/necessary")
                    end = time.time()
                    print(result.stderr)
                    duration = end - start  
                    execution_times.append(float(duration))
                
            
                median_time = statistics.median(execution_times)
                print(f'Median execution time: {median_time}')

                params_val = False
                if "params" in scenario:
                    params_val =True
                
                new_highs = count_new_best(fitness_values)
                data["items"].append({
                "item_name": item,
                "median_execution_time": median_time,
                "duration":duration_magpie ,
                "patch_contents": patch_contents,
                "number_of_retries": retries,
                "number_of_steps": max_patch,
                "best_fitness": best_fitness,
                "reference_fitness": ref_fitness,
                "params": params_val,
                "fitness_values": fitness_values,
                "new_highs": new_highs,
                "new_highs_ratio": new_highs / len(fitness_values) if len(fitness_values) > 0 else "N/A"
                })
                with open(f'{main_directory}/performance_data_temp.json', 'w') as file:
                    json.dump(data, file, indent=4)
        except Exception as e:
            print(f"An error occurred: {e}")
            print(f"Erroneeous in {erroneous}")
            with open(f'{main_directory}/performance_data2.json', 'w') as file:
                json.dump(data, file, indent=4)  
            return -1
    print(f"Erroneeous = {erroneous}")    

if __name__ == "__main__":
    if len(sys.argv) != 8:
        print("Usage: python script.py <dir name> <scenario> <scenario_params> <executable> <compile command> <improved_file> <params_file>")
    else:
        main_directory = f"minisat_run16"
        #main_directory = create_directory_with_suffix(main_directory)
        if sys.argv[2] != "":
            print("Executing with normal scenario")
            main(sys.argv[1], sys.argv[2], sys.argv[4], sys.argv[5], sys.argv[6],main_directory,sys.argv[7] )
        if sys.argv[3] != "":
            print("Executing with params scenario")
            main(sys.argv[1], sys.argv[3], sys.argv[4], sys.argv[5], sys.argv[6], main_directory, sys.argv[7])

        with open(f'{main_directory}/performance_data.json', 'w') as file:
            json.dump(data, file, indent=4)
