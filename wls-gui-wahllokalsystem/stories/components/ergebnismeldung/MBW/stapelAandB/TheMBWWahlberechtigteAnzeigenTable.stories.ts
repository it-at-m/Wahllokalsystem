import type { Meta, StoryObj } from "@storybook/vue3-vite";

import { useAWerteTestDataFactory } from "@tests/utils/ergebnismeldung/common/aWerteTestDataFactory.ts";

import TheMBWWahlberechtigteAnzeigenTable from "@/components/ergebnismeldung/MBW/stapelAB/TheMBWWahlberechtigteAnzeigenTable.vue";

const { createAWerte } = useAWerteTestDataFactory();

const meta = {
  component: TheMBWWahlberechtigteAnzeigenTable,
  args: {},
} satisfies Meta<typeof TheMBWWahlberechtigteAnzeigenTable>;

export default meta;

type Story = StoryObj<typeof meta>;
export const Default: Story = {
  args: {
    wahlberechtigte: createAWerte(),
  },
};
