import re

def process_file(file_path):
    # Regular expression to match the lines with the specified pattern
    # This regex captures lines that start with spaces, followed by two percentages, and ends with capturing the last word before a whitespace
    pattern = re.compile(r"^\s+(\d+\.\d+)%\s+(\d+\.\d+)%.*\s+(\S+)$")

    # Dictionary to store the results
    results = {}
    # Open and read the file
    with open(file_path, 'r') as file:
        for line in file:
            match = pattern.search(line)
            if match:
                # Extract the second percentage
                percentage = match.group(2)

                # Extract the last string of the line which should be taken as the key for the dictionary
                function_identifier = match.group(3)

                # Remove any leading underscores and '@' symbols
                cleaned_function_identifier = re.sub(r'^[_@]+', '', function_identifier)
                #if the key already exists keep the highest of the two values
                if cleaned_function_identifier in results:
                    if results[cleaned_function_identifier] < percentage:
                        results[cleaned_function_identifier] = percentage
                else:
                    # Add to the dictionary
                    results[cleaned_function_identifier] = percentage
    
    return results

# Usage example, you'll need to replace 'your_file_path.txt' with your actual file path
file_path = 'report1.txt'
result_dict = process_file(file_path)
print(result_dict)
print(len(result_dict))


#print the results in a file
with open('output.txt', 'w') as f:
    for key, value in result_dict.items():
        f.write('%s:%s\n' % (key, value))
