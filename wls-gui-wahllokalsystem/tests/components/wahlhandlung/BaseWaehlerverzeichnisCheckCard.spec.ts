import type { VueWrapper } from "@vue/test-utils";

import { createTestingPinia } from "@pinia/testing";
import {
  COMPONENT_EVENT_TESTS,
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
import { usePflegeWaehlerverzeichnisTestDataFactory } from "@tests/utils/wahlhandlung/PflegeWaehlerverzeichnisTestDataFactory.ts";
import { flushPromises, mount } from "@vue/test-utils";
import { beforeEach, describe, expect, it, vi } from "vitest";

import BaseWaehlerverzeichnisCheckCard from "@/components/wahlhandlung/BaseWaehlerverzeichnisCheckCard.vue";
import vuetify from "@/plugins/vuetify.ts";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";

const { preparePflegeWaehlerverzeichnis } =
  usePflegeWaehlerverzeichnisTestDataFactory();

describe("BaseWaehlerverzeichnisCheckCard.vue", () => {
  let wrapper: VueWrapper;

  const ResizeObserverMock = vi.fn(
    class MockedResizeObserverMock {
      observe = vi.fn();
      unobserve = vi.fn();
      disconnect = vi.fn();
    } as never
  );
  vi.stubGlobal("ResizeObserver", ResizeObserverMock);

  beforeEach(() => {
    wrapper = mount(BaseWaehlerverzeichnisCheckCard, {
      global: {
        plugins: [
          createTestingPinia({
            createSpy: vi.fn,
          }),
          vuetify,
        ],
      },
    });
    vi.clearAllMocks();
  });

  describe(COMPONENT_RENDER_TESTS, () => {
    it("should_renderWithNoChangesInWaehlerverzeichnisAndAllCheckboxesSelected_when_mounted", async (context) => {
      useWahlbezirkStore().pflegeWaehlerverzeichnisState.pflegeWaehlerverzeichnis =
        preparePflegeWaehlerverzeichnis()
          .mitteilungUeberUngueltigeWahlscheineErhalten(true)
          .nachtraeglicheBerichtigung(true)
          .waehlerverzeichnisUnchanged(true)
          .build();

      await flushPromises();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderWithChangesInWaehlerverzeichnisAndAllCheckboxesSelected_when_mounted", async (context) => {
      useWahlbezirkStore().pflegeWaehlerverzeichnisState.pflegeWaehlerverzeichnis =
        preparePflegeWaehlerverzeichnis()
          .mitteilungUeberUngueltigeWahlscheineErhalten(true)
          .nachtraeglicheBerichtigung(true)
          .waehlerverzeichnisUnchanged(false)
          .build();

      await flushPromises();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderWithNoChangesInWaehlerverzeichnisAndNoCheckboxesSelected_when_mounted", async (context) => {
      useWahlbezirkStore().pflegeWaehlerverzeichnisState.pflegeWaehlerverzeichnis =
        preparePflegeWaehlerverzeichnis()
          .mitteilungUeberUngueltigeWahlscheineErhalten(false)
          .nachtraeglicheBerichtigung(false)
          .waehlerverzeichnisUnchanged(true)
          .build();

      await flushPromises();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderSaveButtonDisabled_when_mitteilungUeberUngueltigeWahlscheineErhaltenIsFalse", async (context) => {
      useWahlbezirkStore().pflegeWaehlerverzeichnisState.pflegeWaehlerverzeichnis =
        preparePflegeWaehlerverzeichnis()
          .mitteilungUeberUngueltigeWahlscheineErhalten(false)
          .nachtraeglicheBerichtigung(false)
          .waehlerverzeichnisUnchanged(true)
          .build();

      await flushPromises();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderSaveInLoadingState_when_pflegeWaehlerverzeichnisIsSavingIsTrue", async (context) => {
      const wahlbezirkStore = useWahlbezirkStore();
      wahlbezirkStore.pflegeWaehlerverzeichnisState.pflegeWaehlerverzeichnis =
        preparePflegeWaehlerverzeichnis()
          .mitteilungUeberUngueltigeWahlscheineErhalten(true)
          .nachtraeglicheBerichtigung(true)
          .waehlerverzeichnisUnchanged(true)
          .build();
      wahlbezirkStore.pflegeWaehlerverzeichnisState.pflegeWaehlerverzeichnisIsSaving = true;

      await flushPromises();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });

  describe(COMPONENT_EVENT_TESTS, () => {
    it("should_triggerSendPflegeWaehlerverzeichnis_when_saveButtonIsClicked", async () => {
      const wahlbezirkStore = useWahlbezirkStore();
      wahlbezirkStore.pflegeWaehlerverzeichnisState.pflegeWaehlerverzeichnis =
        preparePflegeWaehlerverzeichnis()
          .mitteilungUeberUngueltigeWahlscheineErhalten(true)
          .nachtraeglicheBerichtigung(true)
          .waehlerverzeichnisUnchanged(true)
          .build();

      await flushPromises();

      const sendPflegeWaehlerverzeichnisSpy = vi.spyOn(
        wahlbezirkStore.pflegeWaehlerverzeichnisActions,
        "sendPflegeWaehlerverzeichnis"
      );

      const saveButton = wrapper.findComponent('[data-test="buttonSave"]');

      expect(sendPflegeWaehlerverzeichnisSpy).toHaveBeenCalledTimes(0);
      await saveButton.trigger("click");
      expect(sendPflegeWaehlerverzeichnisSpy).toHaveBeenCalledTimes(1);
    });
  });
});
