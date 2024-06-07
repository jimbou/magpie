import subprocess
import os

def run_command():
    print("Environment from Python:")
    for key, value in os.environ.items():
        print(key, "=", value)

    cmd = "pytest"
    process = subprocess.Popen(cmd, shell=True, stderr=subprocess.PIPE)
    stdout, stderr = process.communicate()

    
    print("STDERR:", stderr.decode())

if __name__ == "__main__":
    run_command()
