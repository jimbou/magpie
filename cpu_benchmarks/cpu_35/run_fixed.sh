#!/bin/bash
sysbench --cpu-max-prime=100 --events=3500000 --time=-1 cpu run
