import type { Meta, StoryObj } from "@storybook/vue3";

import BaseButtonRefresh from "@/components/common/buttons/BaseButtonRefresh.vue";

// More on how to set up stories at: https://storybook.js.org/docs/writing-stories
const meta = {
  component: BaseButtonRefresh,
  args: {},
} satisfies Meta<typeof BaseButtonRefresh>;

export default meta;
type Story = StoryObj<typeof meta>;
export const Default: Story = {
  args: {},
};
