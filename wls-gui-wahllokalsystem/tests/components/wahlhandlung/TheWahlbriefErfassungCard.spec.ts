import type { TestingPinia } from "@pinia/testing";

import { createTestingPinia } from "@pinia/testing";
import {
  COMPONENT_EVENT_TESTS,
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
  mockAndStubResizeObserver,
} from "@tests/utils/testutils.ts";
import { mount, VueWrapper } from "@vue/test-utils";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { nextTick } from "vue";

import BaseWlsButtonSave from "@/components/common/buttons/BaseWlsButtonSave.vue";
import TheWahlbriefErfassungCard from "@/components/wahlhandlung/TheWahlbriefErfassungCard.vue";
import vuetify from "@/plugins/vuetify.ts";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";

declare module "@vue/runtime-core" {
  interface ComponentCustomProperties {
    anzahlWahlbriefeValid: boolean | null;
    anzahlVerzeichnisseValid: boolean | null;
    anzahlNachtraegeValid: boolean | null;
    anzahlNachtraeglichUeberbrachteValid: boolean | null;
  }
}

const mockDefinitions = vi.hoisted(() => ({
  postWahlbriefdaten: vi.fn(),
}));

vi.mock(
  import("@/composables/briefwahl/briefwahlService.ts"),
  async (importOriginal) => {
    const mod = await importOriginal();
    return {
      useBriefwahlService: () => ({
        ...mod.useBriefwahlService(),
        postWahlbriefdaten: mockDefinitions.postWahlbriefdaten,
      }),
    };
  }
);

describe("TheWahlbriefErfassungCard.vue", () => {
  let wrapper: VueWrapper<InstanceType<typeof TheWahlbriefErfassungCard>>;
  let wahlbezirkStore: ReturnType<typeof useWahlbezirkStore>;
  let testPinia: TestingPinia;

  mockAndStubResizeObserver();

  const validWahlbriefDaten = {
    wahlbriefe: 1,
    verzeichnisseUngueltige: 0,
    nachtraege: 0,
    nachtraeglichUeberbrachte: 0,
    zeitNachtraeglichUeberbrachte: undefined,
  };

  beforeEach(() => {
    const mockedNow = new Date();
    mockedNow.setHours(10, 0, 0);
    vi.useFakeTimers({ now: mockedNow });

    testPinia = createTestingPinia({
      stubActions: false,
      createSpy: vi.fn,
    });
    wrapper = mount(TheWahlbriefErfassungCard, {
      global: {
        plugins: [testPinia, vuetify],
      },
    });
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  describe(COMPONENT_RENDER_TESTS, () => {
    it("should_renderWahlbriefErfassungCardWithDisabledSave_when_mounted", async (context) => {
      const saveButton = wrapper.findComponent(BaseWlsButtonSave);
      expect(saveButton.element.hasAttribute("disabled")).toStrictEqual(true);

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderWahlbriefErfassungCardWithEnabledSave_when_hasValidData", async (context) => {
      _initValidData();

      await nextTick();

      const saveButton = wrapper.findComponent(BaseWlsButtonSave);
      expect(saveButton.element.hasAttribute("disabled")).toStrictEqual(false);

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });

  describe(COMPONENT_EVENT_TESTS, () => {
    it("should_callSendWahlbriefdaten_when_saveButtonIsClicked", async () => {
      _initValidData();

      await nextTick();

      const sendWahlbriefdatenSpy = vi.spyOn(
        wahlbezirkStore.wahlbriefDatenActions,
        "sendWahlbriefdaten"
      );

      const saveButton = wrapper.findComponent(BaseWlsButtonSave);
      await saveButton.trigger("click");

      mockDefinitions.postWahlbriefdaten.mockResolvedValue(Promise.resolve());

      expect(sendWahlbriefdatenSpy).toHaveBeenCalled();
    });
  });

  function _initValidData() {
    wahlbezirkStore = useWahlbezirkStore(testPinia);
    wahlbezirkStore.wahlbriefDatenState.wahlbriefDaten = validWahlbriefDaten;

    wrapper.vm.anzahlWahlbriefeValid = true;
    wrapper.vm.anzahlVerzeichnisseValid = true;
    wrapper.vm.anzahlNachtraegeValid = true;
    wrapper.vm.anzahlNachtraeglichUeberbrachteValid = true;
  }
});
