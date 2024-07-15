#!/bin/bash
python3.11 run_examples_temp.py weka "" scenario_runtime_config1 "bash run_fixed.sh" "" weka.params weka.params local_search
python3.11 run_examples_temp.py weka "" scenario_runtime_config3 "bash run_fixed.sh" "ant compile" src/main/java/weka/classifiers/trees/RandomForest.java  "" local_search
