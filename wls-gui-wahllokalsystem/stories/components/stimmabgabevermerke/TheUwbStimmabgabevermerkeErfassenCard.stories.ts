import type { Meta, StoryObj } from "@storybook/vue3";

import { createPinia, setActivePinia, storeToRefs } from "pinia";

import TheUWBStimmabgabevermerkeErfassenCard from "@/components/stimmabgabevermerke/TheUWBStimmabgabevermerkeErfassenCard.vue";
import pinia from "@/plugins/pinia.ts";
import { useStimmabgabevermerkeStore } from "@/stores/stimmabgabevermerkeStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { EingenommenerWahlscheinStimmzettelartEnum } from "@/types/stimmabgabevermerke/EingenommenerWahlscheinStimmzettelartEnum.ts";
import { StimmzettelStimmzettelartEnum } from "@/types/stimmabgabevermerke/StimmzettelStimmzettelartEnum.ts";
import { WahlWahlartEnum } from "@/types/wahl/WahlWahlartEnum.ts";

const meta = {
  component: TheUWBStimmabgabevermerkeErfassenCard,
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
} satisfies Meta<typeof TheUWBStimmabgabevermerkeErfassenCard>;

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
      wahldaten: new Set([
        {
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
                  anzahl: 15,
                  stimmzettelart: StimmzettelStimmzettelartEnum.Klein,
                },
              ],
            },
          ],
        },
        {
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
