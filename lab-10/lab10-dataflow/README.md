# Lab 10 — Өгөгдлийн урсгалын шинжилгээ (DU pairs)

## Оюутны мэдээлэл

- Нэр: Цэгмид Энхболд (Tsegmid Enkhbold)
- Оюутны код: B232270107

## Төслийн бүтэц

- `src/main/java/mn/edu/must/sqat/DataFlowTargets.java` — лабораторийн starter код. **Шинжилж буй эх кодыг өөрчлөөгүй.**
- `src/test/java/mn/edu/must/sqat/GcdTest.java` — GCD функцийн all-DU-pairs тестүүд.
- `src/test/java/mn/edu/must/sqat/BonusTest.java` — bonus функцийн all-defs, all-DU-pairs болон mutation-kill тестүүд.
- `cfg-bonus.png` — bonus функцийн CFG (репозиторийн lab10-dataflow root).
- `docs/gcd-du-table.md`, `docs/bonus-du-table.md` — def-use, kill, def-clear path болон боломжгүй хосын тайлбар.
- `docs/bonus-def-use.md` — B2 мөр бүрийн бүрэн def/c-use/p-use жагсаалт.
- `results/` — CI-аар шинээр үүсгэсэн Maven log, JaCoCo CSV болон mutation туршилтын үр дүн.

## Орчны мэдээлэл

Локал орчинд ашигласан хувилбарууд:

```text
java version "27" 2026-09-15
Java(TM) SE Runtime Environment (build 27+35-2325)
Java HotSpot(TM) 64-Bit Server VM (build 27+35-2325, mixed mode, sharing)

Apache Maven 3.9.9 (8e8579a9e76f7d0155a411bc0898af1a51f5472)
Maven home: C:\\Users\\tsegm\\AppData\\Local\\Temp\\apache-maven-3.9.9
Java version: 27, vendor: Oracle Corporation, runtime: C:\\Program Files\\Java\\jdk-27
Default locale: en_US, platform encoding: UTF-8
OS name: "Windows 11", version: "10.0", arch: "amd64", family: "windows"
```

Java 27-ийн class-file major version 71-тэй JaCoCo 0.8.12 нийцэхгүй байсан тул `pom.xml` дахь JaCoCo plugin-ийг **0.8.16** болгосон. Шинэ нотолгоог GitHub Actions дээр JDK 17.0.21, Maven 3.9.15 ашиглан бодитоор үүсгэсэн. CI-ийн бүтэн гаралт `results/java-version.txt`, `results/maven-version.txt` файлд хадгалагдсан. Хуучин JaCoCo 0.8.12 log/CSV-г шинэ үр дүн гэж ашиглахгүй.

CI орчны бодит хувилбарын гаралт:

```text
openjdk version "17.0.21" 2026-10-20
OpenJDK Runtime Environment Temurin-17.0.21+9 (build 17.0.21+9)
OpenJDK 64-Bit Server VM Temurin-17.0.21+9 (build 17.0.21+9, mixed mode, sharing)

Apache Maven 3.9.15 (98b2cdbfdb5f1ac8781f537ea9acccaed7922349)
Maven home: /usr/share/apache-maven-3.9.15
Java version: 17.0.21, vendor: Eclipse Adoptium, runtime: /usr/lib/jvm/temurin-17-jdk-amd64
Default locale: en, platform encoding: UTF-8
OS name: "linux", version: "7.0.0-1019-azure", arch: "amd64", family: "unix"
```

## A. GCD — all-DU-pairs

GCD-ийн бүх reachable DU хосыг доор болон `docs/gcd-du-table.md`-д жагсаав. Assignment statement-ийн зүүн тал нь definition болохоос use биш гэдгийг тооцсон. Тухайлбал `x = y` мөрөнд `y` уншигдаж `x` тодорхойлогдоно; өмнөх `x`-ийн use үүсэхгүй. `y = tmp` мөрөнд `tmp` уншигдаж `y` тодорхойлогдоно; өмнөх `y`-ийн use үүсэхгүй. G1–G6 тестүүд loop skip, loop true/false, loop-carried definition болон return замуудыг хамарна.

## B. Bonus — def/use болон all-defs

Bonus функцийн бүх хувьсагчийн definition, c-use, p-use, kill болон def-clear path-ийг `docs/bonus-du-table.md`-д тайлбарласан. All-defs-ийн хамгийн бага багц **2 тест** байна:

