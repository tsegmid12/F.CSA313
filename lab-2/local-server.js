const http = require('http');

const port = 8080;

function sleep(milliseconds) {
  return new Promise((resolve) => setTimeout(resolve, milliseconds));
}

const server = http.createServer(async (request, response) => {
  if (request.url === '/health') {
    response.writeHead(200, { 'Content-Type': 'text/plain' });
    response.end('ok');
    return;
  }

  if (request.url === '/slow') {
    await sleep(100);
  }

  response.writeHead(200, { 'Content-Type': 'application/json' });
  response.end(JSON.stringify({
    ok: true,
    path: request.url,
  }));
});

server.listen(port, '127.0.0.1', () => {
  console.log(`Local k6 test server: http://127.0.0.1:${port}`);
});
