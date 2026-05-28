import type { Meta, StoryObj } from "@storybook/vue3-vite";

import { fn } from "storybook/test";

import BaseButtonFolding from "@/components/common/buttons/BaseButtonFolding.vue";

const meta = {
  component: BaseButtonFolding,
  argTypes: {
    modelValue: {
      description: "Aktueller Zustand des Buttons",
      control: { type: "boolean" },
      table: {
        category: "props",
        type: {
          summary: "boolean",
        },
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
  args: {},
} satisfies Meta<typeof BaseButtonFolding>;

export default meta;
type Story = StoryObj<typeof meta>;
export const Default: Story = {
  args: {
    "onUpdate:modelValue": fn(),
  },
};

export const Expanded: Story = {
  args: {
    ...Default.args,
    modelValue: true,
  },
};

export const Folded: Story = {
  args: {
    ...Default.args,
    modelValue: false,
  },
};

export const UndefinedModelValueIsFolded: Story = {
  args: {
    ...Default.args,
    modelValue: undefined,
  },
};
