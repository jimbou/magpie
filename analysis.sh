#!/bin/bash
#plotter
# python3 plotter.py reruns_genetic_1800_results/minisat_normal/performance_data.json
# python3 plotter.py reruns_genetic_1800_results/minisat_params/performance_data.json
# python3 plotter.py reruns_genetic_1800_results/minisat_hack_normal/performance_data.json
# python3 plotter.py reruns_genetic_1800_results/minisat_hack_params/performance_data.json
# python3 plotter.py reruns_genetic_1800_results/lpg/performance_data.json
# python3 plotter.py reruns_genetic_1800_results/scipy/performance_data.json
# python3 plotter.py reruns_genetic_1800_results/zlib/performance_data.json
# python3 plotter.py reruns_genetic_1800_results/sat4j_normal/performance_data.json
# python3 plotter.py reruns_genetic_1800_results/sat4j_params/performance_data.json
# python3 plotter.py reruns_genetic_1800_results/weka_params/performance_data.json
# python3 plotter.py reruns_genetic_1800_results/weka_normal/performance_data.json

#plot_lines
# python3 plot_lines.py reruns_genetic_1800_results/minisat_normal performance_data.json
# python3 plot_lines.py reruns_genetic_1800_results/minisat_params performance_data.json
# python3 plot_lines.py reruns_genetic_1800_results/minisat_hack_normal performance_data.json
# python3 plot_lines.py reruns_genetic_1800_results/minisat_hack_params performance_data.json
# python3 plot_lines.py reruns_genetic_1800_results/lpg performance_data.json
# python3 plot_lines.py reruns_genetic_1800_results/scipy performance_data.json
# python3 plot_lines.py reruns_genetic_1800_results/zlib performance_data.json
# python3 plot_lines.py reruns_genetic_1800_results/sat4j_normal performance_data.json
# python3 plot_lines.py reruns_genetic_1800_results/sat4j_params performance_data.json
# python3 plot_lines.py reruns_genetic_1800_results/weka_params performance_data.json
# python3 plot_lines.py reruns_genetic_1800_results/weka_normal performance_data.json



#mad
# python3 best_fit_mad.py reruns_genetic_1800_results/minisat_normal/performance_data.json
# python3 best_fit_mad.py reruns_genetic_1800_results/minisat_params/performance_data.json
# python3 best_fit_mad.py reruns_genetic_1800_results/minisat_hack_normal/performance_data.json
# python3 best_fit_mad.py reruns_genetic_1800_results/minisat_hack_params/performance_data.json
# python3 best_fit_mad.py reruns_genetic_1800_results/lpg/performance_data.json
# python3 best_fit_mad.py reruns_genetic_1800_results/scipy/performance_data.json
# python3 best_fit_mad.py reruns_genetic_1800_results/zlib/performance_data.json
# python3 best_fit_mad.py reruns_genetic_1800_results/sat4j_normal/performance_data.json
# python3 best_fit_mad.py reruns_genetic_1800_results/sat4j_params/performance_data.json
# python3 best_fit_mad.py reruns_genetic_1800_results/weka_params/performance_data.json
# python3 best_fit_mad.py reruns_genetic_1800_results/weka_normal/performance_data.json

#largest improvement ratio
# python3 largest_improvement_ratio_new.py reruns_genetic_1800_results/minisat_normal/performance_data.json
# python3 largest_improvement_ratio_new.py reruns_genetic_1800_results/minisat_params/performance_data.json
# python3 largest_improvement_ratio_new.py reruns_genetic_1800_results/minisat_hack_normal/performance_data.json
# python3 largest_improvement_ratio_new.py reruns_genetic_1800_results/minisat_hack_params/performance_data.json
# python3 largest_improvement_ratio_new.py reruns_genetic_1800_results/lpg/performance_data.json
# python3 largest_improvement_ratio_new.py reruns_genetic_1800_results/scipy/performance_data.json
# python3 largest_improvement_ratio_new.py reruns_genetic_1800_results/zlib/performance_data.json
# python3 largest_improvement_ratio_new.py reruns_genetic_1800_results/sat4j_normal/performance_data.json
# python3 largest_improvement_ratio_new.py reruns_genetic_1800_results/sat4j_params/performance_data.json
# python3 largest_improvement_ratio_new.py reruns_genetic_1800_results/weka_normal/performance_data.json
# python3 largest_improvement_ratio_new.py reruns_genetic_1800_results/weka_params/performance_data.json


# python3 get_median_for_plot_lines.py reruns_genetic_1800_results/

# python3 find_median_across_benchmarks.py reruns_genetic_1800_results/ average_rank_per_retry.csv
# python3 find_median_across_benchmarks.py reruns_genetic_1800_results/ median_execution_time_data.csv
# python3 find_median_across_benchmarks.py reruns_genetic_1800_results/ mean_median_execution_times.csv
# python3 find_median_across_benchmarks.py reruns_genetic_1800_results/ best_fit_mad.csv


# python3 find_mean_median_mad.py  reruns_genetic_1800_results/best_fit_mad_stats.csv #ouputs to the main dir
python3 find_mean_median_mad_item.py reruns_genetic_1800_results/best_fit_mad_stats.csv
python3 find_best_retry_per_item.py reruns_genetic_1800_results/