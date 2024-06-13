def get_solver_callstring(instance_filename, seed, param_hashmap)
	solver_callstring = "java -Xms1g -Xmx1g -jar sat4j-sat.jar -s Default -S "
	params_string = "PARAMS=SearchParams"
        param_hashmap.keys.each_with_index do |param_name, index|
	        param_value = param_hashmap[param_name]
	        if param_name == "CLADECAY"
	            params_string += "/claDecay:"+param_value
	            next
	        end
	        if param_name == "INITCONFLICTBOUND"
	            params_string += "/initConflictBound:"+param_value
	            next
	        end
	        if param_name == "VARDECAY"
	            params_string += "/varDecay:"+param_value
	            next
	        end
	        if param_name == "CONFLICTBOUNDINCFACTOR"
	            params_string += "/conflictBoundIncFactor:"+param_value
	            next
	        end
        	solver_callstring += "#{param_name}=#{param_value}"
        	if param_value == "LubyRestarts"
        	   solver_callstring += "/factor:"+param_hashmap["LUBYFACTOR"]
        	end
        	if param_value == "FixedPeriodRestarts"
        	   solver_callstring += "/period:"+param_hashmap["FIXEDPERIOD"]
        	end
        	solver_callstring += ","
	end
	solver_callstring += params_string
	return solver_callstring + " #{instance_filename}"
end
