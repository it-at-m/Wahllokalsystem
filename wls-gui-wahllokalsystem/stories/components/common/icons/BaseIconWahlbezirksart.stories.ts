import type { Meta, StoryObj } from "@storybook/vue3";

import { createPinia, setActivePinia } from "pinia";

import BaseIconWahlbezirksart from "@/components/common/icons/BaseIconWahlbezirksart.vue";
import pinia from "@/plugins/pinia";
import { useUserStore } from "@/stores/user.ts";
import User from "@/types/User.ts";

const meta = {
  component: BaseIconWahlbezirksart,
  args: {},
  decorators: [
    (story) => {
      const pinia = createPinia();
      setActivePinia(pinia);
      return {
        component: { story },
        template: "<story />",
      };
    },
  ],
} satisfies Meta<typeof BaseIconWahlbezirksart>;

export default meta;
type Story = StoryObj<typeof meta>;

export const WahlartIsUWB: Story = {
  async beforeEach() {
    const store = useUserStore(pinia);
    const u = new User();
    u.wahlbezirksArt = "UWB";
    store.setUser(u);
  },
};

export const WahlartIsBWB: Story = {
  async beforeEach() {
    const store = useUserStore(pinia);
    const u = new User();
    u.wahlbezirksArt = "BWB";
    store.setUser(u);
  },
};
