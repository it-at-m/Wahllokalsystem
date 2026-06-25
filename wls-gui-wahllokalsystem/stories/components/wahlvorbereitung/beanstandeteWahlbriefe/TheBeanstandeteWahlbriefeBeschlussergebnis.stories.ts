import type { Meta, StoryObj } from "@storybook/vue3-vite";

import { useWahlTestDataFactory } from "@tests/utils/wahl/WahlTestDataFactory.ts";

import TheBeanstandeteWahlbriefeBeschlussergebnis from "@/components/wahlhandlung/beanstandeteWahlbriefe/TheBeanstandeteWahlbriefeBeschlussergebnis.vue";
import pinia from "@/plugins/pinia.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

const { createWahl } = useWahlTestDataFactory();

const meta: Meta<typeof TheBeanstandeteWahlbriefeBeschlussergebnis> = {
  component: TheBeanstandeteWahlbriefeBeschlussergebnis,
  args: {},
};

export default meta;
type Story = StoryObj<typeof meta>;
export const Default: Story = {
  async beforeEach() {
    const store = useWahlenStore(pinia);
    store.wahlenState.wahlen = [createWahl(), createWahl()];
  },
  args: {},
};
