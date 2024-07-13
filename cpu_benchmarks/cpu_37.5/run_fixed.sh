#!/bin/bash
sysbench --cpu-max-prime=100 --events=3750000 --time=-1 cpu run
