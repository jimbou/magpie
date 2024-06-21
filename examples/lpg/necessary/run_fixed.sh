#!/bin/sh
./lpg-td -f blocksworld/problems/pfile01-002.pddl -o blocksworld/domain.pddl -speed -out foo2 -seed 111000 $@
./Val-20211204.1-Linux/bin/Validate -v blocksworld/domain.pddl blocksworld/problems/pfile01-002.pddl  foo2.SOL 
rm foo2.SOL foo2
./lpg-td -f blocksworld/problems/pfile01-001.pddl -o blocksworld/domain.pddl -speed -out foo1 -seed 110000 $@
./Val-20211204.1-Linux/bin/Validate -v blocksworld/domain.pddl blocksworld/problems/pfile01-001.pddl  foo1.SOL 
rm foo1.SOL foo1
./lpg-td -f blocksworld/problems/pfile01-005.pddl -o blocksworld/domain.pddl -speed -out foo5 -seed 110111 $@
./Val-20211204.1-Linux/bin/Validate -v blocksworld/domain.pddl blocksworld/problems/pfile01-005.pddl  foo5.SOL 
rm foo5.SOL foo5