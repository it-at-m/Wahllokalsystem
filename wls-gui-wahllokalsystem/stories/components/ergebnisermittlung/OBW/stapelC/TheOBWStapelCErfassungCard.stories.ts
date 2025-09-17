import type { Meta, StoryObj } from "@storybook/vue3";

import { useErgebnisseTestDataFactory } from "@tests/utils/ergebnismeldung/ergebnisseTestDataFactory.ts";
import { useWahlvorschlaegeTestDataFactory } from "@tests/utils/wahlvorschlaege/WahlvorschlaegeTestDataFactory.ts";

import TheOBWStapelCErfassungCard from "@/components/ergebnisermittlung/OBW/stapelC/TheOBWStapelCErfassungCard.vue";
import pinia from "@/plugins/pinia";
import { useErgebnismeldungStore } from "@/stores/ergebnismeldungStore.ts";
import { useWahlvorschlaegeStore } from "@/stores/wahlvorschlaegeStore.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

const { prepareErgebnisse, prepareErgebnis } = useErgebnisseTestDataFactory();
const { prepareKandidat, prepareWahlvorschlaege, prepareWahlvorschlag } =
  useWahlvorschlaegeTestDataFactory();

const meta = {
  component: TheOBWStapelCErfassungCard,
  args: {},
} satisfies Meta<typeof TheOBWStapelCErfassungCard>;

const wahlID = "wahlID";
const wahlbezirkID = "wahlbezirkID";

export default meta;
type Story = StoryObj<typeof meta>;
export const Default: Story = {
  loaders: [
    async () => {
      const ergebnismeldungsStore = useErgebnismeldungStore(pinia);

      ergebnismeldungsStore.ergebnisse = [
        prepareErgebnisse()
          .bezirkUndWahlIDStapelart({
            wahlID,
            wahlbezirkID,
            stapelArt: StapelArtEnum.ObwCUngueltig,
          })
          .ergebnisse([
            prepareErgebnis().ergebnis(1).numIndex(1).build(),
            prepareErgebnis().ergebnis(1).numIndex(3).build(),
          ])
          .build(),
        prepareErgebnisse()
          .bezirkUndWahlIDStapelart({
            wahlID,
            wahlbezirkID,
            stapelArt: StapelArtEnum.ObwCGueltig,
          })
          .ergebnisse([
            prepareErgebnis()
              .ergebnis(1)
              .wahlvorschlagID("wahlvorschlag1")
              .numIndex(2)
              .build(),
            prepareErgebnis()
              .ergebnis(1)
              .wahlvorschlagID("wahlvorschlag2")
              .numIndex(4)
              .build(),
            prepareErgebnis()
              .ergebnis(1)
              .wahlvorschlagID("wahlvorschlag1")
              .numIndex(5)
              .build(),
          ])
          .build(),
      ];

      const wahlvorschlaegStore = useWahlvorschlaegeStore(pinia);
      wahlvorschlaegStore.wahlvorschlaege = [
        prepareWahlvorschlaege()
          .wahlID(wahlID)
          .wahlbezirkID(wahlbezirkID)
          .wahlvorschlaege(
            new Set([
              prepareWahlvorschlag()
                .identifikator("wahlvorschlag1")
                .kurzname("Wahlvorschlag1")
                .kandidaten(
                  new Set([prepareKandidat().name("Kandidat 11").build()])
                )
                .build(),
              prepareWahlvorschlag()
                .identifikator("wahlvorschlag2")
                .kurzname("Wahlvorschlag2")
                .kandidaten(
                  new Set([prepareKandidat().name("Kandidat 21").build()])
                )
                .build(),
            ])
          )
          .build(),
      ];
    },
  ],
  args: {
    wahlID: wahlID,
    wahlbezirkID: wahlbezirkID,
  },
};
