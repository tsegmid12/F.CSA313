# All-defs phase

The minimum all-defs set for `bonus()` is three tests:

| ID | Test | Definitions reached |
|---|---|---|
| T1 | `allDefs_T1_allZero` | `present@35`, `streak@36/@46`, `best@37`, `a@38`, `score@49/@51` |
| T2 | `allDefs_T2_longStreak` | `present@40`, `streak@41`, `best@43`, `score@54` |
| T3 | `allDefs_T3_noBonus` | `score@49` direct return path |

T1 supplies the reset definition, T2 supplies the updated streak/best and
streak bonus definitions, and T3 preserves the no-base-bonus path. Therefore
removing any one of the three leaves at least one definition without a
reachable use.
