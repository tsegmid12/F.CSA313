# GCD DU table

Source: `src/main/java/mn/edu/must/sqat/DataFlowTargets.java`.

| Variable | Def line | Use line | Kind | Test ID | Def-clear path |
|---|---:|---:|---|---|---|
| x | 13 | 16 | c-use | G2,G6 | entry x to loop body |
| x | 13 | 17 | c-use | G2 | entry x to x=y |
| x | 13 | 20 | c-use | G1 | y=0, loop skipped |
| x | 17 | 16 | c-use | G3 | updated x to next modulo |
| x | 17 | 17 | c-use | G3,G5 | updated x to next assignment |
| x | 17 | 20 | c-use | G4 | updated x to return |
| y | 13 | 15 | p-use T/F | G1,G2 | entry y to loop predicate |
| y | 13 | 16 | c-use | G2 | entry y to modulo |
| y | 13 | 18 | c-use | G2 | entry y to assignment |
| y | 18 | 15 | p-use T/F | G3,G4 | updated y to predicate |
| y | 18 | 16 | c-use | G3 | updated y to modulo |
| y | 18 | 18 | c-use | G3 | updated y to assignment |
| tmp | 16 | 18 | c-use | G2,G3 | modulo result to y=tmp |

All rows are reachable and def-clear; no infeasible GCD DU pair was identified.
