import type { Meta, StoryObj } from "@storybook/vue3";

import TheBeanstandeteWahlbriefeBeschlussergebnis from "@/components/wahlvorbereitung/beanstandeteWahlbriefe/TheBeanstandeteWahlbriefeBeschlussergebnis.vue";

const meta: Meta<typeof TheBeanstandeteWahlbriefeBeschlussergebnis> = {
  component: TheBeanstandeteWahlbriefeBeschlussergebnis,
  args: {},
};

export default meta;
type Story = StoryObj<typeof meta>;
export const Default: Story = {
  args: {},
};
