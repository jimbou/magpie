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

# Other commands before git phase
echo "Executing commands before git phase"
cp today.sh today23.sh

# Git phase
echo "Starting git phase"

# Pull the latest changes
git pull || true

# Add changes to staging area
git add . || true

# Commit the changes
git commit -m "test commit 2 automation" || true

# Push the changes
git push origin || true

echo "Git phase completed"

# Other commands after git phase
echo "Executing commands after git phase"
cp today.sh today24.sh
