#!/bin/bash

# python3.11 run_examples_custom_genetic.py weka "" scenario_runtime_config1 "bash run_fixed.sh" "" weka.params weka.params genetic_programming
# #rerun for the other measures and retry combos
# sleep 600
python3.11 run_examples_custom_genetic2.py minisat "" scenario_runtime_config1 "bash run_fixed.sh" "" minisat_simplified.params minisat_simplified.params genetic_programming
sleep 600
python3.11 run_examples_custom_genetic2.py minisat "" scenario_runtime_config3 "bash run_fixed.sh" "bash compile.sh" core/Solver.cc "" genetic_programming
sleep 600
python3.11 run_examples_custom_genetic2.py sat4j "" scenario_runtime_config1 "bash run_fixed.sh" "" test.params test.params genetic_programming
sleep 600
python3.11 run_examples_custom_genetic2.py sat4j "" scenario_runtime_config3 "bash run_fixed.sh" "ant sat" org.sat4j.core/src/main/java/org/sat4j/minisat/core/Solver.java "" genetic_programming
sleep 600
python3.11 run_examples_custom_genetic2.py minisat_hack "" scenario_runtime_config1 "bash run_fixed.sh" "" minisat_advanced.params minisat_advanced.params genetic_programming
sleep 600

python3.11 run_examples_custom_genetic2.py minisat_hack "" scenario_runtime_config3 "bash run_fixed.sh" "./build.sh" sources/core/Solver.cc "" genetic_programming
sleep 600
python3.11 run_examples_custom_genetic2.py weka "" scenario_runtime_config1 "bash run_fixed.sh" "" weka.params weka.params genetic_programming
sleep 600
python3.11 run_examples_custom_genetic2.py weka "" scenario_runtime_config3 "bash run_fixed.sh" "ant compile" src/main/java/weka/classifiers/trees/RandomForest.java  "" genetic_programming
sleep 600
python3.11 run_examples_custom_genetic2.py zlib "" scenario_runtime_config1 "bash run_fixed.sh" "" zlib.params zlib.params genetic_programming 
sleep 600

python3.11 run_examples_custom_genetic2.py lpg "" scenario_runtime_config1 "bash run_fixed.sh" "" lpg.params lpg.params genetic_programming 
sleep 600
python3.11 run_examples_custom_genetic2.py scipy "" scenario_runtime_config1 "bash run_fixed.sh" "" scipy.params scipy.params genetic_programming
