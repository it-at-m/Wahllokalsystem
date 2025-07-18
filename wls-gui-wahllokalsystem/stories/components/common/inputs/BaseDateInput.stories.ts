import type { Meta, StoryObj } from "@storybook/vue3";

import BaseDateInput from "@/components/common/inputs/BaseDateInput.vue";

const meta = {
  component: BaseDateInput,
  args: {},
} satisfies Meta<typeof BaseDateInput>;

export default meta;
type Story = StoryObj<typeof meta>;
export const Default: Story = {
  args: {},
};
