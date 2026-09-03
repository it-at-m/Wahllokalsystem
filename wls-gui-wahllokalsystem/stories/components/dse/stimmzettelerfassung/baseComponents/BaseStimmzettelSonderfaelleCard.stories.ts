import type { Stimmzettel } from "@/types/dse/stimmzettelerfassung/Stimmzettel.ts";
import type { Meta, StoryObj } from "@storybook/vue3-vite";

import { createPinia, setActivePinia } from "pinia";

import BaseStimmzettelSonderfaelleCard from "@/components/dse/stimmzettelerfassung/baseComponents/BaseStimmzettelSonderfaelleCard.vue";
import pinia from "@/plugins/pinia.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { createUserLocalDevelopment } from "@/types/User.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

function createDummyStimmzettel(): Stimmzettel {
  return {
    stimmzettelkennung: 0,
    wahlvorschlaege: [],
    wahlvorstandBeschlussvorschlag: [],
    systemBeschlussvorschlag: [],
    beschlussfassung: null,
    invalideVotes: 0,
    gueltigkeit: null,
  };
}

const meta = {
  component: BaseStimmzettelSonderfaelleCard,
  argTypes: {
    modelValue: {
      table: {
        category: "props",
        type: { summary: "Stimmzettel" },
      },
    },
    "onUpdate:modelValue": {
      name: "update:modelValue",
      table: {
        category: "events",
      },
    },
  },
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
} satisfies Meta<typeof BaseStimmzettelSonderfaelleCard>;

export default meta;

type Story = StoryObj<typeof meta>;
export const UWB: Story = {
  async beforeEach() {
    const store = useUserStore(pinia);
    const user = createUserLocalDevelopment();
    user.wahlbezirksArt = WahlbezirksArtEnum.UWB;
    store.setUser(user);
  },
  args: {
    modelValue: createDummyStimmzettel(),
    modelValueInvalidVotes: 0,
    modelValueGueltigkeit: null,
  },
};
export const BWB: Story = {
  async beforeEach() {
    const store = useUserStore(pinia);
    const user = createUserLocalDevelopment();
    user.wahlbezirksArt = WahlbezirksArtEnum.BWB;
    store.setUser(user);
  },
  args: {
    modelValue: createDummyStimmzettel(),
    modelValueInvalidVotes: 0,
    modelValueGueltigkeit: null,
  },
};
