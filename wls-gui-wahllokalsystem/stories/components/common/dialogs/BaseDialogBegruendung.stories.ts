import type { Meta, StoryObj } from "@storybook/vue3-vite";

import { fn } from "storybook/test";

import BaseDialogBegruendung from "@/components/common/dialogs/BaseDialogBegruendung.vue";

const meta: Meta<typeof BaseDialogBegruendung> = {
  component: BaseDialogBegruendung,
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
  },
};
