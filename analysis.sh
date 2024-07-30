#!/bin/bash
#plotter
# python3 plotter.py results_1800_ucl_local/minisat_normal/performance_data.json
# python3 plotter.py results_1800_ucl_local/minisat_params/performance_data.json
# python3 plotter.py results_1800_ucl_local/minisat_hack_normal/performance_data.json
# python3 plotter.py results_1800_ucl_local/minisat_hack_params/performance_data.json
# python3 plotter.py results_1800_ucl_local/lpg/performance_data.json
# python3 plotter.py results_1800_ucl_local/scipy/performance_data.json
# python3 plotter.py results_1800_ucl_local/zlib/performance_data.json
# python3 plotter.py results_1800_ucl_local/sat4j_normal/performance_data.json
# python3 plotter.py results_1800_ucl_local/sat4j_params/performance_data.json
# python3 plotter.py results_1800_ucl_local/weka_params/performance_data.json

#plot_lines
# python3 plot_lines.py results_1800_ucl_local/minisat_normal performance_data.json
# python3 plot_lines.py results_1800_ucl_local/minisat_params performance_data.json
# python3 plot_lines.py results_1800_ucl_local/minisat_hack_normal performance_data.json
# python3 plot_lines.py results_1800_ucl_local/minisat_hack_params performance_data.json
# python3 plot_lines.py results_1800_ucl_local/lpg performance_data.json
# python3 plot_lines.py results_1800_ucl_local/scipy performance_data.json
# python3 plot_lines.py results_1800_ucl_local/zlib performance_data.json
# python3 plot_lines.py results_1800_ucl_local/sat4j_normal performance_data.json
# python3 plot_lines.py results_1800_ucl_local/sat4j_params performance_data.json
# python3 plot_lines.py results_1800_ucl_local/weka_params performance_data.json


#mad
# python3 best_fit_mad.py results_1800_ucl_local/minisat_normal/performance_data.json
# python3 best_fit_mad.py results_1800_ucl_local/minisat_params/performance_data.json
# python3 best_fit_mad.py results_1800_ucl_local/minisat_hack_normal/performance_data.json
# python3 best_fit_mad.py results_1800_ucl_local/minisat_hack_params/performance_data.json
# python3 best_fit_mad.py results_1800_ucl_local/lpg/performance_data.json
# python3 best_fit_mad.py results_1800_ucl_local/scipy/performance_data.json
# python3 best_fit_mad.py results_1800_ucl_local/zlib/performance_data.json
# python3 best_fit_mad.py results_1800_ucl_local/sat4j_normal/performance_data.json
# python3 best_fit_mad.py results_1800_ucl_local/sat4j_params/performance_data.json
# python3 best_fit_mad.py results_1800_ucl_local/weka_params/performance_data.json

#largest improvement ratio
python3 largest_improvement_ratio_new.py results_1800_ucl_local/minisat_normal/performance_data.json
python3 largest_improvement_ratio_new.py results_1800_ucl_local/minisat_params/performance_data.json
python3 largest_improvement_ratio_new.py results_1800_ucl_local/minisat_hack_normal/performance_data.json
python3 largest_improvement_ratio_new.py results_1800_ucl_local/minisat_hack_params/performance_data.json
python3 largest_improvement_ratio_new.py results_1800_ucl_local/lpg/performance_data.json
python3 largest_improvement_ratio_new.py results_1800_ucl_local/scipy/performance_data.json
python3 largest_improvement_ratio_new.py results_1800_ucl_local/zlib/performance_data.json
python3 largest_improvement_ratio_new.py results_1800_ucl_local/sat4j_normal/performance_data.json
python3 largest_improvement_ratio_new.py results_1800_ucl_local/sat4j_params/performance_data.json
python3 largest_improvement_ratio_new.py results_1800_ucl_local/weka_params/performance_data.json