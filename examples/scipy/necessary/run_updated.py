#give the path to find numpy

import numpy as np
from scipy.optimize import minimize, Bounds
import importlib.util
import argparse
import os

# Setup argument parser
parser = argparse.ArgumentParser(description="Run optimization on a user-specified objective function with given parameters.")
parser.add_argument("function_file", type=str, help="Path to the Python file containing the objective function.")
parser.add_argument("--method", type=str, choices=['Nelder-Mead', 'Powell', 'CG', 'BFGS', 'Newton-CG', 'L-BFGS-B', 'TNC', 'COBYLA', 'SLSQP', 'trust-constr', 'dogleg', 'trust-ncg', 'trust-exact', 'trust-krylov', None], help="Optimization method.")
parser.add_argument("--jac", type=str, choices=['callable','2-point', '3-point', 'cs', None, 'None', 'True', 'False'], help="Jacobian computation method.")
parser.add_argument("--hess", type=str, choices=['callable', '2-point', '3-point', 'cs', None, 'None'], help="Method for computing the Hessian matrix.")
parser.add_argument("--hessp", type=str, choices=['callable', None, 'None'], help="Hessian of objective function times an arbitrary vector p.")
parser.add_argument("--bounds", nargs=2, type=float, default=None, help="Lower and upper bounds for all parameters. Enter two values.")
parser.add_argument("--constraint_type", type=str, default=None, help="Type of constraint: 'eq' or 'ineq'.")
parser.add_argument("--constraint_fun", type=str, default=None, help="Constraint function as a string to eval.")
parser.add_argument("--tol", type=float, default=0.0001, help="Tolerance for termination.")
parser.add_argument("--disp", type=bool, default=False, help="Set to True to print convergence messages.")
parser.add_argument("--maxiter", type=int, default=1000, help="Maximum number of iterations to perform.")
parser.add_argument("--maxfun", type=int, default=None, help="Maximum number of function evaluations (specific to TNC).")

args = parser.parse_args()

def load_function_from_file(filename):
    spec = importlib.util.spec_from_file_location("module.name", filename)
    module = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(module)
    return module.complex_objective_function

def create_bounds(lower, upper, dimension):
    if lower is not None and upper is not None:
        return Bounds([lower]*dimension, [upper]*dimension)
    return None

def create_constraint(constraint_type, constraint_fun):
    if constraint_type and constraint_fun:
        return {'type': constraint_type, 'fun': lambda x: eval(constraint_fun)}
    return None

# Load the objective function from the specified file
func = load_function_from_file(args.function_file)
dimension = 10  # Assuming the dimensionality of the problem
x0 = np.random.rand(dimension)  # Initial guess

# Prepare bounds and constraints
bounds = create_bounds(args.bounds[0], args.bounds[1], dimension) if args.bounds else None
constraint = create_constraint(args.constraint_type, args.constraint_fun)

# Options dictionary to handle optional arguments dynamically
# #if method is TNC, maxfun is required else maxiter is required
# #if method is not TNC, maxiter is required
options= {}
if args.method == 'TNC':
    options = {
        "maxfun": args.maxiter,
        "disp": args.disp
    }
else:
    options = {
        "maxiter": args.maxiter,
        "disp": args.disp
    }

# Collect parameters for minimize
minimize_args = {
    "fun": func,
    "x0": x0,
    "method": args.method,
    "jac": args.jac if args.jac not in ['None', None, 'False'] else None,
    "hess": args.hess if args.hess not in ['None', None] else None,
    "hessp": args.hessp if args.hessp not in ['None', None] else None,
    "bounds": bounds,
    "constraints": constraint if constraint else (),
    "tol": args.tol,
    "options": options
}

# Filter out None values
minimize_args = {k: v for k, v in minimize_args.items() if v is not None}

# Perform the optimization
#print the minimize_args
print(minimize_args)
result = minimize(**minimize_args)

# Output results
print(f"Using function from {args.function_file}")
print("Method used:", args.method)
print("Jacobian option:", args.jac)
print("Hessian option:", args.hess)
print("Hessian product option:", args.hessp)
print("Bounds:", bounds)
print("Constraint:", constraint)
print("Optimization options:", options)
print("Optimized parameters:", result.x)
print("Minimum objective function value:", result.fun)
