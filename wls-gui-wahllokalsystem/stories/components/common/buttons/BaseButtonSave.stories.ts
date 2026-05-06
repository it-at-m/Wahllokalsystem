import type { Meta, StoryObj } from "@storybook/vue3";

import BaseWlsButtonSave from "@/components/common/buttons/BaseWlsButtonSave.vue";

const meta = {
  component: BaseWlsButtonSave,
  args: {},
} satisfies Meta<typeof BaseWlsButtonSave>;

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

export const CustomText: Story = {
  args: {
    saveText: "Beliebiger Speichertext",
  },
};
