import type { MbwErgebnisseAndWahlvorschlag } from "@/types/ergebnismeldung/MBW/MbwErgebnisseAndWahlvorschlag.ts";
import type { Meta, StoryObj } from "@storybook/vue3-vite";

import { useErgebnisseTestDataFactory } from "@tests/utils/ergebnismeldung/common/ergebnisseTestDataFactory.ts";
import { useWahlvorschlaegeTestDataFactory } from "@tests/utils/wahlvorschlaege/WahlvorschlaegeTestDataFactory.ts";

import TheMbwGueltigeStimmenAnzeigenSchnellmeldungTable from "@/components/ergebnismeldung/MBW/stapelAB/TheMBWGueltigeStimmenAnzeigenSchnellmeldungTable.vue";

const { prepareErgebnis } = useErgebnisseTestDataFactory();
const { prepareWahlvorschlag } = useWahlvorschlaegeTestDataFactory();

const meta = {
  component: TheMbwGueltigeStimmenAnzeigenSchnellmeldungTable,
  args: {},
} satisfies Meta<typeof TheMbwGueltigeStimmenAnzeigenSchnellmeldungTable>;

export default meta;

type Story = StoryObj<typeof meta>;
export const Default: Story = {
  args: {
    ergebnisseAndWahlvorschlaege: _getWahlvorschlaegeAndErgebnisseMbw(),
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
