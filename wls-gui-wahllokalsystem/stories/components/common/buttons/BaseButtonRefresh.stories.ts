import type { Meta, StoryObj } from "@storybook/vue3";

import BaseButtonRefresh from "@/components/common/buttons/BaseButtonRefresh.vue";

const meta = {
  component: BaseButtonRefresh,
  args: {},
} satisfies Meta<typeof BaseButtonRefresh>;

export default meta;
type Story = StoryObj<typeof meta>;
export const Default: Story = {
  args: {},
};
