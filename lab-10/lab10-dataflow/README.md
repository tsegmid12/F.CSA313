# Lab 10 — Өгөгдлийн урсгалын DU хосууд

## Оюутны мэдээлэл

Нэр: Э.Цэгмид  
Код: B232270107  

## Орчны гаралт

```text
java version "27" 2026-09-15
Java(TM) SE Runtime Environment (build 27+35-2325)
Java HotSpot(TM) 64-Bit Server VM (build 27+35-2325, mixed mode, sharing)

Apache Maven 3.9.9
Java version: 27, vendor: Oracle Corporation
OS name: "Windows 11", arch: "amd64"
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
| Bonus all-DU-pairs | 23 | 0 failure, 0 error |
| Нийт | 32 | 0 failure, 0 error |

Бодит Maven гаралтыг [results/](C:/4A/F.CSA313/lab-10/lab10-dataflow/results/) дотор хадгалсан.

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

Дэлгэрэнгүй test-to-pair mapping нь [GcdTest.java](C:/4A/F.CSA313/lab-10/lab10-dataflow/src/test/java/mn/edu/must/sqat/GcdTest.java)-д байна.

## Bonus CFG ба DU

[cfg-bonus.png](C:/4A/F.CSA313/lab-10/lab10-dataflow/docs/cfg-bonus.png) нь
мөрийн дугаартай зангилаа, тестийн нэртэй ирмэгүүдийг харуулна.
Нөхцөлт use бүрийн true/false зам, kill тэмдэглэгээ, бүх DU хос болон
биелэшгүй хосын үндэслэлийг [bonus-du-table.md](C:/4A/F.CSA313/lab-10/lab10-dataflow/docs/bonus-du-table.md)-д
өгсөн.

## JaCoCo

Эцсийн тайлангийн зорилтот үзүүлэлт:

```text
BRANCH_MISSED = 0
LINE_MISSED = 1
```

`LINE_MISSED=1` нь далд default constructor бөгөөд зөвшөөрөгдөнө.

## Fault seeding

| Mutant | Өөрчлөлт | Үр дүн | Унагах тест |
|---|---|---|---|
| M1 | `a > 0`-ийг `a >= 0` болгосон | Killed | `DU_a_def38_use39_FALSE`, `allDefs_T1_allZero` |
| M2 | `best >= 5`-ийг `best > 5` болгосон | Killed | `allDefs_T2_longStreak`, `DU_score_def51_use54`, `DU_best_def43_use53_TRUE` |
| M3 | `int tmp;`-ийг `int tmp = 0;` болгосон | Survived | Анхны `tmp=0` definition нь line 16-д overwrite хийгддэг dead definition |

M2 нь `best=5` үед `+3` оноо өгөх ёстой гэсэн гурван assertion-аар баригдана.
M3-ийн нэмэгдсэн `tmp=0` definition нь line 16-ийн `tmp=x%y`-ээр
ашиглагдахаас өмнө overwrite хийгддэг тул dead definition бөгөөд observable
үр дүнд нөлөөлөхгүй.

Эцсийн `src/main` нь starter source-той ижил.
