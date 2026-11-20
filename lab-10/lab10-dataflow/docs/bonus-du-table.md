# Bonus DU table

`T1`–`T2` are the minimum all-defs tests. `T4` onward are targeted all-DU-pairs tests. Source line numbers refer to `DataFlowTargets.java`; a predicate pair is listed separately for its true and false edge.

| Variable | Definition | Reachable use(s) | Kill / path condition | Test IDs |
|---|---|---|---|---|
| attendance | 31 parameter | 32 p-use T/F | null check at line 32; throw terminates invalid path | T4,T5 |
| attendance | 31 parameter | 38 loop p-use T/F | empty vs non-empty array | T6,T7 |
| threshold | 31 parameter | 32 p-use T/F | negative check; null attendance short-circuits threshold use | T5,T6,T7 |
| threshold | 31 parameter | 50 p-use T/F | threshold compared after loop | T13,T14 |
| present | 35 init | 40 c-use | first positive attendance reads init before redefining | T1,T2,T10 |
| present | 35 init | 50 p-use T/F | empty loop preserves initial zero until threshold check | T6 |
| present | 40 increment | 40 c-use | next positive day reads previous count before redefining | T10 |
| present | 40 increment | 50 p-use T/F | final count compared with threshold | T11,T12 |
| streak | 36 init | 41 c-use | first positive day reads initial streak | T1,T2,T22 |
| streak | 41 increment | 41 c-use | next consecutive positive day reads previous streak | T1,T2,T22 |
| streak | 41 increment | 42 p-use T/F | streak compared with best | T23,T24 |
| streak | 46 reset | 41 c-use | next positive day reads reset value in `streak+1`, then redefines it | T8,T9,T26 |
| best | 37 init | 42 p-use T | first positive day has streak 1 > initial best 0 | T1,T2,T26 |
| best | 37 init | 53 p-use F | if no positive attendance occurs, best remains 0 | T15 |
| best | 43 update | 42 p-use T/F | later reset can make a new streak no greater than saved best | T16,T24 |
| best | 43 update | 53 p-use T/F | best may be below 5 after reset or at least 5 after long streak | T8,T17 |
| a | 38 foreach element | 39 p-use T/F | current element is tested for positive/zero | T18,T19 |
| score | 49 init | 54 c-use | base score branch skipped, streak bonus branch taken | T2,T17 |
| score | 49 init | 56 c-use | both score-changing branches skipped | T21 |
| score | 51 base score | 54 c-use | streak bonus adds 3 to base score | T20 |
| score | 51 base score | 56 c-use | streak bonus branch skipped | T25 |
| score | 54 streak bonus | 56 c-use | final score returned without another definition | T1,T2,T17,T20 |

## Minimum all-defs argument

Two executions are necessary because `score@49` reaches a later use only when the base-score predicate at line 50 is false, while `score@51` exists only when that predicate is true. These mutually exclusive paths cannot be covered in one execution.

- **T1**: `[1,0,1,1,1,1,1]`, threshold `5`. It reaches the initial and loop-carried definitions, reset/recovery, best update, base score, and streak bonus.
- **T2**: `[1,1,1,1,1]`, threshold `6`. It leaves `score@49` alive until line 54 and then uses `score@54` at return.

## Infeasible pairs and interpretation

- `best@37 -> use@42 FALSE` is infeasible: the first positive day sets streak to 1, which is always greater than the initial best 0; without a positive day, line 42 is not reached.
- `best@37 -> use@53 TRUE` is infeasible: if best remains at its initial value 0, the predicate is false; if it becomes at least 5, line 43 has redefined it first.
- `streak@36 -> use@42` is infeasible because line 41 redefines streak before line 42 is reached.
- `streak@46 -> use@42` is infeasible because line 41 reads the reset value and redefines streak before line 42.
- Validation throws at line 32 terminate execution, so parameter definitions on invalid paths cannot reach loop/body uses.
- A definition cannot be paired with a later use if another definition of the same variable occurs first.
