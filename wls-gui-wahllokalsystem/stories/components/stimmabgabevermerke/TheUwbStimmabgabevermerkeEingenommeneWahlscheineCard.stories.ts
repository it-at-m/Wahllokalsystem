import type { Stimmabgabevermerke } from "@/types/stimmabgabevermerke/Stimmabgabevermerke.ts";
import type { Meta, StoryObj } from "@storybook/vue3-vite";

import { createPinia, setActivePinia, storeToRefs } from "pinia";

import TheUWBStimmabgabevermerkeEingenommeneWahlscheineCard from "@/components/stimmabgabevermerke/TheUWBStimmabgabevermerkeEingenommeneWahlscheineCard.vue";
import TheUWBStimmabgabevermerkeEingenommeneWahlscheineTable from "@/components/stimmabgabevermerke/TheUWBStimmabgabevermerkeEingenommeneWahlscheineTable.vue";
import pinia from "@/plugins/pinia.ts";
import { useStimmabgabevermerkeStore } from "@/stores/stimmabgabevermerkeStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { EingenommenerWahlscheinStimmzettelartEnum } from "@/types/stimmabgabevermerke/EingenommenerWahlscheinStimmzettelartEnum.ts";
import { StimmzettelStimmzettelartEnum } from "@/types/stimmabgabevermerke/StimmzettelStimmzettelartEnum.ts";
import { WahlWahlartEnum } from "@/types/wahl/WahlWahlartEnum.ts";

const meta = {
  component: TheUWBStimmabgabevermerkeEingenommeneWahlscheineCard,
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

    const stimmabgabevermerkeOne: Stimmabgabevermerke = {
      anzahlBlaetter: 0,
      waehlerverzeichnisNummer: 0,
      wahlbezirkID: "wahlbezikID1",
      wahldaten: [
        {
          wahlbezirkID: "wahlbezikID1",
          wahlID: "wahlID",
          waehlerverzeichnisNummer: 0,
          eingenommeneWahlscheine: new Map([
            [EingenommenerWahlscheinStimmzettelartEnum.Klein, 50],
          ]),
          vermerke: [
            {
              blattnummer: 2,
              stimmzettel: [
                {
                  anzahl: 20,
                  stimmzettelart: StimmzettelStimmzettelartEnum.Klein,
                },
              ],
            },
          ],
        },
      ],
    };

    const stimmabgabevermerkeTwo: Stimmabgabevermerke = {
      anzahlBlaetter: 0,
      waehlerverzeichnisNummer: 0,
      wahlbezirkID: "wahlbezirkID2",
      wahldaten: [
        {
          wahlbezirkID: "wahlbezirkID2",
          wahlID: "wahlID2",
          waehlerverzeichnisNummer: 0,
          eingenommeneWahlscheine: new Map([
            [EingenommenerWahlscheinStimmzettelartEnum.Klein, 70],
          ]),
          vermerke: [
            {
              blattnummer: 2,
              stimmzettel: [
                {
                  anzahl: 20,
                  stimmzettelart: StimmzettelStimmzettelartEnum.Klein,
                },
              ],
            },
          ],
        },
      ],
    };

    stimmabgabevermerke.value = [
      stimmabgabevermerkeOne,
      stimmabgabevermerkeTwo,
    ];
    const { wahlenState } = storeToRefs(useWahlenStore(pinia));
    wahlenState.value.wahlen = [
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
        stimmzettelumschlaege: {
          anzahlWaehler: 0,
        },
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
        stimmzettelumschlaege: {
          anzahlWaehler: 0,
        },
      },
    ];
  },
  args: {},
};
