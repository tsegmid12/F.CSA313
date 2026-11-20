# Lab 10 - Data-flow testing

Student name: not provided  
Student code: not provided

## Environment

```text
java version "27" 2026-09-15
Java(TM) SE Runtime Environment (build 27+35-2325)
Java HotSpot(TM) 64-Bit Server VM (build 27+35-2325, mixed mode, sharing)

Apache Maven 3.10.0 (c43a36b8d67be7e0805a411bc0898af1a51f5472)
Maven home: C:\apache-maven-3.10.0
Java version: 27, vendor: Oracle Corporation, runtime: C:\Program Files\Java\jdk-27
Default locale: en_US, platform encoding: UTF-8, time zone: Asia/Ulaanbaatar
OS name: "windows 11", version: "10.0", arch: "amd64", family: "windows"
```

The source under test is intentionally unchanged. Tests use JUnit 5 and JaCoCo.

## A. gcd DU analysis

Definitions and uses are numbered using `DataFlowTargets.java` source lines.

| Variable | Def | Use | Kind | Def-clear path | Test | Covered |
|---|---:|---:|---|---|---|---|
| x | 13 | 16 | c-use | 13-15T-16 | `twoIterationsCoverInitialDefinitions` | yes |
| x | 13 | 20 | c-use | 13-15F-20 | `zeroSecondArgumentCoversDirectReturnPairs` | yes |
| y | 13 | 15 | p-use T | 13-15T | `twoIterationsCoverInitialDefinitions` | yes |
| y | 13 | 15 | p-use F | 13-15F | `zeroSecondArgumentCoversDirectReturnPairs` | yes |
| y | 13 | 16 | c-use | 13-15T-16 | `twoIterationsCoverInitialDefinitions` | yes |
| tmp | 16 | 15 | p-use T/F | 16-17-18-15 | `twoIterationsCoverInitialDefinitions` | yes |
| tmp | 16 | 18 | c-use | 16-18 | `twoIterationsCoverInitialDefinitions` | yes |
| x | 17 | 16 | c-use | 17-15T-16 | `twoIterationsCoverInitialDefinitions` | yes |
| x | 17 | 20 | c-use | 17-15F-20 | `twoIterationsCoverInitialDefinitions` | yes |
| y | 18 | 15 | p-use T/F | 18-15 | `twoIterationsCoverInitialDefinitions` | yes |
| y | 18 | 16 | c-use | 18-15T-16 | `twoIterationsCoverInitialDefinitions` | yes |

`gcd(7, 0)` is the only test that directly covers the initial `x` definition
to the return (`x13 -> x20`) without entering the loop. `gcd(12, 8)` performs
two iterations and is required for the loop-carried pairs. `gcd(0, 5)` also
checks the alternative remainder path.

## B. bonus analysis

The CFG is in [cfg-bonus.png](./cfg-bonus.png). The nodes include the source
line numbers; edge labels identify the corresponding test or loop condition.

### Definitions and uses

| Line | Definitions | Uses |
|---:|---|---|
| 32 | - | `attendance` p-use, `threshold` p-use |
| 35 | `present` | - |
| 36 | `streak` | - |
| 37 | `best` | - |
| 38 | `a` | `attendance` p-use (next-element/loop exhaustion) |
| 39 | - | `a` p-use |
| 41 | `present` | `present` c-use |
| 42 | `streak` | `streak` c-use |
| 43 | - | `streak` p-use |
| 44 | `best` | `streak` p-use and c-use |
| 46 | `streak` | - |
| 49 | `score` | - |
| 51 | - | `present` p-use, `threshold` p-use |
| 52 | `score` | - |
| 54 | - | `best` p-use |
| 55 | `score` | `score` c-use |
| 56 | - | `score` c-use |

### DU pairs

The following table groups identical loop-carried pairs; `T/F` means that
both feasible predicate outcomes are covered.

