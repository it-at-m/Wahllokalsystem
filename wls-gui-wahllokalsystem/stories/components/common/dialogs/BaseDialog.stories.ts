import type { Meta, StoryObj } from "@storybook/vue3-vite";

import { fn } from "storybook/test";

import BaseDialog from "@/components/common/dialogs/BaseDialog.vue";

const meta: Meta<typeof BaseDialog> = {
  component: BaseDialog,
  argTypes: {
    onCancel: {
      table: {
        disable: true,
      },
    },
    onConfirm: {
      table: {
        disable: true,
      },
    },
  },
  args: {
    onCancel: fn(),
    onConfirm: fn(),
  },
};

export default meta;
type Story = StoryObj<typeof meta>;
export const Default: Story = {
  args: {
    default: "Hi, i am the default slot content",
    dialogtitle: "My dialog title",
    visible: false,
    confirmtext: "Bestätigen",
    icon: "$information",
  },
};

export const Cancelable: Story = {
  args: {
    default: "Hi, i am the default slot content",
    dialogtitle: "My dialog title",
    visible: false,
    confirmtext: "Bestätigen",
    canceltext: "Abbrechen",
    icon: "$information",
  },
};
