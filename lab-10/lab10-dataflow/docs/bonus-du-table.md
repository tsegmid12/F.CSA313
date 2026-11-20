# Bonus DU table

`T1`–`T2` are the minimum all-defs suite. `T4` onward are targeted all-DU-pairs tests; test method names in `BonusTest.java` are authoritative. Source line numbers refer to `DataFlowTargets.java`.

| Variable | Definition | Reachable use(s) | Kill / path condition | Test IDs |
|---|---|---|---|---|
| attendance | 31 parameter | 32 p-use T/F; 38 loop p-use T/F | null validation terminates; array iteration controls loop | T4,T5,T6,T7 |
| threshold | 31 parameter | 32 p-use T/F; 50 p-use T/F | negative validation terminates | T5,T6,T7,T13,T14 |
| present | 35 init | 40 c-use; 50 p-use | first positive day reads it at line 40; empty loop preserves it to line 50 | T1,T6 |
| present | 40 increment | 40 c-use; 50 p-use T/F | next positive-day increment kills previous def | T10,T11,T12 |
| streak | 36 init | 41 c-use | line 41 or 46 redefines streak before later reads | T1,T2 |
| streak | 41 increment | 41 c-use; 42 p-use T/F | next increment or reset kills previous def | T22,T23,T24 |
| streak | 46 reset | 41 c-use | next positive day reads reset value in `streak+1`, then line 41 redefines it | T8,T9,T26 |
| best | 37 init | 42 p-use T; 53 p-use F | first positive day always has streak 1 > initial best 0; if no positive day, best remains 0 and fails the threshold | T1,T2,T15,T26 |
| best | 43 update | 42 p-use T/F; 53 p-use T/F | later line 43 update kills prior def; reset can make a later streak no greater than best | T8,T16,T17,T24 |
| a | 38 foreach element | 39 p-use T/F | next iteration assigns new a | T18,T19 |
| score | 49 init | 54 c-use; 56 c-use | line 51 kills it when present >= threshold; line 54 kills it after adding streak bonus | T2,T17,T21 |
| score | 51 base score | 54 c-use; 56 c-use | line 54 kills it when best >= 5 | T20,T25 |
| score | 54 streak bonus | 56 c-use | returned without further redefinition | T1,T2,T17,T20 |

## Minimum all-defs argument

Two executions are necessary because `score@49` reaches a later use only when the base-score predicate at line 50 is false, while `score@51` exists only when that predicate is true. These mutually exclusive paths cannot be covered by one execution.

- **T1**: `[1,0,1,1,1,1,1]`, threshold `5`. It covers the initial and loop-carried definitions, reset/recovery, best update, base score, and streak bonus.
- **T2**: `[1,1,1,1,1]`, threshold `6`. It leaves `score@49` alive until line 54 and then uses `score@54` at return.

## Infeasible pairs and interpretation

- `best@37 -> use@42 FALSE` is infeasible: the first positive day sets streak to 1, which is always greater than the initial best 0; without a positive day, line 42 is not reached.
- `best@37 -> use@53 TRUE` is infeasible: if best remains at its initial value 0, the predicate is false; if it becomes at least 5, line 43 has redefined it first.
- `streak@36 -> use@42` is infeasible because line 41 redefines streak before line 42 is reached.
- `streak@46 -> use@42` is infeasible because line 41 reads the reset value and redefines streak before line 42.


- Line 17 is `x = y`: it uses `y` and defines `x`; it does not use the previous `x`.
- Line 18 is `y = tmp`: it uses `tmp` and defines `y`; it does not use the previous `y`.
- Validation throws at line 32 terminate execution, so parameter definitions on those invalid paths cannot reach loop/body uses.
- A definition cannot be paired with a later use if another definition of the same variable occurs first (for example, `score@49 -> use@56` is not def-clear on the path that passes line 51).

