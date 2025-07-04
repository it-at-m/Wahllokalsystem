import type { Meta, StoryObj } from "@storybook/vue3";

import WahlbriefZulassungBeschlussergebnis from "@/components/wahlbriefzulassung/WahlbriefZulassungBeschlussergebnis.vue";

const meta: Meta<typeof WahlbriefZulassungBeschlussergebnis> = {
  component: WahlbriefZulassungBeschlussergebnis,
  args: {},
};

export default meta;
type Story = StoryObj<typeof meta>;
export const Default: Story = {
  args: {},
};
