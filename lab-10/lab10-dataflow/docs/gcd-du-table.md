# GCD DU table

Source: `src/main/java/mn/edu/must/sqat/DataFlowTargets.java`.

For a use at a statement, only variables read on the right-hand side or in the predicate are uses. An assignment target is a definition, not a use.

| Variable | Def line | Use line | Kind | Test ID | Def-clear path |
|---|---:|---:|---|---|---|
| x | 13 | 16 | c-use | G2,G6 | entry x to modulo |
| x | 13 | 20 | c-use | G1 | loop skipped, return x |
| x | 17 | 16 | c-use | G3,G5 | x assigned from y, then used by next modulo |
| x | 17 | 20 | c-use | G4 | final x assignment to return |
| y | 13 | 15 | p-use T | G2 | entry y to loop predicate true |
| y | 13 | 15 | p-use F | G1 | entry y to loop predicate false |
| y | 13 | 16 | c-use | G2 | entry y to modulo |
| y | 13 | 17 | c-use | G2 | entry y read as RHS of x=y |
| y | 18 | 15 | p-use T | G3 | updated y to next loop predicate true |
| y | 18 | 15 | p-use F | G4 | updated y to loop exit predicate false |
| y | 18 | 16 | c-use | G3 | updated y to next modulo |
| y | 18 | 17 | c-use | G3 | updated y read as RHS of x=y |
| tmp | 16 | 18 | c-use | G2,G3 | modulo result to y=tmp |

There is no `x@13 -> use@17`: line 17 reads `y`, not `x`. There is no `y -> use@18`: line 18 reads `tmp` and defines `y`; its left-hand side is not a use. Similarly, `x@17 -> use@17` is not a DU pair because line 17 does not read x. All listed pairs are feasible and def-clear.
