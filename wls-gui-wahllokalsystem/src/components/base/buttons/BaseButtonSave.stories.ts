import type { Meta, StoryObj } from "@storybook/vue3";

import BaseButtonSave from "./BaseButtonSave.vue";

// More on how to set up stories at: https://storybook.js.org/docs/writing-stories
const meta = {
  component: BaseButtonSave,
  // This component will have an automatically generated docsPage entry: https://storybook.js.org/docs/writing-docs/autodocs
  tags: ["autodocs"],
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
