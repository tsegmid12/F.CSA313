# Лаборатори №2 — Гүйцэтгэлийн хэмжүүрийг k6-аар хэмжих

**Хичээл:** F.CSA313 — Программ хангамжийн чанарын баталгаа ба тест (2026)  
**Гүйцэтгэсэн:** Э.Цэгмид  
**Оюутны код:** B232270107  
**Хэрэгсэл:** Grafana k6 (AGPL v3, нээлттэй эх)  
**Тестлэх бай:** `https://test.k6.io` ба локал сервер `http://127.0.0.1:8080`

> **Ёс зүй:** Бүх load test зөвхөн k6-ийн албан ёсны дадлагын веб болон өөрийн локал сервер рүү хийгдсэн. Сургуулийн болон бусад бодит вебсайт руу хүсэлт илгээгээгүй.

---

## Туршилтын орчин

| Хэмжээ | Утга |
|---|---|
| k6 хувилбар | `k6.exe v2.2.0 (commit/00a9a1b7f5, go1.26.5, windows/amd64)` |
| Үйлдлийн систем | Windows, PowerShell |
| Node.js локал сервер | 100ms delay-тай `/slow` endpoint |
| Бүтэн гаралт | [results/k6-version.txt](./results/k6-version.txt) |

`k6 version` командын гаралтыг репозиторид хадгалсан.

---

## Алхам 2 — Локал `/slow` endpoint-ийн baseline

SLO болон PASS/FAIL-г нэг endpoint дээр харьцуулахын тулд анхны baseline-г локал `/slow` endpoint дээр 5 VU, 30 секундээр хэмжсэн.

```bash
k6 run script-baseline.js | tee results/run-baseline-05vu-30s.txt
```

| Хэмжүүр | Утга | Лекцийн ойлголт |
|---|---:|---|
| `http_req_duration` avg | 107.57 ms | дундаж хариу өгөх хугацаа |
| `http_req_duration` p(90) | 114.83 ms | 90%-ийн хүсэлтийн latency |
| **`http_req_duration` p(95)** | **116.42 ms** | **Энэ endpoint-ийн BASELINE — доорх SLO-г эндээс гаргав** |
| `http_reqs` | 140 (4.510799 req/s) | throughput |
| `http_req_failed` | 0.00% (0/140) | error rate |
| `checks_succeeded` | 100.00% (140/140) | бүх хүсэлт 200 status-д давсан |

Бүтэн гаралт: [results/run-baseline-05vu-30s.txt](./results/run-baseline-05vu-30s.txt).

---

## Алхам 3 — Ачаалал шатлан өсөх

Зааврын дагуу `stages`-гүй `script.js`-ыг 5, 30, 100 VU түвшин тус бүрд тусад нь нэг минут ажиллуулсан.

```bash
k6 run --vus 5   --duration 1m script.js | tee results/run-05vu.txt
k6 run --vus 30  --duration 1m script.js | tee results/run-30vu.txt
k6 run --vus 100 --duration 1m script.js | tee results/run-100vu.txt
```

| VU | p(90) | p(95) | avg | Throughput | Error rate | Гаралт |
|---:|---:|---:|---:|---:|---:|---|
| 5 | 241.43 ms | 246.34 ms | 157.36 ms | 7.459878 req/s | 0.00% (0/456) | [run-05vu.txt](./results/run-05vu.txt) |
| 30 | 245.42 ms | 252.33 ms | 166.27 ms | 43.7969 req/s | 0.00% (0/2684) | [run-30vu.txt](./results/run-30vu.txt) |
| 100 | 244.84 ms | 251.83 ms | 162.89 ms | 146.213849 req/s | 0.00% (0/9006) | [run-100vu.txt](./results/run-100vu.txt) |

### Нэгж хэрэглэгчийн туршлага муудаж эхэлсэн цэг

5-аас 30 VU болтол p95 нь 246.34 ms-аас 252.33 ms болж 6.0 ms өссөн. 30-аас 100 VU руу өсөхөд p95 нь 251.83 ms болж бараг хэвээрээ байсан. Энэ test target дээр 100 VU хүртэл тодорхой `зөрчил/ханалт` харагдаагүй: throughput 7.46-аас 146.21 req/s болж 19.6 дахин өссөн ч p95 буцаад 251.83 ms хүртэл стабил байлаа. Энэ туршилтад хамгийн тод доройтол нь latency биш throughput-тэй бараг шугаман өссөн явдал байсан.

### Stages туршилт

```bash
k6 run stages.js | tee results/stages.txt
```

Stages test `5 → 30 → 100 → 0 VU` хувилбарт ажиллав. Нэгдсэн summary нь p90=246.64 ms, p95=254.41 ms, throughput=44.543166 req/s, error rate=0.00% гарсан. [Бүтэн гаралт](./results/stages.txt) дээр 6722 хүсэлт бүртгэгдсэн. Уг нэгтгэсэн тоо нь халаалт, оргил, буулт гурвыг хамтаар дундажласан учраас дээрх 5/30/100 түвшний хүснэгтийг дуудаж хэлбэлзлийг үүнээс тооцохгүй.

Тестийн screenshot: [02-run-05vu.png](./screenshots/02-run-05vu.png), [03-run-30vu.png](./screenshots/03-run-30vu.png), [04-run-100vu.png](./screenshots/04-run-100vu.png), [05-stages.png](./screenshots/05-stages.png).

---

## Алхам 4 — Threshold, SLO ба quality gate

