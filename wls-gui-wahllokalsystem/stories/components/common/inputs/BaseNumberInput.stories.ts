import type { Meta, StoryObj } from "@storybook/vue3";

import { fn } from "@storybook/test";

import BaseNumberInput from "@/components/common/inputs/BaseNumberInput.vue";
import { REQUIRED } from "@/util/rules.ts";

const meta = {
  component: BaseNumberInput,
  argTypes: {
    label: {
      description: "Label für das Input Feld",
    },
    modelValue: {
      description: "aktueller Wert",
      control: {
        type: "number",
      },
      table: {
        category: "props",
        type: { summary: "number" },
      },
    },
    rules: {
      description: "Validierungsregeln",
    },
    "onUpdate:modelValue": {
      // TODO: gibt den neuen Wert 2x zurück + wenn aus dem feld geklickt wird verschwindet die zahl
      description:
        "Wird ausgelöst wenn sich der aktuelle Wert ändert und gibt den neuen Wert zurück",
      table: {
        category: "events",
      },
    },
  },
  args: {
    "onUpdate:modelValue": fn(),
  },
} satisfies Meta<typeof BaseNumberInput>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    label: "Zahl eingeben",
    rules: [],
  },
};

/**
 * Pflichtfeld
 */
export const Required: Story = {
  args: {
    ...Default.args,
    rules: [REQUIRED], // TODO: funktioniert, aber im code sind die rules leer
    //rules: [REQUIRED.toString()], // TODO: funktioniert, aber falscher fehlertext wird angezeigt
  },
};

/**
 * Vorbelegter wert
 */
export const ValuePreSet: Story = {
  args: {
    ...Default.args,
    modelValue: "2",
  },
};
