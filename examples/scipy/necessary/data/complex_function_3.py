import numpy as np
def complex_objective_function(x):
    result = np.sum(x**2 - 10 * np.cos(2 * np.pi * x) + 10)
    for i in range(len(x)):
        result += np.sin(x[i]) * np.cos(x[i]) * np.exp(np.abs(x[i]))
    for _ in range(1000):
        result += np.sum(np.sin(x) * np.sin(x))
    noise = np.random.normal(0, 1)
    result += noise
    return result
x0 = np.array([1, 1])

print( complex_objective_function (x0))
