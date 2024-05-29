import re

def calculate_weight(line):
    return 1

def process_assembly_line(line):
    # Strip whitespace and ignore lines not starting with a number followed by a colon
    line = line.strip()
    if not re.match(r'^\d+\.\d+ :', line):
        return None  # Skip non-matching lines

    # Remove comments and extract the number and assembly instruction
    line = re.split(r'#', line)[0].strip()
    parts = re.split(r' : ', line, maxsplit=1)
    if len(parts) < 2:
        return None  # If line does not split into at least two parts, ignore it

    percentage = float(parts[0])
    #if the percentage is 0 then return 0
    if percentage == 0:
        return 0
    instruction = parts[1]

    # Remove address and colon following it to isolate the command
    command_parts = re.split(r':', instruction, maxsplit=1)
    if len(command_parts) < 2:
        return None  # If line does not split into at least two parts, ignore it

    command = command_parts[1].strip()

    # Split the command into operation and operands
    operation_parts = re.split(r'\s+', command, maxsplit=1)
    operation = operation_parts[0].strip()
    operands = operation_parts[1].split()[0] if len(operation_parts) > 1 else None

    # Further split operands if present
    if operands:
        operands = re.split(r',', operands)
        operands = [op.strip() for op in operands]
    else:
        operands = []

    # Return the extracted data as a tuple
    print(percentage, operation, operands)
    return 0

def read_and_process_file(filename):
    with open(filename, 'r') as file:
        sections = {}
        current_section = None
        current_weight = 0
        collecting = False

        for line in file:
            line = line.strip()
            if 'Disassembly of section' in line:
                if current_section is not None:
                    sections[current_section] = current_weight
                collecting = True
                current_weight = 0
                continue
            if collecting:
                if line == '':
                    continue  # Skip the empty line right after section start
                if '<' in line and line.endswith('>:'):
                    # This line contains the section identifier
                    current_section_dirty = line.split('<')[1].split('>')[0]
                    current_section = re.sub(r'^[_@]+', '', current_section_dirty)

                    continue
                if 'Percent |' in line:
                    sections[current_section] = current_weight
                    collecting = False
                    current_section = None
                    continue
                # Process the line to extract the weight, assuming it starts with a float
                #if the line starts with a number followed by a colon, then it is a weight
                if re.match(r'^\d+\.\d+ :', line):
                    weight = float(line.split(':')[0].strip())
                    current_weight += weight*calculate_weight(line)
                    process_assembly_line(line)
                else:
                    # Ignore lines that do not start with a number followed by a colon
                    continue
                
        # In case the last section does not end with a 'Percent |' line
        if current_section and collecting:
            sections[current_section] = current_weight

    return sections

# Usage
filename = 'report2.txt'
section_weights = read_and_process_file(filename)
print(section_weights)
print(len(section_weights))
