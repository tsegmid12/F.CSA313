# Lab 10 — Өгөгдлийн урсгалын DU хосууд

## Оюутны мэдээлэл

Нэр: Э.Цэгмид  
Код: B232270107  

## Орчны гаралт

```text
java version "27" 2026-09-15
Java(TM) SE Runtime Environment (build 27+35-2325)
Java HotSpot(TM) 64-Bit Server VM (build 27+35-2325, mixed mode, sharing)

Apache Maven 3.9.9 (8e8579a9e76f7d0155a411bc0898af1a51f5472)
Maven home: C:\Users\tsegm\AppData\Local\Temp\apache-maven-3.9.9
Java version: 27, vendor: Oracle Corporation, runtime: C:\Program Files\Java\jdk-27
Default locale: en_US, platform encoding: UTF-8
OS name: "Windows 11", version: "10.0", arch: "amd64", family: "windows"
```

## Тестийн үе шат

`BonusTest`-ийн эхний 3 тест нь all-defs үе шат бөгөөд бүх definition-д дор
хаяж нэг reachable use өгнө. Энэ нь minimum 3 тест юм: T1 нь reset болон
эхний defs, T2 нь урт streak ба `score=score+3`, T3 нь `score=0` замыг
хамарна. Үлдсэн тестүүд нь all-DU-pairs үе шатны зорилтот тестүүд.

| Үе шат | Тестийн тоо | Үр дүн |
|---|---:|---|
| GCD all-DU-pairs | 6 | 0 failure, 0 error |
| Bonus all-defs | 3 | 0 failure, 0 error |
| Bonus all-DU-pairs | 24 | 0 failure, 0 error |
| Нийт final | 33 | 0 failure, 0 error |

All-defs үеийн verify-д GCD-ийн 6, Bonus-ийн 3 буюу нийт 9 тест ажилласан.
Бодит Maven гаралтыг `results/` дотор хадгалсан.

## GCD DU-ийн бүрэн хүснэгт

Line number нь `DataFlowTargets.java`-ийн source line number. `T` нь тестийн
ID, зам нь definition-ээс use хүртэлх def-clear execution path юм.

| Variable | Definition | Use | Use type | T | Def-clear зам / утга |
|---|---:|---:|---|---|---|
| x | 13 | 16 | c-use | G2, G6 | entry `x` → loop body `x % y` |
| x | 13 | 17 | c-use | G2 | entry `x` → `x=y` |
| x | 13 | 20 | c-use | G1 | entry `y=0` → return |
| x | 17 | 16 | c-use | G3 | `x=y` → дараагийн `x % y` |
| x | 17 | 17 | c-use | G3, G5 | `x=y` → дараагийн `x=y` |
| x | 17 | 20 | c-use | G4 | `x=y` → loop exit → return |
| y | 13 | 15 | p-use T | G2 | entry `y` → loop condition true |
| y | 13 | 15 | p-use F | G1 | entry `y=0` → loop condition false |
| y | 13 | 16 | c-use | G2 | entry `y` → `x % y` |
| y | 13 | 18 | c-use | G2 | entry `y` → `y=tmp` |
| y | 18 | 15 | p-use T | G3 | updated `y` → next loop condition true |
| y | 18 | 15 | p-use F | G4 | updated `y=0` → exit |
| y | 18 | 16 | c-use | G3 | updated `y` → next `x % y` |
| y | 18 | 18 | c-use | G3 | updated `y` → next assignment |
| tmp | 16 | 18 | c-use | G2, G3 | `tmp=x%y` → `y=tmp` |

Эдгээр нь x, y, tmp-ийн бүх reachable def-clear хосууд; `GcdTest.java`
доторх G1–G6 тестийн нэр/тайлбар нь хүснэгтийн T баганатай таарна.

## Bonus def/use жагсаалт ба DU хүснэгт

