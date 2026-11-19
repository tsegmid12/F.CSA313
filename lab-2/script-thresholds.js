// Алхам 4 (PASS): SLO-г k6 threshold болгон кодоор шалгуулах
//
// SLO-г ЗААВРЫН жишээ тооноос биш, ӨӨРИЙН baseline хэмжилтээс гаргасан:
//   Алхам 2-ын baseline (5 VU / 30s, results/run-baseline-05vu-30s.txt):
//     p(95) = 234.57ms
//   SLO = baseline p95 × 1.5 ≈ 352ms → 350ms гэж бөөрөнхийлсөн.
// Яагаад 1.5 дахин? Ачаалал өсөхөд 50% хүртэлх латентын доройтлыг
// "хэвийн" гэж үзэж, түүнээс хэтэрвэл л асуудал гэж тооцох гэсэн санаа.
// (Зааврын p(95)<300 нь энд хэзээ ч биелэхгүй: гадаад сайт руу зөвхөн
//  TLS handshake л ~130ms, med нь аль хэдийн ~215ms байна.)
import http from 'k6/http';
import { sleep, check } from 'k6';

export const options = {
  // АНХААР: stages-ээ устгасан. stages + vus/duration хамт байвал
  // stages давамгайлж, vus/duration чимээгүй үл тоогдоно.
  vus: 30,
  duration: '1m',
  thresholds: {
    http_req_duration: ['p(95)<350'],  // SLO: p95 < 350ms (baseline 234.57 × 1.5)
    http_req_failed:   ['rate<0.01'],  // SLO: error rate < 1% (availability ≥ 99%)
  },
};

export default function () {
  const res = http.get('https://test.k6.io');
  check(res, { 'status 200 байна': (r) => r.status === 200 });
  sleep(1);
}
