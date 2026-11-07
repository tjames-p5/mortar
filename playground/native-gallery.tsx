/* Native gallery: each core component on the web, beside the SwiftUI and
   Jetpack Compose snapshot references that CI compares against. The images
   are the committed references, so the page shows exactly what the native
   tests check. */
import { useState, type ReactNode } from "react";
import {
  Avatar,
  Badge,
  Button,
  Card,
  EmptyState,
  Input,
  Notice,
  PageHeading,
  SegmentedControl,
  Select,
  Switch,
} from "../src/index.js";

const apple = import.meta.glob<string>(
  "../native/apple/Tests/MortarTests/__Snapshots__/MortarSnapshotTests/*.png",
  { eager: true, query: "?url", import: "default" },
);
const android = import.meta.glob<string>(
  "../native/android/mortar/src/test/snapshots/images/*.png",
  { eager: true, query: "?url", import: "default" },
);

type Variant = "light" | "dark" | "large-text";

interface Entry {
  id: string;
  label: string;
  /** Snapshot test names: SwiftUI's testX, Compose's x. */
  swift: string;
  compose: string;
  devices: [string, string];
  variants: Variant[];
  web: () => ReactNode;
}

const tones = ["neutral", "info", "success", "warning", "danger"] as const;
const title = (word: string) => word[0].toUpperCase() + word.slice(1);

const ENTRIES: Entry[] = [
  {
    id: "button",
    label: "Button",
    swift: "testButton",
    compose: "button",
    devices: ["iPhone 13", "Pixel 5"],
    variants: ["light", "dark", "large-text"],
    web: () => (
      <div className="m-stack items-start">
        {(["primary", "secondary", "ghost", "danger", "accent"] as const).map(
          (variant) => (
            <Button key={variant} variant={variant}>
              {title(variant)} action
            </Button>
          ),
        )}
        <div className="flex gap-2">
          <Button variant="primary" size="sm">
            Small
          </Button>
          <Button variant="primary" size="lg">
            Large
          </Button>
        </div>
        <Button variant="primary" loading>
          Saving
        </Button>
        <Button variant="primary" disabled>
          Disabled
        </Button>
      </div>
    ),
  },
  {
    id: "badge",
    label: "Badge",
    swift: "testBadge",
    compose: "badge",
    devices: ["iPhone 13", "Pixel 5"],
    variants: ["light", "dark", "large-text"],
    web: () => (
      <div className="m-stack items-start">
        {tones.map((tone) => (
          <Badge key={tone} tone={tone}>
            {title(tone)}
          </Badge>
        ))}
      </div>
    ),
  },
  {
    id: "card",
    label: "Card",
    swift: "testCard",
    compose: "card",
    devices: ["iPhone 13", "Pixel 5"],
    variants: ["light", "dark", "large-text"],
    web: () => (
      <div className="m-stack">
        <Card
          title="Billing"
          description="Your plan renews on 1 December."
          actions={<Button size="sm">Change</Button>}
          footer="Invoices go to billing@example.com"
        >
          Team plan, 12 seats
        </Card>
        <Card padding="compact" tone="muted">
          A compact, muted card without a header.
        </Card>
      </div>
    ),
  },
  {
    id: "text-field",
    label: "Text field",
    swift: "testTextField",
    compose: "textField",
    devices: ["iPhone 13", "Pixel 5"],
    variants: ["light", "dark", "large-text"],
    web: () => (
      <div className="m-stack">
        <Input
          label="Email"
          placeholder="you@example.com"
          hint="We'll send a confirmation link."
          required
        />
        <Input
          label="Workspace name"
          defaultValue="Hive"
          error="That name is taken. Try another."
        />
        <Input label="Password" type="password" defaultValue="hunter22" />
        <Input label="Invite code" defaultValue="MORTAR" disabled />
      </div>
    ),
  },
  {
    id: "switch",
    label: "Switch",
    swift: "testSwitch",
    compose: "switch",
    devices: ["iPhone 13", "Pixel 5"],
    variants: ["light", "dark", "large-text"],
    web: () => (
      <div className="m-stack">
        <Switch
          label="Email notifications"
          description="A summary of activity each morning."
          defaultChecked
        />
        <Switch label="Product updates" />
        <Switch label="Two-step sign-in" defaultChecked disabled />
      </div>
    ),
  },
  {
    id: "avatar",
    label: "Avatar",
    swift: "testAvatar",
    compose: "avatar",
    devices: ["iPhone 13", "Pixel 5"],
    variants: ["light", "dark", "large-text"],
    web: () => (
      <div className="m-stack items-start">
        <div className="flex items-center gap-3">
          {(["xs", "sm", "md", "lg", "xl"] as const).map((size) => (
            <Avatar key={size} name="Ada Lovelace" size={size} />
          ))}
        </div>
        <div className="flex items-center gap-3">
          <Avatar name="Grace Hopper" size="lg" status="online" />
          <Avatar
            name="Alan Turing"
            size="lg"
            shape="square"
            status="offline"
          />
        </div>
      </div>
    ),
  },
  {
    id: "notice",
    label: "Notice",
    swift: "testNotice",
    compose: "notice",
    devices: ["iPhone 13", "Pixel 5"],
    variants: ["light", "dark", "large-text"],
    web: () => (
      <div className="m-stack">
        {tones.map((tone) => (
          <Notice key={tone} title={`${title(tone)} notice`} tone={tone}>
            Something happened that you should know about.
          </Notice>
        ))}
      </div>
    ),
  },
  {
    id: "empty-state",
    label: "Empty state",
    swift: "testEmptyState",
    compose: "emptyState",
    devices: ["iPhone 13", "Pixel 5"],
    variants: ["light", "dark", "large-text"],
    web: () => (
      <div className="m-stack">
        <EmptyState
          title="No projects yet"
          description="Projects you create or join appear here."
          action={<Button variant="primary">New project</Button>}
        />
        <EmptyState
          title="No results"
          description="Try a different search."
          dashed
        />
      </div>
    ),
  },
  {
    id: "tablet",
    label: "Branded tablet screen",
    swift: "testBrandOnIPad",
    compose: "brandOnTablet",
    devices: ["iPad Pro 11", "Pixel C"],
    variants: ["light", "dark"],
    web: () => null,
  },
];

