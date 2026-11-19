// Алхам 5: Локал сервер рүү чиглэсэн тест
// Ажиллуулахаас өмнө: node local-server/server.js
//
// Endpoint-ыг орчны хувьсагчаар сонгоно:
//   k6 run -e ENDPOINT=slow --vus 5 --duration 30s script-local.js
//
// Энд sleep() БАЙХГҮЙ — зорилго нь бодит хэрэглэгчийг дуурайх биш,
// серверийг хамгийн их хүчин чадлаараа (saturation) ажиллуулж үзэх юм.
// sleep(1) байвал 100 VU нь секундэд дээд тал нь 100 хүсэлт л илгээх тул
// серверийн тааз хэзээ ч харагдахгүй.
import http from 'k6/http';
import { check } from 'k6';

const ENDPOINT = __ENV.ENDPOINT || 'fast';

export const options = { vus: 5, duration: '30s' };

export default function () {
  const res = http.get(`http://127.0.0.1:3000/${ENDPOINT}`);
  check(res, { 'status 200 байна': (r) => r.status === 200 });
}