| Variable | Definition | Uses reached by a def-clear path | Killed definitions on paths | Test IDs |
|---|---:|---|---|---|
| attendance | parameter | 32 p-use; 38 loop p-use | none | `invalidInputs`, `emptyAttendance` |
| threshold | parameter | 32 p-use; 51 p-use | none | `invalidInputs`, `thresholdFalseAndLongStreakUseScore` |
| present | 35 | 41 c-use; 51 p-use | killed by line 41 before a later present update | `onePresentDay`, `thresholdFalseAndLongStreakUseScore` |
| present | 41 | later 41 c-use; 51 p-use | each line 41 definition kills the preceding `present` definition | `fivePresentDays`, `brokenStreak`, `thresholdFalseAndLongStreakUseScore` |
| streak | 36 | 42 c-use; 43/44 p-use | killed by line 46 on the first missed day | `onePresentDay` |
| streak | 42 | 42 c-use; 43/44 p-use | killed by line 46 before a later streak use on a missed day | `fivePresentDays`, `brokenStreak` |
| streak | 46 | 42 c-use; 43/44 p-use | killed by the next line 42 or line 46 definition | `brokenStreak` |
| best | 37 | 44 p-use/c-use; 54 p-use | killed by line 44 only when `streak > best` is true | `onePresentDay`, `fivePresentDays` |
| best | 44 | later 44 p-use/c-use; 54 p-use | killed by a later line 44 update | `fivePresentDays`, `brokenStreak` |
| score | 49 | 56 c-use, or 55 c-use when the best branch is true | killed by line 52 when `present >= threshold` is true | `emptyAttendance`, `thresholdFalseAndLongStreakUseScore` |
| score | 52 | 55 c-use or 56 c-use | killed by no later score definition before return | `fivePresentDays`, `thresholdFalseAndLongStreakUseScore` |
| a | 38 | 39 p-use | killed by the next loop assignment to `a` | every non-empty attendance test |

The table explicitly marks the kill at every redefinition. The `streak = 0`
definition at line 46 kills an earlier streak definition before any later
streak use on a missed day. Therefore an earlier `streak` definition followed
by a missed-day line 46 is not a DU pair. Similarly, line 52 kills the line
49 `score` definition on the threshold-true path, and each line 41 or 44
redefinition kills the preceding `present` or `best` definition. The
impossible predicate combinations are excluded when the source definition is
already killed or when a loop has no next element.

The minimum all-defs set is four tests in the recorded run: two gcd tests and
two bonus tests. For `bonus`, `emptyAttendance` reaches the initial
definitions and `score = 0`, while `onePresentDay` reaches the positive
definitions and `score = 5`. One execution cannot use both score definitions
because line 52 kills line 49 whenever the threshold predicate is true.

The full suite has 11 tests. `jacoco-alldefs.csv` records 11 covered and 5
missed branches for the four-test all-defs run. `jacoco-final.csv` records
16/16 branches and 23/24 lines; the one missed line is the implicit default
constructor at source line 7. Branch coverage is not DU coverage: a branch
can be taken while a particular definition-to-use path remains untested.

## C. Fault seeding

Each mutation was applied directly, tested, and removed before the final
verification. The result files contain the complete Maven output.

| Mutation | Result | Evidence |
|---|---|---|
| M1: remove `streak = streak + 1` | 3 failures; positive streak tests report expected 8/3 but actual 5/0 | [mvn-test-M1.txt](./results/mvn-test-M1.txt) |
| M2: replace `best = streak` with `best = present` | all 11 tests pass; the ordinary DU suite does not distinguish the values at the update point | [mvn-test-M2.txt](./results/mvn-test-M2.txt) |
| M2 killing test | `separatedPresentDaysDoNotEarnStreakBonus`: expected 5, was 8 | [mvn-test-M2-kill.txt](./results/mvn-test-M2-kill.txt) |
| M3: add unused `total` definition | all 11 tests pass | [mvn-test-M3.txt](./results/mvn-test-M3.txt) |

M2 survives the original DU suite because, on its existing inputs, `present`
and `streak` have the same value when the best update is reached. The added
killing input has five present days but a maximum streak of three, so the
wrong definition changes the result. M3 is a dead definition and cannot be
observed by a functional test; an IDE/compiler static-analysis inspection
should report `total` as unused, while `javac` itself need not warn.

Final evidence: [mvn-verify-final.txt](./results/mvn-verify-final.txt) and
[jacoco-final.csv](./results/jacoco-final.csv). The final run reports 11 tests,
zero failures, and a successful build.

## Conclusion

DU analysis identifies relationships between a particular definition and its
later uses, not only whether a predicate outcome was executed. This exposes
stale values and killed definitions that ordinary branch coverage can miss.
Branch coverage is cheaper to collect automatically, while DU analysis needs
manual CFG and path reasoning. The M2 experiment demonstrates that even a
complete-looking DU suite needs values that distinguish related variables.
The M3 experiment also shows why static analysis complements dynamic tests.
The most time-consuming step was enumerating loop-carried pairs and checking
which paths were killed. AI assistance was used to organize the test and
documentation skeleton; all paths, expected outputs, and Maven evidence were
checked against the source and local test runs.
