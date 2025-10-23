import type { Meta, StoryObj } from "@storybook/vue3";

import { useErgebnisseTestDataFactory } from "@tests/utils/ergebnismeldung/ergebnisseTestDataFactory.ts";

import BaseCardSnippedErgebnis from "@/components/ergebnisermittlung/BaseCardSnippedErgebnis.vue";

const { prepareErgebnis } = useErgebnisseTestDataFactory();

const meta = {
  component: BaseCardSnippedErgebnis,
  args: {},
  decorators: [
    (story) => {
      return {
        components: { story },
        template: "<story />",
      };
    },
  ],
} satisfies Meta<typeof BaseCardSnippedErgebnis>;

export default meta;

type Story = StoryObj<typeof meta>;
export const Default: Story = {
  args: {
    modelValue: prepareErgebnis().build(),
    snippedTitle: "Ungültige Stimmzettel",
  },
};

export const MinMaxValue: Story = {
  args: {
    modelValue: prepareErgebnis().build(),
    snippedTitle: "Ungültige Stimmzettel",
    minValue: 5,
    maxValue: 20,
  },
};
