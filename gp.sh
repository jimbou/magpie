#!/bin/bash

sudo python3.11 run_examples_gp.py minisat "" scenario_config1_gp "bash run_fixed.sh" "" minisat_simplified.params minisat_simplified.params genetic_programming
sudo python3.11 run_examples_gp.py minisat "" scenario_config3_gp "bash run_fixed.sh" "bash compile.sh" core/Solver.cc "" genetic_programming