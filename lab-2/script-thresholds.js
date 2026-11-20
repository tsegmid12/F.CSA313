// Алхам 4 (PASS): Локал /slow endpoint-ийн өөрийн baseline дээр SLO-г
// threshold болгон автомат шалгадаг эцсийн PASS скрипт.
import http from 'k6/http';
import { sleep, check } from 'k6';

export const options = {
  vus: 30,
  duration: '1m',
  thresholds: {
    http_req_duration: ['p(95)<300'],
    http_req_failed: ['rate<0.01'],
  },
};

export default function () {
  const res = http.get('http://127.0.0.1:8080/slow');
  check(res, { 'status 200 байна': (r) => r.status === 200 });
  sleep(0.1);
}
