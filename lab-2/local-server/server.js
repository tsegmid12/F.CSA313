// Алхам 5: Локал тест сервер (гуравдагч сангүй — зөвхөн Node-ийн built-in http)
// Ажиллуулах:  node local-server/server.js
//
// Endpoint-ууд:
//   GET /fast  — ямар ч ажилгүй, шууд хариулна (сүлжээний "шал" хэмжинэ)
//   GET /slow  — 100ms хиймэл саатал (DB дуудлага/гадаад API-г дуурайв, I/O-bound)
//   GET /cpu   — ~3ms CPU ажил (JSON render, шифрлэлт г.м.-ийг дуурайв, CPU-bound)
//
// Node нь НЭГ утсан (single-threaded) event loop дээр ажилладаг тул:
//   /slow — хүлээлт нь async, зэрэгцээ олон хүсэлт зэрэг хүлээж чадна → ачаалал
//           өсөхөд latency тогтвортой, throughput шугаман өснө.
//   /cpu  — CPU ажил event loop-ыг ЭЗЭЛНЭ, хүсэлтүүд дараалалд ордог → ачаалал
//           өсөхөд throughput тодорхой таазанд тулж, latency нь ДЭЛБЭРНЭ.
// Яг энэ ялгаа нь лекц 2-ын "throughput ба latency-ийн зөрчил"-ийн эх сурвалж.
const http = require("http");

const PORT = 3000;

// ~ms миллисекундын турш процессорыг завгүй барих (busy loop).
// setTimeout-оос ялгаатай нь энэ нь event loop-ыг БҮРЭН блоклоно.
function burnCpu(ms) {
  const end = Date.now() + ms;
  let x = 0;
  while (Date.now() < end) x += Math.sqrt(x + 1);
  return x;
}

function json(res, body) {
  const payload = JSON.stringify(body);
  res.writeHead(200, {
    "Content-Type": "application/json",
    "Content-Length": Buffer.byteLength(payload),
  });
  res.end(payload);
}

const server = http.createServer((req, res) => {
  const path = req.url.split("?")[0];

  if (path === "/fast") {
    return json(res, { ok: true, endpoint: "fast" });
  }

  if (path === "/slow") {
    // I/O-bound саатал: event loop-ыг блоклохгүй
    return setTimeout(
      () => json(res, { ok: true, endpoint: "slow", delayMs: 100 }),
      100,
    );
  }

  if (path === "/cpu") {
    // CPU-bound ажил: event loop-ыг блоклоно
    burnCpu(3);
    return json(res, { ok: true, endpoint: "cpu", cpuMs: 3 });
  }

  res.writeHead(404, { "Content-Type": "application/json" });
  res.end(
    JSON.stringify({ error: "not found", try: ["/fast", "/slow", "/cpu"] }),
  );
});

server.listen(PORT, "127.0.0.1", () => {
  console.log(`Test server listening on http://127.0.0.1:${PORT}`);
  console.log("Endpoints: /fast  /slow (100ms)  /cpu (~3ms CPU)");
});
