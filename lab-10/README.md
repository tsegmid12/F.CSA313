# Lab 10 — Өгөгдлийн урсгалын DU хосууд (Data Flow Testing)

## Оюутны мэдээлэл

- **Нэр:** Э.Цэгмид  
- **Оюутны код:** B232270107  

## Орчны гаралт

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

---

## 1. Тестийн үе шатууд ба Тоон үр дүн

Энэхүү лабораторийн ажил нь All-defs болон All-DU-pairs гэсэн хоёр үндсэн шаттайгаар хийгдсэн бөгөөд тусдаа commit-уудаар баталгаажсан.

### Тестийн багцын хуваарилалт

| Үе шат | Тестийн анги | Тестийн тоо | Амжилт | Алдаа / Failure |
|---|---|---:|---|---|
| GCD all-DU-pairs | `GcdTest` | 6 | 6 Passed | 0 failure, 0 error |
| Bonus all-defs | `BonusTest` (T1–T3) | 3 | 3 Passed | 0 failure, 0 error |
| Bonus all-DU-pairs | `BonusTest` (T4–T27) | 24 | 24 Passed | 0 failure, 0 error |
| **Нийт эцсийн багц** | `GcdTest` + `BonusTest` | **33** | **33 Passed** | **0 failure, 0 error** |

### JaCoCo Хамралтын хэмжилт (`results/` файлуудтай 1:1 таарна)

| Үе шат | Results файл | Instructions | Branches | Lines | Тайлбар |
|---|---|---|---|---|---|
| **All-defs** | `results/jacoco-alldefs.csv`<br>`results/mvn-verify-alldefs.txt` | 73 / 81 (90.1%)<br>`MISSED=8` | 13 / 16 (81.3%)<br>`MISSED=3` | 22 / 24 (91.7%)<br>`MISSED=2` | 9 тест (GCD: 6, Bonus: 3) ажилласан. Exception болон нөхцөлт зарим салбарууд хамрагдаагүй. |
| **All-DU-pairs (Final)** | `results/jacoco-final.csv`<br>`results/mvn-verify-final.txt` | 78 / 81 (96.3%)<br>`MISSED=3` | 16 / 16 (**100.0%**)<br>**`BRANCH_MISSED=0`** | 23 / 24 (95.8%)<br>**`LINE_MISSED=1`** | 33 тест бүгд ногоон. `LINE_MISSED=1` нь 7-р мөрийн далд default constructor. |

> Бүх бодит консол log болон тайлангууд [lab10-dataflow/results/](lab10-dataflow/results/) хавтсанд UTF-8 текст хэлбэрээр хадгалагдсан.

---

## 2. GCD DU-ийн бүрэн хүснэгт

`DataFlowTargets.gcd(int x, int y)` функцын хувьсагчид (`x`, `y`, `tmp`), эх кодын мөрийн дугаар, def-clear замууд:

| Хувьсагч | Def мөр | Use мөр | Хэрэглээний төрөл | Тестийн ID | Def-clear гүйцэтгэх зам |
|---|---:|---:|---|---|---|
| `x` | 13 | 16 | c-use (`x % y`) | G2, G6 | [13, 15(T), 16] |
| `x` | 13 | 20 | c-use (`return x`) | G1 | [13, 15(F), 20] |
| `x` | 17 | 16 | c-use (`x % y`) | G3 | [17, 18, 15(T), 16] (дараагийн давталт) |
| `x` | 17 | 20 | c-use (`return x`) | G2, G4 | [17, 18, 15(F), 20] (давталтаас гарах үед) |
| `y` | 13 | 15 | p-use (TRUE) | G2 | [13, 15(T)] (`y != 0`) |
| `y` | 13 | 15 | p-use (FALSE) | G1 | [13, 15(F)] (`y == 0`) |
| `y` | 13 | 16 | c-use (`x % y`) | G2 | [13, 15(T), 16] |
| `y` | 13 | 17 | c-use (`x = y`) | G2 | [13, 15(T), 16, 17] |
| `y` | 18 | 15 | p-use (TRUE) | G3 | [18, 15(T)] (2 дахь давталтын нөхцөл) |
| `y` | 18 | 15 | p-use (FALSE) | G4 | [18, 15(F)] (`tmp == 0` болоод гарах) |
| `y` | 18 | 16 | c-use (`x % y`) | G3 | [18, 15(T), 16] |
| `y` | 18 | 17 | c-use (`x = y`) | G3 | [18, 15(T), 16, 17] |
| `tmp` | 16 | 18 | c-use (`y = tmp`) | G2, G3 | [16, 17, 18] |

