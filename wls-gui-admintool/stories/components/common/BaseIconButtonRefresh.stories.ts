import type { Meta, StoryObj } from "@storybook/vue3-vite";

import { fn } from "storybook/test";

import { default as StoryComponent } from "@/components/common/BaseIconButtonRefresh.vue";

const meta: Meta<typeof StoryComponent> = {
  component: StoryComponent,
  argTypes: {
    /* @ts-expect-error: error cause not explicit defined as event */
    onClick: {
      table: {
        disable: true,
      },
    },
  },
  args: {
    /* @ts-expect-error: error cause not explicit defined as event */
    onClick: fn(),
  },
};

export default meta;

type Story = StoryObj<typeof meta>;

export const Default: Story = {};
