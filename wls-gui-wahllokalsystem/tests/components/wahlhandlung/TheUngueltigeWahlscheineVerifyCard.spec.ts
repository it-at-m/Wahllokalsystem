import type { TestingPinia } from "@pinia/testing";
import type { VueWrapper } from "@vue/test-utils";

import { createTestingPinia } from "@pinia/testing";
import {
  COMPONENT_EVENT_TESTS,
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
  mockAndStubResizeObserver,
} from "@tests/utils/testutils.ts";
import { useWahlbezirkTestDataFactory } from "@tests/utils/wahlbezirk/WahlbezirkTestDataFactory.ts";
import { flushPromises, mount } from "@vue/test-utils";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import {VTextarea, type VTextField} from "vuetify/components";

import BaseButtonRefresh from "@/components/common/buttons/BaseButtonRefresh.vue";
import BaseWlsButtonSave from "@/components/common/buttons/BaseWlsButtonSave.vue";
import TheUngueltigeWahlscheineVerifyCard from "@/components/wahlhandlung/TheUngueltigeWahlscheineVerifyCard.vue";
import vuetify from "@/plugins/vuetify.ts";
import { useEreignisStore } from "@/stores/ereignisStore.ts";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";

const mockDefinitions = vi.hoisted(() => ({
  getUngueltigeWahlscheine: vi.fn(),
  saveEreignisse: vi.fn(),
}));

vi.mock(
  import("@/composables/basisdaten/ungueltigeWahlscheineService.ts"),
  () => ({
    useUngueltigeWahlscheineService: () => ({
      getUngueltigeWahlscheine: mockDefinitions.getUngueltigeWahlscheine,
    }),
  })
);
vi.mock(
  import("@/composables/vorfaelleundvorkommnisse/ereignisService.ts"),
  async (importOriginal) => {
    const mod = await importOriginal();
    return {
      useEreignisService: () => ({
        ...mod.useEreignisService(),
        saveEreignisse: mockDefinitions.saveEreignisse,
      }),
    };
  }
);

const { createUngueltigerWahlschein, prepareUngueltigerWahlschein } =
  useWahlbezirkTestDataFactory();

const ABSTIMMUNGSERGEBNIS = "einstimmig zurückgewiesen";

