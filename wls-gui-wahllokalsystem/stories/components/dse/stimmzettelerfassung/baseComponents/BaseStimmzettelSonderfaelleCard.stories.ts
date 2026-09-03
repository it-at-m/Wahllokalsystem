import type { Meta, StoryObj } from "@storybook/vue3-vite";

import { createPinia, setActivePinia } from "pinia";

import BaseStimmzettelSonderfaelleCard from "@/components/dse/stimmzettelerfassung/baseComponents/BaseStimmzettelSonderfaelleCard.vue";
import { SystemBeschlussgrundReasonEnum } from "@/types/dse/beschlussfassung/SystemBeschlussgrundReasonEnum.ts";

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
  args: {
    isBWB: false,
    invalidVotes: 0,
    gueltigkeit: null,
    systemBeschlussgruende: [],
    wahlvorstandBeschlussvorschlag: [],
    stimmzettelkennung: 42,
    teamId: "A",
  },
};

export const BWB: Story = {
  args: {
    isBWB: true,
    invalidVotes: 0,
    gueltigkeit: null,
    systemBeschlussgruende: [],
    wahlvorstandBeschlussvorschlag: [],
    stimmzettelkennung: 42,
    teamId: "A",
  },
};

export const BWBWithSystemBeschluesse: Story = {
  args: {
    isBWB: true,
    invalidVotes: 0,
    gueltigkeit: null,
    systemBeschlussgruende: [
      { reason: SystemBeschlussgrundReasonEnum.EinzelneStimmenUngueltig },
      { reason: SystemBeschlussgrundReasonEnum.NichtAmtlicherStimmzettel },
    ],
    wahlvorstandBeschlussvorschlag: [],
    stimmzettelkennung: 42,
    teamId: "A",
  },
};
