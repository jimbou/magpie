import json
import os
import subprocess
import statistics
import time

for value in ["instructions", "cycles", "cache_references" ,"cache_misses", "branches", "branch_misses", "cpu_clock", "task_clock", "faults", "minor_faults", "major_faults", "cs", "migrations", "L1_dcache_loads", "L1_dcache_load_misses" "dTLB_loads", "dTLB_load_misses"]:
    print(f"perf stat -e {value} $command")