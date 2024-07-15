#!/bin/bash

# Path to your SSH key
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
# Ensure the SSH agent is running and the key is added
eval "$(ssh-agent -s)"
ssh-add "$SSH_KEY_PATH"

# Configure Git user information
git config --global user.email "jimbou1999@gmail.com"
git config --global user.name "jimbou"
cp today.sh today23.sh

git pull || true
# Add changes to staging area
git add . || true
# Commit the changes
git commit -m "test commit automation" || true
# Push the changes
git push origin|| true

cp today.sh today24.sh