// Алхам 2: Анхны тест — baseline хэмжилт
// Зорилго: http_req_duration (avg/p90/p95), http_reqs (throughput),
//          http_req_failed (error rate) хэмжүүрүүдийг унших.
//
// Энэ файлд stages БАЙХГҮЙ — Алхам 3-ын хүснэгтийн 3 мөрийг гаргахын тулд
// --vus / --duration туг (flag)-аар түвшин бүрийг ТУСАД НЬ ажиллуулна:
//   k6 run --vus 5   --duration 1m script.js
//   k6 run --vus 30  --duration 1m script.js
//   k6 run --vus 100 --duration 1m script.js
import http from 'k6/http';
import { sleep, check } from 'k6';

// Тугаар дарж бичихгүй бол ашиглагдах анхны утга (Алхам 2-ын 5 VU / 30s)
export const options = { vus: 5, duration: '30s' };

export default function () {
  // Зөвшөөрөгдсөн бай: k6-ийн албан ёсны дадлагын сайт
  const res = http.get('https://test.k6.io');

  // check нь хүсэлт бүрийн хариуг шалгана (unit test-ийн assert-тэй адил).
  // check унавал тест FAIL болохгүй — зөвхөн checks хэмжүүр буурна.
  check(res, { 'status 200 байна': (r) => r.status === 200 });

  // Бодит хэрэглэгчийн "бодох хугацаа" (think time).
  // Үүнээс болж 1 VU секундэд ойролцоогоор 1 хүсэлт л илгээнэ.
  sleep(1);
}
