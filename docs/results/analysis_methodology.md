# Analysis Methodology for MAGPIE

This document outlines the analysis methodology used to evaluate the results from MAGPIE's genetic improvement experiments. All analysis scripts are located in the `useful_scripts` folder, and each script has its specific README for detailed instructions.

## Overview of Analysis Steps

1. **Ranking and Execution Time Analysis**
   - **Script:** `plotter.py`
   - **Command:** `python3 plotter.py results_1800_ucl_local/minisat_normal/performance_data.json`
   - **Outputs:**
     - Rankings by Median Execution Time and New Highs
       - `execution_time_ranking[name].png`
       - `median_execution_time_data[name].csv`
       - `new_highs_ranking[name].png`
       - `new_highs_data[name].csv`
     - Average Rank of Execution Time by Number of Retries
       - `average_rank_per_retry[name].png`
       - `average_rank_per_retry[name].csv`
     - Mean Median Execution Times by Item Name
       - `mean_median_execution_times[name].png`
       - `mean_median_execution_times[name].csv`

2. **Line Plots of Fitness Values**
   - **Script:** `plot_lines.py`
   - **Command:** `python3 plot_lines.py results_1800_ucl_local/minisat_normal performance_data.json`
   - **Outputs:**
     - PNGs of the fitness values showing all values and improvements for all combinations of metrics and retries.
     - A CSV containing detailed stats like total decreases, proportion of decreases, average decrease per step, etc.

3. **Mean Absolute Deviation (MAD) Analysis**
   - **Script:** `best_fit_mad.py`
   - **Command:** `python3 best_fit_mad.py results_1800_ucl_local/minisat_normal/performance_data.json`
   - **Outputs:**
     - Detailed MAD statistics for each measure and retry combination.

4. **Aggregated Analysis Across Benchmarks**
   - **Script:** `get_median_for_plot_lines.py`
   - **Command:** `python3 get_median_for_plot_lines.py results_1800_ucl_local/`
   - **Purpose:** Groups statistics across benchmarks for retries and measures used such as total decreases, average decrease percentage per step, and proportion of decreases.

5. **Finding Median and Mean Across Benchmarks**
   - **Script:** `find_median_across_benchmarks.py`
   - **Command:** `python3 find_median_across_benchmarks.py results_1800_ucl_local/ average_rank_per_retry.csv`
   - **Purpose:** Calculates averages and medians for data across multiple benchmarks.

6. **Aggregate MAD Analysis**
   - **MAD Per Retry:** `find_mean_median_mad.py`
       - **Command:** `python3 find_mean_median_mad.py results_1800_ucl_local/best_fit_mad_stats.csv`
   - **MAD Per Measure:** `find_mean_median_mad_item.py`
       - **Command:** `python3 find_mean_median_mad_item.py results_1800_ucl_local/best_fit_mad_stats.csv`

7. **Determining Best Retry Number Per Measure**
   - **Script:** `find_best_retry_per_item.py`
   - **Command:** `python3 find_best_retry_per_item.py results_1800_ucl_local/`
   - **Purpose:** Identifies the optimal number of retries for each measure across benchmarks.

All the examples assume results_1800_ucl_local as the directory containing all benchmark result folders.

