/* Email theme. Email clients cannot read Mortar's stylesheet: Gmail drops CSS
   variables and Outlook for Windows renders with Word. So an email takes its
   colours and fonts as literal values, resolved here from a brand the way
   tokens.css resolves it for the light appearance: omitted values keep
   Mortar's defaults or derive from primary, and derived colours
   (color-mix(in oklab, …)) are computed in JS. tests/email.test.mjs checks
   the defaults against tokens.css. */

import { isBrandFont, type BrandDefinition } from "./brand.js";
import { hex, mix, parseColour } from "./colour.js";

/** Literal colours (#rrggbb) and font stacks for an email. */
export interface EmailTheme {
  /** The page behind the message. */
  paper: string;
  /** The message card. */
  surface: string;
  /** Recessed areas such as a one-time code. */
  sunken: string;
  line: string;
  ink: string;
  secondary: string;
  muted: string;
  primary: string;
  onPrimary: string;
  accent: string;
  onAccent: string;
  link: string;
  /** A tint of primary on the paper, for highlighted rows and callouts. */
  selected: string;
  success: string;
  warning: string;
  danger: string;
  info: string;
  /** Body font stack, ending in fonts every email client has. */
  font: string;
  /** Heading font stack; the body stack unless the brand sets one. */
  fontHeading: string;
  mono: string;
}

/* The light defaults in tokens.css. */
const DEFAULTS = {
  paper: "#ffffff",
  surface: "#ffffff",
  sunken: "#f5f5f7",
  line: "#e5e5ea",
  ink: "#000000",
  secondary: "#3a3a3c",
  muted: "#6e6e73",
  primary: "#000000",
  onPrimary: "#ffffff",
  success: "#1f7a36",
  warning: "#945700",
  danger: "#c70014",
  info: "#0060c7",
};
/* The neutral brand's tuned selected tint (no brand at all). */
const NEUTRAL_SELECTED = "#f2f2f4";

/* Mortar's system stack, plus the fonts Windows and Android mail apps have. */
const SANS =
  '-apple-system, BlinkMacSystemFont, "Segoe UI", "Helvetica Neue", Helvetica, Arial, sans-serif';
const SERIF = 'Georgia, "Times New Roman", Times, serif';
const MONO = '"SF Mono", Menlo, Consolas, "Courier New", monospace';
const GENERIC: Record<string, string> = {
  "sans-serif": SANS,
  "system-ui": SANS,
  "ui-sans-serif": SANS,
  serif: SERIF,
  "ui-serif": SERIF,
  monospace: MONO,
  "ui-monospace": MONO,
};

/**
 * A brand font list for email: `var()` entries (which email cannot resolve)
 * and generic families are dropped, and a stack every client has is
 * appended, serif or monospace when the brand's generic family is one.
 */
function fontStack(value: string | undefined, key: string): string | undefined {
  const text = value?.trim();
  if (!text) return undefined;
  if (!isBrandFont(text))
    throw new Error(
      `Invalid Mortar brand font for fonts.${key}: "${text}". Use family names, quoted names and generic families separated by commas, e.g. "Inter", sans-serif.`,
    );
  let fallback = SANS;
  const names = text
    .split(",")
    .map((name) => name.trim())
    .filter((name) => {
      if (/^var\(/i.test(name)) return false;
      const generic = GENERIC[name.toLowerCase()];
      if (generic) fallback = generic;
      return !generic;
    });
  return [...names, fallback].join(", ");
}

function colour(value: string | undefined, key: string): string | undefined {
  const text = value?.trim();
  if (!text) return undefined;
  try {
    return hex(parseColour(text));
  } catch {
    throw new Error(
      `Mortar email colours must be literal and opaque, for light.${key}: "${text}". Use a hex value, white, black, or rgb(), hsl(), oklab() or oklch().`,
    );
  }
}

/**
 * The email theme for a brand, in its light appearance: emails render light,
 * since most clients ignore or override a dark one. Without a brand, Mortar's
 * neutral defaults. Brand colours must be literal (`var()`, `color-mix()`
 * and `light-dark()` need a browser) and opaque; the accent gradient is not
 * used, as most clients cannot paint one, so accents use the flat `accent`.
 */
export function emailTheme(brand?: BrandDefinition): EmailTheme {
  const light = brand?.light ?? {};
  const paper = colour(light.paper, "paper") ?? DEFAULTS.paper;
  const primary = colour(light.primary, "primary") ?? DEFAULTS.primary;
  const onPrimary = colour(light.onPrimary, "onPrimary") ?? DEFAULTS.onPrimary;
  const font = fontStack(brand?.fonts?.body, "body") ?? SANS;
  return {
    ...DEFAULTS,
    paper,
    surface: colour(light.surface, "surface") ?? DEFAULTS.surface,
    sunken: colour(light.sunken, "sunken") ?? DEFAULTS.sunken,
    primary,
    onPrimary,
    accent: colour(light.accent, "accent") ?? primary,
    onAccent: colour(light.onAccent, "onAccent") ?? onPrimary,
    link: colour(light.link, "link") ?? primary,
    selected:
      colour(light.selected, "selected") ??
      (brand ? mix(primary, paper, 8) : NEUTRAL_SELECTED),
    font,
    fontHeading: fontStack(brand?.fonts?.heading, "heading") ?? font,
    mono: MONO,
  };
}
