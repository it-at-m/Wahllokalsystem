import type { Meta, StoryObj } from "@storybook/vue3";

import { fn } from "@storybook/test";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useErgebnisseTestDataFactory } from "@tests/utils/ergebnismeldung/common/ergebnisseTestDataFactory.ts";
import { useWahlvorschlaegeTestDataFactory } from "@tests/utils/wahlvorschlaege/WahlvorschlaegeTestDataFactory.ts";

import BaseRowStapelC from "@/components/ergebnismeldung/OBW/stapelC/BaseRowStapelC.vue";
import { StapelArtEnum } from "@/types/ergebnismeldung/common/StapelArtEnum.ts";

const { generateRandomNumber } = useCommonTestDataFactory();
const { prepareErgebnis } = useErgebnisseTestDataFactory();
const { prepareKandidat, prepareWahlvorschlag } =
  useWahlvorschlaegeTestDataFactory();

const meta: Meta<typeof BaseRowStapelC> = {
  component: BaseRowStapelC,
  argTypes: {
    onSelectionChanged: {
      table: {
        disable: true,
      },
    },
  },
  args: {
    index: generateRandomNumber(2),
    onSelectionChanged: fn(),
  },
  decorators: [
    (story) => {
      return {
        components: { story },
        template: "<table style='width:100%'><story /></table>",
      };
    },
  ],
};

const wahlvorschlaege = [
  prepareWahlvorschlag()
    .identifikator("wahlvorschlagId1")
    .kurzname("Wahlvorschlag1")
    .kandidaten([prepareKandidat().name("Kandidat 11").build()])
    .build(),
  prepareWahlvorschlag()
    .identifikator("wahlvorschlagId2")
    .kurzname("Wahlvorschlag2")
    .kandidaten([prepareKandidat().name("Kandidat 21").build()])
    .build(),
];

export default meta;
type Story = StoryObj<typeof meta>;
export const Default: Story = {
  args: {
    index: generateRandomNumber(2),
    stapelArt: StapelArtEnum.ObwCGueltig,
    modelValue: prepareErgebnis().ergebnis(1).build(),
    wahlvorschlaege,
  },
};

export const UngueltigSelected: Story = {
  args: {
    stapelArt: StapelArtEnum.ObwCUngueltig,
    modelValue: prepareErgebnis().ergebnis(1).build(),
    wahlvorschlaege,
  },
};
