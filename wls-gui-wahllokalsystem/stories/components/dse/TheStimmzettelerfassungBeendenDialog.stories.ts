import type { Meta, StoryObj } from "@storybook/vue3-vite";

import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

import TheStimmzettelerfassungBeendenDialog from "@/components/dse/TheStimmzettelerfassungBeendenDialog.vue";

const { generateRandomString } = useCommonTestDataFactory();

const meta = {
  component: TheStimmzettelerfassungBeendenDialog,
  args: {},
} satisfies Meta<typeof TheStimmzettelerfassungBeendenDialog>;

export default meta;
type Story = StoryObj<typeof meta>;
export const Default: Story = {
  args: {
    modelValue: false,
    wahlId: generateRandomString(10),
    wahlbezirkId: generateRandomString(10),
  },
};
