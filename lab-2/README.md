# Lab 2 — k6-аар гүйцэтгэлийн хэмжүүр

F.CSA313 — Программ хангамжийн чанарын баталгаа ба тест (2026)

Гүйцэтгэсэн: Э.Цэгмид  
Оюутны код: B232270107

## Орчин

```text
k6.exe v2.2.0 (commit/00a9a1b7f5, go1.26.5, windows/amd64)
```

`k6` скриптүүд нь зөвшөөрөгдсөн `https://test.k6.io` болон локал `http://127.0.0.1:8080/slow` endpoint рүү тест хийсэн.

## Скриптүүд

- `script.js` — анхны болон 5/30/100 VU ганцаарчилсан тест
- `stages.js` — 5 → 30 → 100 → 0 VU stage test
- `local-server.js` — 100ms хариу хугацаатай локал Node.js сервер
- `threshold-pass.js` — p95 < 300ms, error rate < 1% PASS хаалга
- `threshold-fail.js` — p95 < 50ms гэсэн санаатай FAIL хаалга

## 5/30/100 VU хэмжилт

| VU | p90 | p95 | Throughput | Error rate |
|---:|---:|---:|---:|---:|
| 5 | 241.43 ms | 246.34 ms | 7.459878 req/s | 0.00% |
| 30 | 245.42 ms | 252.33 ms | 43.7969 req/s | 0.00% |
| 100 | 244.84 ms | 251.83 ms | 146.213849 req/s | 0.00% |

Бүх гүйцэтгэлийн текст [results/run-05vu.txt](./results/run-05vu.txt), [results/run-30vu.txt](./results/run-30vu.txt), [results/run-100vu.txt](./results/run-100vu.txt) файлд хадгалагдсан бөгөөд хүснэгтийн тоо эдгээр summary-аас авсан. Хэсэг хугацааны network variation учраас 100 VU үр дүн 30 VU-тай логикоор эерэг чиглэлийн гадна эвдэрсэнүй шиг болсныг ажигласан.

Stages ажиллуулалтын бүтэн текст [results/stages.txt](./results/stages.txt), хүснэгтийн screenshot [screenshots/summary.png](./screenshots/summary.png) хадгалагдсан.

## Baseline ба SLO

Анхдагч 5 VU test-ийн p95 = 246.34 ms байсан. Локал серверийн 100ms sleep endpoint-д үндэслэн локал pass SLO-оо p95 < 300ms болгож сонгосон. [PASS үр дүн](./results/threshold-pass.txt)-д p95 115.55ms байсан тул PASS; [FAIL үр дүн](./results/threshold-fail.txt)-д p95 115.15ms байсан тул p95 < 50ms хаалга cross болж, тест FAIL гарсан.

## Дүгнэлт

Ачаалал 5 VU-аас 30 VU болсон ч p95 246.34ms-аас 252.33ms болж арай өссөн. 30 VU-аас 100 VU үед p95 251.83ms болж ижил өндөр, учир нь test.k6.io-ийн хариу хурд энэ туршилтанд стабил байлаа. Throughput 5 VU дахь 7.46 req/s-аас 30 VU дахь 43.80 req/s, 100 VU дахь 146.21 req/s болж мэдэгдэхүйц өссөн. Бүх VU түвшин дээр http_req_failed 0.00% байсан тул алдаа өсөөгүй. Stages test-ийн бүх түвшинд HTTP status 200, нийт throughput 44.54 req/s, нийт p95 254.41ms гарсан. Локал 100ms endpoint дээр 30 VU үед p95 115ms орчим, throughput 146.79 req/s болж PASS threshold бүтэн давлаа. FAIL threshold-ийг p95 < 50ms болгон тодорхойлсон учир локал hариун дунджаас өндөртэй тул quality gate сознтойгоор FAIL гаргасан. Энэ лабораторид throughput нэмэгдэх нь latencyг шууд бууруулдаггүй гэх ойлголт ижил test target дээр тодорхой болов. Capacity planning-д throughput, p95, error rate болон availability-г хамт авч үзэх шаардлагатай.

## Тест ажиллуулах

```bash
k6 run --vus 5 --duration 1m script.js | tee results/run-05vu.txt
k6 run --vus 30 --duration 1m script.js | tee results/run-30vu.txt
k6 run --vus 100 --duration 1m script.js | tee results/run-100vu.txt
k6 run stages.js | tee results/stages.txt
k6 run threshold-pass.js | tee results/threshold-pass.txt
k6 run threshold-fail.js | tee results/threshold-fail.txt
```

Локал тест эхлүүлэх:

```bash
node local-server.js
```

## Нэмэлт: AI дүгнэлт

AI-д k6 load test скрипт үүсгэх санааг асуусан тохиолдолд target URL-ыг заавал өөрөө шалгах ёстой. Энэ лабораторид AI-ий санал болгож болох random URL-ыг ашиглаагүй бөгөөд зөвхөн test.k6.io болон localhost endpoint рүү хязгаарласан. AI-ийн stages болон checks бүтээц нь үндсэндээ зөв боловч зөвхөн external URL-д тулгуурлаж threshold утга санамсаргүй санал болгох аюултай. Threshold-уудыг локал server-ийн бодит p95 дээр үндэслэж өөрчилсөн. AI нь кодны first draft хурдан бэлтгэдэг давуу талтай, харин ёс зүйн хязгаар, SLO утга болон гадаад сүлжээний орчноос хамааралтайг гараар батлах шаардлагатай.
