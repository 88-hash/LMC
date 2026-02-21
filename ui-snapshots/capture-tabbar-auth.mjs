import { chromium } from 'playwright';
import fs from 'fs';

const outDir = 'ui-snapshots';
if (!fs.existsSync(outDir)) fs.mkdirSync(outDir, { recursive: true });

const browser = await chromium.launch({ headless: true });
const context = await browser.newContext({ viewport: { width: 390, height: 844 } });
const page = await context.newPage();

await page.goto('http://127.0.0.1:5173/login', { waitUntil: 'domcontentloaded' });
await page.evaluate(() => {
  localStorage.setItem('token', 'dev-dummy-token');
});

const routes = [
  ['home', 'http://127.0.0.1:5173/'],
  ['cart', 'http://127.0.0.1:5173/cart'],
  ['order', 'http://127.0.0.1:5173/order'],
  ['profile', 'http://127.0.0.1:5173/profile']
];

for (const [name, url] of routes) {
  await page.goto(url, { waitUntil: 'domcontentloaded' });
  await page.waitForTimeout(2000);
  await page.screenshot({ path: `${outDir}/tabbar-${name}-auth-current.png`, fullPage: false });
}

await browser.close();
