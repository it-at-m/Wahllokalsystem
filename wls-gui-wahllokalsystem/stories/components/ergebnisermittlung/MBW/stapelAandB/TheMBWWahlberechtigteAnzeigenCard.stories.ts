import type { Meta, StoryObj } from "@storybook/vue3";

import { useAWerteTestDataFactory } from "@tests/utils/ergebnisermittlung/aWerteTestDataFactory.ts";
import { delay, http, HttpResponse } from "msw";

import TheMBWWahlberechtigteAnzeigenCard from "@/components/ergebnisermittlung/MBW/stapelAB/TheMBWWahlberechtigteAnzeigenCard.vue";

const { createAWerte } = useAWerteTestDataFactory();

const meta = {
  component: TheMBWWahlberechtigteAnzeigenCard,
  args: {},
} satisfies Meta<typeof TheMBWWahlberechtigteAnzeigenCard>;

export default meta;

type Story = StoryObj<typeof meta>;
export const Default: Story = {
  args: {
    wahlberechtigte: createAWerte(),
  },
};
