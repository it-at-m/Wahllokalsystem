import type { Meta, StoryObj } from "@storybook/vue3";

import BaseButtonRefresh from "./BaseButtonRefresh.vue";

// More on how to set up stories at: https://storybook.js.org/docs/writing-stories
const meta = {
  component: BaseButtonRefresh,
  // This component will have an automatically generated docsPage entry: https://storybook.js.org/docs/writing-docs/autodocs
  tags: ["autodocs"],
  args: {},
} satisfies Meta<typeof BaseButtonRefresh>;

export default meta;
type Story = StoryObj<typeof meta>;
export const Default: Story = {
  args: {},
};