Дэлгэрэнгүйг [lab10-dataflow/docs/gcd-du-table.md](lab10-dataflow/docs/gcd-du-table.md) болон [lab10-dataflow/src/test/java/mn/edu/must/sqat/GcdTest.java](lab10-dataflow/src/test/java/mn/edu/must/sqat/GcdTest.java)-аас үзнэ үү.

---

## 3. Bonus CFG ба DU шинжилгээ

- **CFG диаграм:** [lab10-dataflow/docs/cfg-bonus.png](lab10-dataflow/docs/cfg-bonus.png) файлд `DataFlowTargets.bonus()` функцын удирдлагын урсгалын графикийг мөрийн дугаартай зангилаанууд, үнэн/худал салбар бүрийг хамрах тестийн нэр/ID-тай ирмэгүүд, давталтын буцах ирмэг (loop back-edge), дараалсан `if` шийдвэрүүд болон exception гаралтын хамт бүрэн зурсан.
- **DU хүснэгт:** [lab10-dataflow/docs/bonus-du-table.md](lab10-dataflow/docs/bonus-du-table.md) файлд 7 хувьсагчийн 35 хосыг c/p-use, kill тэмдэглэгээ, тестийн ID, биелэшгүй (infeasible) хосын үндэслэлийн хамт нэг бүрчлэн гаргасан.

### All-defs үе шатны тестийн тооны үндэслэл
`bonus()` функцын хувьд бүх definition-д (нийт 11 def цэг: `attendance@31`, `threshold@31`, `present@35,@40`, `streak@36,@41,@46`, `best@37,@43`, `a@38`, `score@49,@51,@54`) дор хаяж нэг reachable use өгөхөд **хамгийн багадаа 3 тест** шаардлагатай:
1. `T1` (`[0, 0, 0]`, thresh=0): Reset (`streak=0@46`, `score=5@51`) болон анхны def-үүдийг хамарна.
2. `T2` (`[1, 1, 1, 1, 1]`, thresh=3): `present=present+1@40`, `streak=streak+1@41`, `best=streak@43`, `score=score+3@54` гэсэн өсөх замуудыг хамарна.
3. `T3` (`[1, 1]`, thresh=5): Босго даваагүй тул `score=0@49`-ийн анхны утга шууд буцах замыг хамарна.

---

## 4. Fault Seeding (Мутацийн үр дүн)

`results/` доторх бодит тестийн гаралтуудтай (`mvn-test-M1.txt`, `mvn-test-M2.txt`, `mvn-test-M2-kill.txt`, `mvn-test-M3.txt`) 1:1 нийцнэ.

