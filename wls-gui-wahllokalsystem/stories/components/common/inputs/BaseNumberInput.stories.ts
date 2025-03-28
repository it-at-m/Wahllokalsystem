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
      control: {
        disable: true,
      },
    },
    "onUpdate:modelValue": {
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
    // TODO: Event wird nicht nur bei Änderung von modelValue sondern auch bei Klick in oder Verlassen des Felds emittiert
    "onUpdate:modelValue": fn(),
  },
};

/**
 * ```
 * const REQUIRED = (value: any) => (!!value && value.trim().length > 0) || "Feld darf nicht leer sein."
 * const rules = [REQUIRED]
 * ```
 */
export const Required: Story = {
  args: {
    ...Default.args,
    rules: [REQUIRED],
  },
};

/**
 * ```
 * const MIN_NUMBER = (min: number) => (value: number) => value >= min || "Eingabe darf nicht kleiner als ${min} sein."
 * const MAX_NUMBER (max: number) => (value: number) => value <= max || "Eingabe darf nicht größer als ${max} sein."
 * const rules = [MIN_NUMBER(5), MAX_NUMBER(10)]
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
