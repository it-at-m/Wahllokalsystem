import type { Meta, StoryObj } from "@storybook/vue3-vite";

import { createPinia, setActivePinia } from "pinia";

import BaseIconWahlbezirksart from "@/components/common/icons/BaseIconWahlbezirksart.vue";
import pinia from "@/plugins/pinia";
import { useUserStore } from "@/stores/userStore.ts";
import { createUserLocalDevelopment } from "@/types/User.ts";

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

export const WahlbezirksartIsUWB: Story = {
  async beforeEach() {
    const store = useUserStore(pinia);
    store.setUser(createUserLocalDevelopment());
  },
};

export const WahlbezirksartIsBWB: Story = {
  async beforeEach() {
    const store = useUserStore(pinia);
    const user = createUserLocalDevelopment();
    user.wahlbezirksArt = "BWB";
    store.setUser(user);
  },
};
