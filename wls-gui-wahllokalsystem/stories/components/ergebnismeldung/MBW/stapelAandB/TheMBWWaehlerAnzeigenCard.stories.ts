import type { Meta, StoryObj } from "@storybook/vue3-vite";

import TheMBWWaehlerAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelAB/TheMBWWaehlerAnzeigenCard.vue";
import pinia from "@/plugins/pinia.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { createUserLocalDevelopment } from "@/types/User.ts";

const wahlID = "wahlID";
const wahlbezirkID = "wahlbezirkID";

const meta = {
  component: TheMBWWaehlerAnzeigenCard,
  args: {},
} satisfies Meta<typeof TheMBWWaehlerAnzeigenCard>;

export default meta;

type Story = StoryObj<typeof meta>;
export const WahlbezirksartIsUWB: Story = {
  beforeEach() {
    const userStore = useUserStore(pinia);
    userStore.setUser(createUserLocalDevelopment());
  },
  args: {
    wahlId: wahlID,
    wahlbezirkId: wahlbezirkID,
  },
};

export const WahlbezirksartIsBWB: Story = {
  beforeEach() {
    const userStore = useUserStore(pinia);
    const user = createUserLocalDevelopment();
    user.wahlbezirksArt = "BWB";
    userStore.setUser(user);
  },
  args: {
    wahlId: wahlID,
    wahlbezirkId: wahlbezirkID,
  },
};
