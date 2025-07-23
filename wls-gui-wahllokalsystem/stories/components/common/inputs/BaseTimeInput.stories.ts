import type { Meta, StoryObj } from "@storybook/vue3";

import BaseTimeInput from "@/components/common/inputs/BaseTimeInput.vue";

const meta = {
  component: BaseTimeInput,
} satisfies Meta<typeof BaseTimeInput>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    modelValue: new Date(),
  },
};

export const WithCustomTime: Story = {
  args: {
    modelValue: new Date("2023-10-10T14:30:00"),
  },
};
