1)RESTARTS (Default: Glucose21Restarts)

Description: Determines the restart strategy used by the algorithm.
Possible Values:
Glucose21Restarts
ArminRestarts
FixedPeriodRestarts
LubyRestarts
NoRestarts
MiniSATRestarts

2)LUBYFACTOR (Default: 512)

Description: A factor used in the Luby restart strategy.
Range: 16 to 2048
Condition: Only applicable when RESTARTS is set to LubyRestarts.

3)FIXEDPERIOD (Default: 100)

Description: The fixed period for restarts in the FixedPeriod strategy.
Range: 1 to 100,000
Condition: Only applicable when RESTARTS is set to FixedPeriodRestarts.


4)PHASE (Default: RSATPhaseSelectionStrategy)

Description: Determines the phase selection strategy.
Possible Values:
NegativeLiteralSelectionStrategy
PositiveLiteralSelectionStrategy
RSATPhaseSelectionStrategy
UserFixedPhaseSelectionStrategy
RandomLiteralSelectionStrategy
RSATLastLearnedClausesPhaseSelectionStrategy
PhaseCachingAutoEraseStrategy
PhaseInLastLearnedClauseSelectionStrategy

5)CLADECAY (Default: 0.999)

Description: The decay factor for clause activities.
Range: 0 to 1


6)INITCONFLICTBOUND (Default: 100)

Description: The initial conflict bound for restarts.
Range: 1 to 1,000
Condition: Only applicable when RESTARTS is MiniSATRestarts or ArminRestarts.

7)VARDECAY (Default: 0.95)

Description: The decay factor for variable activities.
Range: 0 to 1

8)CONFLICTBOUNDINCFACTOR (Default: 2)

Description: The factor by which the conflict bound increases.
Range: 1.5 to 4
Condition: Only applicable when RESTARTS is set to MiniSATRestarts.

9)SIMP (Default: EXPENSIVE_SIMPLIFICATION)

Description: Level of simplification applied.
Possible Values:
NO_SIMPLIFICATION
SIMPLE_SIMPLIFICATION
EXPENSIVE_SIMPLIFICATION

10)CLEANING (Default: LBD2)

Description: Strategy for clause cleaning.
Possible Values:
ACTIVITY
LBD
LBD2
Constraint: When RESTARTS is Glucose21Restarts, CLEANING must be either LBD or LBD2.

General Constraints and Conditions:
LUBYFACTOR is only active when RESTARTS is set to LubyRestarts.
FIXEDPERIOD is only active when RESTARTS is set to FixedPeriodRestarts.
INITCONFLICTBOUND is only active when RESTARTS is MiniSATRestarts or ArminRestarts.
CONFLICTBOUNDINCFACTOR is only active when RESTARTS is set to MiniSATRestarts.
When RESTARTS is set to Glucose21Restarts, CLEANING must be either LBD or LBD2.