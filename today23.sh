#!/bin/bash
cd final_ucl/minisat_normal_seed_genetic_1800
python3 solve_problem.py 
sleep 60
cd ../minisat_normal_seed_genetic_1800
python3 solve_problem.py 
cd ../../
sleep 60
python3.11 run_examples.py sat4j "" scenario_runtime_config1 "bash run_fixed.sh" "" test.params test.params local_search
sleep 600
python3.11 run_examples.py sat4j "" scenario_runtime_config3 "bash run_fixed.sh" "ant sat" org.sat4j.core/src/main/java/org/sat4j/minisat/core/Solver.java "" local_search


sleep 600
