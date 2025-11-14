import type { Meta, StoryObj } from "@storybook/vue3";

import { useErgebnisseTestDataFactory } from "@tests/utils/ergebnismeldung/ergebnisseTestDataFactory.ts";
import { useWahlvorschlaegeTestDataFactory } from "@tests/utils/wahlvorschlaege/WahlvorschlaegeTestDataFactory.ts";

import TheOBWStapelCSummaryCard from "@/components/ergebnisermittlung/OBW/stapelC/TheOBWStapelCSummaryCard.vue";
import pinia from "@/plugins/pinia.ts";
import { useErgebnismeldungStore } from "@/stores/ergebnismeldungStore.ts";
import { useWahlvorschlaegeStore } from "@/stores/wahlvorschlaegeStore.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

const wahlID = "wahlID";
const wahlbezirkID = "wahlbezirkID";

const meta = {
  component: TheOBWStapelCSummaryCard,
  args: {
    wahlId: wahlID,
    wahlbezirkId: wahlbezirkID,
  },
} satisfies Meta<typeof TheOBWStapelCSummaryCard>;

const { prepareErgebnis, prepareErgebnisse } = useErgebnisseTestDataFactory();
const { prepareWahlvorschlag, prepareWahlvorschlaege, prepareKandidat } =
  useWahlvorschlaegeTestDataFactory();

const defaultErgebnisseObwCUngueltig = prepareErgebnisse()
  .bezirkUndWahlIDStapelart({
    wahlbezirkID,
    wahlID,
    stapelArt: StapelArtEnum.ObwCUngueltig,
  })
  .ergebnisse([
    prepareErgebnis().ergebnis(1).build(),
    prepareErgebnis().ergebnis(1).build(),
  ])
  .build();
const defaultErgebnisseObwCGueltig = prepareErgebnisse()
  .bezirkUndWahlIDStapelart({
    wahlbezirkID,
    wahlID,
    stapelArt: StapelArtEnum.ObwCGueltig,
  })
  .ergebnisse([
    prepareErgebnis().wahlvorschlagID("id1").ergebnis(1).build(),
    prepareErgebnis().wahlvorschlagID("id1").ergebnis(3).build(),
    prepareErgebnis().wahlvorschlagID("id2").ergebnis(1).build(),
    prepareErgebnis().wahlvorschlagID("id2").ergebnis(4).build(),
    prepareErgebnis().wahlvorschlagID("id1").ergebnis(5).build(),
  ])
  .build();

const defaultWahlvorschlaege = prepareWahlvorschlaege()
  .wahlID(wahlID)
  .wahlbezirkID(wahlbezirkID)
  .wahlvorschlaege([
    prepareWahlvorschlag()
      .identifikator("id1")
      .kurzname("Wahlvorschlag 1")
      .kandidaten([prepareKandidat().name("Kandidat 11").build()])
      .build(),
    prepareWahlvorschlag()
      .identifikator("id2")
      .kurzname("Wahlvorschlag 2")
      .kandidaten([prepareKandidat().name("Kandidat 21").build()])
      .build(),
    prepareWahlvorschlag()
      .identifikator("id3")
      .kurzname("Wahlvorschlag 3")
      .kandidaten([prepareKandidat().name("Kandidat 31").build()])
      .build(),
  ])
  .build();

export default meta;
type Story = StoryObj<typeof meta>;
export const Default: Story = {
  async beforeEach() {
    const ergebnismeldungsStore = useErgebnismeldungStore(pinia);
    ergebnismeldungsStore.ergebnisse = [
      defaultErgebnisseObwCUngueltig,
      defaultErgebnisseObwCGueltig,
    ];

    const wahlvorschlaegeStore = useWahlvorschlaegeStore(pinia);
    wahlvorschlaegeStore.wahlvorschlaege = [defaultWahlvorschlaege];
  },
};

export const KeineUngueltigen: Story = {
  async beforeEach() {
    const ergebnismeldungsStore = useErgebnismeldungStore(pinia);
    ergebnismeldungsStore.ergebnisse = [defaultErgebnisseObwCGueltig];

    const wahlvorschlaegeStore = useWahlvorschlaegeStore(pinia);
    wahlvorschlaegeStore.wahlvorschlaege = [defaultWahlvorschlaege];
  },
};

export const KeineGueltigen: Story = {
  async beforeEach() {
    const ergebnismeldungsStore = useErgebnismeldungStore(pinia);
    ergebnismeldungsStore.ergebnisse = [defaultErgebnisseObwCUngueltig];

    const wahlvorschlaegeStore = useWahlvorschlaegeStore(pinia);
    wahlvorschlaegeStore.wahlvorschlaege = [defaultWahlvorschlaege];
  },
};

export const WederGueltigeNochUngueltige: Story = {
  async beforeEach() {
    const ergebnismeldungsStore = useErgebnismeldungStore(pinia);
    ergebnismeldungsStore.ergebnisse = [];

    const wahlvorschlaegeStore = useWahlvorschlaegeStore(pinia);
    wahlvorschlaegeStore.wahlvorschlaege = [defaultWahlvorschlaege];
  },
};
