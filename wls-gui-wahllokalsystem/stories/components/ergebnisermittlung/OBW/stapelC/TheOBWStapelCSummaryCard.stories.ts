import type { Meta, StoryObj } from "@storybook/vue3";

import { useErgebnisseTestDataFactory } from "@tests/utils/ergebnismeldung/ergebnisseTestDataFactory.ts";
import { useWahlvorschlaegeTestDataFactory } from "@tests/utils/wahlvorschlaege/WahlvorschlaegeTestDataFactory.ts";

import TheOBWStapelCSummaryCard from "@/components/ergebnisermittlung/OBW/stapelC/TheOBWStapelCSummaryCard.vue";
import pinia from "@/plugins/pinia.ts";
import { useErgebnismeldungStore } from "@/stores/ergebnismeldungStore.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

const meta = {
  component: TheOBWStapelCSummaryCard,
  args: {},
} satisfies Meta<typeof TheOBWStapelCSummaryCard>;

const { prepareErgebnisse, prepareErgebnis } = useErgebnisseTestDataFactory();
const { prepareWahlvorschlag, prepareKandidat } =
  useWahlvorschlaegeTestDataFactory();

export default meta;
type Story = StoryObj<typeof meta>;
export const Default: Story = {
  async beforeEach() {
    const ergebnisseStore = useErgebnismeldungStore(pinia);
    ergebnisseStore.ergebnisse = [
      prepareErgebnisse()
        .bezirkUndWahlIDStapelart({
          wahlID: "wahlID",
          wahlbezirkID: "wahlbezirkID",
          stapelArt: StapelArtEnum.ObwCUngueltig,
        })
        .ergebnisse([
          prepareErgebnis().ergebnis(1).build(),
          prepareErgebnis().ergebnis(1).build(),
        ])
        .build(),
    ];
  },
  args: {
    ergebnisseStapelCUngueltig: [
      prepareErgebnis().ergebnis(1).build(),
      prepareErgebnis().ergebnis(1).build(),
    ],
    ergebnisseStapelCGueltig: [
      prepareErgebnis().wahlvorschlagID("id1").ergebnis(1).build(),
      prepareErgebnis().wahlvorschlagID("id1").ergebnis(3).build(),
      prepareErgebnis().wahlvorschlagID("id2").ergebnis(1).build(),
      prepareErgebnis().wahlvorschlagID("id2").ergebnis(4).build(),
      prepareErgebnis().wahlvorschlagID("id1").ergebnis(5).build(),
    ],
    wahlvorschlaege: [
      prepareWahlvorschlag()
        .identifikator("id1")
        .kurzname("Wahlvorschlag 1")
        .kandidaten(new Set([prepareKandidat().name("Kandidat 11").build()]))
        .build(),
      prepareWahlvorschlag()
        .identifikator("id2")
        .kurzname("Wahlvorschlag 2")
        .kandidaten(new Set([prepareKandidat().name("Kandidat 21").build()]))
        .build(),
      prepareWahlvorschlag()
        .identifikator("id3")
        .kurzname("Wahlvorschlag 3")
        .kandidaten(new Set([prepareKandidat().name("Kandidat 31").build()]))
        .build(),
    ],
  },
};

