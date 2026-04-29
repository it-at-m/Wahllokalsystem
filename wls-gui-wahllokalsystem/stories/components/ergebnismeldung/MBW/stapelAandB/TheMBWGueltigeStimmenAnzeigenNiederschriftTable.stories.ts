import type { WahlvorschlagWithKandidatenErgebnissen } from "@/types/ergebnismeldung/common/WahlvorschlagWithKandidatenErgebnissen.ts";
import type { MbwErgebnisseAndWahlvorschlag } from "@/types/ergebnismeldung/MBW/MbwErgebnisseAndWahlvorschlag.ts";
import type { Meta, StoryObj } from "@storybook/vue3";

import { useErgebnisseTestDataFactory } from "@tests/utils/ergebnismeldung/common/ergebnisseTestDataFactory.ts";
import { useWahlvorschlaegeTestDataFactory } from "@tests/utils/wahlvorschlaege/WahlvorschlaegeTestDataFactory.ts";

import TheMBWGueltigeStimmenAnzeigenNiederschriftTable from "@/components/ergebnismeldung/MBW/stapelAB/TheMBWGueltigeStimmenAnzeigenNiederschriftTable.vue";

const { prepareErgebnis } = useErgebnisseTestDataFactory();
const { prepareWahlvorschlag, prepareKandidat } =
  useWahlvorschlaegeTestDataFactory();

const meta = {
  component: TheMBWGueltigeStimmenAnzeigenNiederschriftTable,
  args: {},
} satisfies Meta<typeof TheMBWGueltigeStimmenAnzeigenNiederschriftTable>;

export default meta;

type Story = StoryObj<typeof meta>;
export const Default: Story = {
  args: {
    ergebnisseAndWahlvorschlaege: _getWahlvorschlaegeAndErgebnisseMbw(),
    wahlvorschlaegeKandidatenErgebnisse:
      _getWahlvorschlaegeKandidatenErgebnisse(),
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

function _getWahlvorschlaegeKandidatenErgebnisse() {
  const ergebnisse: WahlvorschlagWithKandidatenErgebnissen[] = [];

  for (let i = 0; i < 5; i++) {
    ergebnisse[i] = {
      kurzname: `Kurz${i}`,
      identifikator: `D${i}`,
      ordnungszahl: i,
      kandidatenErgebnisse: [
        {
          ergebnis: prepareErgebnis().build(),
          kandidat: prepareKandidat().build(),
        },
      ],
    };
  }
  return ergebnisse;
}
