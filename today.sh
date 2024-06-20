#!/bin/bash
python3.11 run_examples.py minisat "" scenario_runtime_config1 "bash run_fixed.sh" "" minisat_simplified.params minisat_simplified.params
sleep 2
python3.11 run_examples.py sat4j "" scenario_runtime_config1 "bash run_fixed.sh" "" test.params test.params

