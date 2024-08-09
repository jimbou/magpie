1) at the beginning compare how all the metrics correlate to execution time just by rerunning them
2) compare how close the achieved fitness and the same value after rerruning 20 times was after the completion for different retries numbersv

3) check the new highs to see if some metrics are more effective and others are more efficient
4) get the new performance metrics and redo the initial calculations
5) report -- page 2, 2.5 broader applicability of optimised -- check related work
6) report -- page 4, top right - what about measurement effort?  --ignore



Analysis part:
1) Rank the metrics for each benchmark and find median rank for each metric (do it one for retries 1 to 5 and then 1 to 3)
2) Rank the metrics for each retry per benhmark and find median rank for each retry across bencharks
3) use plot_lines and get_median_for_plot_lines.py to get across benchmarks the total_decreases`, `average_decrease_percentage_per_step`, `std_deviation_of_decreases`, and `proportion_of_large_decreases`. once for retries and once for metrics  (add 25th percentile and 75th percentile)
4) calculate the overhead of each metric by ranking the median number of steps  rank for that metric across benchmarks
so for every benchmark rank the metrics based on number of steps (get the median from the number of retries probably retries=3) and then get average rank for each benchmark
5) find how much the number of retries decreases the number of steps as a percentage for each metric . Find the decrease from 1 to 2 then 1 to 3 then 1 to 4 then 1 to 5  for each metric at each benchmark and then get the median for each metric across benchmarks
6) talk about energy specifically
7) talk about in correlation with the initial calculations which the best metric to correlate with execution time are to be used as surrogates.
8) best fit line -- mad thing
9) find coverage of tests
10) weka normal rerun with better tests



scipy energy 5
lpg perf_cpu_clock 2
lpg energy 1 3 5 
minisat hack normal energy 1
minisat params energy 1
 
 enrgy  1 weka params
 energy 1, weights 4, perf_L1_dcache_loads 3, time 4, perf_cache_misses 4, perf_branches 5, weka normal
sat4j params energy 1, weights 2 4,
minisat params energy


11) find retries per metric
group mad per item name
