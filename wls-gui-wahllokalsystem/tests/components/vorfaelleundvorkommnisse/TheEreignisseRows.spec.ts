import type { Ereignis } from "@/types/vorfaelleundvorkommnisse/Ereignis.ts";

import { createTestingPinia } from "@pinia/testing";
import {
  COMPONENT_EVENT_TESTS,
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
import { useVorfaelleundvorkommnisseTestDataFactory } from "@tests/utils/vorfaelleundvorkommnisse/VorfaelleundvorkommnisseTestDataFactory.ts";
import {
  enableAutoUnmount,
  flushPromises,
  mount,
  VueWrapper,
} from "@vue/test-utils";
import { createPinia } from "pinia";
import {
  afterEach,
  beforeAll,
  beforeEach,
  describe,
  expect,
  it,
  vi,
} from "vitest";
import { nextTick } from "vue";

import BaseDialog from "@/components/common/dialogs/BaseDialog.vue";
import BaseEreignisRow from "@/components/vorfaelleundvorkommnisse/BaseEreignisRow.vue";
import TheEreignisseRows from "@/components/vorfaelleundvorkommnisse/TheEreignisseRows.vue";
import vuetify from "@/plugins/vuetify";
import { useEreignisStore } from "@/stores/ereignisStore.ts";

const { prepareEreignis } = useVorfaelleundvorkommnisseTestDataFactory();

describe("TheEreignisseRows.vue", () => {
  let wrapper: VueWrapper;
  vi.stubGlobal("visualViewport", new EventTarget());
  // Mock the ResizeObserver
  const ResizeObserverMock = vi.fn(() => ({
    observe: vi.fn(),
    unobserve: vi.fn(),
    disconnect: vi.fn(),
  }));
  // Stub the global ResizeObserver
  vi.stubGlobal("ResizeObserver", ResizeObserverMock);

  beforeAll(() => {
    createPinia();
  });

  beforeEach(() => {
    wrapper = mount(TheEreignisseRows, {
      global: {
        plugins: [
          createTestingPinia({
            createSpy: vi.fn,
            stubActions: false,
          }),
          vuetify,
        ],
      },
    });
    vi.clearAllMocks();
  });

  enableAutoUnmount(afterEach);

  describe(COMPONENT_RENDER_TESTS, () => {
    it("component mounted", () => {
      expect(wrapper.exists()).toBeTruthy();
    });

    it("should_showNoRows_when_noEreignisIsGiven", async (context) => {
      const ereignisStore = useEreignisStore();
      ereignisStore.wahlbezirkEreignisse.ereigniseintraege = [] as Ereignis[];

      await nextTick();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_showOneRow_when_oneEreignisIsGiven", async (context) => {
      const ereignisStore = useEreignisStore();
      const ereigniseintraege = [] as Ereignis[];

      const date = new Date("2025-07-29");
      date.setHours(12, 0);
      ereigniseintraege.push(
        prepareEreignis().uhrzeit(date).beschreibung(`Vorfall Nr.: 1`).build()
      );

      ereignisStore.wahlbezirkEreignisse.ereigniseintraege = ereigniseintraege;

      await nextTick();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_showMultipleRows_when_multipleEreignisseAreGiven", async (context) => {
      const ereignisStore = useEreignisStore();

      const ereigniseintraege = [] as Ereignis[];
      for (let i = 0; i < 5; i++) {
        const date = new Date("2025-07-29");
        date.setHours(i, 0);
        ereigniseintraege.push(
          prepareEreignis()
            .uhrzeit(date)
            .beschreibung(`Vorfall Nr.: ${i}`)
            .build()
        );
      }

      ereignisStore.wahlbezirkEreignisse.ereigniseintraege = ereigniseintraege;

      await nextTick();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });

  describe(COMPONENT_EVENT_TESTS, () => {
    it("should_openDialogAndDelete_when_deleteWasEmittedByARowAndDeletionWasConfirmed", async () => {
      const ereignisStore = useEreignisStore();
      const ereigniseintraege = [] as Ereignis[];

      const date = new Date();
      date.setHours(12, 0);
      ereigniseintraege.push(
        prepareEreignis().uhrzeit(date).beschreibung(`Beschreibung`).build()
      );

      ereignisStore.wahlbezirkEreignisse.ereigniseintraege = ereigniseintraege;

      await nextTick();

      const baseEreignisRow = wrapper.findComponent(BaseEreignisRow);
      const payload = {
        dateOnly: date,
        timeOnly: date,
        beschreibung: "Beschreibung",
      };
      baseEreignisRow.vm.$emit("delete", payload);

      await flushPromises();

      const deleteDialog = wrapper.findComponent(BaseDialog);
      expect(deleteDialog.exists()).toBe(true);

      deleteDialog.vm.$emit("confirm");
      await nextTick();

      expect(ereignisStore.wahlbezirkEreignisse.ereigniseintraege).toHaveLength(
        0
      );
    });

    it("should_openDialogButNotDelete_when_deleteWasEmittedByARowAndDeletionWasCanceled", async () => {
      const ereignisStore = useEreignisStore();
      const ereigniseintraege = [] as Ereignis[];

      const date = new Date();
      date.setHours(12, 0);
      ereigniseintraege.push(
        prepareEreignis().uhrzeit(date).beschreibung(`Beschreibung`).build()
      );

      ereignisStore.wahlbezirkEreignisse.ereigniseintraege = ereigniseintraege;

      await nextTick();

      const baseEreignisRow = wrapper.findComponent(BaseEreignisRow);
      const payload = {
        dateOnly: date,
        timeOnly: date,
        beschreibung: "Beschreibung",
      };
      baseEreignisRow.vm.$emit("delete", payload);

      await flushPromises();

      const deleteDialog = wrapper.findComponent(BaseDialog);
      expect(deleteDialog.exists()).toBe(true);
      expect(deleteDialog.vm.visible).toBe(true);

      deleteDialog.vm.$emit("cancel");
      await nextTick();

      expect(ereignisStore.wahlbezirkEreignisse.ereigniseintraege).toHaveLength(
        1
      );
    });

    it("should_notOpenDialogButDelete_when_deleteWasEmittedByARowWithAllFieldsUndefinedOrEmpty", async () => {
      const ereignisStore = useEreignisStore();
      const ereigniseintraege = [] as Ereignis[];

      const date = new Date();
      date.setHours(12, 0);
      ereigniseintraege.push(
        prepareEreignis().uhrzeit(date).beschreibung(`Beschreibung`).build()
      );

      ereignisStore.wahlbezirkEreignisse.ereigniseintraege = ereigniseintraege;

      await nextTick();

      const baseEreignisRow = wrapper.findComponent(BaseEreignisRow);
      const payload = {
        dateOnly: undefined,
        timeOnly: undefined,
        beschreibung: "",
      };
      baseEreignisRow.vm.$emit("delete", payload);

      await flushPromises();

      const deleteDialog = wrapper.findComponent(BaseDialog);
      expect(deleteDialog.exists()).toBe(true);
      expect(deleteDialog.vm.visible).toBe(false);

      expect(ereignisStore.wahlbezirkEreignisse.ereigniseintraege).toHaveLength(
        0
      );
    });

    it("should_notOpenDialogButDelete_when_deleteWasEmittedByARowWithOnlyBeschreibungEmpty", async () => {
      const ereignisStore = useEreignisStore();
      const ereigniseintraege = [] as Ereignis[];

      const date = new Date();
      date.setHours(12, 0);
      ereigniseintraege.push(
        prepareEreignis().uhrzeit(date).beschreibung(`Beschreibung`).build()
      );

      ereignisStore.wahlbezirkEreignisse.ereigniseintraege = ereigniseintraege;

      await nextTick();

      const baseEreignisRow = wrapper.findComponent(BaseEreignisRow);
      const payload = {
        dateOnly: date,
        timeOnly: date,
        beschreibung: "",
      };
      baseEreignisRow.vm.$emit("delete", payload);

      await flushPromises();

      const deleteDialog = wrapper.findComponent(BaseDialog);
      expect(deleteDialog.exists()).toBe(true);
      expect(deleteDialog.vm.visible).toBe(false);

      expect(ereignisStore.wahlbezirkEreignisse.ereigniseintraege).toHaveLength(
        0
      );
    });
  });
});
