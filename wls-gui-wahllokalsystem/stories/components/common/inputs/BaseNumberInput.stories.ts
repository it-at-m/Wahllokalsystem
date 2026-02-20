import type { Meta, StoryObj } from "@storybook/vue3";

import { fn } from "@storybook/test";

import BaseNumberInput from "@/components/common/inputs/BaseNumberInput.vue";
import { useRules } from "@/composables/common/rules.ts";

const { maxNumber, minNumber, required } = useRules();

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
    // @ts-expect-error/disabled-because-if-omitted-it-will-still-be-displayed-in-storybook
    "update:modelValue": {
      table: {
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
    "onUpdate:modelValue": fn(),
  },
};

/**
 * ```
 * const required = (value: any) => (!!value && value.trim().length > 0) || "Feld darf nicht leer sein."
 * const rules = [required]
 * ```
 */
export const Required: Story = {
  args: {
    ...Default.args,
    rules: [required],
  },
};

/**
 * ```
 * const minNumber = (min: number) => (value: number) => value >= min || "Eingabe darf nicht kleiner als ${min} sein."
 * const maxNumber = (max: number) => (value: number) => value <= max || "Eingabe darf nicht größer als ${max} sein."
 * const rules = [minNumber(5), maxNumber(10)]
 * ```
 */
export const InputRange: Story = {
  args: {
    ...Default.args,
    rules: [maxNumber(10), minNumber(5)],
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
