#!/bin/bash

# Absolute path to your SSH key
SSH_KEY_PATH="/home/dbouras/.ssh/id_rsa"

# Debugging output
echo "Checking SSH key at: $SSH_KEY_PATH"
ls -l $SSH_KEY_PATH

# Check if SSH key exists
if [ ! -f "$SSH_KEY_PATH" ]; then
    echo "SSH key not found at $SSH_KEY_PATH"
    exit 1
else
    echo "SSH key found at $SSH_KEY_PATH"
fi

# Ensure the SSH agent is running and the key is added
eval "$(ssh-agent -s)"
if ssh-add "$SSH_KEY_PATH"; then
    echo "SSH key added successfully"
else
    echo "Failed to add SSH key"
    exit 1
fi

# Configure Git user information
git config --global user.email "jimbou1999@gmail.com"
git config --global user.name "jimbou"

# Ensure the remote URL is using SSH
git remote set-url origin git@github.com:jimbou/magpie.git

# # Pull the latest changes
# git pull || true
# # Add changes to staging area
# git add . || true
# # Commit the changes
# git commit -m "starting long run" || true
# # Push the changes
# git push origin || true

# cd final_ucl/minisat_normal_seed_genetic_1800
# python3 solve_problem.py 
# sleep 600
# cd ../minisat_normal_seed_genetic_1800
# python3 solve_problem.py 
# cd ../../
# sleep 600

# # Pull the latest changes
# git pull || true
# # Add changes to staging area
# git add . || true
# # Commit the changes
# git commit -m "fixed minisat normal issue" || true
# # Push the changes
# git push origin || true


# python3.11 run_examples.py minisat_hack "" scenario_runtime_config3 "bash run_fixed.sh" "./build.sh" sources/core/Solver.cc "" local_search


# # Pull the latest changes
# git pull || true
# # Add changes to staging area
# git add . || true
# # Commit the changes
# git commit -m "minisat hack normal rerun" || true
# # Push the changes
# git push origin || true
# sleep 1800


# python3.11 run_examples.py sat4j "" scenario_runtime_config1 "bash run_fixed.sh" "" test.params test.params local_search
# sleep 1800
# python3.11 run_examples.py sat4j "" scenario_runtime_config3 "bash run_fixed.sh" "ant sat" org.sat4j.core/src/main/java/org/sat4j/minisat/core/Solver.java "" local_search

# # Pull the latest changes
# git pull || true
# # Add changes to staging area
# git add . || true
# # Commit the changes
# git commit -m "completed sat4j" || true
# # Push the changes
# git push origin || true

# sleep 1800
# python3.11 run_examples.py weka "" scenario_runtime_config1 "bash run_fixed.sh" "" weka.params weka.params local_search
# sleep 1800
python3.11 run_examples.py weka "" scenario_runtime_config3 "bash run_fixed.sh" "ant compile" src/main/java/weka/classifiers/trees/RandomForest.java  "" local_search


# Pull the latest changes
git pull || true
# Add changes to staging area
git add . || true
# Commit the changes
git commit -m "completed weka" || true
# Push the changes
git push origin || true


# sleep 1800
# python3.11 run_examples.py lpg "" scenario_runtime_config1 "bash run_fixed.sh" "" lpg.params lpg.params local_search

# # Pull the latest changes
# git pull || true
# # Add changes to staging area
# git add . || true
# # Commit the changes
# git commit -m "completed lpg" || true
# # Push the changes
# git push origin || true

# sleep 1800
# python3.11 run_examples.py scipy "" scenario_runtime_config1 "bash run_fixed.sh" "" scipy.params scipy.params local_search


# # Pull the latest changes
# git pull || true
# # Add changes to staging area
# git add . || true
# # Commit the changes
# git commit -m "completed scipy" || true
# # Push the changes
# git push origin || true

# sleep 1800
# python3.11 run_examples.py zlib "" scenario_runtime_config1 "bash run_fixed.sh" "" zlib.params zlib.params local_search

# # Pull the latest changes
# git pull || true
# # Add changes to staging area
# git add . || true
# # Commit the changes
# git commit -m "completed zlib" || true
# # Push the changes
# git push origin || true