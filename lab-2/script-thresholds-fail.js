// Алхам 4 (FAIL): /slow endpoint дээр threshold-ыг зорилгоор хатуу болгож
// quality gate-ийн унах явдлыг харуулдаг эцсийн FAIL скрипт.
import http from 'k6/http';
import { sleep, check } from 'k6';

export const options = {
  vus: 30,
  duration: '1m',
  thresholds: {
    http_req_duration: ['p(95)<50'],   // САНААТАЙ хатуу — унах ёстой
    http_req_failed:   ['rate<0.01'],
  },
};

export default function () {
  const res = http.get('http://127.0.0.1:8080/slow');
  check(res, { 'status 200 байна': (r) => r.status === 200 });
  sleep(0.1);
}
