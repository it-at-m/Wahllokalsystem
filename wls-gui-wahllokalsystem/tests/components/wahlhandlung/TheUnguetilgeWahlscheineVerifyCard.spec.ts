import type { TestingPinia } from "@pinia/testing";
import type { VueWrapper } from "@vue/test-utils";

import { createTestingPinia } from "@pinia/testing";
import {
  COMPONENT_EVENT_TESTS,
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
import { useWahlbezirkTestDataFactory } from "@tests/utils/wahlbezirk/WahlbezirkTestDataFactory.ts";
import { flushPromises, mount } from "@vue/test-utils";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { VNumberInput } from "vuetify/components";

import BaseButtonRefresh from "@/components/common/buttons/BaseButtonRefresh.vue";
import TheUnguetilgeWahlscheineVerifyCard from "@/components/wahlhandlung/TheUnguetilgeWahlscheineVerifyCard.vue";
import vuetify from "@/plugins/vuetify.ts";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";

const { createUngueltigerWahlschein, prepareUngueltigerWahlschein } =
  useWahlbezirkTestDataFactory();

const WAHLSCHEIN_MIN_VALUE = 1;
const WAHLSCHEIN_MAX_VALUE = 9999999;

describe("TheUnguetilgeWahlscheineVerifyCard.vue", () => {
  let wrapper: VueWrapper;
  let testPinia: TestingPinia;

  const ResizeObserverMock = vi.fn(() => ({
    observe: vi.fn(),
    unobserve: vi.fn(),
    disconnect: vi.fn(),
  }));
  vi.stubGlobal("ResizeObserver", ResizeObserverMock);

  describe(COMPONENT_RENDER_TESTS, () => {
    beforeEach(() => {
      testPinia = createTestingPinia({
        stubActions: false,
        createSpy: vi.fn,
      });
      wrapper = mount(TheUnguetilgeWahlscheineVerifyCard, {
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
      await wahlscheinnummerInput.vm.validate();

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

    it("should_renderInvalidWahlscheinAndChangeSearchButtonLabel_when_wahlscheinnummerIsPartOfUngueltigeWahlscheine", async (context) => {
      const wahlscheinnummerInput = getInputWahlscheinnummer();
      await wahlscheinnummerInput.setValue(123);
      await wahlscheinnummerInput.vm.validate();

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

    it("should_renderNoErrorMessage_when_wahlscheinnummerIsMinValue", async (context) => {
      const wahlscheinnummerInput = getInputWahlscheinnummer();
      await wahlscheinnummerInput.setValue(WAHLSCHEIN_MIN_VALUE);
      await wahlscheinnummerInput.vm.validate();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderNoErrorMessage_when_wahlscheinnummerIsMaxValue", async (context) => {
      const wahlscheinnummerInput = getInputWahlscheinnummer();
      await wahlscheinnummerInput.setValue(WAHLSCHEIN_MAX_VALUE);
      await wahlscheinnummerInput.vm.validate();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderErrorMessage_when_wahlscheinnummerIsBelowMinValue", async (context) => {
      const wahlscheinnummerInput = getInputWahlscheinnummer();
      await wahlscheinnummerInput.setValue(WAHLSCHEIN_MIN_VALUE - 1);
      await wahlscheinnummerInput.vm.validate();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderErrorMessage_when_wahlscheinnummerIsAboveMaxValue", async (context) => {
      const wahlscheinnummerInput = getInputWahlscheinnummer();
      await wahlscheinnummerInput.setValue(WAHLSCHEIN_MAX_VALUE + 1);
      await wahlscheinnummerInput.vm.validate();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });

  describe(COMPONENT_EVENT_TESTS, () => {
    beforeEach(() => {
      testPinia = createTestingPinia({
        createSpy: vi.fn,
      });
      wrapper = mount(TheUnguetilgeWahlscheineVerifyCard, {
        global: {
          plugins: [testPinia, vuetify],
        },
      });
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

      expect(getWahlscheinSpy).toHaveBeenCalledWith("123");
    });

    it("should_triggerResetWahlscheinnummerInput_when_searchButtonIsClickedAndASearchWasDoneBefore", async () => {
      const wahlbezirkStore = useWahlbezirkStore();

      const wahlscheinnummerInput = getInputWahlscheinnummer();
      await wahlscheinnummerInput.setValue(123);
      await wahlscheinnummerInput.vm.validate();

      wahlbezirkStore.ungueltigeWahlscheineActions.getUngueltigerWahlscheinByWahlscheinnummer =
        vi.fn(() => {
          return createUngueltigerWahlschein();
        });

      expect(wahlscheinnummerInput.vm.value).toStrictEqual("123");
      const searchButton = getSearchButton();
      await searchButton.trigger("click");

      await flushPromises();

      await searchButton.trigger("click");
      await flushPromises();
      expect(wahlscheinnummerInput.vm.value).toStrictEqual("");
    });

    it("should_triggerLoadUngueltigeWahlscheine_when_refreshWasClicked", async () => {
      const loadWahlscheinSpy = vi.spyOn(
        useWahlbezirkStore().ungueltigeWahlscheineActions,
        "loadUngueltigeWahlscheine"
      );

      const refreshButton = getRefreshButton();
      await refreshButton.trigger("click");

      expect(loadWahlscheinSpy).toHaveBeenCalled();
    });
  });

  function getInputWahlscheinnummer() {
    const wahlscheinnummerInput = wrapper.findComponent(VNumberInput);
    expect(
      wahlscheinnummerInput.element.getAttribute("data-test")
    ).toStrictEqual("number-input-wahlscheinnummer");
    return wahlscheinnummerInput;
  }

  function getSearchButton() {
    return wrapper.findComponent('[data-test="button-search"]');
  }

  function getRefreshButton() {
    return wrapper.findComponent(BaseButtonRefresh);
  }
});