| Variable | Definition | Use (kind) | Kill |
|---|---|---|---|
| attendance | 31 parameter | 32 p-use, 38 p-use | validation terminates invalid path |
| threshold | 31 parameter | 32 p-use, 50 p-use | validation terminates invalid path |
| present | 35 init, 40 increment | 40 c-use, 50 p-use, 54 c-use | line 40 |
| streak | 36 init, 41 increment, 46 reset | 41 c-use, 42 p-use | lines 41 and 46 |
| best | 37 init, 43 update | 42 p-use, 53 p-use | line 43 |
| a | 38 for-each definition | 39 p-use | next iteration definition |
| score | 49 init, 51 base, 54 streak bonus | 54 c-use, 56 c-use | lines 51 and 54 |

| Def | Use | Kind | Kill before use | Test IDs | Feasibility |
|---|---|---|---|---|---|
| attendance@31 | 32 | p-use T/F | validation | T4,T5 | reachable |
| attendance@31 | 38 | p-use T/F | none | T6,T7 | reachable |
| threshold@31 | 32 | p-use T/F | validation | T5 | reachable |
| threshold@31 | 50 | p-use T/F | none | T13,T14 | reachable |
| present@35 | 40,50 | c/p-use | 40 | T1,T2,T3 | reachable |
| present@40 | 40,50 | c/p-use | 40 | T10,T11,T12 | reachable |
| streak@36 | 41,42 | c/p-use | 41/46 | T1,T2 | reachable |
| streak@41 | 41,42 | c/p-use | 41/46 | T22,T23,T24 | reachable |
| streak@46 | 41,42 | c/p-use | 41/46 | T8,T9,T26 | reachable |
| best@37 | 42,53 | p-use | 43 | T1,T15 | reachable |
| best@43 | 42,53 | p-use | 43 | T16,T17 | reachable |
| a@38 | 39 | p-use T/F | next iteration | T18,T19 | reachable |
| score@49 | 54,56 | c-use | 51/54 | T3,T17,T21 | reachable |
| score@51 | 54,56 | c-use | 54 | T20,T25 | reachable |
| score@54 | 56 | c-use | return | T2,T17,T20 | reachable |

Validation throw paths cannot reach loop/body uses after line 32; those
cross-path combinations are infeasible because execution terminates.

## Bonus CFG ба DU

[cfg-bonus.png](docs/cfg-bonus.png) нь мөрийн дугаартай зангилаа, бодит
тестийн method name-тэй ирмэгүүдийг харуулна.
Нөхцөлт use бүрийн true/false зам, kill тэмдэглэгээ, бүх DU хос болон
биелэшгүй хосын үндэслэлийг [bonus-du-table.md](docs/bonus-du-table.md)-д
өгсөн.

## JaCoCo

JaCoCo plugin-ийн хувилбарыг Java 27-той нийцүүлэхээр **0.8.16** болгосон. Өмнөх `results/` доторх Maven log болон CSV файлууд JaCoCo 0.8.12 ашигласан хуучин run тул шинэ хувилбараар `mvn clean verify` дахин ажиллуулж, шинэ тайлангаар солих шаардлагатай. Доорх coverage тоонууд нь өмнөх хадгалсан CSV-ээс авсан бөгөөд шинэ run-оор баталгаажтал эцсийн үр дүн гэж үзэхгүй.

Эцсийн тайлангийн зорилтот үзүүлэлт:

```text
BRANCH_MISSED = 0
LINE_MISSED = 1
```

`LINE_MISSED=1` нь далд default constructor бөгөөд зөвшөөрөгдөнө.

Хуучин `jacoco-alldefs.csv`: `BRANCH_MISSED=3`, `BRANCH_COVERED=13`,
`LINE_MISSED=2`, `LINE_COVERED=22`. Хуучин `jacoco-final.csv`:
`BRANCH_MISSED=0`, `BRANCH_COVERED=16`, `LINE_MISSED=1`,
`LINE_COVERED=23`.

All-defs үед branch coverage бүрэн биш байж болно: all-defs нь definition
бүрээс дор хаяж нэг use рүү хүрэхийг шалгадаг, харин predicate-ийн бүх
true/false салааг заавал шаардахгүй. Эсрэгээр branch 100% болсон ч DU хос
дутуу байж болно; жишээ нь нэг if-ийн хоёр салааг ажиллуулсан ч loop-carried
`streak@41 -> streak@41` зэрэг хосыг олон давталтгүйгээр хамруулахгүй.

## Fault seeding

