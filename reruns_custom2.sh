#!/bin/bash
python3.11 run_examples_custom_weka_params.py weka "" scenario_runtime_config1 "bash run_fixed.sh" "" weka.params weka.params local_search
python3.11 run_examples_custom_weka_normal.py weka "" scenario_runtime_config3 "bash run_fixed.sh" "ant compile" src/main/java/weka/classifiers/trees/RandomForest.java  "" local_search
python3.11 run_examples_custom_sat4j_params.py sat4j "" scenario_runtime_config1 "bash run_fixed.sh" "" test.params test.params local_search
python3.11 run_examples_custom_minisat_params.py minisat "" scenario_runtime_config1 "bash run_fixed.sh" "" minisat_simplified.params minisat_simplified.params local_search


python3.11 run_examples_custom_weka_params.py weka "" scenario_runtime_config1 "bash run_fixed.sh" "" weka.params weka.params local_search
python3.11 run_examples_custom_weka_normal.py weka "" scenario_runtime_config3 "bash run_fixed.sh" "ant compile" src/main/java/weka/classifiers/trees/RandomForest.java  "" local_search
python3.11 run_examples_custom_sat4j_params.py sat4j "" scenario_runtime_config1 "bash run_fixed.sh" "" test.params test.params local_search
python3.11 run_examples_custom_minisat_params.py minisat "" scenario_runtime_config1 "bash run_fixed.sh" "" minisat_simplified.params minisat_simplified.params local_search