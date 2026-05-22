import type { Meta, StoryObj } from "@storybook/vue3-vite";

import WlsClock from "@/components/wlsComponents/WlsClock.vue";

const meta = {
  component: WlsClock,
  args: {},
} satisfies Meta<typeof WlsClock>;

export default meta;
type Story = StoryObj<typeof meta>;
export const Default: Story = {
  args: {},
};
