# Understanding the Results

This document explains the structure of the `performance_data.json` file, which is generated when running benchmarks with different retries and measure combinations in the extended Magpie framework.

## File Structure

The `performance_data.json` file contains detailed performance data for each run of a benchmark with specified configurations. Here's a breakdown of its structure:

### General Structure

```json
{
    "original": {
        "median_execution_time": float,
        "search_type": string
    },
    "items": [
        {
            "item_name": string,
            "median_execution_time": float,
            "duration": float,
            "patch_contents": string,
            "number_of_retries": int,
            "number_of_steps": int,
            "best_fitness": float,
            "reference_fitness": float,
            "params": bool,
            "fitness_values": [float],
            "new_highs": int,
            "new_highs_ratio": float
        },
        ...
    ]
}
```


## Description of Fields

- **original**: Contains data about the original benchmark without modifications.
  - **median_execution_time**: The median execution time calculated from multiple(20) runs to minimize variability.
  - **search_type**: The type of search process used, e.g., `local_search`.

- **items**: An array of objects, each representing the results of running the benchmark with a specific performance measure and retry setting.
  - **item_name**: The name of the performance measure used.
  - **median_execution_time**: Median execution time for this configuration.
  - **duration**: Total time taken to complete the search in seconds.
  - **patch_contents**: Description of the changes made to the software to achieve this variant.
  - **number_of_retries**: How many times each fitness measurement was taken.
  - **number_of_steps**: Total number of evolutionary steps taken during the search.
  - **best_fitness**: The best fitness value achieved during the search.
  - **reference_fitness**: The initial fitness value before improvements.
  - **params**: Indicates whether the configuration was done through parameter tuning.
  - **fitness_values**: A list of all fitness values recorded during the evolutionary process.
  - **new_highs**: Count of times a new best fitness was found.
  - **new_highs_ratio**: Ratio of new highs to total steps, showing how often improvements were made.

## Example

Here's a compact example of what entries in `performance_data.json` might look like for two measures, "time" and "energy":

```json
{
    "original": {
        "median_execution_time": 21.916,
        "search_type": "local_search"
    },
    "items": [
        {
            "item_name": "time",
            "median_execution_time": 6.369,
            "duration": 1899.92,
            "patch_contents": "ParamSetting changes",
            "number_of_retries": 1,
            "number_of_steps": 255,
            "best_fitness": 5.7,
            "reference_fitness": 21.14,
            "params": false,
            "fitness_values": [21.14, 12.98, 6.56, ...],
            "new_highs": 12,
            "new_highs_ratio": 0.0545
        },
        {
            "item_name": "energy",
            "median_execution_time": 6.161,
            "duration": 2147.79,
            "patch_contents": "ParamSetting changes",
            "number_of_retries": 4,
            "number_of_steps": 73,
            "best_fitness": 313048115.0,
            "reference_fitness": 1281417698.5,
            "params": false,
            "fitness_values": [1281417698.5, 790788969.25, ...],
            "new_highs": 10,
            "new_highs_ratio": 0.1754
        }
    ]
}

```