| Тест | Оролт | Хүлээгдэж буй үр дүн | Гол хамрах definition |
|---|---|---:|---|
| T1 | `[1,0,1,1,1,1,1]`, threshold `5` | 8 | attendance/threshold parameters, present, streak, reset/recovery, best, foreach element, score@51, score@54 |
| T2 | `[1,1,1,1,1]`, threshold `6` | 3 | score@49 survives to use@54; score@54 reaches return |

Хоёр тест шаардлагатай шалтгаан нь `score@49`-ийн definition дараагийн use-д хүрэхийн тулд `present >= threshold` нөхцөл худал байх ёстой, харин `score@51` үүсэхийн тулд уг нөхцөл үнэн байх ёстой. Эдгээр зам нэг execution-д зэрэг биелэх боломжгүй. T1 нь streak reset-ийн дараа дараагийн эерэг өдөр `streak@46 -> use@41` хосыг мөн хамарна.

## B. Bonus — all-DU-pairs

`BonusTest.java` дахь `DU_*` тестүүд нь параметрийн validation, foreach loop-ийн true/false, present/streak-ийн давталтын үеийн use, best-ийн predicate, score-ийн салбаруудыг хамарна. `streak@46 -> use@42` нь боломжгүй хос: line 42 нь эерэг ирцийн салаанд line 41-ийн дахин definition-ийн дараа байрладаг. Иймээс reset-ийн definition зөвхөн дараагийн эерэг өдрийн `streak+1` илэрхийлэлд буюу line 41-д хүрнэ. Хүснэгтэд боломжгүй хосуудыг яагаад def-clear замгүйг тайлбарлан хассан.

## C. Fault seeding

| Mutant | Оруулсан алдаа | Хүлээгдэж буй ажиглалт |
|---|---|---|
| M1 | `streak = streak + 1`-ийг өөрчлөх | All-defs болон DU тестүүдийн нэг буюу хэд хэдэн assertion унана |
| M2 | `best = streak`-ийг `best = present` болгох | DU багцад амьд үлдэж болох ч `M2Kill_bestUpdateUsesStreak` тусгай тестээр унана |
| M3 | Ашиглагдахгүй `total` definition нэмэх | Runtime тестүүд амьд үлдэнэ; unused-local/static-analysis анхааруулгаар илрүүлэх ёстой |

Mutation log болон exit code-ууд `results/mvn-test-M1.txt`, `results/mvn-test-M2.txt`, `results/mvn-test-M2-kill.txt`, `results/mvn-test-M3.txt` болон `results/mutant-*-exit.txt` файлд байна. M3-ийн PMD тайлан `results/pmd-M3.txt`, `results/pmd-M3.xml` файлд хадгалагдсан.

| Туршилт | Бодит үр дүн | Дүгнэлт |
|---|---|---|
| M1 | 26 тест, 5 failure | Mutant killed; exit code 1 |
| M2 DU suite | 23 тест, 0 failure | Mutant survived; exit code 0 |
| M2 kill test | 1 тест, 1 failure (`expected 5, actual 8`) | Mutant killed; exit code 1 |
| M3 runtime suite | 26 тест, 0 failure | Runtime-аар survived; exit code 0 |
| M3 PMD | `UnusedAssignment`, variable `total`, line 49 | Static analysis-аар илэрсэн |

M3 нь dead definition тул runtime assertion түүнийг илрүүлэх боломжгүй; PMD-ийн `UnusedAssignment` дүрэм ашиглагдаагүй `total` хувьсагчийг бодитоор илрүүлсэн.

## Тестийн бодит үр дүн

2026-11-20-ны GitHub Actions run дээр JaCoCo 0.8.16 ашиглан дараах үр дүн гарсан:

| Үе шат | Тест | Failure | Error | Build |
|---|---:|---:|---:|---|
| Bonus all-defs minimum | 2 | 0 | 0 | SUCCESS |
| GCD + Bonus all-DU-pairs | 29 | 0 | 0 | SUCCESS |
| Final complete suite | 32 | 0 | 0 | SUCCESS |

| Coverage тайлан | Branch missed | Branch covered | Line missed | Line covered |
|---|---:|---:|---:|---:|
| All-defs | 5 | 11 | 7 | 17 |
| All-DU-pairs | 0 | 16 | 1 | 23 |
| Final | 0 | 16 | 1 | 23 |

