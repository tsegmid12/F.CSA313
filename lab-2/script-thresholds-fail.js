// Алхам 4 (FAIL): threshold-ыг САНААТАЙГААР хатуу болгож quality gate-ийг унагаах
//
// p(95)<50 нь биелэх боломжгүй: гадаад сайт руу зөвхөн TLS handshake л
// ~130ms орддог. CI pipeline дээр яг ийм зарчмаар quality gate ажиллана —
// threshold унавал k6 нь exit code 99-ээр гарах тул pipeline улаан болно.
import http from 'k6/http';
import { sleep, check } from 'k6';

export const options = {
  vus: 30,
  duration: '30s',
  thresholds: {
    http_req_duration: ['p(95)<50'],   // САНААТАЙ хатуу — унах ёстой
    http_req_failed:   ['rate<0.01'],
  },
};

export default function () {
  const res = http.get('https://test.k6.io');
  check(res, { 'status 200 байна': (r) => r.status === 200 });
  sleep(1);
}
