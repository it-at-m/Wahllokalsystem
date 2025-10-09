import type { Meta, StoryObj } from "@storybook/vue3";

import { fn } from "@storybook/test";

import BaseTableRowManager from "@/components/common/tables/BaseTableRowManager.vue";
import { useRules } from "@/composables/common/rules.ts";

const { minNumber, maxNumber } = useRules();

const meta: Meta<typeof BaseTableRowManager> = {
  component: BaseTableRowManager,
  args: {
    currentRowCount: 0,
    onRowCountChanged: fn(),
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
    onRowCountChanged: {
      table: {
        disable: true,
      },
    },
    // todo: rules control anpassen
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

export const AdditionalRulesMinAndMaxInput: Story = {
  args: {
    rules: [minNumber(5), maxNumber(10)],
  },
};
