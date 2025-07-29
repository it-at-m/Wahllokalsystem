import type { Meta, StoryObj } from "@storybook/vue3";

import { useVorfaelleundvorkommnisseTestDataFactory } from "@tests/utils/vorfaelleundvorkommnisse/VorfaelleundvorkommnisseTestDataFactory.ts";

import BaseEreignisRow from "@/components/vorfaelleundvorkommnisse/BaseEreignisRow.vue";

const meta = {
  component: BaseEreignisRow,
  args: {},
} satisfies Meta<typeof BaseEreignisRow>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    lineNumber: 42,
    modelValue: useVorfaelleundvorkommnisseTestDataFactory().createEreignis(),
  },
};
