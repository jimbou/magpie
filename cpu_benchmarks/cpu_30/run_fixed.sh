#!/bin/bash
sysbench --cpu-max-prime=100 --events=3000000 --time=-1 cpu run