| SLO | Утга | Яагаад сонгосон |
|---|---|---|
| `http_req_duration: p(95)<300` | 300 ms | Эцсийн `/slow` baseline p95=116.42 ms тул 300 ms нь 183.58 ms-ын margin өгдөг |
| `http_req_failed: rate<0.01` | 1%-ээс бага | Алдааны түвшинг лекцийн availability ≥99%-тэй нийцүүлсэн |

Эцсийн SLO нь `test.k6.io`-ийн гадаад сүлжээний baseline-ээс биш `/slow` endpoint-ийн өөрийн 5 VU/30с baseline-аас гаргасан. Түүнчлэн PASS/FAIL туршилтыг мөн яг тэр `/slow` endpoint дээр ажиллуулсан.

### PASS

```bash
node local-server.js
k6 run script-thresholds.js | tee results/run-threshold-pass.txt
```

Эцсийн PASS скрипт нь `script-thresholds.js`, 30 VU, 1 минут, `/slow` endpoint дээр ажиллана:

```text
http_req_duration
✓ 'p(95)<300' p(95)=116.13ms
http_req_failed
✓ 'rate<0.01' rate=0.00%
```

Бүтэн гаралт: [results/run-threshold-pass.txt](./results/run-threshold-pass.txt). Exit code 0 байсан.

### FAIL

```bash
k6 run script-thresholds-fail.js 2>&1 | tee results/run-threshold-fail.txt
```

Санаатайгоор хурдан 100ms endpoint-д зориулж боломжгүй `<50ms` threshold сонгосон:

```text
http_req_duration
✗ 'p(95)<50' p(95)=115.32ms
http_req_failed
✓ 'rate<0.01' rate=0.00%
```

Бүтэн гаралт: [results/run-threshold-fail.txt](./results/run-threshold-fail.txt). k6 threshold cross болсон учраас exit code 99 буцсан бөгөөд CI quality gate үүнийг build/deploy-ээс зогсооход ашиглаж болно.

---

## Алхам 5 — Локал серверийн нэмэлт тест

Локал сервер `/slow` endpoint-д 100ms controlled delay өгдөг.

```bash
node local-server.js
k6 run script-thresholds.js | tee results/run-threshold-pass.txt
k6 run script-thresholds-fail.js | tee results/run-threshold-fail.txt
```

PASS run-д 30 VU үед p95=116.13 ms, throughput=145.203343 req/s, error rate=0.00% гарсан. Node серверийн 100ms delay тогтмол байснаар тогтвортой хариу гарсан.

FAIL run-д p95=115.32 ms, throughput=146.195226 req/s, error rate=0.00% гарсан ч `p(95)<50` threshold-аас давуулсан учраас quality gate FAIL болсон. Энэ нь функциональ тест pass байх ч performance SLO унаж болохыг харуулна.

---

## Дүгнэлт

Нийт дүгнэлтээр локал `/slow` endpoint-ийн 5 VU/30с baseline p95=116.42 ms гарсан. Ижил endpoint дээрх PASS run p95=116.13 ms болж 300 ms SLO-г давсан. FAIL run p95=115.32 ms, 50 ms threshold-аас өндөр учраас quality gate унав. Гадаад `test.k6.io`-ийн 5/30/100 VU p95 246.34/252.33/251.83 ms гарч, throughput 7.459878-аас 146.213849 req/s хүртэл өссөн ч энэ нь локал baseline/SLO-г тогтоох түүвэр биш. Stages run-ийн нэгтгэсэн p95 254.41 ms нь гадаад сайтын ерөнхий хариуг харуулахад ашиглана, харин эцсийн PASS/FAIL-г локал endpoint дээр шалгасан. Лекцийн latency ба throughput-г хамт хэмжих шаардлагатай гэсэн ойлголт батлагдсан. Error rate 0.00% байсан тул availability сайн боловч latency-н асуудлыг далдлуулж болно. Нийтдээ local server дээр p95, throughput, error rate-г хамт авч үзэх capacity planning илүү үнэн зөв юм.

---

## Нэмэлт: AI дүгнэлт

AI-д k6 load test скрипт үүсгэхийг асуухад төслийн гол бүтэц болох `options`, `http.get`, `check`, `sleep`, threshold болон stages хэсгийг хурдан бэлдсэн давуу талтай. Гэхдээ AI ихэвчлэн санамсаргүй target URL эсвэл үндэслэлгүй threshold утга санал болгох аюултай. Энэ лабораторид AI-ийн саналыг шууд ашиглаагүй; зөвхөн test.k6.io болон локал 127.0.0.1 endpoint-ээр хязгаарласан. Threshold утгыг дагаж шууд хуулалгүй `/slow` endpoint-ийн өөрийн 5 VU/30с baseline p95=116.42ms-д үндэслэн 300ms PASS threshold, 50ms FAIL threshold-ийг сонгосон. AI-ийн stages нь дөрвөн stage-аар ачааллыг өргөжүүлэх зориулалттай байсан боловч 5/30/100 VU-г тусад нь run хийх шаардлагатайг README-д тусгасан. AI-ийн скриптэд functional check болон threshold нь хоёр ялгаатай quality gate болохыг тусад нь тайлбарласан. Тиймээс AI нь эхний төсөл/санаа хурдан гаргаж өгөхөд тохиромжтой боловч target URL, SLO утга, ёс зүйн хязгаар болон CI exit code-г хүн шалгах ёстой.
