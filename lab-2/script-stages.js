// Алхам 3: Ачааллыг шатлан өсгөх (ramp-up / peak / ramp-down)
// Энэ нь ачаалал өсөх ба буух үеийн ЕРӨНХИЙ зургийг харуулна.
// АНХААР: stages ашигласан нэг ажиллуулалт нь тестийн төгсгөлд НЭГТГЭСЭН
// ганц summary өгдөг тул 5/30/100 VU тус бүрийн p95-ыг САЛГАЖ өгөхгүй.
// README-гийн хүснэгтийн тоог script.js-ийн 3 тусдаа ажиллуулалтаас авна.
import http from 'k6/http';
import { sleep, check } from 'k6';

export const options = {
  stages: [
    { duration: '30s', target: 5 },    // халаалт
    { duration: '1m',  target: 30 },   // өсгөлт
    { duration: '30s', target: 100 },  // оргил
    { duration: '30s', target: 0 },    // буулт
  ],
};

export default function () {
  const res = http.get('https://test.k6.io');
  check(res, { 'status 200 байна': (r) => r.status === 200 });
  sleep(1);
}
