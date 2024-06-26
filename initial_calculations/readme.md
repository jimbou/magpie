in the directory examples create a dir for every benchmark
copy the contents of the dir necessary in the benchmark folder
lets say that the executable for the benchmar is ./run_fixed.sh

go into the benchmark dir and run sudo ./total.sh "./run_fixed.sh" 
this wil take a while but it will create :
1) the medians_all.json with all the values for every metric
2) the medians.json with the median value for every metric
3) factors.json with the  factor of the median of every metric compared to the median time
4) variance.json with the variance for every metric


once you have done all the benchmarks
go to the initial calculations dir and run
python3 create_factor_variance.py exaples
this will create the factor_variance.json in the examples folder with the variance of the factors across benchmarks (this tells you which metric is better related to execution time)

then run
python3 find_median_variance.py exaples
this will create the median_variance.json in the examples folder with the median variance for every metric across all the benchmarks( and tells you which benchmar is the most consistent)