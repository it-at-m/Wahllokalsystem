import type { Meta, StoryObj } from "@storybook/vue3";

import { fn } from "@storybook/test";

import BaseNumberInput from "@/components/common/inputs/BaseNumberInput.vue";
import { MAX_NUMBER, MIN_NUMBER, REQUIRED } from "@/util/rules.ts";

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
 * ```
 * const rules = [REQUIRED]
 * const REQUIRED = (value: any) => (!!value && value.trim().length > 0) || "Feld darf nicht leer sein."
 * ```
 */
export const Required: Story = {
  args: {
    ...Default.args,
    rules: [REQUIRED],
  },
};

/**
 * Input muss zwischen 5 und 10 liegen
 * ```
 * const rules = [MIN_NUMBER(5), MAX_NUMBER(10)]
 * const MIN_NUMBER = (min: number) => (value: number) => value >= min || "Eingabe darf nicht kleiner als ${min} sein."
 * const MAX_NUMBER (max: number) => (value: number) => value <= max || "Eingabe darf nicht größer als ${max} sein."
 * ```
 */
export const InputRange: Story = {
  args: {
    ...Default.args,
    rules: [MAX_NUMBER(10), MIN_NUMBER(5)],
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