export const KeineUngueltigen: Story = {
  async beforeEach() {
    const ergebnisseStore = useErgebnismeldungStore(pinia);
    ergebnisseStore.ergebnisse = [
      prepareErgebnisse()
        .bezirkUndWahlIDStapelart({
          wahlID: "wahlID",
          wahlbezirkID: "wahlbezirkID",
          stapelArt: StapelArtEnum.ObwCUngueltig,
        })
        .ergebnisse([
          prepareErgebnis().ergebnis(1).build(),
          prepareErgebnis().ergebnis(1).build(),
        ])
        .build(),
    ];
  },
  args: {
    ergebnisseStapelCGueltig: [
      prepareErgebnis().wahlvorschlagID("id1").ergebnis(1).build(),
      prepareErgebnis().wahlvorschlagID("id1").ergebnis(3).build(),
      prepareErgebnis().wahlvorschlagID("id2").ergebnis(1).build(),
      prepareErgebnis().wahlvorschlagID("id2").ergebnis(4).build(),
      prepareErgebnis().wahlvorschlagID("id1").ergebnis(5).build(),
    ],
    wahlvorschlaege: [
      prepareWahlvorschlag()
        .identifikator("id1")
        .kurzname("Wahlvorschlag 1")
        .kandidaten(new Set([prepareKandidat().name("Kandidat 11").build()]))
        .build(),
      prepareWahlvorschlag()
        .identifikator("id2")
        .kurzname("Wahlvorschlag 2")
        .kandidaten(new Set([prepareKandidat().name("Kandidat 21").build()]))
        .build(),
      prepareWahlvorschlag()
        .identifikator("id3")
        .kurzname("Wahlvorschlag 3")
        .kandidaten(new Set([prepareKandidat().name("Kandidat 31").build()]))
        .build(),
    ],
  },
};

export const KeineGueltigen: Story = {
  async beforeEach() {
    const ergebnisseStore = useErgebnismeldungStore(pinia);
    ergebnisseStore.ergebnisse = [
      prepareErgebnisse()
        .bezirkUndWahlIDStapelart({
          wahlID: "wahlID",
          wahlbezirkID: "wahlbezirkID",
          stapelArt: StapelArtEnum.ObwCUngueltig,
        })
        .ergebnisse([
          prepareErgebnis().ergebnis(1).build(),
          prepareErgebnis().ergebnis(1).build(),
        ])
        .build(),
    ];
  },
  args: {
    ergebnisseStapelCUngueltig: [
      prepareErgebnis().ergebnis(1).build(),
      prepareErgebnis().ergebnis(1).build(),
    ],
    wahlvorschlaege: [
      prepareWahlvorschlag()
        .identifikator("id1")
        .kurzname("Wahlvorschlag 1")
        .kandidaten(new Set([prepareKandidat().name("Kandidat 11").build()]))
        .build(),
      prepareWahlvorschlag()
        .identifikator("id2")
        .kurzname("Wahlvorschlag 2")
        .kandidaten(new Set([prepareKandidat().name("Kandidat 21").build()]))
        .build(),
      prepareWahlvorschlag()
        .identifikator("id3")
        .kurzname("Wahlvorschlag 3")
        .kandidaten(new Set([prepareKandidat().name("Kandidat 31").build()]))
        .build(),
    ],
  },
};

export const WederGueltigeNochUngueltige: Story = {
  async beforeEach() {
    const ergebnisseStore = useErgebnismeldungStore(pinia);
    ergebnisseStore.ergebnisse = [
      prepareErgebnisse()
        .bezirkUndWahlIDStapelart({
          wahlID: "wahlID",
          wahlbezirkID: "wahlbezirkID",
          stapelArt: StapelArtEnum.ObwCUngueltig,
        })
        .ergebnisse([
          prepareErgebnis().ergebnis(1).build(),
          prepareErgebnis().ergebnis(1).build(),
        ])
        .build(),
    ];
  },
  args: {
    wahlvorschlaege: [
      prepareWahlvorschlag()
        .identifikator("id1")
        .kurzname("Wahlvorschlag 1")
        .kandidaten(new Set([prepareKandidat().name("Kandidat 11").build()]))
        .build(),
      prepareWahlvorschlag()
        .identifikator("id2")
        .kurzname("Wahlvorschlag 2")
        .kandidaten(new Set([prepareKandidat().name("Kandidat 21").build()]))
        .build(),
      prepareWahlvorschlag()
        .identifikator("id3")
        .kurzname("Wahlvorschlag 3")
        .kandidaten(new Set([prepareKandidat().name("Kandidat 31").build()]))
        .build(),
    ],
  },
};
