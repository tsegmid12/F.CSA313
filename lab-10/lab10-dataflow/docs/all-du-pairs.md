# All-DU-pairs phase

The all-DU-pairs phase is separate from the two-test all-defs minimum set. The targeted tests are `DU_*` methods in `BonusTest` (23 tests) and `G1`–`G6` in `GcdTest` (6 tests), for **29 tests total**.

They cover predicate uses on true and false edges, c-uses after redefinitions, loop-carried definitions, validation throw paths, and empty versus non-empty iteration paths. The complete pair tables and infeasible-pair explanations are:

- [GCD DU table](gcd-du-table.md)
- [Bonus DU table](bonus-du-table.md)

The separate mutation regression test `M2Kill_bestUpdateUsesStreak` is not counted as a DU-pairs test; it is included in the final complete suite.
