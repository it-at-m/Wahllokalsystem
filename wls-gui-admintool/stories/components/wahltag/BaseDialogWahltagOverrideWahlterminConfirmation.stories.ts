import type { Meta, StoryObj } from "@storybook/vue3";

import { fn } from "@storybook/test";

import { default as StoryComponent } from "@/components/wahltag/BaseDialogWahltagOverrideWahlterminConfirmation.vue";

const meta: Meta<typeof StoryComponent> = {
  component: StoryComponent,
  argTypes: {
    onCancelDelete: {
      table: {
        disable: true,
      },
    },
    onConfirmDelete: {
      table: {
        disable: true,
      },
    },
  },
  args: {
    onCancelDelete: fn(),
    onConfirmDelete: fn(),
  },
};

export default meta;

type Story = StoryObj<typeof meta>;

export const Default: Story = {};
