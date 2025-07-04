import type { Ereignis } from "@/types/vorfaelleundvorkommnisse/Ereignis.ts";

import { createTestingPinia } from "@pinia/testing";
import {
  COMPONENT_EVENT_TESTS,
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
import { enableAutoUnmount, mount, VueWrapper } from "@vue/test-utils";
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

import YesNoDialog from "@/components/common/YesNoDialog.vue";
import TheEreignisseRow from "@/components/vorfaelleundvorkommnisse/TheEreignisseRow.vue";
import vuetify from "@/plugins/vuetify";
import { useEreignisStore } from "@/stores/ereignisStore.ts";
import { EreignisBuilder } from "@/types/vorfaelleundvorkommnisse/Ereignis.ts";

describe("TheEreignisseRow.vue", () => {
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
    wrapper = mount(TheEreignisseRow, {
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

      const date = new Date();
      date.setHours(12, 0);
      ereigniseintraege.push(
        EreignisBuilder.createComplete()
          .withUhrzeit(date)
          .withBeschreibung(`Vorfall Nr.: 1`)
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
      for (let i = 1; i < 6; i++) {
        const date = new Date(Date.UTC(2025, 6, 4));
        date.setHours(i, 0);
        ereigniseintraege.push(
          EreignisBuilder.createComplete()
            .withUhrzeit(date)
            .withBeschreibung(`Vorfall Nr.: ${i}`)
        );
      }

      ereignisStore.wahlbezirkEreignisse.ereigniseintraege = ereigniseintraege;

      await nextTick();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
    it("should_showErrorMessage_when_beschreibungIsNotSetCorrectly", async () => {
      const ereignisStore = useEreignisStore();
      const ereigniseintraege = [] as Ereignis[];

      const date = new Date();
      date.setHours(12, 0);
      ereigniseintraege.push(
        EreignisBuilder.createComplete().withUhrzeit(date).withBeschreibung(``)
      );

      ereignisStore.wahlbezirkEreignisse.ereigniseintraege = ereigniseintraege;

      await nextTick();

      // Triggern des Updates der VTextarea mit weniger als 4 Zeichen
      const textarea = wrapper.findComponent({ name: "VTextarea" });
      expect(textarea.exists()).toBe(true); // Überprüfen, ob die Textarea existiert
      await textarea.setValue("abc"); // weniger als 4 Zeichen

      await nextTick();

      // Überprüfen, ob die Fehlermeldung angezeigt wird
      const errorMessage = wrapper.get(".v-messages__message");
      expect(errorMessage.text()).toContain("Minimale Länge ist 4 Zeichen.");
    });

    it("should_showErrorMessage_when_uhrzeitIsNotSetCorrectly", async () => {
      const ereignisStore = useEreignisStore();
      const ereigniseintraege = [] as Ereignis[];

      const date = new Date();
      date.setHours(12, 0);
      ereigniseintraege.push(
        EreignisBuilder.createComplete()
          .withUhrzeit(date)
          .withBeschreibung(`Beschreibung`)
      );

      ereignisStore.wahlbezirkEreignisse.ereigniseintraege = ereigniseintraege;

      await nextTick();

      // Triggern des Updates des VTextfield mit undefined
      const textfield = wrapper.findComponent({ name: "v-text-field" });
      expect(textfield.exists()).toBe(true); // Überprüfen, ob das Textfeld existiert
      await textfield.setValue(undefined);

      await nextTick();

      // Überprüfen, ob die Fehlermeldung angezeigt wird
      const errorMessage = wrapper.get(".v-messages__message");
      expect(errorMessage.text()).toContain("Feld darf nicht leer sein.");
    });
  });

  describe(COMPONENT_EVENT_TESTS, () => {
    it("should_openYesNoDialog_when_deleteIconIsClicked", async () => {
      const ereignisStore = useEreignisStore();
      const ereigniseintraege = [] as Ereignis[];

      const date = new Date();
      date.setHours(12, 0);
      ereigniseintraege.push(
        EreignisBuilder.createComplete()
          .withUhrzeit(date)
          .withBeschreibung(`Beschreibung`)
      );

      ereignisStore.wahlbezirkEreignisse.ereigniseintraege = ereigniseintraege;

      await nextTick();

      const deleteIcon = wrapper.findComponent(
        '[data-test="delete-ereignis-icon"]'
      );
      expect(deleteIcon.exists()).toBe(true);

      await deleteIcon.trigger("click");

      const deleteDialog = wrapper.findComponent(YesNoDialog);
      expect(deleteDialog.exists()).toBe(true);

      deleteDialog.vm.$emit("yes");
      await nextTick();

      expect(ereignisStore.wahlbezirkEreignisse.ereigniseintraege).toHaveLength(
        0
      );
    });

    it("should_triggerUpdateUhrzeitInStore_when_uhrzeitOfEreignisWasChanged", async () => {
      const ereignisStore = useEreignisStore();

      const ereigniseintraege = [] as Ereignis[];
      for (let i = 1; i < 6; i++) {
        const date = new Date();
        date.setHours(i, 10);
        date.setFullYear(2025, 7, 30);
        ereigniseintraege.push(
          EreignisBuilder.createComplete()
            .withUhrzeit(date)
            .withBeschreibung(`Vorfall Nr.: ${i}`)
        );
      }

      ereignisStore.wahlbezirkEreignisse.ereigniseintraege = ereigniseintraege;

      await nextTick();

      const indexOfTimeInputForChange = 3;
      const firstEreignisTimeinput = wrapper.findAllComponents(
        '[data-test="baseTimeInput"]'
      )[indexOfTimeInputForChange];
      const newValue = new Date();
      await firstEreignisTimeinput.setValue(newValue);

      expect(ereignisStore.updateUhrzeitByIndex).toHaveBeenCalledWith(
        newValue,
        indexOfTimeInputForChange
      );
      expect(ereignisStore.updateUhrzeitByIndex).toHaveBeenCalledTimes(1);
    });

    it("should_triggerUpdateUhrzeitInStore_when_dateOfEreignisWasChanged", async () => {
      const ereignisStore = useEreignisStore();

      const ereigniseintraege = [] as Ereignis[];
      for (let i = 1; i < 6; i++) {
        const date = new Date();
        date.setHours(1, 10);
        date.setFullYear(2025, 12, i);
        ereigniseintraege.push(
          EreignisBuilder.createComplete()
            .withUhrzeit(date)
            .withBeschreibung(`Vorfall Nr.: ${i}`)
        );
      }

      ereignisStore.wahlbezirkEreignisse.ereigniseintraege = ereigniseintraege;

      await nextTick();

      const indexOfTimeInputForChange = 3;
      const firstEreignisTimeinput = wrapper.findAllComponents(
        '[data-test="baseDateInput"]'
      )[indexOfTimeInputForChange];
      const newValue = new Date();
      await firstEreignisTimeinput.setValue(newValue);

      expect(ereignisStore.updateUhrzeitByIndex).toHaveBeenCalledWith(
        newValue,
        indexOfTimeInputForChange
      );
      expect(ereignisStore.updateUhrzeitByIndex).toHaveBeenCalledTimes(1);
    });
  });
});
