# Лаборатори №9 — Код хамралт, JaCoCo

Нэр: Э.Цэгмид
Оюутны код: B232270107

Орчны гаралт:

```text
openjdk version "27.0.1.0.3" 2026-10-24
OpenJDK Runtime Environment Homebrew (build 27.0.1.0.3)
OpenJDK 64-Bit Server VM Homebrew (build 27.0.1.0.3, mixed mode, sharing)

Apache Maven 3.9.9 (8e8579a9e76f7d015ee5ec7bfcdc97d260186937)
Maven home: /opt/homebrew/Cellar/maven/3.9.9/libexec
Java version: 27.0.1.0.3, vendor: Homebrew, runtime: /opt/homebrew/Cellar/openjdk/27.0.1.0.3/libexec/openjdk.jdk/Contents/Home
Default locale: en_MN, platform encoding: UTF-8
OS name: "mac os x", version: "26.6.2", arch: "aarch64", family: "mac"
```

## A — Happy path ба эхний хэмжилт

5 хэвийн урсгалын тест нэмсэн. `results/jacoco-A.csv`-ийн бодит хэмжилт:

| Үзүүлэлт     | Хамрагдсан | Нийт |  Хувь |
| ------------ | ---------: | ---: | ----: |
| Instructions |        103 |  152 | 67.8% |
| Branches     |         20 |   41 | 48.8% |
| Lines        |         16 |   25 | 64.0% |

Шар ромбо нь `letterGrade()`-ийн дүнгийн сонголтууд, `gpa()`-ийн switch болон `scholarship()`-ийн нөхцлүүдийн зарим зам дутуу байсныг харуулсан. Улаан мөрүүд нь exception зам, зөрчилтэй оюутны зам, доод босгоноос бага голч зэрэг тестлэгдээгүй замууд байсан.

## B — CFG ба 100% хамралт

`cfg.png` нь `scholarship()`-ийн шийдвэрийн зангилаа, ирмэг бүр дээр тухайн ирмэгийг дайрдаг бодит тестийн методын нэрийг харуулна. 20 тест ажилласны дараа `results/jacoco-B.csv` дээр `BRANCH_MISSED=0`, `LINE_MISSED=0` болсон.

## C — Давталтын зааг

Дараах 3 тестийг нэмсэн:

- `gpaLoopBoundaryZeroItemsThrows()` — 0 элемент, `assertThrows`
- `gpaLoopBoundaryOneItem()` — 1 элемент
- `gpaLoopBoundaryTwoOrMoreItems()` — 2+ элемент

Эдгээр тест нь хамралтын хувийг заавал өсгөхгүй, учир нь давталтын замууд өмнөх тестүүдэд хамрагдсан байж болно. Гэхдээ 0/1/2+ дүрэм нь давталтын хоосон оролт, ганц оролт, олон оролтын бодит зан төлөвийг тусад нь баталгаажуулдаг.

## D — 100% хамралттай боловч алдаа илэрсэн нь

Заагийн тестүүдээс `letterGradeBoundary60IsD()` унасан: `expected: <D> but was: <F>`. Starter код `score > 60` гэж бичсэн байсан тул спецификацийн дагуу 60 оноо D байх ёстойг зөрчсөн. `mvn-test-D-fail.txt` файлд уналтын бүтэн гаралт хадгалагдсан.

Алдааг `score >= 60` болгон зассан. Эцсийн байдлаар 30 тест бүгд ногоон, `results/jacoco-final.csv` дээр branch болон line missed хоёулаа 0 байна.

Branch хамралт 100% байсан ч алдаа үлдсэн, учир нь `score > 60` ба `score >= 60` нь ижил хоёр салаа үүсгэдэг. Хамралт нь кодын салаа, мөр ажилласныг хэмждэг боловч заагийн утга зөв ангилагдсан эсэхийг өөрөө баталдаггүй. Тиймээс заагийн тест нь хамралтын тайланг нөхөж, спецификацийн oracle-ийг шалгадаг. Энэ лаборатори хамралт шаардлагатай боловч дангаараа хангалттай нотолгоо биш гэдгийг харууллаа.

## Нэмэлт даалгавар — Coverage gate ба GitHub Actions

`pom.xml`-д JaCoCo `check` goal нэмсэн. Энэ дүрэм нь branch хамралт 90%-иас доош орвол `mvn verify`-г fail болгоно. `.github/workflows/ci.yml` нь `main` branch руу push хийх болон pull request бүрд Ubuntu дээр Java 17 суулгаж, `mvn verify` ажиллуулна. Одоогийн 100% branch хамралт gate-ийг давж байна.
