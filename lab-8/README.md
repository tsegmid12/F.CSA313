# Lab 8: Mock объектууд — Mockito-гоор хамаарлыг орлуулж тестлэх

## Оюутны мэдээлэл

**Нэр:** Э.Цэгмид
**Код:** B232270107

### Орчны хувилбарууд

```
java version "27" 2026-09-15
Java(TM) SE Runtime Environment (build 27+35-2325)
Java HotSpot(TM) 64-Bit Server VM (build 27+35-2325, mixed mode, sharing)

Apache Maven 3.10.0 (c43a36b8d67be7e0805a411bc0898af1a51f5472)
Java version: 27, vendor: Oracle Corporation
OS name: "windows 11", version: "10.0", arch: "amd64"
```

---

## Хэсэг A — Тестийн тоо

`results/mvn-test.txt`-ийн сүүлийн мөрөөр: **Tests run: 10, Failures: 0, Errors: 0, Skipped: 0**

| # | Тест | Хэрэгсэл |
|---|------|----------|
| A1 | `register_activeStudentNoCoursePrerequisites_returnsOkAndSavesAndSendsEmail` | `when` + `verify` |
| A2 | `register_studentNotFound_returnsErrorNoStudentAndNeverSavesOrEmails` | `verify(..., never())` |
| A3 | `register_inactiveStudent_returnsErrorInactiveAndNoInteractionWithEmail` | `verifyNoInteractions` |
| A4 | `register_prerequisitesMissing_returnsErrorPrerequisitesAndNeverSaves` | `when` + `never` |
| A5 | `register_emailSenderThrows_returnsOkAndRegistrationStillSaved` | `doThrow(...).when(...)` |
| A6 | `register_nullInputs_throwsIllegalArgumentExceptionAndNoRepoInteraction` | `assertThrows` |
| A7 | `register_successfulRegistration_emailBodyContainsCourseId` | `ArgumentCaptor` |
| A8 | `register_errorCases_returnsExpectedError` (3 параметр) | `@ParameterizedTest` + `@CsvSource` |

---

## A8 — `lenient()` тайлбар

`@ParameterizedTest`-ийн `@CsvSource` дахь мөр бүр өөр өөр stub шаардана: нэг мөр `findStudent("UNKNOWN")` → null, нөгөө нь `findStudent("S006")` → идэвхтэй оюутан гэх мэт. Mockito-гийн **strict stubs** горим нь ашиглагдаагүй stub-ийг `UnnecessaryStubbingException`-ээр алдаа өгч унагадаг. `lenient().when(...)` ашигласнаар тухайн тестийн мөрт хэрэглэгдэхгүй stub-ийг хатуу шалгахаас чөлөөлж, параметржүүлсэн тестийн бүх мөрийг нэг блоктод stub-дах боломжийг олгодог.

---

## Хэсэг B — Мутацийн тест

### Мутантуудын тайлбар

| Мутант | Эвдсэн зүйл | Унасан тестүүд | Файл |
|--------|-------------|----------------|------|
| **Mutant-1** | `if (!student.active())` мөрийг комментолсон (идэвхгүй оюутны шалгалт алга) | `register_inactiveStudent_returnsErrorInactiveAndNoInteractionWithEmail` (A3), `register_errorCases_returnsExpectedError[2]` (A8-ийн 2-р мөр) — **2 Failure** | `results/mvn-test-mutant-1.txt` |
| **Mutant-2** | `repository.saveRegistration(...)` дуудалтыг комментолсон (хадгалалт болохгүй) | `register_activeStudentNoCoursePrerequisites_returnsOkAndSavesAndSendsEmail` (A1), `register_emailSenderThrows_returnsOkAndRegistrationStillSaved` (A5) — **2 Failure** | `results/mvn-test-mutant-2.txt` |
| **Mutant-3** | `\|\| courseId == null` хэсгийг устгасан (зөвхөн studentId-г null шалгана) | `register_nullInputs_throwsIllegalArgumentExceptionAndNoRepoInteraction` (A6) — **1 Failure** | `results/mvn-test-mutant-3.txt` |

Мутант бүр `BUILD FAILURE` буцаасан — тестүүд нь бодит алдааг барьж байна.

---

## Дүгнэлт

Mock объект болон гараар бичсэн fake-ийн хоёр гол ялгаа нь автоматизаци ба верификаци юм: mock-ийг Mockito цаана нь үүсгэж, методын дуудалтыг хялбархан баталгаажуулах (verify, never) боломжтой болгодог бол fake бол бизнес логикийг дуурайлгахын тулд гараар класс бичих хэрэгтэй. Mock хэзээ хэрэглэх ёсгүй вэ гэвэл тестлэгдэж буй өөрийн логикийг — тухайлбал `CourseRegistrationService`-ийг өөрийг нь mock хийх нь утгагүй, учир нь биз. логик нь mock дотор оршихгүй, гаднаас ирдэг хамаарлуудыг л орлуулдаг. `verify`-ийг хэтрүүлэн хэрэглэвэл тест нь хэрэгжүүлэлтийн дотоод дарааллаа шалгадаг болж хэврэг (brittle) болдог — жижиг рефакторинг хийхэд тест унах эрсдэл нэмэгддэг. Mockito-гийн strict stubs горим нь ашиглагдаагүй stub-ийг автоматаар илрүүлж, "үхсэн код" болох stub-ыг цэвэрлэхэд тусалдаг. Мутацийн тест нь тестийн "чанар"-ыг хэмждэг — тест үр дүн зөв байхаас гадна код эвдэгдэхэд мэдрэмжтэй байхыг баталгаажуулдаг. Энэ лабораторид тест 10 ширхэг байж байсан ч гурван мутант бүгдийг барьж чадсан нь тест хангалттай бүрхэлттэй болсныг харуулна.

---

## Репозиторийн бүтэц

```
lab08-mockito/
├── pom.xml
├── README.md
├── .gitignore
├── results/
│   ├── mvn-test.txt
│   ├── mvn-test-mutant-1.txt
│   ├── mvn-test-mutant-2.txt
│   └── mvn-test-mutant-3.txt
└── src/
    ├── main/java/mn/edu/must/sqat/
    │   ├── Student.java
    │   ├── StudentRepository.java
    │   ├── EmailSender.java
    │   └── CourseRegistrationService.java
    └── test/java/mn/edu/must/sqat/
        └── CourseRegistrationServiceTest.java
```
