import type { Meta, StoryObj } from "@storybook/vue3-vite";

import BaseListWahllokalBenutzer from "@/components/wahltag/BaseListWahllokalBenutzer.vue";

const meta: Meta<typeof BaseListWahllokalBenutzer> = {
  title: "Components/Wahltag/BaseListWahllokalBenutzer",
  component: BaseListWahllokalBenutzer,
};

export default meta;

type Story = StoryObj<typeof BaseListWahllokalBenutzer>;

// Bewusst unsortierte Eingabe, um die Sortierung nach Wahlbezirksnummer zu zeigen.
const sampleCsv = [
  "qrla-0007",
  "kueh-0001",
  "wjcd-0012",
  "mfpz-0003",
  "tbxn-0009",
  "ploe-0002",
  "hgua-0011",
  "znks-0005",
].join("\n");

const prefixes = ["kueh", "mfpz", "qrla", "tbxn", "wjcd", "ploe", "hgua"];
const manyUsersCsv = Array.from(
  { length: 48 },
  (_, index) =>
    `${prefixes[index % prefixes.length]}-${String(index + 1).padStart(4, "0")}`
).join("\n");

export const Default: Story = {
  args: {
    csv: sampleCsv,
  },
};

export const VieleBenutzer: Story = {
  args: {
    csv: manyUsersCsv,
  },
};

export const Leer: Story = {
  args: {
    csv: "Keine Nutzer zum angegebenen Wahltag gefunden.",
  },
};