Final suite-ийн branch coverage **16/16 = 100%**. Line coverage нь **23/24**; үлдсэн нэг missed line нь ашиглагддаггүй default constructor. Шинэ Maven log-ууд JaCoCo 0.8.16 ашигласан бөгөөд `BUILD SUCCESS`-ээр төгссөн.

## JaCoCo болон дахин давтагдах шалгалт

`pom.xml` дахь JaCoCo 0.8.16 нь coverage тайлан үүсгэнэ. GitHub Actions workflow `.github/workflows/lab10-dataflow.yml` нь:
1. all-defs-ийн 2 тестийг тусад нь ажиллуулж `mvn-verify-alldefs.txt`, `jacoco-alldefs.csv` үүсгэнэ;
2. GCD болон Bonus-ийн DU тестүүдийг тусад нь ажиллуулж `mvn-verify-allpairs.txt`, `jacoco-allpairs.csv` үүсгэнэ;
3. бүх тестийг `mvn clean verify`-ээр ажиллуулж `mvn-verify-final.txt`, `jacoco-final.csv` үүсгэнэ;
4. M1/M2/M3 mutation туршилтыг ажиллуулж тус бүрийн log, exit code хадгална; M3-ийн unused local definition-ийг PMD `UnusedAssignment` дүрмээр шалгана;
5. шинээр үүсгэсэн нотолгоог repository-ийн `results/` хавтаст commit хийнэ.

Final coverage тайланг JaCoCo CSV-ийн `BRANCH_MISSED`, `BRANCH_COVERED`, `LINE_MISSED`, `LINE_COVERED` баганаас уншина. Зорилтот үр дүн нь бүх branch хамрагдсан байх; далд default constructor-оос үүдэх ганц missed line зөвшөөрөгдөнө. Coverage тоог гараар таамаглан бичихгүй, зөвхөн шинээр ажилласан `results/jacoco-*.csv`-ээс авна.

Локал орчинд дахин шалгах команд:

```bash
mvn -version
mvn clean verify
```

## AI туслахын ашиглалт

Энэ лабораторийн ажлыг боловсруулахдаа AI туслахыг DU хүснэгтийн логик, def-clear path-ийн тайлбар, JUnit тестийн нэр/тайлбар, Maven болон JaCoCo-ийн алдааны оношилгоонд ашигласан. AI-ийн санал болгосон DU хосыг эх кодын бодит assignment болон predicate-тэй тулгаж шалгасан. AI-аас гарсан coverage тоо эсвэл тестийн үр дүнг бодит run-гүйгээр нотолгоо гэж үзээгүй. Эх кодын starter хувилбарыг хадгалж, тест болон баримтжуулалтын өөрчлөлтийг тусад нь хийсэн.

## Дүгнэлт

Энэ лабораторийн ажлаар GCD болон bonus функцийн өгөгдлийн урсгалын definition, c-use, p-use болон kill харилцааг шинжилсэн. GCD функцийн DU хосыг assignment-ийн зүүн талын definition ба баруун талын use-ийг ялган тооцож засварласан. Bonus функцийн all-defs шалгуурт хоёр execution хангалттай болохыг score-ийн хоёр mutually exclusive замаар үндэслэсэн. All-DU-pairs тестүүдэд loop-carried definition, reset/recovery, predicate-ийн true/false болон score-ийн салбаруудыг тусгасан. Fault seeding-ээр streak өсөлт болон best update-ийн алдааг илрүүлэх тест нэмсэн. Ашиглагдахгүй definition нь runtime тестээр заавал илрэхгүй тул static analysis тусдаа шаардлагатайг M3 жишээгээр харуулсан. JaCoCo-ийн хуучин хувилбарын compatibility асуудлыг plugin шинэчлэлт болон JDK 17 CI орчноор шийдэхээр тохируулсан. Эцсийн үнэлгээг зөвхөн шинээр үүссэн Maven log, JaCoCo CSV, mutation log-оор баталгаажуулна.


---

## Хавсралт A1. GCD-ийн бүрэн DU хүснэгт

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


---

## Хавсралт B2. Bonus мөр бүрийн Def/Use жагсаалт

# Bonus B2 — Def/Use жагсаалт

Энэ жагсаалт нь `DataFlowTargets.java` файлын бодит мөрийн дугаарыг ашиглав. Assignment-ийн RHS болон нөхцөлд уншигдсан хувьсагч use болно; LHS нь зөвхөн definition болно.

