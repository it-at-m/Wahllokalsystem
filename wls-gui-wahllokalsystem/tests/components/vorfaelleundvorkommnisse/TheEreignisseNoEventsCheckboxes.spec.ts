import { createTestingPinia } from "@pinia/testing";
import { getSnapshotFilename } from "@tests/utils/testutils.ts";
import { mount, VueWrapper } from "@vue/test-utils";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { nextTick } from "vue";

import TheEreignisseNoEventsCheckboxes from "@/components/vorfaelleundvorkommnisse/TheEreignisseNoEventsCheckboxes.vue";
import vuetify from "@/plugins/vuetify";
import { useEreignisStore } from "@/stores/ereignisStore.ts";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";

describe("TheEreignisseNoEventsCheckboxes.vue", () => {
  let wrapper: VueWrapper<InstanceType<typeof TheEreignisseNoEventsCheckboxes>>;
  beforeEach(() => {
    wrapper = mount(TheEreignisseNoEventsCheckboxes, {
      global: {
        plugins: [
          createTestingPinia({
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

  describe("visual logic", () => {
    describe("databinding", () => {
      it("should_renderCheckboxesSelected_when_keineEreignisseFlagsInStoreAreTrue", async (context) => {
        const ereignisStore = useEreignisStore();
        ereignisStore.wahlbezirkEreignisse.keineVorfaelle = true;
        ereignisStore.wahlbezirkEreignisse.keineVorkommnisse = true;

        await nextTick();

        await expect(wrapper.html()).toMatchFileSnapshot(
          getSnapshotFilename(context)
        );
      });

      it("should_renderCheckboxesUnselected_when_keineEreignisseFlagsInStoreAreFalse", async (context) => {
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
      it("should_renderKeineVorfaelleDisabled_when_vorfaelleAreGivenInStore", async (context) => {
        useEreignisStore().wahlbezirkEreignisse.ereigniseintraege = [
          { ereignisart: "VORFALL" },
        ];

        await nextTick();

        await expect(wrapper.html()).toMatchFileSnapshot(
          getSnapshotFilename(context)
        );
      });

      it("should_renderKeineVorfaelleEnabled_when_noVorfaelleAreGivenInStore", async (context) => {
        useEreignisStore().wahlbezirkEreignisse.ereigniseintraege = [];

        await nextTick();

        await expect(wrapper.html()).toMatchFileSnapshot(
          getSnapshotFilename(context)
        );
      });
    });

    describe("keineVorkommnisse", () => {
      it("should_renderKeineVorkommnisseDisabled_when_vorkommnisseAreGivenInStore", async (context) => {
        useEreignisStore().wahlbezirkEreignisse.ereigniseintraege = [
          { ereignisart: "VORKOMMNIS" },
        ];
        useWahlbezirkStore().schliessungsUhrzeitSent = undefined;

        await nextTick();

        await expect(wrapper.html()).toMatchFileSnapshot(
          getSnapshotFilename(context)
        );
      });

      it("should_renderKeineVorkommnisseEnabled_when_noVorkommnisseAreGivenInStoreAndSchliessunguhrzeitIsSet", async (context) => {
        useEreignisStore().wahlbezirkEreignisse.ereigniseintraege = [];
        useWahlbezirkStore().schliessungsUhrzeitSent = new Date();

        await nextTick();

        await expect(wrapper.html()).toMatchFileSnapshot(
          getSnapshotFilename(context)
        );
      });

      it("should_renderKeineVorkommnisseDisabled_when_noVorkommnisseAreGivenInStoreButSchliessungsuhrzeitIsNotSet", async (context) => {
        useEreignisStore().wahlbezirkEreignisse.ereigniseintraege = [];
        useWahlbezirkStore().schliessungsUhrzeitSent = undefined;

        await nextTick();

        await expect(wrapper.html()).toMatchFileSnapshot(
          getSnapshotFilename(context)
        );
      });
    });
  });

  describe("behavioral logic", () => {
    it("should_updateKeineVorfaelleToTrue_when_keineVorfaelleCheckboxWasSelected", async () => {
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

    it("should_updateKeineVorkommnisseToTrue_when_keineVorkommnisseCheckboxWasSelected", async () => {
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
  });
});
