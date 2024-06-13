#!/bin/bash
sudo python3.11 run_examples_temp.py minisat "" scenario_runtime_config1 "bash run_fixed.sh" "" minisat_simplified.params minisat_simplified.params
sleep 200
sudo python3.11 run_examples.py minisat "" scenario_runtime_config3 "bash run_fixed.sh" "bash compile.sh" core/Solver.cc ""

