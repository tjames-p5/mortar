/* Colour maths for brands resolved outside a browser (email, native apps):
   the opaque colour syntax a brand can resolve without one, converted to sRGB
   and written as #rrggbb, and color-mix(in oklab, …) as tokens.css uses it.
   tests/email.test.mjs checks the conversions against Chromium's. */

export type Rgb = [number, number, number];

const linear = (v: number) =>
  v <= 0.04045 ? v / 12.92 : ((v + 0.055) / 1.055) ** 2.4;
const gamma = (v: number) =>
  v <= 0.0031308 ? v * 12.92 : 1.055 * v ** (1 / 2.4) - 0.055;

function toOklab([r, g, b]: Rgb): Rgb {
  const [lr, lg, lb] = [r, g, b].map(linear);
  const l = Math.cbrt(
    0.4122214708 * lr + 0.5363325363 * lg + 0.0514459929 * lb,
  );
  const m = Math.cbrt(
    0.2119034982 * lr + 0.6806995451 * lg + 0.1073969566 * lb,
  );
  const s = Math.cbrt(
    0.0883024619 * lr + 0.2817188376 * lg + 0.6299787005 * lb,
  );
  return [
    0.2104542553 * l + 0.793617785 * m - 0.0040720468 * s,
    1.9779984951 * l - 2.428592205 * m + 0.4505937099 * s,
    0.0259040371 * l + 0.7827717662 * m - 0.808675766 * s,
  ];
}
function fromOklab([L, a, b]: Rgb): Rgb {
  const l = (L + 0.3963377774 * a + 0.2158037573 * b) ** 3;
  const m = (L - 0.1055613458 * a - 0.0638541728 * b) ** 3;
  const s = (L - 0.0894841775 * a - 1.291485548 * b) ** 3;
  return [
    4.0767416621 * l - 3.3077115913 * m + 0.2309699292 * s,
    -1.2684380046 * l + 2.6097574011 * m - 0.3413193965 * s,
    -0.0041960863 * l - 0.7034186147 * m + 1.707614701 * s,
  ].map(gamma) as Rgb;
}

/** sRGB channels in 0–1 as #rrggbb, clipped to the sRGB gamut. */
export function hex(rgb: Rgb): string {
  return `#${rgb
    .map((v) =>
      Math.round(Math.min(1, Math.max(0, v)) * 255)
        .toString(16)
        .padStart(2, "0"),
    )
    .join("")}`;
}

/** A number, or a percentage of `whole`. */
function amount(value: string, whole: number): number {
  const n = parseFloat(value);
  if (!/^[+-]?(?:\d+\.?\d*|\.\d+)%?$/.test(value) || Number.isNaN(n))
    throw new Error();
  return value.endsWith("%") ? (n / 100) * whole : n;
}
function hue(value: string): number {
  const m = /^([+-]?(?:\d+\.?\d*|\.\d+))(deg|turn|rad)?$/.exec(value);
  if (!m) throw new Error();
  const n = parseFloat(m[1]);
  return m[2] === "turn" ? n * 360 : m[2] === "rad" ? (n * 180) / Math.PI : n;
}

function hslToRgb(h: number, s: number, l: number): Rgb {
  const k = (n: number) => (n + h / 30) % 12;
  const a = s * Math.min(l, 1 - l);
  return [0, 8, 4].map(
    (n) => l - a * Math.max(-1, Math.min(k(n) - 3, 9 - k(n), 1)),
  ) as Rgb;
}

/** sRGB channels in 0–1; throws for anything but opaque literal syntax. */
export function parseColour(value: string): Rgb {
  const text = value.trim().toLowerCase();
  if (text === "white") return [1, 1, 1];
  if (text === "black") return [0, 0, 0];
  const short = /^#([0-9a-f])([0-9a-f])([0-9a-f])$/.exec(text);
  if (short) return short.slice(1).map((d) => parseInt(d + d, 16) / 255) as Rgb;
  const long = /^#([0-9a-f]{2})([0-9a-f]{2})([0-9a-f]{2})$/.exec(text);
  if (long) return long.slice(1).map((d) => parseInt(d, 16) / 255) as Rgb;
  const fn = /^(rgba?|hsla?|oklab|oklch)\(([^()]*)\)$/.exec(text);
  if (!fn) throw new Error();
  const [channels, alpha] = fn[2].split("/");
  const parts = channels.trim().split(/[\s,]+/);
  const extraAlpha = parts.length === 4 ? parts.pop() : undefined;
  const opacity = alpha ?? extraAlpha;
  if (opacity !== undefined && amount(opacity.trim(), 1) !== 1)
    throw new Error();
  if (parts.length !== 3) throw new Error();
  const [x, y, z] = parts;
  switch (fn[1]) {
    case "rgb":
    case "rgba":
      return [x, y, z].map((v) => amount(v, 255) / 255) as Rgb;
    case "hsl":
    case "hsla":
      return hslToRgb(hue(x), amount(y, 100) / 100, amount(z, 100) / 100);
    case "oklab":
      return fromOklab([amount(x, 1), amount(y, 0.4), amount(z, 0.4)]);
    default: {
      const c = amount(y, 0.4),
        h = (hue(z) * Math.PI) / 180;
      return fromOklab([amount(x, 1), c * Math.cos(h), c * Math.sin(h)]);
    }
  }
}

/** color-mix(in oklab, a p%, b) as #rrggbb. */
export function mix(a: string, b: string, percentA: number): string {
  const t = percentA / 100;
  const [x, y] = [toOklab(parseColour(a)), toOklab(parseColour(b))];
  return hex(fromOklab(x.map((v, i) => v * t + y[i] * (1 - t)) as Rgb));
}
