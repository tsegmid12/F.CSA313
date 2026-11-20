// Нэмэлт локал saturation тест — local-server/server.js (127.0.0.1:3000).
// Эхлээд серверийг тусдаа терминалд асаана: node local-server/server.js
//
// Жишээ:
//   k6 run -e ENDPOINT=slow --vus 5 --duration 30s script-local.js
//   k6 run -e ENDPOINT=cpu  --vus 100 --duration 30s script-local.js
//
// ENDPOINT нь fast (default), slow (100ms async delay), cpu (~3ms CPU work).
// sleep() байхгүй тул VU бүр хүсэлтээ аль болох хурдан давтана. Иймээс энэ нь
// бодит хэрэглэгчийн think-time загвар биш, saturation төрлийн нэмэлт тест юм.
// Энэ сервер 3000 порт ашигладаг; baseline/SLO сервер local-server.js-ийн 8080
// портоос тусдаа бөгөөд түүний үр дүнтэй шууд харьцуулж болохгүй.
import http from 'k6/http';
import { check } from 'k6';

const ENDPOINT = __ENV.ENDPOINT || 'fast';

export const options = { vus: 5, duration: '30s' };

export default function () {
  const res = http.get(`http://127.0.0.1:3000/${ENDPOINT}`);
  check(res, { 'status 200 байна': (r) => r.status === 200 });
}
