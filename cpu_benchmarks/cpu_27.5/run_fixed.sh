#!/bin/bash
sysbench --cpu-max-prime=100 --events=2750000 --time=-1 cpu run
