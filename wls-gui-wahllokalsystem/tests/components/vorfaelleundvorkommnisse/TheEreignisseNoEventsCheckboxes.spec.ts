import { createTestingPinia } from "@pinia/testing";
import {
  COMPONENT_EVENT_TESTS,
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
import { useUserTestDataFactory } from "@tests/utils/user/UserTestDataFactory.ts";
import { mount, VueWrapper } from "@vue/test-utils";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { nextTick } from "vue";

import TheEreignisseNoEventsCheckboxes from "@/components/vorfaelleundvorkommnisse/TheEreignisseNoEventsCheckboxes.vue";
import vuetify from "@/plugins/vuetify";
import { useEreignisStore } from "@/stores/ereignisStore.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

vi.mock("@/composables/vorfaelleundvorkommnisse/ereignisService.ts", () => ({
  useEreignisService: () => ({
    getEreignisse: vi.fn(),
    saveEreignisse: vi.fn(),
  }),
}));

describe("TheEreignisseNoEventsCheckboxes.vue", () => {
  const { prepareUser } = useUserTestDataFactory();
  let wrapper: VueWrapper<InstanceType<typeof TheEreignisseNoEventsCheckboxes>>;
  beforeEach(() => {
    wrapper = mount(TheEreignisseNoEventsCheckboxes, {
      global: {
        plugins: [
          createTestingPinia({
            stubActions: false,
            createSpy: vi.fn,
          }),
          vuetify,
        ],
      },
    });
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  describe(COMPONENT_RENDER_TESTS, () => {
    describe("databinding", () => {
      it("should_renderCheckboxesSelected_when_keineEreignisseFlagsInStoreAreTrueForUWB", async (context) => {
        const ereignisStore = useEreignisStore();
        ereignisStore.wahlbezirkEreignisse.keineVorfaelle = true;
        ereignisStore.wahlbezirkEreignisse.keineVorkommnisse = true;

        await nextTick();

        await expect(wrapper.html()).toMatchFileSnapshot(
          getSnapshotFilename(context)
        );
      });

      it("should_renderCheckboxesUnselected_when_keineEreignisseFlagsInStoreAreFalseForUWB", async (context) => {
        const ereignisStore = useEreignisStore();
        ereignisStore.wahlbezirkEreignisse.keineVorfaelle = false;
        ereignisStore.wahlbezirkEreignisse.keineVorkommnisse = false;

        await nextTick();

        await expect(wrapper.html()).toMatchFileSnapshot(
          getSnapshotFilename(context)
        );
      });
    });

    describe("keineVorfaelle", () => {
      it("should_renderKeineVorfaelleDisabled_when_vorfaelleAreGivenInStoreForUWB", async (context) => {
        useEreignisStore().wahlbezirkEreignisse.ereigniseintraege = [
          { ereignisart: "VORFALL" },
        ];

        await nextTick();

        await expect(wrapper.html()).toMatchFileSnapshot(
          getSnapshotFilename(context)
        );
      });

      it("should_renderKeineVorfaelleEnabled_when_noVorfaelleAreGivenInStoreForUWB", async (context) => {
        useEreignisStore().wahlbezirkEreignisse.ereigniseintraege = [];

        await nextTick();

        await expect(wrapper.html()).toMatchFileSnapshot(
          getSnapshotFilename(context)
        );
      });
    });

    describe("keineVorkommnisse", () => {
      it("should_renderKeineVorkommnisseDisabled_when_vorkommnisseAreGivenInStoreForUWB", async (context) => {
        useEreignisStore().wahlbezirkEreignisse.ereigniseintraege = [
          { ereignisart: "VORKOMMNIS" },
        ];
        useWahlbezirkStore().schliessungsuhrzeitState.schliessungsuhrzeitSent =
          undefined;

        await nextTick();

        await expect(wrapper.html()).toMatchFileSnapshot(
          getSnapshotFilename(context)
        );
      });

      it("should_renderKeineVorkommnisseEnabled_when_noVorkommnisseAreGivenInStoreAndSchliessunguhrzeitIsSetForUWB", async (context) => {
        const schliessungsuhrzeit = new Date();
        useEreignisStore().wahlbezirkEreignisse.ereigniseintraege = [];
        useWahlbezirkStore().schliessungsuhrzeitState.schliessungsuhrzeitSent =
          schliessungsuhrzeit;
        await useEreignisStore().onSchliessungsuhrzeitSentChanged(
          schliessungsuhrzeit
        );

        await nextTick();

        await expect(wrapper.html()).toMatchFileSnapshot(
          getSnapshotFilename(context)
        );
      });

      it("should_renderKeineVorkommnisseDisabled_when_noVorkommnisseAreGivenInStoreButSchliessungsuhrzeitIsNotSetForUWB", async (context) => {
        useEreignisStore().wahlbezirkEreignisse.ereigniseintraege = [];
        useWahlbezirkStore().schliessungsuhrzeitState.schliessungsuhrzeitSent =
          undefined;

        await nextTick();

        await expect(wrapper.html()).toMatchFileSnapshot(
          getSnapshotFilename(context)
        );
      });

      it("should_renderKeineVorkommnisseEnabled_when_noVorkommnisseAreGivenInStoreAndSchliessungsuhrzeitIsNotSetForBWB", async (context) => {
        useEreignisStore().wahlbezirkEreignisse.ereigniseintraege = [];
        useWahlbezirkStore().schliessungsuhrzeitState.schliessungsuhrzeitSent =
          undefined;

        useUserStore().setUser(
          prepareUser().wahlbezirksArt(WahlbezirksArtEnum.BWB).build()
        );

        await nextTick();

        await expect(wrapper.html()).toMatchFileSnapshot(
          getSnapshotFilename(context)
        );
      });
    });
  });

  describe(COMPONENT_EVENT_TESTS, () => {
    it("should_updateKeineVorfaelleToTrue_when_keineVorfaelleCheckboxWasSelectedForUWB", async () => {
      const ereignisStore = useEreignisStore();
      ereignisStore.wahlbezirkEreignisse.keineVorfaelle = false;
      ereignisStore.wahlbezirkEreignisse.ereigniseintraege = [];

      await nextTick();

      const checkboxKeineVorfaelle = wrapper.getComponent(
        '[data-test="checkboxKeineVorfaelle"]'
      );
      await checkboxKeineVorfaelle.setValue(true);

      expect(ereignisStore.wahlbezirkEreignisse.keineVorfaelle).toStrictEqual(
        true
      );
    });

    it("should_updateKeineVorkommnisseToTrue_when_keineVorkommnisseCheckboxWasSelectedForUWB", async () => {
      const ereignisStore = useEreignisStore();
      ereignisStore.wahlbezirkEreignisse.keineVorkommnisse = false;
      ereignisStore.wahlbezirkEreignisse.ereigniseintraege = [];

      await nextTick();

      const checkboxKeineVorfaelle = wrapper.getComponent(
        '[data-test="checkboxKeineVorkommnisse"]'
      );
      await checkboxKeineVorfaelle.setValue(true);

      expect(
        ereignisStore.wahlbezirkEreignisse.keineVorkommnisse
      ).toStrictEqual(true);
    });

    it("should_updateKeineVorkommnisseToTrue_when_keineVorkommnisseCheckboxWasSelectedForBWB", async () => {
      const ereignisStore = useEreignisStore();
      useUserStore().setUser(
        prepareUser().wahlbezirksArt(WahlbezirksArtEnum.BWB).build()
      );
      ereignisStore.wahlbezirkEreignisse.keineVorkommnisse = false;
      ereignisStore.wahlbezirkEreignisse.ereigniseintraege = [];

      await nextTick();

      const checkboxKeineVorfaelle = wrapper.getComponent(
        '[data-test="checkboxKeineVorkommnisse"]'
      );
      await checkboxKeineVorfaelle.setValue(true);

      expect(
        ereignisStore.wahlbezirkEreignisse.keineVorkommnisse
      ).toStrictEqual(true);
    });
  });
});
