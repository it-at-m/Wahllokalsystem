import type { Meta, StoryObj } from "@storybook/vue3";

import { createPinia, setActivePinia, storeToRefs } from "pinia";

import TheUWBStimmabgabevermerkeEingenommeneWahlscheineTable from "@/components/stimmabgabevermerke/TheUWBStimmabgabevermerkeEingenommeneWahlscheineTable.vue";
import pinia from "@/plugins/pinia.ts";
import { useStimmabgabevermerkeStore } from "@/stores/stimmabgabevermerkeStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { EingenommenerWahlscheinStimmzettelartEnum } from "@/types/stimmabgabermerke/EingenommenerWahlscheinStimmzettelartEnum.ts";
import { WahlWahlartEnum } from "@/types/wahl/WahlWahlartEnum.ts";

const meta = {
  component: TheUWBStimmabgabevermerkeEingenommeneWahlscheineTable,
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
} satisfies Meta<typeof TheUWBStimmabgabevermerkeEingenommeneWahlscheineTable>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  async beforeEach() {
    const { stimmabgabevermerke } = storeToRefs(
      useStimmabgabevermerkeStore(pinia)
    );

    stimmabgabevermerke.value = {
      anzahlBlaetter: 0,
      waehlerverzeichnisNummer: 0,
      wahlbezirkID: "wahlbezirkID",
      wahldaten: new Set([
        {
          wahlbezirkID: "wahlbezirkID",
          wahlID: "wahlID",
          waehlerverzeichnisNummer: 0,
          eingenommeneWahlscheine: new Map([
            [EingenommenerWahlscheinStimmzettelartEnum.Klein, 50],
          ]),
        },
        {
          wahlbezirkID: "wahlbezirkID2",
          wahlID: "wahlID2",
          waehlerverzeichnisNummer: 0,
          eingenommeneWahlscheine: new Map([
            [EingenommenerWahlscheinStimmzettelartEnum.Klein, 70],
          ]),
        },
      ]),
    };
    const { wahlen } = storeToRefs(useWahlenStore(pinia));
    wahlen.value = [
      {
        beanstandeteWahlbriefe: [],
        farbe: undefined,
        name: "NameWahlOne",
        nummer: undefined,
        reihenfolge: 0,
        waehlerverzeichnisNummer: 0,
        wahlID: "wahlID",
        wahlart: WahlWahlartEnum.Beb,
        wahltag: "",
      },
      {
        beanstandeteWahlbriefe: [],
        farbe: undefined,
        name: "NameWahlTwo",
        nummer: undefined,
        reihenfolge: 0,
        waehlerverzeichnisNummer: 0,
        wahlID: "wahlID2",
        wahlart: WahlWahlartEnum.Beb,
        wahltag: "",
      },
    ];
  },
  args: {},
};
