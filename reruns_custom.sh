#!/bin/bash
python3.11 run_examples_custom_minisat_hack.py minisat_hack "" scenario_runtime_config3 "bash run_fixed.sh" "./build.sh" sources/core/Solver.cc "" local_search
python3.11 run_examples_custom_minisat_hack.py minisat "" scenario_runtime_config1 "bash run_fixed.sh" "" minisat_simplified.params minisat_simplified.params local_search
python3.11 run_examples_custom_lpg.py lpg "" scenario_runtime_config1 "bash run_fixed.sh" "" lpg.params lpg.params local_search
python3.11 run_examples_custom_scipy.py scipy "" scenario_runtime_config1 "bash run_fixed.sh" "" scipy.params scipy.params local_search
