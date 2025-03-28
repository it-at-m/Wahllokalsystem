import type { Meta, StoryObj } from "@storybook/vue3";

import { fn } from "@storybook/test";

import BaseNumberInput from "@/components/common/inputs/BaseNumberInput.vue";
import { REQUIRED } from "@/util/rules.ts";

const meta: Meta<typeof BaseNumberInput> = {
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
      // TODO: gibt den neuen Wert 2x zurück
      name: "update:modelValue", // to show name of event not eventHandler
      description:
        "Wird ausgelöst wenn sich der aktuelle Wert ändert und gibt den neuen Wert zurück",
      table: {
        category: "events",
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    label: "Zahl eingeben",
    rules: [],
    "onUpdate:modelValue": fn(),
  },
};

/**
 * Pflichtfeld
 *
 * Für ein Beispiel, wie die Rules korrekt übergeben werden, siehe
 * [Vue Doku](https://vuetifyjs.com/en/components/text-fields/#validation-26-rules)
 */
export const Required: Story = {
  args: {
    ...Default.args,
    rules: [REQUIRED],
  },
};

/**
 * Vorbelegter Wert
 */
export const ValuePreSet: Story = {
  args: {
    ...Default.args,
    modelValue: 2,
  },
};
