#!/bin/bash
cd minisat_run11
python3 solve_problem.py 
cd ../
sleep 60
python3.11 run_examples_2.py minisat "" scenario_runtime_config3 "bash run_fixed.sh" "bash compile.sh" core/Solver.cc "" genetic_programming
sleep 600
python3.11 run_examples.py minisat_hack "" scenario_runtime_config1 "bash run_fixed.sh" "" minisat_advanced.params minisat_advanced.params local_search
sleep 600
python3.11 run_examples.py minisat_hack "" scenario_runtime_config3 "bash run_fixed.sh" "./build.sh" sources/core/Solver.cc "" local_search
