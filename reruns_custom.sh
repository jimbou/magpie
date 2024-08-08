#!/bin/bash
python3.11 run_examples_custom_lpg.py lpg "" scenario_runtime_config1 "bash run_fixed.sh" "" lpg.params lpg.params local_search
python3.11 run_examples_custom_scipy.py scipy "" scenario_runtime_config1 "bash run_fixed.sh" "" scipy.params scipy.params local_search
