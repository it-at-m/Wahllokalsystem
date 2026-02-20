import type { Meta, StoryObj } from "@storybook/vue3";

import BaseTimeInput from "@/components/common/inputs/BaseTimeInput.vue";

const meta = {
  component: BaseTimeInput,
  args: {},
} satisfies Meta<typeof BaseTimeInput>;

export default meta;
type Story = StoryObj<typeof meta>;
export const Default: Story = {
  args: {},
};
