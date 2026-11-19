// Lab 03 — Алхам 6: threshold-оо зориуд эвдэх
// /report нь серверт 200-400 мс хүлээдэг тул p95<100 нь бараг найдвартай FAIL болдог.
// /cart/add нь localhost дээр 1-5 мс хариулдаг тул p95<50 нь ихэвчлэн PASS хэвээр үлддэг —
//   учир нь босго нь бодит хэмжигдэхүүнээс хол сул тохируулагдсан.
import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  vus: 20,
  duration: '30s',
  thresholds: {
    'http_req_duration{name:cart}':   ['p(95)<50'],   // localhost дээр PASS хэвээр байх магадлалтай
    'http_req_duration{name:report}': ['p(95)<100'],  // ЗААВАЛ FAIL — сервер 200 мс-ээс хурдан хариулдаггүй
    'http_req_failed{name:pay}':      ['rate<0.08'],  // Reliability SLO
    'checks':                         ['rate>0.90'],  // Availability SLO
  },
};

export default function () {
  const base = 'http://localhost:3000';
  const c = http.post(`${base}/cart/add`, null, { tags: { name: 'cart' } });
  const r = http.get(`${base}/report`,           { tags: { name: 'report' } });
  const p = http.post(`${base}/pay`, null,       { tags: { name: 'pay' } });

  check(c, { 'cart 200': (x) => x.status === 200 });
  check(r, { 'report 200': (x) => x.status === 200 });
  check(p, { 'pay 200': (x) => x.status === 200 });

  sleep(1);
}
