import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'https://test.k6.io';

export const options = {
  vus: 5,
  duration: '30s',
};

export default function () {
  const res = http.get(BASE_URL);

  check(res, {
    'status 200 байна': (r) => r.status === 200,
  });

  sleep(1);
}
