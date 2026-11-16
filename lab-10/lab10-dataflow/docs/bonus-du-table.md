# Bonus DU table

`T1`–`T3` are all-defs tests. `T4`–`T26` are targeted all-DU-pairs tests.
`K` marks a kill/redefinition before the listed use.

| Variable | Definition | Use / predicate | Kill | Test IDs | Feasibility |
|---|---|---|---|---|---|
| attendance | 31 | 32, 38 p-use | 32 validation | T4,T6,T7 | all feasible |
| threshold | 31 | 32, 50 p-use | 32 validation | T5,T13,T14 | all feasible |
| present | 35 | 40 c-use, 50 p-use | 40 K | T1,T2,T3 | feasible |
| present | 40 | 40 c-use, 50 p-use | 40 K | T10–T12 | feasible |
| streak | 36 | 41 c-use, 42 p-use | 41 K | T1,T2 | feasible |
| streak | 41 | 41 c-use, 42 p-use | 41/46 K | T22–T24 | feasible |
| streak | 46 | 41 c-use, 42 p-use | 41/46 K | T8,T9,T26 | feasible |
| best | 37 | 42 p-use, 53 p-use, 54 c-use | 43 K | T1,T15 | feasible |
| best | 43 | 42 p-use, 53 p-use | 43 K | T16,T17 | feasible |
| a | 38 | 39 p-use | next iteration K | T18,T19 | feasible |
| score | 49 | 51 c-use, 54 c-use, 56 c-use | 51/54 K | T3,T21,T25 | feasible |
| score | 51 | 54 c-use, 56 c-use | 54 K | T20,T25 | feasible |
| score | 54 | 56 c-use | return | T2,T20 | feasible |

## Infeasible pairs

No additional infeasible pair is claimed. A definition is considered covered
only when a path exists without crossing a redefinition of that variable.
The validation throw paths terminate before the later loop/body uses, so they
cannot be paired with uses after line 32; those pairs are infeasible for T4
and T5 by construction.
