#!/bin/bash

# sudo python3.11 run_examples_gp.py minisat "" scenario_config1_gp "bash run_fixed.sh" "" minisat_simplified.params minisat_simplified.params genetic_programming
# sudo python3.11 run_examples_gp.py minisat "" scenario_config3_gp "bash run_fixed.sh" "bash compile.sh" core/Solver.cc "" genetic_programming

# sudo python3.11 run_examples_gp.py minisat_hack "" scenario_config1_gp "bash run_fixed.sh" "" minisat_advanced.params minisat_advanced.params genetic_programming
# sudo python3.11 run_examples_gp.py minisat_hack "" scenario_config3_gp "bash run_fixed.sh" "./build.sh" sources/core/Solver.cc "" genetic_programming
# sleep 60
# sudo python3.11 run_examples_gp.py sat4j "" scenario_config1_gp "bash run_fixed.sh" "" test.params test.params genetic_programming

# sudo python3.11 run_examples_gp.py sat4j "" scenario_config3_gp "bash run_fixed.sh" "ant sat" org.sat4j.core/src/main/java/org/sat4j/minisat/core/Solver.java "" genetic_programming


sudo python3.11 run_examples_gp_temp.py lpg "" scenario_config1_gp "bash run_fixed.sh" "" lpg.params lpg.params genetic_programming
sleep 60
sudo python3.11 run_examples_gp_temp.py weka "" scenario_config1_gp "bash run_fixed.sh" "" weka.params weka.params genetic_programming
sleep 60
sudo python3.11 run_examples_gp.py weka "" scenario_config3_gp "bash run_fixed.sh" "ant compile" src/main/java/weka/classifiers/trees/RandomForest.java  "" genetic_programming

sudo python3.11 run_examples_gp.py scipy "" scenario_config1_gp  "bash run_fixed.sh" "" scipy.params scipy.params genetic_programming


sleep 60
sudo python3.11 run_examples_gp.py zlib "" scenario_config1_gp  "bash run_fixed.sh" "" zlib.params zlib.params genetic_programming