import type { Meta, StoryObj } from "@storybook/vue3";

import { fn } from "@storybook/test";
import { useVorfaelleundvorkommnisseTestDataFactory } from "@tests/utils/vorfaelleundvorkommnisse/VorfaelleundvorkommnisseTestDataFactory.ts";

import BaseEreignisRow from "@/components/vorfaelleundvorkommnisse/BaseEreignisRow.vue";

const meta: Meta<typeof BaseEreignisRow> = {
  component: BaseEreignisRow,
  argTypes: {
    modelValue: {
      description: "Verwaltete Ereigniszeile",
      table: {
        category: "props",
        type: {
          summary: `Ereignis`,
        },
        required: true,
      },
    },
    onDelete: {
      table: {
        disable: true,
      },
    },
  },
  args: {
    onDelete: fn(),
    onUhrzeitChanged: fn(),
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    lineNumber: 1,
    modelValue: useVorfaelleundvorkommnisseTestDataFactory().createEreignis(),
  },
};
