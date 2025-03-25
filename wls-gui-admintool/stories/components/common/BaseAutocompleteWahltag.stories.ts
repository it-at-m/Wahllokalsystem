import type { Meta, StoryObj } from "@storybook/vue3";

import BaseAutocompleteWahltag from "../../../src/components/common/BaseAutocompleteWahltag.vue";

const meta = {
  component: BaseAutocompleteWahltag,
  argTypes: {
    items: {
      description: "Liste der verfügbaren Wahltage",
      control: {
        type: "object",
      },
    },
  },
} satisfies Meta<typeof BaseAutocompleteWahltag>;

export default meta;

type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    items: ["01.01.2000", "01.01.2001"],
  },
};

export const Empty: Story = {
  args: {
    items: [],
  },
};
