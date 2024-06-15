def get_command_line_cmd(runargs, config):
    '''
    Returns the command line call string to execute the target algorithm (here: Spear).
    Args:
        runargs: a map of several optional arguments for the execution of the target algorithm.
                {
                  "instance": <instance>,
                  "specifics" : <extra data associated with the instance>,
                  "cutoff" : <runtime cutoff>,
                  "runlength" : <runlength cutoff>,
                  "seed" : <seed>
                }
        config: a mapping from parameter name to parameter value
    Returns:
        A command call list to execute the target algorithm.
    '''
    binary_path = "solvers/minisat_HACK_999ED_CSSC/minisat_HACK_999ED_CSSC_static"
    cmd = "%s" %(binary_path)
    for name, value in config.items():
        cmd += " %s=%s" %(name,  value)
    cmd += " %s" %(runargs["instance"])
    return cmd
