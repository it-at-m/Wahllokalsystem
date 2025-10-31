import type { MbwErgebnisseAndWahlvorschlag } from "@/types/ergebnisermittlung/MbwErgebnisseAndWahlvorschlag.ts";
import type { Meta, StoryObj } from "@storybook/vue3";

import { useErgebnisseTestDataFactory } from "@tests/utils/ergebnismeldung/ergebnisseTestDataFactory.ts";
import { useWahlvorschlaegeTestDataFactory } from "@tests/utils/wahlvorschlaege/WahlvorschlaegeTestDataFactory.ts";
import { delay, http, HttpResponse } from "msw";

import TheMBWGueltigeStimmenAnzeigenCard from "@/components/ergebnisermittlung/MBW/stapelAB/TheMBWGueltigeStimmenAnzeigenCard.vue";

const { prepareErgebnis } = useErgebnisseTestDataFactory();
const { prepareWahlvorschlag } = useWahlvorschlaegeTestDataFactory();

const wahlID = "wahlID";
const wahlbezirkID = "wahlbezirkID";

const meta = {
  component: TheMBWGueltigeStimmenAnzeigenCard,
  args: {
    wahlID,
    wahlbezirkID,
  },
  parameters: {
    msw: {
      handlers: [
        http.all("/api/*", async () => {
          await delay(1000);
          return new HttpResponse(null, {
            status: 200,
          });
        }),
      ],
    },
  },
  decorators: [
    (story) => {
      return {
        components: { story },
        template: "<story />",
      };
    },
  ],
} satisfies Meta<typeof TheMBWGueltigeStimmenAnzeigenCard>;

export default meta;

type Story = StoryObj<typeof meta>;
export const Default: Story = {
  args: {
    modelValue: _getWahlvorschlaegeAndErgebnisseMbw(),
    wahlID: wahlID,
    wahlbezirkID: "wahlbezirkID",
  },
};

function _getWahlvorschlaegeAndErgebnisseMbw() {
  const ergebnisse: MbwErgebnisseAndWahlvorschlag[] = [];

  for (let i = 0; i < 5; i++) {
    const ergebnisA = prepareErgebnis()
      .wahlvorschlagID(`D${i}`)
      .ergebnis(i * 2)
      .build();
    const ergebnisB = prepareErgebnis()
      .wahlvorschlagID(`D${i}`)
      .ergebnis(i + 2)
      .build();
    ergebnisse[i] = {
      ergebnisStapelA: ergebnisA,
      ergebnisStapelB: ergebnisB,
      wahlvorschlag: prepareWahlvorschlag()
        .identifikator(`D${i}`)
        .ordnungszahl(i)
        .build(),
    };
  }
  return ergebnisse;
}
