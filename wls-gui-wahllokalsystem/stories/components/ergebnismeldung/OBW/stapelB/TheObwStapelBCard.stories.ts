import type { Meta, StoryObj } from "@storybook/vue3";

import { useUserTestDataFactory } from "@tests/utils/user/UserTestDataFactory.ts";
import { useWahlTestDataFactory } from "@tests/utils/wahl/WahlTestDataFactory.ts";
import { flushPromises } from "@vue/test-utils";

import TheOBWStapelBCard from "@/components/ergebnismeldung/OBW/stapelB/TheOBWStapelBCard.vue";
import pinia from "@/plugins/pinia.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const { prepareWahl } = useWahlTestDataFactory();
const { prepareUser } = useUserTestDataFactory();

const wahlId = "wahlID";

const meta = {
  component: TheOBWStapelBCard,
  args: {
    wahlId,
  },
  decorators: [
    (story) => {
      return {
        components: { story },
        template: "<story />",
      };
    },
  ],
} satisfies Meta<typeof TheOBWStapelBCard>;

export default meta;

type Story = StoryObj<typeof meta>;
export const Default: Story = {
  async beforeEach() {
    const wahlenStore = useWahlenStore(pinia);
    wahlenStore.wahlenState.wahlen = [prepareWahl().wahlID(wahlId).build()];
    await flushPromises();

    const userStore = useUserStore(pinia);
    userStore.setUser(
      prepareUser()
        .wahlbezirksArt(WahlbezirksArtEnum.UWB)
        .wahlMetaData([
          {
            wahlbezirkID: "wahlbezirkID",
            wahlID: wahlId,
            wahlnummer: "wahlnummer",
          },
        ])
        .build()
    );
  },
  args: {
    wahlId: wahlId,
  },
};

export const UserWithWahlbezirksartBwb: Story = {
  async beforeEach() {
    const wahlenStore = useWahlenStore(pinia);
    wahlenStore.wahlenState.wahlen = [prepareWahl().wahlID(wahlId).build()];

    const userStore = useUserStore(pinia);
    userStore.setUser(
      prepareUser()
        .wahlbezirksArt(WahlbezirksArtEnum.BWB)
        .wahlMetaData([
          {
            wahlbezirkID: "wahlbezirkID",
            wahlID: wahlId,
            wahlnummer: "wahlnummer",
          },
        ])
        .build()
    );

    await flushPromises();
  },
  args: {
    wahlId: wahlId,
  },
};
