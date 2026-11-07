/* Email preview: each transactional template from @tonycodes-ui/mortar/email,
   rendered by renderEmail as a mail client receives it, at phone and desktop
   widths, beside its plain-text part. */
import { useEffect, useState, type ReactElement } from "react";
import {
  CodeBlock,
  PageHeading,
  SegmentedControl,
  Select,
} from "../src/index.js";
import {
  Invite,
  MagicLink,
  Notification,
  PasswordReset,
  Receipt,
  VerifyEmail,
  emailTheme,
  renderEmail,
  type EmailTheme,
} from "../src/email.js";

const BRANDS: Record<string, { label: string; theme?: EmailTheme }> = {
  neutral: { label: "Neutral" },
  green: {
    label: "Green",
    theme: emailTheme({
      light: { primary: "#047857", onPrimary: "#ffffff", selected: "#e6f5ed" },
    }),
  },
  blue: {
    label: "Blue, serif headings",
    theme: emailTheme({
      light: { primary: "#1d4ed8", paper: "#f8fafc" },
      fonts: { heading: "Georgia, serif" },
    }),
  },
};

const product = "Hive";
const TEMPLATES: {
  id: string;
  label: string;
  render: (theme?: EmailTheme) => ReactElement;
}[] = [
  {
    id: "verify",
    label: "Verify email",
    render: (theme) => (
      <VerifyEmail
        productName={product}
        theme={theme}
        code="482913"
        href="https://hive.example/verify?token=abc"
        recipient="ada@example.com"
        expiresIn="10 minutes"
      />
    ),
  },
  {
    id: "magic-link",
    label: "Magic link",
    render: (theme) => (
      <MagicLink
        productName={product}
        theme={theme}
        href="https://hive.example/sign-in?token=abc"
        expiresIn="15 minutes"
        requestedFrom="Safari on iPhone, near London"
      />
    ),
  },
  {
    id: "password-reset",
    label: "Password reset",
    render: (theme) => (
      <PasswordReset
        productName={product}
        theme={theme}
        href="https://hive.example/reset?token=abc"
        recipient="ada@example.com"
        expiresIn="1 hour"
      />
    ),
  },
  {
    id: "invite",
    label: "Invite",
    render: (theme) => (
      <Invite
        productName={product}
        theme={theme}
        inviterName="Grace Hopper"
        workspaceName="Compilers"
        href="https://hive.example/invite/abc"
        message="We're planning next quarter here. Come and join in."
        expiresIn="7 days"
      />
    ),
  },
  {
    id: "receipt",
    label: "Receipt",
    render: (theme) => (
      <Receipt
        productName={product}
        theme={theme}
        orderNumber="HV-10482"
        date="7 November 2026"
        items={[
          { name: "Team plan", price: "£96.00", quantity: 12, meta: "Monthly" },
          { name: "Extra storage", price: "£8.00", meta: "100 GB" },
        ]}
        lines={[
          { label: "Subtotal", value: "£104.00" },
          { label: "VAT (20%)", value: "£20.80" },
        ]}
        total={{ label: "Total", value: "£124.80" }}
        paymentMethod="Visa ending 4242"
        href="https://hive.example/orders/HV-10482"
      />
    ),
  },
  {
    id: "notification",
    label: "Notification",
    render: (theme) => (
      <Notification
        productName={product}
        theme={theme}
        title="Grace mentioned you in Roadmap"
        action={{
          label: "Open the comment",
          href: "https://hive.example/roadmap#c42",
        }}
        preferencesHref="https://hive.example/settings/email"
      >
        “Ada, can you check the estimates for the compiler work before Friday?”
      </Notification>
    ),
  },
];

const WIDTHS = { phone: 375, desktop: 600 } as const;
type Width = keyof typeof WIDTHS;

export function EmailPreview() {
  const read = () => new URLSearchParams(location.search);
  const [id, setId] = useState(
    () => TEMPLATES.find((t) => t.id === read().get("email"))?.id ?? "verify",
  );
  const [brand, setBrand] = useState(() => {
    const value = read().get("email-brand") ?? "";
    return value in BRANDS ? value : "neutral";
  });
  const [width, setWidth] = useState<Width>(() =>
    read().get("width") === "desktop" ? "desktop" : "phone",
  );
  const [rendered, setRendered] = useState<{ html: string; text: string }>();
  const [error, setError] = useState<string>();
  const template = TEMPLATES.find((t) => t.id === id) ?? TEMPLATES[0];

  useEffect(() => {
    let current = true;
    setError(undefined);
    renderEmail(template.render(BRANDS[brand].theme)).then(
      (result) => current && setRendered(result),
      (reason: unknown) => current && setError(String(reason)),
    );
    return () => {
      current = false;
    };
  }, [template, brand]);

  const choose = (key: string, value: string) => {
    const params = read();
    params.set(key, value);
    history.replaceState(null, "", `?${params}`);
  };
  const size = rendered ? new Blob([rendered.html]).size : 0;

  return (
    <>
      <PageHeading
        title="Email preview"
        description="The transactional templates from @tonycodes-ui/mortar/email, rendered by renderEmail as a mail client receives them, with the plain-text part each email also sends."
      />
      <section
        className="pg-example-toolbar pg-tool-toolbar"
        aria-label="Email settings"
      >
        <Select
          label="Template"
          value={template.id}
          options={TEMPLATES.map((t) => ({ value: t.id, label: t.label }))}
          onChange={(e) => {
            setId(e.target.value);
            choose("email", e.target.value);
          }}
        />
        <Select
          label="Brand"
          value={brand}
          options={Object.entries(BRANDS).map(([value, b]) => ({
            value,
            label: b.label,
          }))}
          onChange={(e) => {
            setBrand(e.target.value);
            choose("email-brand", e.target.value);
          }}
        />
        <SegmentedControl
          label="Width"
          value={width}
          options={[
            { value: "phone", label: "Phone, 375px" },
            { value: "desktop", label: "Desktop, 600px" },
          ]}
          onChange={(value) => {
            setWidth(value as Width);
            choose("width", value);
          }}
        />
      </section>
      {error ? (
        <p role="alert">The email could not render: {error}</p>
      ) : (
        <div className="pg-email">
          {/* Not sandboxed: renderEmail's own HTML, with no scripts, so axe
              checks the email as part of the page. */}
          <figure className="pg-email-frame">
            <figcaption>
              HTML{rendered && `, ${(size / 1024).toFixed(1)} KB`}
            </figcaption>
            <iframe
              title={`${template.label} email, HTML`}
              srcDoc={rendered?.html ?? ""}
              style={{ width: WIDTHS[width] }}
            />
          </figure>
          <CodeBlock label="Plain text" code={rendered?.text ?? "Rendering…"} />
        </div>
      )}
    </>
  );
}
