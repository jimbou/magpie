import numpy as np
from scipy.optimize import minimize, Bounds
import importlib.util
import os
import random

def load_function_from_file(filename):
    spec = importlib.util.spec_from_file_location("module.name", filename)
    module = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(module)
    return module.complex_objective_function

# Paths and method configuration
path_to_functions = './'
function_files = [f for f in os.listdir(path_to_functions) if f.endswith('.py') and 'complex_function' in f]

# Define possible parameters for minimize with constraints
methods = ['Nelder-Mead', 'Powell', 'CG', 'BFGS', 'L-BFGS-B', 'TNC', 'COBYLA', 'SLSQP', 'trust-constr']
constraints_options = [None, {'type': 'ineq', 'fun': lambda x: x[0] - 1}]

# Method requires Jacobian
methods_require_jac = ['CG', 'BFGS', 'Newton-CG', 'L-BFGS-B', 'TNC', 'SLSQP']

# Function to select method and check for requirements
def select_method(bounds_present, jacobian_required):
    if bounds_present:
        eligible_methods = [m for m in methods if m not in ['Newton-CG', 'dogleg', 'trust-ncg', 'trust-krylov', 'trust-exact']]
    else:
        eligible_methods = methods

    if jacobian_required:
        eligible_methods = [m for m in eligible_methods if m in methods_require_jac]

    return random.choice(eligible_methods) if eligible_methods else None

for function_file in function_files:
    func = load_function_from_file(os.path.join(path_to_functions, function_file))
    print(func)
    dimension = 10  # Assuming the dimensionality of the problem
    x0 = np.random.rand(dimension)

    # Randomly determine if Jacobian is needed
    jacobian_required = random.choice([True, False])
    jac_option = '2-point' if jacobian_required else None
    
    # Generate random bounds
    lower_bounds = np.random.randint(-10, 0, size=dimension)
    upper_bounds = np.random.randint(1, 10, size=dimension)
    bounds = Bounds(lower_bounds, upper_bounds)
    
    # Select a method considering bounds and Jacobian
    method = select_method(bounds_present=True, jacobian_required=jacobian_required)

    constraints = random.choice(constraints_options)

    if method:
        result = minimize(func, x0, method=method, jac=jac_option, bounds=bounds, constraints=constraints)
        print(f"Using function from {function_file}")
        print("Method used:", method)
        print("Jacobian option:", jac_option)
        print("Bounds:", bounds)
        print("Constraints:", constraints)
        print("Optimized parameters:", result.x)
        print("Minimum objective function value:", result.fun)
        print("--------------------------------------------------")
    else:
        print("No suitable method found for the selected options.")
