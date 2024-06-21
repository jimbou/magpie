import numpy as np
from scipy.optimize import minimize, Bounds
import random

# Define the objective function
def objective_function(x):
    return x[0]**2 + x[1]**2

# Initial guess
x0 = np.array([1, 1])

# Define possible methods
methods = ['Nelder-Mead', 'Powell', 'CG', 'BFGS', 'Newton-CG', 'L-BFGS-B', 'TNC', 'COBYLA', 'SLSQP', 'trust-constr']

# Define possible choices for Jacobian computation
jacobians = [None, '2-point', '3-point', 'cs']

# Randomly select method and Jacobian
method = random.choice(methods)
jac_option = random.choice(jacobians)

# Options for the minimize function
options = {'disp': True}

# Constraints and bounds (examples)
bounds = Bounds([0, -1], [2, 1])  # Example bounds
constraints = {'type': 'ineq', 'fun': lambda x: x[0] - 1}  # Example constraint: x[0] >= 1

# Calling the minimize function with randomly selected parameters
result = minimize(objective_function, x0, method=method, jac=jac_option, bounds=bounds if method in ['L-BFGS-B', 'TNC', 'SLSQP', 'trust-constr'] else None, constraints=constraints if method in ['COBYLA', 'SLSQP', 'trust-constr'] else None, options=options)

# Print results
print("Optimization Result:")
print("Method used:", method)
print("Jacobian option:", jac_option)
print("Solution:", result.x)
print("Objective function value:", result.fun)