| Мөр | Кодын үйлдэл | Definition (def) | c-use | p-use |
|---:|---|---|---|---|
| 31 | `bonus(int[] attendance, int threshold)` | `attendance@31`, `threshold@31` (параметрийн эхний def) | — | — |
| 32 | `attendance == null || threshold < 0` | — | — | `attendance@31`, `threshold@31` (T/F; short-circuit нөхцөл) |
| 33 | `throw ...` | — | — | — |
| 35 | `int present = 0` | `present@35` | — | — |
| 36 | `int streak = 0` | `streak@36` | — | — |
| 37 | `int best = 0` | `best@37` | — | — |
| 38 | `for (int a : attendance)` | `a@38` (давталт бүрт дараагийн элементийн def) | — | `attendance@31` (дараагийн элемент байгаа эсэх T/F) |
| 39 | `if (a > 0)` | — | — | `a@38` (T/F) |
| 40 | `present = present + 1` | `present@40` | `present@35/@40` (өмнөх утга) | — |
| 41 | `streak = streak + 1` | `streak@41` | `streak@36/@41/@46` (өмнөх утга) | — |
| 42 | `if (streak > best)` | — | — | `streak@41`, `best@37/@43` (T/F) |
| 43 | `best = streak` | `best@43` | `streak@41` | — |
| 46 | `streak = 0` (else) | `streak@46` | — | — |
| 49 | `int score = 0` | `score@49` | — | — |
| 50 | `if (present >= threshold)` | — | — | `present@35/@40`, `threshold@31` (T/F) |
| 51 | `score = 5` | `score@51` | — | — |
| 53 | `if (best >= 5)` | — | — | `best@37/@43` (T/F) |
| 54 | `score = score + 3` | `score@54` | `score@49/@51` | — |
| 56 | `return score` | — | `score@49/@51/@54` | — |

## Kill / redefinition

- `present@40` нь өмнөх `present@35` эсвэл `present@40`-ийг дараагийн давталтын assignment дээр дахин тодорхойлно.
- `streak@41` нь өмнөх `streak@36`, `streak@41`, эсвэл `streak@46`-ийг дараагийн эерэг өдөр дахин тодорхойлно.
- **Else-ийн `streak@46` нь өмнөх `streak@36` эсвэл `streak@41` definition-ийг шууд kill хийнэ.** Харин `streak@46` өөрөө дараагийн эерэг өдрийн line 41-ийн RHS use-д хүрч чадна.
- `best@43` нь `best@37` эсвэл өмнөх `best@43`-ийг kill хийнэ.
- `score@51` нь `score@49`-ийг kill хийнэ; `score@54` нь `score@49` эсвэл `score@51`-ийг kill хийнэ. `score@54`-ийн RHS use нь тухайн мөрийн өөрийн шинэ def-ээс өмнө уншигдах тул DU хос үүсгэнэ.

## Def-clear ба биелэшгүй замын тайлбар

- Foreach-ийн `attendance@31 -> loop@38` p-use нь массив хоосон эсэхээс хамаарч T/F хоёр ирмэгтэй.
- `streak@46 -> use@42` хос боломжгүй: line 42 хүрэхийн өмнө эерэг өдөр line 41 дээр reset утгыг уншаад streak-ийг дахин тодорхойлно. Иймээс def-clear биш.
- `streak@36 -> use@42` мөн боломжгүй: line 42 хүрэхийн өмнө line 41 streak-ийг дахин тодорхойлно.
- `best@37 -> use@42` T боломжтой (эхний эерэг өдөр streak=1 > best=0); F боломжгүй, учир нь эхний эерэг өдөр заавал T болж best шинэчлэгдэнэ. Эерэг өдөр байхгүй бол line 42 огт хүрэхгүй.
- `best@37 -> use@53` T боломжгүй: best анхны 0 хэвээр бол нөхцөл F; 5 буюу түүнээс дээш болгохын тулд line 43 дээр дахин def хийнэ.
- Хүчингүй оролтын line 32 дахь throw нь функцийг дуусгадаг тул параметрийн def-үүд loop/body-ийн use-д хүрэхгүй.
- `score@49 -> use@54` зөвхөн line 50 F үед боломжтой; `score@51 -> use@54` зөвхөн line 50 T үед боломжтой. Эдгээр нь mutually exclusive замууд.


---

## Хавсралт B3. Bonus-ийн бүрэн DU хүснэгт, kill ба боломжгүй хос

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

