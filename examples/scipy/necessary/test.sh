#!/bin/bash
ARGV=$@

# Parse and store all arguments in an associative array
declare -A params
for arg in "$@"; do
    key="${arg%%=*}"    # Extract everything before '='
    value="${arg#*=}"   # Extract everything after '='
    key=${key#--}       # Remove leading '--'
    key=$(echo $key | tr '[:upper:]' '[:lower:]')  # Convert key to lowercase to avoid case sensitivity issues
    params[$key]=$value
done

# Define valid method combinations for jac, hess, and hessp
declare -A valid_jac=(["cg"]=1 ["bfgs"]=1 ["newton-cg"]=1 ["l-bfgs-b"]=1 ["tnc"]=1 ["slsqp"]=1 ["dogleg"]=1 ["trust-ncg"]=1 ["trust-krylov"]=1 ["trust-exact"]=1 ["trust-constr"]=1)
declare -A valid_hess=(["newton-cg"]=1 ["dogleg"]=1 ["trust-ncg"]=1 ["trust-krylov"]=1 ["trust-exact"]=1 ["trust-constr"]=1)
declare -A valid_hessp=(["newton-cg"]=1 ["trust-ncg"]=1 ["trust-krylov"]=1 ["trust-constr"]=1)

# Debug output to verify inputs
echo "Method: ${params[method]}"
echo "Jac: ${params[jac]}"
echo "Hess: ${params[hess]}"
echo "HessP: ${params[hessp]}"
echo "Maxfun: ${params[maxfun]}"
echo "Maxiter: ${params[maxiter]}"
# Validate the parameter combinations
if [[ -n ${params[jac]} && ${params[jac]} != "None" && -z ${valid_jac[${params[method],,}]} ]]; then
    echo "Invalid combination: 'jac' parameter is not compatible with method '${params[method]}'"
    exit -1
fi

if [[ -n ${params[hess]} && ${params[hess]} != "None" && -z ${valid_hess[${params[method],,}]} ]]; then
    echo "Invalid combination: 'hess' parameter is not compatible with method '${params[method]}'"
    exit -1
fi

if [[ -n ${params[hessp]} && ${params[hessp]} != "None" && -z ${valid_hessp[${params[method],,}]} ]]; then
    echo "Invalid combination: 'hessp' parameter is not compatible with method '${params[method]}'"
    exit -1
fi

if [[ -n ${params[maxfun]} && ${params[maxfun]} != "None" && ${params[method],,} != "tnc" ]]; then
    echo "Invalid combination: 'maxfun' parameter can only be used with method 'TNC'"
    exit -1
fi

# if [[ -n ${params[maxiter]} && ${params[maxiter]} != "None" && ${params[method],,} == "tnc" ]]; then
#     #make the maxfun and maxiter the same and then make maxiter None
#     params[maxiter]=${params[maxfun]}
#     params[maxfun]=None
# fi

echo "All parameter combinations are valid. Proceeding with execution."
# Add your script execution logic here, for example:
# python your_script.py "${params[@]}"



# Sample execution (you would replace this with your actual script call)
echo "Running with settings:"
for key in "${!params[@]}"; do
    echo "$key = ${params[$key]}"
done

my_test() {
    FILENAME1=$1
    EXPECTED=$2
   /home/jim/magpie/.venv/bin/python run_updated.py $FILENAME1 $ARGV  
    RETURN=$?
    echo "RETURN1:" $RETURN
    if [ $RETURN -ne $((EXPECTED)) ]; then
        echo "FAILED ON FILE:" $FILENAME1
        echo "GOT:" $RETURN
        echo "EXPECTED:" $EXPECTED
        exit -1
    fi
}

my_test data/complex_function_1.py  0
my_test data/complex_function_2.py  0


exit 0