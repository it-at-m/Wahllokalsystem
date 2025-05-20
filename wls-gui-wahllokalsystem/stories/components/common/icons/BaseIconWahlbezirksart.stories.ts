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

export const WahlbezirksartIsUWB: Story = {
  async beforeEach() {
    const store = useUserStore(pinia);
    store.setUser({
      username: "Local Development User",
      email: "",
      wahlbezirksArt: "UWB",
      pin: "",
      authorities: new Set<string>(),
    });
  },
};

export const WahlbezirksartIsBWB: Story = {
  async beforeEach() {
    const store = useUserStore(pinia);
    store.setUser({
      username: "Local Development User",
      email: "",
      wahlbezirksArt: "BWB",
      pin: "",
      authorities: new Set<string>(),
    });
  },
};
