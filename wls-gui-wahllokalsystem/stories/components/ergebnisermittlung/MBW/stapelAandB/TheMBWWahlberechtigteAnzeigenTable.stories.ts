import type { Meta, StoryObj } from "@storybook/vue3";

import { useAWerteTestDataFactory } from "@tests/utils/ergebnisermittlung/aWerteTestDataFactory.ts";

import TheMBWWahlberechtigteAnzeigenTable from "@/components/ergebnisermittlung/MBW/stapelAB/TheMBWWahlberechtigteAnzeigenTable.vue";

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
