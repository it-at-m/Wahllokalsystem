import type { Meta, StoryObj } from "@storybook/vue3";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";

const meta = {
  component: BaseButtonSave,
  args: {},
} satisfies Meta<typeof BaseButtonSave>;

export default meta;
type Story = StoryObj<typeof meta>;
export const Default: Story = {
  args: {},
};

export const Disabled: Story = {
  args: {
    disabled: true,
  },
};
