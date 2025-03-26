import type { Meta, StoryObj } from "@storybook/vue3";

import BaseAutocompleteWahltag from "@/components/common/BaseAutocompleteWahltag.vue";

const meta = {
  component: BaseAutocompleteWahltag,
  argTypes: {
    items: {
      description: "Liste der verfügbaren Wahltage",
      control: {
        type: "object",
      },
    },
    multiple: {
      description: "ermöglicht Mehrfachauswahl",
      control: {
        type: "boolean",
      },
    },
    modelValue: {
      description: "Aktuell gewählte(r) Wert(e)",
      control: {
        type: "object",
      },
    },
  },
  args: {},
} satisfies Meta<typeof BaseAutocompleteWahltag>;

export default meta;

type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    items: ["27.02.1988", "31.01.1998", "04.04.2000"],
    multiple: true,
  },
};

/**
 * Nur ein Wahltag kann ausgewählt werden.
 */
export const SingleSelect: Story = {
  args: {
    ...Default.args,
    multiple: false, // überschreibt den Wert aus ...Default.args
  },
};

/**
 * Ein Wahltag ist vorausgewählt.
 */
export const PreSelect: Story = {
  args: {
    ...Default.args,
    modelValue: "04.04.2000", // ergänzt die Werte aus ...Default.args
  },
};
