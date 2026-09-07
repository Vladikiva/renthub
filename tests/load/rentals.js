import encoding from 'k6/encoding';
import http from 'k6/http';
import { check, group, sleep } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const ADMIN = `${__ENV.ADMIN_USERNAME || 'admin'}:${__ENV.ADMIN_PASSWORD || 'admin'}`;

export const options = {
  scenarios: {
    browsing: {
      executor: 'ramping-vus',
      startVUs: 1,
      stages: [
        { duration: '30s', target: 20 },
        { duration: '1m', target: 20 },
        { duration: '30s', target: 0 },
      ],
    },
  },
  // Mirrors the SLOs documented in docs/slo.md.
  thresholds: {
    http_req_failed: ['rate<0.01'],
    'http_req_duration{expected_response:true}': ['p(99)<500'],
  },
};

export default function () {
  group('public', () => {
    const res = http.get(`${BASE_URL}/`);
    check(res, { 'index is 200': (r) => r.status === 200 });
  });

  group('admin api', () => {
    const res = http.get(`${BASE_URL}/admin/running-rentals`, {
      headers: { Authorization: `Basic ${encoding.b64encode(ADMIN)}` },
    });
    check(res, { 'running rentals is 200': (r) => r.status === 200 });
  });

  sleep(1);
}
