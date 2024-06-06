#!/bin/bash


sudo python3.11 run_examples.py triangle-c scenario_slow "" run_triangle "make run_triangle" triangle.c
sudo python3.11 run_examples.py triangle-cpp scenario_slow "" build/run_triangle "bash compile.sh" triangle.cpp.xml