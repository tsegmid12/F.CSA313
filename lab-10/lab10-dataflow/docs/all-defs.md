# All-defs phase

The minimum all-defs set for `bonus()` is **two tests**. Every definition reaches at least one reachable use across these two executions.

| ID | Test | Input | Definitions reached |
|---|---|---|---|
| T1 | `allDefs_T1_allDefinitionsExceptInitialScore` | `[1,0,1,1,1,1,1]`, threshold 5 | parameter defs `attendance@31`, `threshold@31`; `present@35/@40`, `streak@36/@41/@46`, `best@37/@43`, `a@38`, `score@51/@54` |
| T2 | `allDefs_T2_initialScoreReachesBonusUse` | `[1,1,1,1,1]`, threshold 6 | `score@49 -> use@54`, `score@54 -> use@56` |

T1 reaches all definitions except `score@49`: the base threshold condition is true and line 51 redefines score before a later use. T2 makes the base threshold condition false, so `score@49` remains live until line 54; the streak bonus then defines score@54 and the return uses it at line 56.

Two executions are necessary because `score@49` reaches a use only when `present >= threshold` is false, while `score@51` exists only when that predicate is true. These paths are mutually exclusive and cannot be covered in one execution.