| Мутант | Байршил | Өөрчлөлт | Үр дүн | Унасан тестүүд (`results/` файлтай таарна) |
|---|---|---|---|---|
| **M1** | Мөр 41 | `streak = streak + 1` устгасан | **Killed** (4 failures) | `BonusTest.DU_best_def43_use42`<br>`BonusTest.DU_best_def43_use53_TRUE`<br>`BonusTest.DU_score_def51_use54`<br>`BonusTest.allDefs_T2_longStreak`<br>*(Файл: `results/mvn-test-M1.txt`)* |
| **M2** | Мөр 43 | `best = streak;` $\rightarrow$ `best = present;` | **Survived** (0 failure)<br>*(Унагах тестээр: **Killed**)* | Эхний 32 тестэд: 0 failure (Амьд үлдсэн — `results/mvn-test-M2.txt`)<br>Унагах тест нэмэхэд: `BonusTest.M2Kill_bestUpdateUsesStreak` (1 failure — `results/mvn-test-M2-kill.txt`)|
| **M3** | Мөр 14 | `int tmp;` $\rightarrow$ `int tmp = 0;` (эсвэл `int total` dead def) | **Survived** (0 failure) | *(Амьд үлдсэн — Dead definition, `results/mvn-test-M3.txt`)* |

### M2 болон M3-ийн дэлгэрэнгүй тайлбар

1. **M2 (Survived $\rightarrow$ Killed with targeted test):**
   - **Мутацийн өөрчлөлт:** `bonus()` функцын 43-р мөрөнд `best = streak;`-ийг `best = present;` болгож мутаци оруулсан.
   - **Амьд үлдсэн шалтгаан:** All-DU-pairs тестийн эхний 32 тестэд (T1–T26) streak тасарсан ч (0 орсны дараа дахин 1 орж) нийт `present >= 5` болсон оролт байгаагүй тул `best = present` болгосон ч `best >= 5` нөхцөлийг шалгах логикт ялгаа гаралгүй мутант амьд үлдсэн (`results/mvn-test-M2.txt`, Tests run: 32, Failures: 0).
   - **Унагах тест:** Мутантыг унагах зорилгоор `M2Kill_bestUpdateUsesStreak` тестийг нэмсэн: `attendance = {1, 1, 0, 1, 1, 1, 1}, threshold = 0`.
     - Зөв кодоор: нийт `present = 6`, харин хамгийн урт streak = 4 тул `best = 4 < 5` болж бонус оноо нэмэгдэхгүй `score = 5` буцна.
     - Мутант кодоор: `best = present = 6 >= 5` болж нэмэлт +3 оноо авснаар `score = 8` буцна.
     - Үр дүнд: `expected: <5> but was: <8>` алдаа зааж мутант баригдаж унасан (`results/mvn-test-M2-kill.txt`, Tests run: 33, Failures: 1).
2. **M3 (Survived — Dead definition):**
   - **Шалтгаан:** 14-р мөрөнд нэмэгдсэн анхны утга олголт / dead definition нь:
     - `y == 0` үед: давталт руу оролгүй шууд 20-р мөрөнд `return x` хийгддэг тул хувьсагч огт уншигдахгүй.
     - `y != 0` үед: давталт руу ормогц хамгийн эхний үйлдэл нь 16-р мөрийн `tmp = x % y;` тул өмнөх тодорхойлолт ямар нэгэн хэрэглээнд очихоос өмнө шууд дарагдаж kill хийгддэг.
   - Иймд энэ тодорхойлолт нь ямар ч def-clear замгүй **Dead Definition** буюу ашиглагдахгүй тодорхойлолт бөгөөд програмын гадаад зан төлөвт огт нөлөөлөхгүй, семантикийн хувьд эквивалент тул ямар ч тестээр унах боломжгүй юм.

> **Анхааруулга:** Шалгалтын дараа эцсийн `src/main` код нь анхны starter кодтой 100% ижил, өөрчлөлтгүй хадгалагдсан.

---

## 5. Төслийг ажиллуулах ба шалгах заавар

Багш төслийг clone хийгээд дараах тушаалаар шалгана:

```bash
mvn clean verify
```

Эсвэл шат тус бүрээр шалгах:

```bash
# All-defs үе шат
mvn clean verify -Dtest="GcdTest,BonusTest#allDefs*"

# All-DU-pairs эцсийн бүрэн шат
mvn clean verify
```
