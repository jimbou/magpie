commits=$(git log origin/master..HEAD --pretty=format:"%H")

# Loop over each commit and list file sizes
for commit in $commits; do
    echo "Commit: $commit"
    git diff-tree --no-commit-id --name-only -r $commit | while read file; do
        # Get the file size in bytes
        size=$(git ls-tree -l $commit $file | awk '{print $4}')
        # Convert size to MB (rounding down)
        size_mb=$((size / 1024 / 1024))
        # Print file if size is greater than 10MB
        if [ $size_mb -gt 10 ]; then
            echo "$file ($size_mb MB)"
        fi
    done
    echo
done