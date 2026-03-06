import type { StoryObj } from "@storybook/vue3";

import { fn } from "@storybook/test";

import BaseFormStimmzettelQuickInput from "@/components/experimental/BaseFormStimmzettelQuickInput.vue";

const meta = {
  component: BaseFormStimmzettelQuickInput,
  argTypes: {
    onCommand: {
      tabl: {
        disable: true,
      },
    },
  },
  args: {
    onCommand: fn(),
  },
} satisfies Meta<typeof BaseFormStimmzettelQuickInput>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {},
};
