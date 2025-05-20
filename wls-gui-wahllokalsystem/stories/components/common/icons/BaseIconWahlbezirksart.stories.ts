import type { User } from "@/types/User.ts";
import type { Meta, StoryObj } from "@storybook/vue3";

import { createPinia, setActivePinia } from "pinia";

import BaseIconWahlbezirksart from "@/components/common/icons/BaseIconWahlbezirksart.vue";
import pinia from "@/plugins/pinia";
import { useUserStore } from "@/stores/userStore.ts";

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
    const userWithUWB = new User();
    userWithUWB.wahlbezirksArt = "UWB";
    store.setUser(userWithUWB);
  },
};

export const WahlartIsBWB: Story = {
  async beforeEach() {
    const store = useUserStore(pinia);
    const userWithBWB = new User();
    userWithBWB.wahlbezirksArt = "BWB";
    store.setUser(userWithBWB);
  },
};

export const WahlartIsUndefined: Story = {
  async beforeEach() {
    const store = useUserStore(pinia);
    const userWithUndefinedWahlart = new User();
    store.setUser(userWithUndefinedWahlart);
  },
};
