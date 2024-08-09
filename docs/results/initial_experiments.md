
# Initial Calculations for Evaluating Measures in MAGPIE

This document describes how to perform the initial evaluations to gauge which performance measures are the most consistent and best correlated with execution time. This process helps in narrowing down the search space for genetic improvement.

## Setting Up

1. **Directory Setup:** Ensure that your benchmark directory contains a `necessary` folder with all required scripts and if parameter tuning is involved, include the params file and executable along with any needed inputs.

2. **Script Requirements:** The `total_initial.sh` script should be placed in the benchmark directory. This script orchestrates the entire initial calculation process. Also the `trun_initial.sh`, `run_rapl_energy.sh` (for energy), the `run<_custom.sh`, `read-total.py` (for weight) and finally the `create_factors.py`and `create_variance.py`. They can all be found in the useful_scripts directory.

## Running Initial Calculations

- **Command:** Place the `total_initial.sh` script in your benchmark directory. Run this script by passing it the command used to execute your benchmark.
  
  ```bash
  ./total_initial.sh "bash run_fixed.sh"
  ```

### What Happens During Execution

1. **Execution Loop:** The `total_initial.sh` script will execute each measure 20 times, capturing the results in `medians-all.json`. It also stores the median for each measure in `median.json`.

2. **Factor and Variance Calculation:**
   - After completing the runs, `create_factors.py` is triggered to calculate the factors that show the relationship between measure values and execution times.
   - `create_variance.py` is then executed to compute the variance of these measured values.

### Post-Execution Analysis Across Benchmarks

1. **Consolidating Results Across Benchmarks:** Once all benchmarks are processed, and their results are stored in a folder with a subfolder for each benchmark, the following scripts are used to analyze the data across all benchmarks:
   - `create_factor_variance.py` computes the Coefficient of Variation (CV) of the factor across all benchmarks.
   - `find_median_variance.py` calculates the median CV of the measured values across all benchmarks, grouped by measure.
   - `find_variance_ranks.py` determines the median ranks of each measure's CV across all benchmarks, helping identify the most stable and reliable measures.

## Summary of Scripts and Their Functions

- **total_initial.sh:** Orchestrates the initial run of the benchmark for multiple measures.
- **create_factors.py:** Calculates factors relating measure values to execution times.
- **create_variance.py:** Computes variance of measured values for stability assessment.
- **create_factor_variance.py:** Aggregates factor CV across benchmarks.
- **find_median_variance.py:** Calculates median CVs across benchmarks.
- **find_variance_ranks.py:** Ranks measures based on their CV across benchmarks.

## Conclusion

This setup allows for a detailed preliminary analysis to select the most reliable and relevant measures for the genetic improvement process. By assessing measure stability and their correlation to execution times, this methodology aids in optimizing the configuration for subsequent genetic improvement experiments.
