#!/bin/bash


sudo python3.11 run_examples.py triangle-c scenario_slow "" "./run_triangle" "make run_triangle" triangle.c ""
sudo python3.11 run_examples.py triangle-cpp scenario_slow "" "./build/run_triangle" "bash compile.sh" triangle.cpp.xml ""
sudo python3.11 run_examples.py triangle-py scenario_slow "" "python3.11 run_triangle.py" "" triangle.py ""
sudo python3.11 run_examples.py minisat "" scenario_runtime_config1 "bash run_fixed.sh" "" minisat_simplified.params minisat_simplified.params
sudo python3.11 run_examples.py minisat "" scenario_runtime_config3 "bash run_fixed.sh" "bash compile.sh" core/Solver.cc ""
sudo python3.11 run_examples.py sat4j "" scenario_runtime_config1 "bash run_fixed.sh" "" test.params test.params
