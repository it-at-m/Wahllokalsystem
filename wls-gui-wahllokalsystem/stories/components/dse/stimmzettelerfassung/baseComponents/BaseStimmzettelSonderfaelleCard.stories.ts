import type { Meta, StoryObj } from "@storybook/vue3-vite";

import { createPinia, setActivePinia } from "pinia";

import BaseStimmzettelSonderfaelleCard from "@/components/dse/stimmzettelerfassung/baseComponents/BaseStimmzettelSonderfaelleCard.vue";
import pinia from "@/plugins/pinia.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { SystemBeschlussgrundReasonEnum } from "@/types/dse/beschlussfassung/SystemBeschlussgrundReasonEnum.ts";
import { createUserLocalDevelopment } from "@/types/User.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const meta = {
  component: BaseStimmzettelSonderfaelleCard,
  argTypes: {},
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
    modelValueInvalidVotes: 0,
    modelValueGueltigkeit: null,
    systemBeschlussgruende: [],
    modelValueWahlvorstandBeschlussvorschlag: [],
    stimmzettelkennung: 42,
    teamId: "A",
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
    modelValueInvalidVotes: 0,
    modelValueGueltigkeit: null,
    systemBeschlussgruende: [],
    modelValueWahlvorstandBeschlussvorschlag: [],
    stimmzettelkennung: 42,
    teamId: "A",
  },
};

export const BWBWithSystemBeschluesse: Story = {
  async beforeEach() {
    const store = useUserStore(pinia);
    const user = createUserLocalDevelopment();
    user.wahlbezirksArt = WahlbezirksArtEnum.BWB;
    store.setUser(user);
  },
  args: {
    modelValueInvalidVotes: 0,
    modelValueGueltigkeit: null,
    systemBeschlussgruende: [
      { reason: SystemBeschlussgrundReasonEnum.EinzelneStimmenUngueltig },
      { reason: SystemBeschlussgrundReasonEnum.NichtAmtlicherStimmzettel },
    ],
    modelValueWahlvorstandBeschlussvorschlag: [],
    stimmzettelkennung: 42,
    teamId: "A",
  },
};
