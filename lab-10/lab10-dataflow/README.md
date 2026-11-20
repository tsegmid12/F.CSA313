# Lab 10 — Өгөгдлийн урсгалын шинжилгээ (DU pairs)

## Оюутны мэдээлэл

- Нэр: Э.Цэгмид
- Оюутны код: B232270107

## Төслийн бүтэц

- `src/main/java/mn/edu/must/sqat/DataFlowTargets.java` — лабораторийн starter код. **Шинжилж буй эх кодыг өөрчлөөгүй.**
- `src/test/java/mn/edu/must/sqat/GcdTest.java` — GCD функцийн all-DU-pairs тестүүд.
- `src/test/java/mn/edu/must/sqat/BonusTest.java` — bonus функцийн all-defs, all-DU-pairs болон mutation-kill тестүүд.
- `docs/cfg-bonus.png` — bonus функцийн CFG.
- `docs/gcd-du-table.md`, `docs/bonus-du-table.md` — def-use, kill, def-clear path болон боломжгүй хосын тайлбар.
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

Java 27-ийн class-file major version 71-тэй JaCoCo 0.8.12 нийцэхгүй байсан тул `pom.xml` дахь JaCoCo plugin-ийг **0.8.16** болгосон. Нотолгооны шинэ run нь GitHub Actions дээр JDK 17 болон Maven ашиглан ажиллана; яг тухайн run-ийн бүтэн хувилбарын гаралт `results/java-version.txt`, `results/maven-version.txt` файлд хадгалагдана. Хуучин JaCoCo 0.8.12 log/CSV-г шинэ үр дүн гэж ашиглахгүй.

## A. GCD — all-DU-pairs

GCD-ийн бүх reachable DU хосыг `docs/gcd-du-table.md`-д жагсаав. Assignment statement-ийн зүүн тал нь definition болохоос use биш гэдгийг тооцсон. Тухайлбал `x = y` мөрөнд `y` уншигдаж `x` тодорхойлогдоно; өмнөх `x`-ийн use үүсэхгүй. `y = tmp` мөрөнд `tmp` уншигдаж `y` тодорхойлогдоно; өмнөх `y`-ийн use үүсэхгүй. G1–G6 тестүүд loop skip, loop true/false, loop-carried definition болон return замуудыг хамарна.

## B. Bonus — def/use болон all-defs

Bonus функцийн бүх хувьсагчийн definition, c-use, p-use, kill болон def-clear path-ийг `docs/bonus-du-table.md`-д тайлбарласан. All-defs-ийн хамгийн бага багц **2 тест** байна:

| Тест | Оролт | Хүлээгдэж буй үр дүн | Гол хамрах definition |
|---|---|---:|---|
| T1 | `[1,0,1,1,1,1,1]`, threshold `5` | 8 | initial/loop-carried present, streak, reset/recovery, best, score@51, score@54 |
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

Mutation log болон exit code-ууд `results/mvn-test-M1.txt`, `results/mvn-test-M2.txt`, `results/mvn-test-M2-kill.txt`, `results/mvn-test-M3.txt` болон `results/mutant-*-exit.txt` файлд байна. M3 нь dead definition тул runtime assertion түүнийг илрүүлэх боломжгүй; энэ нь static analysis-ийн зорилтот жишээ юм.

## JaCoCo болон дахин давтагдах шалгалт

`pom.xml` дахь JaCoCo 0.8.16 нь coverage тайлан үүсгэнэ. GitHub Actions workflow `.github/workflows/lab10-dataflow.yml` нь:
1. all-defs-ийн 2 тестийг тусад нь ажиллуулж `mvn-verify-alldefs.txt`, `jacoco-alldefs.csv` үүсгэнэ;
2. GCD болон Bonus-ийн DU тестүүдийг тусад нь ажиллуулж `mvn-verify-allpairs.txt`, `jacoco-allpairs.csv` үүсгэнэ;
3. бүх тестийг `mvn clean verify`-ээр ажиллуулж `mvn-verify-final.txt`, `jacoco-final.csv` үүсгэнэ;
4. M1/M2/M3 mutation туршилтыг ажиллуулж тус бүрийн log, exit code хадгална;
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
