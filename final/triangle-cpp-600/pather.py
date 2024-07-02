import os
import subprocess

def patch_subdirectories():
    # Change to the base directory
    
    
    # List all entries in the directory
    for entry in os.listdir():
        # Check if the entry is a directory
        if os.path.isdir(f"{entry}/necessary"):
            # Change to the subdirectory
            os.chdir(f"{entry}/necessary")
            subprocess.run('rm -rf triangle.cpp', shell=True, check=True)
            subprocess.run('rm -rf build', shell=True, check=True)
            subprocess.run('cp ../../triangle.cpp .', shell=True, check=True)
            
            # Execute the patch command
            try:
                subprocess.run('patch triangle.cpp ../*.diff', shell=True, check=True)
                print(f"Patch command executed successfully in {entry}")
                subprocess.run('bash setup.sh', shell=True, check=True)
            except subprocess.CalledProcessError:
                print(f"Patch command failed in {entry}")
            
            # Return to the base directory
            os.chdir('../../')

if __name__ == "__main__":
    # base_directory = input("Enter the path to the base directory: ")
    patch_subdirectories()