| Mutant | Өөрчлөлт | Үр дүн | Унагах тест |
|---|---|---|---|
| M1 | line 41-ийн `streak = streak + 1` устгасан | Killed, 4 failures | `DU_best_def43_use42`, `DU_best_def43_use53_TRUE`, `DU_score_def51_use54`, `allDefs_T2_longStreak` |
| M2 | `best = streak`-ийг `best = present` болгосон | Survived эхний suite | `mvn-test-M2.txt`; нэмэлт kill: `M2Kill_bestUpdateUsesStreak` |
| M3 | `int total = present + best` unused definition нэмсэн | Survived | Dead definition; `mvn-test-M3.txt` |

M2 нь анхны all-DU-pairs тестүүдэд амьд үлдсэн: `best` шинэчлэгдэх мөчид
зарим оролт дээр `present` ба `streak` ижил утгатай байдаг. Нэмэлт
`M2Kill_bestUpdateUsesStreak` тест нь `[1,1,0,1,1,1,1]` оролтоор зөв
хариу 5, M2-mutant-ийн хариу 8 болохыг шалгана. M3-ийн `total` definition
ямар ч use-д хүрдэггүй тул runtime тест унагахгүй; static analysis/IDE
unused-local warning үүнийг илрүүлэх ёстой.

Эцсийн `src/main` нь starter source-той ижил.


## AI туслахын ашиглалт

Энэ лабораторийн ажлыг боловсруулах, шалгах явцад AI туслахыг дараах
хязгаарлагдмал зорилгоор ашигласан:

- DU хүснэгт болон def-clear замын тайлбарыг шалгах, цэгцлэхэд туслуулсан.
- JUnit тестийн тайлбар болон README-ийн бүтэц, найруулгыг сайжруулахад ашигласан.
- Maven/JaCoCo-ийн log, CSV-г тайлбарлах, алдааны шалтгааныг оношлоход ашигласан.

AI-ийн санал болгосон үр дүнг шууд үнэн гэж үзээгүй. Эцсийн тестийн үр дүн,
coverage тоо болон эх кодын нийцлийг бодит эх файл, Maven гаралт,
JaCoCo тайлангаар баталгаажуулах шаардлагатай. AI нь тест ажиллуулсан
бодит орчныг орлохгүй.

## Дүгнэлт

Энэ лабораторийн ажлаар GCD болон bonus функцийн өгөгдлийн урсгалыг
шинжилж, all-defs болон all-DU-pairs шалгуурт зориулсан JUnit 5 тестүүдийг
боловсруулав. Definition бүрээс холбогдох use-д хүрэх зам, predicate-ийн
true/false салаа, давталтын үеийн loop-carried DU хос, мөн завсрын
definition-ээр kill болох замуудыг авч үзсэн.

Мутацийн туршилтаар тестийн багц зөвхөн мөр/салаа хамралт бус, логикийн
алдааг илрүүлэх чадвартай эсэхийг шалгасан. M1 болон нэмэлт M2 kill тест
буруу өөрчлөлтийг илрүүлсэн; M2-ийн анхны хувилбар болон dead definition
M3 нь нэмэлт тест, статик шинжилгээ шаардлагатайг харуулсан.

**Нотолгооны төлөв:** Өмнөх coverage тоонуудыг JaCoCo 0.8.16-аар дахин
үүсгэсэн үр дүн гэж үзэхгүй. Эцсийн submission хийхийн өмнө Maven-ийг
ажиллуулж шинэ log болон CSV-г хадгалах шаардлагатай.

```bash
mvn -version
mvn clean verify
```

All-DU-pairs үе шатны log ба CSV-г тухайн үеийн all-DU-pairs test suite-ээр
verify хийж дараах файлуудад хадгална:

- `results/mvn-verify-allpairs.txt`
- `results/jacoco-allpairs.csv`

Эдгээр файлыг хуучин log, өөр repository-ийн үр дүн, эсвэл гараар зохиосон
coverage тоогоор орлуулахгүй. Final бүх тестийг ажиллуулсан үр дүнг
`results/mvn-verify-final.txt` болон `results/jacoco-final.csv)-д
хадгалж, JaCoCo 0.8.16 ашигласныг log-оос баталгаажуулна.
