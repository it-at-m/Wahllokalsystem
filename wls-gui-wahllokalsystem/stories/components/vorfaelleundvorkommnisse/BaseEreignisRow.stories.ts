import type { Meta, StoryObj } from "@storybook/vue3";

import { fn } from "@storybook/test";
import { useVorfaelleundvorkommnisseTestDataFactory } from "@tests/utils/vorfaelleundvorkommnisse/VorfaelleundvorkommnisseTestDataFactory.ts";

import BaseEreignisRow from "@/components/vorfaelleundvorkommnisse/BaseEreignisRow.vue";

const meta: Meta<typeof BaseEreignisRow> = {
  component: BaseEreignisRow,
  argTypes: {
    onDelete: {
      table: {
        disable: true,
      },
    },
  },
  args: {
    onDelete: fn(),
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    lineNumber: 42,
    modelValue: useVorfaelleundvorkommnisseTestDataFactory().createEreignis(),
  },
};
