import type { Meta, StoryObj } from "@storybook/vue3";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";

// More on how to set up stories at: https://storybook.js.org/docs/writing-stories
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