/** A committed reference image, or undefined when it has not been recorded. */
function reference(platform: "swift" | "compose", entry: Entry, v: Variant) {
  const images = platform === "swift" ? apple : android;
  const file =
    platform === "swift"
      ? `/${entry.swift}.${v}.png`
      : `/codes.tony.mortar_MortarSnapshotTest_${entry.compose}_${v}.png`;
  return Object.entries(images).find(([path]) => path.endsWith(file))?.[1];
}

function Shot({
  label,
  device,
  src,
  variant,
}: {
  label: string;
  device: string;
  src?: string;
  variant: Variant;
}) {
  return (
    <figure className="pg-native-shot" data-platform={label}>
      <figcaption>
        <strong>{label}</strong> <span>{device}</span>
      </figcaption>
      {src ? (
        <img
          src={src}
          alt={`${label} snapshot on ${device}, ${variant.replace("-", " ")}`}
          loading="lazy"
        />
      ) : (
        <p>No reference recorded yet.</p>
      )}
    </figure>
  );
}

export function NativeGallery() {
  const read = () => new URLSearchParams(location.search);
  const [id, setId] = useState(
    () => ENTRIES.find((e) => e.id === read().get("native"))?.id ?? "button",
  );
  const [variant, setVariant] = useState<Variant>(
    () =>
      (["light", "dark", "large-text"] as const).find(
        (v) => v === read().get("variant"),
      ) ?? "light",
  );
  const entry = ENTRIES.find((e) => e.id === id) ?? ENTRIES[0];
  const shown = entry.variants.includes(variant) ? variant : "light";
  const choose = (next: { native?: string; variant?: string }) => {
    const params = read();
    for (const [key, value] of Object.entries(next))
      if (value) params.set(key, value);
    history.replaceState(null, "", `?${params}`);
    if (next.native) setId(next.native);
    if (next.variant) setVariant(next.variant as Variant);
  };
  const web = entry.web();
  return (
    <>
      <PageHeading
        title="Native gallery"
        description="Each core component on the web, in SwiftUI and in Jetpack Compose. The native images are the snapshot references CI checks the Swift package and the Compose library against."
      />
      <section
        className="pg-example-toolbar pg-tool-toolbar"
        aria-label="Gallery settings"
      >
        <Select
          label="Component"
          value={entry.id}
          options={ENTRIES.map((e) => ({ value: e.id, label: e.label }))}
          onChange={(e) => choose({ native: e.target.value })}
        />
        <SegmentedControl
          label="Variant"
          value={shown}
          options={entry.variants.map((v) => ({
            value: v,
            label: v === "large-text" ? "Large text" : title(v),
          }))}
          onChange={(value) => choose({ variant: value })}
        />
      </section>
      <div className="pg-native-grid">
        {web && (
          <figure className="pg-native-shot" data-platform="Web">
            <figcaption>
              <strong>Web</strong> <span>React, live</span>
            </figcaption>
            <div
              className="pg-native-web"
              data-mortar
              data-mortar-appearance={shown === "dark" ? "dark" : "light"}
              data-large-text={shown === "large-text" ? "" : undefined}
            >
              {web}
            </div>
          </figure>
        )}
        <Shot
          label="SwiftUI"
          device={entry.devices[0]}
          src={reference("swift", entry, shown)}
          variant={shown}
        />
        <Shot
          label="Compose"
          device={entry.devices[1]}
          src={reference("compose", entry, shown)}
          variant={shown}
        />
      </div>
    </>
  );
}
