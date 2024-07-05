#!/bin/bash
python3.11 run_examples.py minisat "" scenario_runtime_config1 "bash run_fixed.sh" "" minisat_simplified.params minisat_simplified.params local_search
sleep 1800
python3.11 run_examples.py minisat "" scenario_runtime_config1 "bash run_fixed.sh" "" minisat_simplified.params minisat_simplified.params genetic_programming

