// Verifies that every relative import in src/ resolves to a real file.
// Run:  node check-imports.mjs
import { readdirSync, readFileSync, statSync, existsSync } from "node:fs";
import { join, dirname, resolve } from "node:path";

const SRC = resolve("src");
const EXTENSIONS = ["", ".js", ".jsx", "/index.js", "/index.jsx"];
const IMPORT_PATTERN = /(?:from\s+|import\s+)["']([^"']+)["']/g;

function collectFiles(dir) {
  const files = [];
  for (const entry of readdirSync(dir)) {
    const full = join(dir, entry);
    if (statSync(full).isDirectory()) {
      files.push(...collectFiles(full));
    } else if (/\.(js|jsx)$/.test(entry)) {
      files.push(full);
    }
  }
  return files;
}

let missing = 0;
for (const file of collectFiles(SRC)) {
  const source = readFileSync(file, "utf8");
  for (const match of source.matchAll(IMPORT_PATTERN)) {
    const specifier = match[1];
    if (!specifier.startsWith("./") && !specifier.startsWith("../")) continue;
    const base = resolve(dirname(file), specifier);
    const found = EXTENSIONS.some((extension) => existsSync(base + extension));
    if (!found) {
      console.error(`MISSING: ${file} -> ${specifier}`);
      missing++;
    }
  }
}

if (missing === 0) {
  console.log("OK: all relative imports resolve. Safe to run npm run dev.");
} else {
  console.error(`FAILED: ${missing} missing import(s).`);
  process.exit(1);
}
