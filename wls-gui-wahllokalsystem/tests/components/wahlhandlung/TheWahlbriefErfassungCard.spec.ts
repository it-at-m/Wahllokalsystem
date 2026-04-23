import type BaseNumberInput from "@/components/common/inputs/BaseNumberInput.vue";
import type { TestingPinia } from "@pinia/testing";
import type { VForm } from "vuetify/components";

import { createTestingPinia } from "@pinia/testing";
import {
  COMPONENT_EVENT_TESTS,
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
import { flushPromises, mount, VueWrapper } from "@vue/test-utils";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { nextTick } from "vue";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import TheWahlbriefErfassungCard from "@/components/wahlhandlung/TheWahlbriefErfassungCard.vue";
import vuetify from "@/plugins/vuetify.ts";
import { useInfomanagementStore } from "@/stores/infomanagementStore.ts";
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

vi.mock("@/composables/briefwahl/briefwahlService", () => ({
  useBriefwahlService: () => ({
    postWahlbriefdaten: mockDefinitions.postWahlbriefdaten,
  }),
}));

describe("TheWahlbriefErfassungCard.vue", () => {
  let wrapper: VueWrapper<InstanceType<typeof TheWahlbriefErfassungCard>>;
  let wahlbezirkStore: ReturnType<typeof useWahlbezirkStore>;
  let testPinia: TestingPinia;

  const ResizeObserverMock = vi.fn(() => ({
    observe: vi.fn(),
    unobserve: vi.fn(),
    disconnect: vi.fn(),
  }));
  vi.stubGlobal("ResizeObserver", ResizeObserverMock);

  const validWahlbriefDaten = {
    wahlbriefe: 1,
    verzeichnisseUngueltige: 0,
    nachtraege: 0,
    nachtraeglichUeberbrachte: 0,
    zeitNachtraeglichUeberbrachte: undefined,
  };

  const invalidWahlbriefDaten = {
    wahlbriefe: 1,
    verzeichnisseUngueltige: 0,
    nachtraege: 0,
    nachtraeglichUeberbrachte: 1,
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
      const saveButton = wrapper.findComponent(BaseButtonSave);
      expect(saveButton.element.hasAttribute("disabled")).toStrictEqual(true);

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderWahlbriefErfassungCardWithEnabledSave_when_hasValidData", async (context) => {
      _initValidData();

      await nextTick();

      const saveButton = wrapper.findComponent(BaseButtonSave);
      expect(saveButton.element.hasAttribute("disabled")).toStrictEqual(false);

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderWahlbriefErfassungCardWithWithFailedValidation_when_timeIsMissing", async (context) => {
      _initInvalidData();

      await nextTick();

      const form = wrapper.findComponent(
        '[data-test="nachtraeglichUeberbrachteForm"'
      ) as VueWrapper<VForm>;

      await form.vm.validate();

      await flushPromises();

      const errorMessages = wrapper.findAll(".v-messages__message");

      expect(errorMessages.length).toBe(1);
      expect(errorMessages[0]?.text()).toBe("Feld darf nicht leer sein.");

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderWithDisabledInputForNachgelieferteWahlbriefe_when_currentTimeIsBeforeFruehesteSchliessungsuhrzeit", async (context) => {
      // @ts-expect-error: cannot set readonly
      useInfomanagementStore().fruehesteSchliessungsuhrzeit = "10:15:00";
      await nextTick();

      wrapper = mount(TheWahlbriefErfassungCard, {
        global: {
          plugins: [testPinia, vuetify],
        },
      });

      const input = wrapper.findComponent<typeof BaseNumberInput>(
        '[data-test="textFieldNachtraeglichUeberbrachteAnzahl"]'
      );

      expect(input.attributes().class).toContain("disabled");
      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderWithEnabledInputForNachgelieferteWahlbriefe_when_currentTimeIsAfterFruehesteSchliessungsuhrzeit", async (context) => {
      // @ts-expect-error: cannot set readonly
      useInfomanagementStore().fruehesteSchliessungsuhrzeit = "09:15:00";
      await nextTick();

      wrapper = mount(TheWahlbriefErfassungCard, {
        global: {
          plugins: [testPinia, vuetify],
        },
      });

      const input = wrapper.findComponent<typeof BaseNumberInput>(
        '[data-test="textFieldNachtraeglichUeberbrachteAnzahl"]'
      );

      expect(input.attributes().class).not.toContain("disabled");
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

      const saveButton = wrapper.findComponent(BaseButtonSave);
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

  function _initInvalidData() {
    wahlbezirkStore = useWahlbezirkStore(testPinia);
    wahlbezirkStore.wahlbriefDatenState.wahlbriefDaten = invalidWahlbriefDaten;

    wrapper.vm.anzahlWahlbriefeValid = true;
    wrapper.vm.anzahlVerzeichnisseValid = true;
    wrapper.vm.anzahlNachtraegeValid = true;
    wrapper.vm.anzahlNachtraeglichUeberbrachteValid = false;
  }
});
