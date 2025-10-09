import type { Meta, StoryObj } from "@storybook/vue3";

import { fn } from "@storybook/test";

import BaseTableRowManager from "@/components/common/tables/BaseTableRowManager.vue";
import { useRules } from "@/composables/common/rules.ts";

const { minNumber, maxNumber } = useRules();

const meta: Meta<typeof BaseTableRowManager> = {
  component: BaseTableRowManager,
  args: {
    currentRowCount: 0,
    onChangeRowCountClicked: fn(),
  },
  argTypes: {
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
    changeRowCountClicked: {
      table: {
        disable: true,
      },
    },
    onChangeRowCountClicked: {
      name: "changeRowCountClicked",
      description:
        "Wird ausgelöst, wenn auf den Button geklickt wird, und gibt den neuen Wert zurück",
      table: {
        category: "events",
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {},
};

export const PreSetModelValue: Story = {
  args: {
    modelValue: 5,
  },
};

export const DifferentLabels: Story = {
  args: {
    applyBtnLabel: "Bestätigen",
    inputFieldLabel: "Neue Zeilenanzahl",
  },
};

/**
 * ```
 * const minNumber = (min: number) => (value: number) => value >= min || "Eingabe darf nicht kleiner als ${min} sein."
 * const maxNumber = (max: number) => (value: number) => value <= max || "Eingabe darf nicht größer als ${max} sein."
 * const rules = [minNumber(5), maxNumber(10)]
 * ```
 */
export const AdditionalRulesMinAndMaxInput: Story = {
  args: {
    rules: [minNumber(5), maxNumber(10)],
  },
};
