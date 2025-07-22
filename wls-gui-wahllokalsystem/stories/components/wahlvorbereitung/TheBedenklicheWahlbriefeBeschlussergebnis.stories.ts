import type { Meta, StoryObj } from "@storybook/vue3";

import TheBedenklicheWahlbriefeBeschlussergebnis from "@/components/wahlvorbereitung/TheBedenklicheWahlbriefeBeschlussergebnis.vue";

const meta: Meta<typeof TheBedenklicheWahlbriefeBeschlussergebnis> = {
  component: TheBedenklicheWahlbriefeBeschlussergebnis,
  args: {},
};

export default meta;
type Story = StoryObj<typeof meta>;
export const Default: Story = {
  args: {},
};
