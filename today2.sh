#!/bin/bash
cp today.sh today23.sh
eval "$(ssh-agent -s)"
ssh-add ~/.ssh/id_rsa  # Ensure this path points to your SSH private key

# Configure Git user information
git config --global user.email "jimbou1999@gmail.com"
git config --global user.name "jimbou"
# Pull the latest changes
git pull || true
# Add changes to staging area
git add . || true
# Commit the changes
git commit -m "test commit automation" || true
# Push the changes
git push origin|| true

cp today.sh today24.sh

