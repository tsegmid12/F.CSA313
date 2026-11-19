// Lab 03 — тестлэх локал API
// 3 endpoint, тус бүр өөр зан төлөвтэй (Алхам 1)
const express = require('express');
const app = express();
const sleep = (ms) => new Promise((r) => setTimeout(r, ms));

// 1) Хурдан endpoint — сагсанд нэмэх (~1-5 мс)
app.post('/cart/add', (req, res) => res.json({ ok: true, items: 1 }));

// 2) Удаан endpoint — тайлан (200-400 мс санамсаргүй)
app.get('/report', async (req, res) => {
  await sleep(200 + Math.random() * 200);
  res.json({ rows: 20000 });
});

// 3) Найдваргүй endpoint — төлбөр (~5% нь 500 алдаа)
app.post('/pay', (req, res) => {
  if (Math.random() < 0.05) return res.status(500).json({ error: 'gateway timeout' });
  res.json({ paid: true });
});

app.listen(3000, () => console.log('API: http://localhost:3000'));
