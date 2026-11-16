# All-DU-pairs phase

The all-DU-pairs phase is separate from the three all-defs tests. The
additional tests use explicit IDs in `BonusTest` (`DUPairs-T4` through
`DUPairs-T26`) and `GcdTest` (`G1` through `G6`).

The tests cover predicate uses on both true and false edges, c-uses after
redefinitions, loop-carried definitions, validation throw paths, and empty
versus non-empty iteration paths. The complete pair tables are:

- [GCD DU table](gcd-du-table.md)
- [Bonus DU table](bonus-du-table.md)