describe("TheUngueltigeWahlscheineVerifyCard.vue", () => {
  let wrapper: VueWrapper;
  let testPinia: TestingPinia;

  mockAndStubResizeObserver();

  describe(COMPONENT_RENDER_TESTS, () => {
    beforeEach(() => {
      testPinia = createTestingPinia({
        stubActions: false,
        createSpy: vi.fn,
      });
      wrapper = mount(TheUngueltigeWahlscheineVerifyCard, {
        global: {
          plugins: [testPinia, vuetify],
        },
      });
    });

    afterEach(() => {
      wrapper.unmount();
    });

    it("should_renderEmptyCard_when_mounted", async (context) => {
      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderWithRequiredErrorMessage_when_wahlscheinNummerInputIsSetEmpty", async (context) => {
      const wahlscheinnummerInput = getInputWahlscheinnummer();
      await wahlscheinnummerInput.setValue(123);
      await wahlscheinnummerInput.setValue(null);
      await wahlscheinnummerInput.vm.validate();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderValidWahlscheinAndChangeSearchButtonLabel_when_wahlscheinnummerIsNotPartOfUngueltigeWahlscheine", async (context) => {
      const wahlscheinnummerInput = getInputWahlscheinnummer();
      await wahlscheinnummerInput.setValue(123);

      useWahlbezirkStore().ungueltigeWahlscheineState.ungueltigeWahlscheine = [
        prepareUngueltigerWahlschein().wahlscheinnummer("1").build(),
      ];

      const searchButton = getSearchButton();
      await searchButton.trigger("click");

      await flushPromises();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderInvalidWahlscheinWithDisabledSaveButtonAndChangeSearchButtonLabel_when_wahlscheinnummerIsPartOfUngueltigeWahlscheine", async (context) => {
      const wahlscheinnummerInput = getInputWahlscheinnummer();
      await wahlscheinnummerInput.setValue(123);

      useWahlbezirkStore().ungueltigeWahlscheineState.ungueltigeWahlscheine = [
        prepareUngueltigerWahlschein()
          .vorname("testerich")
          .familienname("testuser")
          .wahlscheinnummer("123")
          .build(),
      ];

      const searchButton = getSearchButton();
      await searchButton.trigger("click");
      await flushPromises();

      const saveBeschlussBtn = getSaveBeschlussButton();
      const inputAbstimmung = getInputStimmenZurueckweisung();

      expect(saveBeschlussBtn.props("disabled")).toStrictEqual(true);
      expect(inputAbstimmung.props("modelValue")).toStrictEqual(undefined);
      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderInvalidWahlscheinWithEnabledSaveButtonAndChangeSearchButtonLabel_when_wahlscheinnummerIsPartOfUngueltigeWahlscheineAndAbstimmungEingetragen", async (context) => {
      const wahlscheinnummerInput = getInputWahlscheinnummer();
      await wahlscheinnummerInput.setValue(123);

      useWahlbezirkStore().ungueltigeWahlscheineState.ungueltigeWahlscheine = [
        prepareUngueltigerWahlschein()
          .vorname("testerich")
          .familienname("testuser")
          .wahlscheinnummer("123")
          .build(),
      ];

      const searchButton = getSearchButton();
      await searchButton.trigger("click");
      await flushPromises();

      const saveBeschlussBtn = getSaveBeschlussButton();
      const inputAbstimmung = getInputStimmenZurueckweisung();
      await inputAbstimmung.setValue(ABSTIMMUNGSERGEBNIS);
      await flushPromises();

      expect(saveBeschlussBtn.props("disabled")).toStrictEqual(false);
      expect(inputAbstimmung.props("modelValue")).toStrictEqual(
        ABSTIMMUNGSERGEBNIS
      );
      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderWarning_when_noDataIsAvailable", async (context) => {
      useWahlbezirkStore().ungueltigeWahlscheineState.ungueltigeWahlscheine =
        [];
      useWahlbezirkStore().ungueltigeWahlscheineState.ungueltigeWahlscheineLoadingFailed = false;
      useWahlbezirkStore().ungueltigeWahlscheineState.ungueltigeWahlscheineIsLoading = false;

      await flushPromises();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderWarning_when_noLoadingOfListeFailed", async (context) => {
      useWahlbezirkStore().ungueltigeWahlscheineState.ungueltigeWahlscheineLoadingFailed = true;
      useWahlbezirkStore().ungueltigeWahlscheineState.ungueltigeWahlscheineIsLoading = false;

      await flushPromises();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderRefreshButtonInLoadingState_when_ungueltigeWahlscheineLoadingIsTrue", async (context) => {
      useWahlbezirkStore().ungueltigeWahlscheineState.ungueltigeWahlscheineIsLoading = true;

      await flushPromises();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });

  describe(COMPONENT_EVENT_TESTS, () => {
    beforeEach(() => {
      testPinia = createTestingPinia({
        createSpy: vi.fn,
        stubActions: false,
      });
      wrapper = mount(TheUngueltigeWahlscheineVerifyCard, {
        global: {
          plugins: [testPinia, vuetify],
        },
      });
      vi.useFakeTimers();
    });

    afterEach(() => {
      wrapper.unmount();
      vi.restoreAllMocks();
    });

    it("should_triggerSearch_when_searchButtonIsClickedAndNoSearchWasDone", async () => {
      const wahlscheinnummerInput = getInputWahlscheinnummer();
      await wahlscheinnummerInput.setValue(123);
      await wahlscheinnummerInput.vm.validate();

      const getWahlscheinSpy = vi.spyOn(
        useWahlbezirkStore().ungueltigeWahlscheineActions,
        "getUngueltigerWahlscheinByWahlscheinnummer"
      );

      const searchButton = getSearchButton();
      await searchButton.trigger("click");
      await flushPromises();

      expect(getWahlscheinSpy).toHaveBeenCalledWith("123");
    });

    it("should_triggerResetWahlscheinnummerInput_when_searchButtonIsClickedAndASearchWasDoneBefore", async () => {
      const wahlbezirkStore = useWahlbezirkStore();

      const wahlscheinnummerInput = getInputWahlscheinnummer();
      console.debug(wahlscheinnummerInput);
      await wahlscheinnummerInput.setValue("123");

      wahlbezirkStore.ungueltigeWahlscheineActions.getUngueltigerWahlscheinByWahlscheinnummer =
        vi.fn(() => {
          return createUngueltigerWahlschein();
        });

      expect(wahlscheinnummerInput.vm.value).toStrictEqual("123");
      const searchButton = getSearchButton();
      await searchButton.trigger("click");
      await flushPromises();

      const inputAbstimmung = getInputStimmenZurueckweisung();
      await inputAbstimmung.setValue(ABSTIMMUNGSERGEBNIS);
      await flushPromises();

      expect(inputAbstimmung.vm.value).toStrictEqual(ABSTIMMUNGSERGEBNIS);
      await searchButton.trigger("click");
      await flushPromises();

      expect(wahlscheinnummerInput.vm.value).toStrictEqual("");
      expect(inputAbstimmung.vm.value).toBeUndefined();
    });

    it("should_triggerLoadUngueltigeWahlscheine_when_refreshWasClicked", async () => {
      const loadWahlscheinSpy = vi.spyOn(
        useWahlbezirkStore().ungueltigeWahlscheineActions,
        "loadUngueltigeWahlscheine"
      );

      mockDefinitions.getUngueltigeWahlscheine.mockReturnValue([]);

      const refreshButton = getRefreshButton();
      await refreshButton.trigger("click");

      expect(loadWahlscheinSpy).toHaveBeenCalled();
    });

    it("should_addAndSaveEreignisAndReset_when_saveBeschlussIsClickedWithValidAbstimmung", async () => {
      const ereignisStore = useEreignisStore();
      const ungueltigerWs = createUngueltigerWahlschein();

      const wahlscheinnummerInput = getInputWahlscheinnummer();
      await wahlscheinnummerInput.setValue(123);

      useWahlbezirkStore().ungueltigeWahlscheineActions.getUngueltigerWahlscheinByWahlscheinnummer =
        vi.fn(() => {
          return ungueltigerWs;
        });

      const searchButton = getSearchButton();
      await searchButton.trigger("click");
      await flushPromises();

      const inputAbstimmung = getInputStimmenZurueckweisung();
      await inputAbstimmung.setValue(ABSTIMMUNGSERGEBNIS);
      await flushPromises();

      expect(ereignisStore.wahlbezirkEreignisse.ereigniseintraege.length).toBe(
        0
      );

      const expectedEreignisBeschreibung =
        `Wahlschein ${ungueltigerWs.wahlscheinnummer} für ${ungueltigerWs.vorname} ${ungueltigerWs.familienname} ist` +
        ` ungültig. Die Person wurde zurückgewiesen. Abstimmungsergebnis: ${ABSTIMMUNGSERGEBNIS}`;

      const saveBeschlussBtn = getSaveBeschlussButton();
      await saveBeschlussBtn.trigger("click");
      await flushPromises();

      expect(ereignisStore.addEreignis).toHaveBeenCalledWith({
        uhrzeit: expect.any(Date),
        beschreibung: expectedEreignisBeschreibung,
      });
      expect(ereignisStore.wahlbezirkEreignisse.ereigniseintraege.length).toBe(
        1
      );
      expect(mockDefinitions.saveEreignisse).toHaveBeenCalled();
      expect(inputAbstimmung.vm.value).toStrictEqual("");
      expect(saveBeschlussBtn.props("disabled")).toStrictEqual(true);
    });
  });

  function getInputStimmenZurueckweisung() {
    return wrapper.findComponent<typeof VTextarea>(
      '[data-test="text-input-abstimmungsergebnis"]'
    );
  }

  function getInputWahlscheinnummer() {
    return wrapper.findComponent<typeof VTextField>(
      '[data-test="input-wahlscheinnummer"]'
    );
  }

  function getSearchButton() {
    return wrapper.findComponent('[data-test="button-search"]');
  }

  function getSaveBeschlussButton() {
    return wrapper.findComponent(BaseWlsButtonSave);
  }

  function getRefreshButton() {
    return wrapper.findComponent(BaseButtonRefresh);
  }
});
