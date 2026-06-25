import type { Meta, StoryObj } from "@storybook/vue3-vite";

import { useErgebnisseTestDataFactory } from "@tests/utils/ergebnismeldung/common/ergebnisseTestDataFactory.ts";
import { fn } from "storybook/test";

import BaseCardSnippedErgebnis from "@/components/ergebnismeldung/common/BaseCardSnippedErgebnis.vue";

const { prepareErgebnis } = useErgebnisseTestDataFactory();

const meta = {
  component: BaseCardSnippedErgebnis,
  args: {
    onSave: fn(),
  },
} satisfies Meta<typeof BaseCardSnippedErgebnis>;

export default meta;

type Story = StoryObj<typeof meta>;
export const Default: Story = {
  args: {
    modelValue: prepareErgebnis().build(),
    snippedTitle: "Ungültige Stimmzettel",
    isWahlFinished: false,
  },
};

export const MinMaxValue: Story = {
  args: {
    modelValue: prepareErgebnis().build(),
    snippedTitle: "Ungültige Stimmzettel",
    minValue: 5,
    maxValue: 20,
    isWahlFinished: false,
  },
};
