import numpy as np
import random
import os

def generate_complex_function(file_id):
    # Generate a filename
    filename = f"complex_function_{file_id}.py"

    # Define the function as a string
    function_code = f"""import numpy as np
def complex_objective_function(x):
    result = np.sum(x**2 - 10 * np.cos(2 * np.pi * x) + 10)
    for i in range(len(x)):
        result += np.sin(x[i]) * np.cos(x[i]) * np.exp(np.abs(x[i]))
    for _ in range(1000):
        result += np.sum(np.sin(x) * np.sin(x))
    noise = np.random.normal(0, 1)
    result += noise
    return result
"""

    # Write the function to a file
    with open(filename, 'w') as file:
        file.write(function_code)

    print(f"Function written to {filename}")

# Generate multiple complex functions
for i in range(6,10):  # Change this number based on how many functions you want to generate
    generate_complex_function(i)
