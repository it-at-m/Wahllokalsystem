import type { ErgebnisAndKandidat } from "@/types/ergebnismeldung/common/ErgebnisAndKandidat.ts";
import type { Meta, StoryObj } from "@storybook/vue3";

import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useErgebnisseTestDataFactory } from "@tests/utils/ergebnismeldung/common/ergebnisseTestDataFactory.ts";
import { useWahlvorschlaegeTestDataFactory } from "@tests/utils/wahlvorschlaege/WahlvorschlaegeTestDataFactory.ts";

import BaseTableWahlvorschlagKandidatenStimmenErfassen from "@/components/ergebnismeldung/MBW/stapelBC/BaseTableWahlvorschlagKandidatenStimmenErfassen.vue";

const meta = {
  component: BaseTableWahlvorschlagKandidatenStimmenErfassen,
  args: {},
} satisfies Meta<typeof BaseTableWahlvorschlagKandidatenStimmenErfassen>;

const { prepareKandidat } = useWahlvorschlaegeTestDataFactory();
const { prepareErgebnis } = useErgebnisseTestDataFactory();
const { generateRandomNumberInRange } = useCommonTestDataFactory();

export default meta;
type Story = StoryObj<typeof meta>;
export const Default: Story = {
  args: {
    wahlvorschlagNummer: generateRandomNumberInRange(1, 12),
    modelValue: createErgebnisseAndKandidaten(),
  },
};

function createErgebnisseAndKandidaten(size = 11): ErgebnisAndKandidat[] {
  const result: ErgebnisAndKandidat[] = [];
  for (let i = 1; i <= size; i++) {
    const kandidat = prepareKandidat().listenposition(i).build();
    const ergebnis = prepareErgebnis()
      .kandidatID(kandidat.identifikator)
      .ergebnis(null)
      .build();
    result.push({
      ergebnis: ergebnis,
      kandidat: kandidat,
    });
  }

  return result;
}
