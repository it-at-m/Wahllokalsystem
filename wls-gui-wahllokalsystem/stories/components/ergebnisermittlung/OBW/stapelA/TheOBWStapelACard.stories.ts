import type { Meta, StoryObj } from "@storybook/vue3";

import { useErgebnisseTestDataFactory } from "@tests/utils/ergebnismeldung/ergebnisseTestDataFactory.ts";
import { useUserTestDataFactory } from "@tests/utils/user/UserTestDataFactory.ts";
import { useWahlvorschlaegeTestDataFactory } from "@tests/utils/wahlvorschlaege/WahlvorschlaegeTestDataFactory.ts";
import { flushPromises } from "@vue/test-utils";

import TheOBWStapelACard from "@/components/ergebnisermittlung/OBW/stapelA/TheOBWStapelACard.vue";
import pinia from "@/plugins/pinia.ts";
import { useErgebnismeldungStore } from "@/stores/ergebnismeldungStore.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlvorschlaegeStore } from "@/stores/wahlvorschlaegeStore.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

const { prepareUser } = useUserTestDataFactory();
const { prepareErgebnis, prepareErgebnisse } = useErgebnisseTestDataFactory();
const { prepareKandidat, prepareWahlvorschlaege, prepareWahlvorschlag } =
  useWahlvorschlaegeTestDataFactory();

const wahlID = "wahlID";

const meta = {
  component: TheOBWStapelACard,
  args: {
    wahlID,
  },
  decorators: [
    (story) => {
      return {
        components: { story },
        template: "<story />",
      };
    },
  ],
} satisfies Meta<typeof TheOBWStapelACard>;

export default meta;

type Story = StoryObj<typeof meta>;
export const Default: Story = {
  async beforeEach() {
    const ergebnismeldungsStore = useErgebnismeldungStore(pinia);
    ergebnismeldungsStore.ergebnisse = [
      prepareErgebnisse()
        .bezirkUndWahlIDStapelart({
          wahlID,
          wahlbezirkID: "wahlbezirkID",
          stapelArt: StapelArtEnum.ObwA,
        })
        .ergebnisse([
          prepareErgebnis()
            .wahlvorschlagID("wahlvorschlag1")
            .ergebnis(null)
            .build(),
          prepareErgebnis()
            .wahlvorschlagID("wahlvorschlag2")
            .ergebnis(null)
            .build(),
          prepareErgebnis()
            .wahlvorschlagID("wahlvorschlag3")
            .ergebnis(null)
            .build(),
          prepareErgebnis()
            .wahlvorschlagID("wahlvorschlag4")
            .ergebnis(null)
            .build(),
        ])
        .build(),
      prepareErgebnisse()
        .bezirkUndWahlIDStapelart({
          wahlID,
          wahlbezirkID: "wahlbezirkID",
          stapelArt: StapelArtEnum.ObwCGueltig,
        })
        .ergebnisse([
          prepareErgebnis().wahlvorschlagID("wahlvorschlag1").build(),
          prepareErgebnis().wahlvorschlagID("wahlvorschlag2").build(),
          prepareErgebnis().wahlvorschlagID("wahlvorschlag2").build(),
          prepareErgebnis().wahlvorschlagID("wahlvorschlag3").build(),
          prepareErgebnis().wahlvorschlagID("wahlvorschlag3").build(),
          prepareErgebnis().wahlvorschlagID("wahlvorschlag3").build(),
        ])
        .build(),
    ];

    const wahlvorschlaegeStore = useWahlvorschlaegeStore(pinia);
    wahlvorschlaegeStore.wahlvorschlaege = [
      prepareWahlvorschlaege()
        .wahlID(wahlID)
        .wahlbezirkID("wahlbezirkID")
        .wahlvorschlaege([
          prepareWahlvorschlag()
            .identifikator("wahlvorschlag1")
            .kurzname("wahlvorschlag1")
            .ordnungszahl(1)
            .kandidaten([prepareKandidat().build()])
            .build(),
          prepareWahlvorschlag()
            .identifikator("wahlvorschlag2")
            .kurzname("wahlvorschlag2")
            .ordnungszahl(2)
            .kandidaten([prepareKandidat().build()])
            .build(),
          prepareWahlvorschlag()
            .identifikator("wahlvorschlag3")
            .kurzname("wahlvorschlag3")
            .ordnungszahl(3)
            .kandidaten([prepareKandidat().build()])
            .build(),
          prepareWahlvorschlag()
            .identifikator("wahlvorschlag4")
            .kurzname("wahlvorschlag4")
            .ordnungszahl(4)
            .kandidaten([prepareKandidat().build()])
            .build(),
        ])
        .build(),
    ];

    const userStore = useUserStore(pinia);
    userStore.setUser(
      prepareUser()
        .wahlMetaData([
          {
            wahlbezirkID: "wahlbezirkID",
            wahlID: "wahlID",
            wahlnummer: "wahlnummer",
          },
        ])
        .build()
    );

    await flushPromises();
  },
  args: {
    wahlID: wahlID,
    wahlbezirkID: "wahlbezirkID",
  },
};
