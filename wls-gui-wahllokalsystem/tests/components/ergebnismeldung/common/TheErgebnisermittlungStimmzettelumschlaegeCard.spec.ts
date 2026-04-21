import type { TestingPinia } from "@pinia/testing";

import { createTestingPinia } from "@pinia/testing";
import {
  COMPONENT_EVENT_TESTS,
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
import { useWahlTestDataFactory } from "@tests/utils/wahl/WahlTestDataFactory.ts";
import { enableAutoUnmount, flushPromises, mount } from "@vue/test-utils";
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
import { VNumberInput } from "vuetify/components";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import TheErgebnisermittlungStimmzettelumschlaegeCard from "@/components/ergebnismeldung/common/TheErgebnisermittlungStimmzettelumschlaegeCard.vue";
import router from "@/plugins/router.ts";
import vuetify from "@/plugins/vuetify.ts";
import { useInfomanagementStore } from "@/stores/infomanagementStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

const { prepareWahl } = useWahlTestDataFactory();

const mockDefinitions = vi.hoisted(() => ({
  postStimmzettelumschlaege: vi.fn(),
  resetAllAnwesenheiten: vi.fn(),
}));

vi.mock("@/composables/ergebnismeldung/common/ergebnisService.ts", () => ({
  useErgebnisService: () => ({
    postStimmzettelumschlaege: mockDefinitions.postStimmzettelumschlaege,
  }),
}));

vi.mock("@/stores/wahlvorstandStore.ts", () => ({
  useWahlvorstandStore: () => ({
    resetAllAnwesenheiten: mockDefinitions.resetAllAnwesenheiten,
  }),
}));

describe("TheErgebnisermittlungStimmzettelumschlaegeCard.vue", () => {
  let testPinia: TestingPinia;

  const ResizeObserverMock = vi.fn(() => ({
    observe: vi.fn(),
    unobserve: vi.fn(),
    disconnect: vi.fn(),
  }));

  vi.stubGlobal("visualViewport", new EventTarget());
  vi.stubGlobal("ResizeObserver", ResizeObserverMock);

  beforeAll(() => {
    createPinia();
  });

  beforeEach(async () => {
    testPinia = createTestingPinia({ createSpy: vi.fn, stubActions: false });
    vi.clearAllMocks();
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  enableAutoUnmount(afterEach);

  describe(COMPONENT_RENDER_TESTS, () => {
    it("should_renderWithEnabledSaveButton_when_anzahlIsZeroAndUseTimeIsFalse", async (context) => {
      _initWahlenStore(0);

      const wrapper = _mountComponent(testPinia);

      router.push = vi.fn();
      await flushPromises(); //update databinding and keep button disabled

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderWithDisabledSaveButton_when_invalidAnzahlIsEnteredAndUseTimeIsFalse", async (context) => {
      _initWahlenStore(-1);

      const wrapper = _mountComponent(testPinia);

      await flushPromises(); //update databinding and keep button disabled

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderWithDisabledSaveButton_when_anzahlExceedsMaximumAndUseTimeIsFalse", async (context) => {
      _initWahlenStore(10000);

      const wrapper = _mountComponent(testPinia);

      await flushPromises();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderWithEnabledSaveButton_when_validAnzahlIsEnteredAndUseTimeIsFalse", async (context) => {
      _initWahlenStore(33);

      const wrapper = _mountComponent(testPinia);

      await flushPromises(); //update databinding and keep button disabled

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderWithSaveButtonInLoadingState_when_isSavingIsTrueAndUseTimeIsFalse", async (context) => {
      const wahlenStore = useWahlenStore();
      wahlenStore.wahlenState.wahlen = [
        prepareWahl()
          .wahlID("123")
          .stimmzettelumschlaege({ anzahlWaehler: 33 })
          .build(),
      ];
      wahlenStore.stimmzettelumschlaegeState.isStimmzettelumschlaegeSaving = true;

      const wrapper = _mountComponent(testPinia);

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });

  describe(COMPONENT_EVENT_TESTS, () => {
    it("should_updateWahlenInStore_when_validAnzahlIsEnteredAndUseTimeIsFalse", async () => {
      const wahlenStore = useWahlenStore();
      wahlenStore.wahlenState.wahlen = [
        prepareWahl()
          .wahlID("123")
          .stimmzettelumschlaege({ anzahlWaehler: 0 })
          .build(),
      ];

      const wrapper = _mountComponent(testPinia);

      const anzahlWaehler = wrapper.findComponent(VNumberInput);

      await anzahlWaehler.setValue(33);

      expect(
        wahlenStore.wahlenState.wahlen[0]?.stimmzettelumschlaege.anzahlWaehler
      ).toBe(33);
    });

    it("should_callSaveStimmzettelumschlaege_when_saveButtonIsClickedAndUseTimeIsFalse", async () => {
      _initWahlenStore(33);

      const wrapper = _mountComponent(testPinia);

      await flushPromises();

      const saveButton = wrapper.findComponent(BaseButtonSave);
      mockDefinitions.postStimmzettelumschlaege.mockReturnValue(
        Promise.resolve()
      );
      await saveButton.trigger("click");

      expect(mockDefinitions.postStimmzettelumschlaege).toHaveBeenCalled();
      expect(mockDefinitions.resetAllAnwesenheiten).not.toHaveBeenCalled();
    });

    it("should_resetAllAnwesenheiten_when_saveIsCompletedInBWB", async () => {
      const infomanagementStore = useInfomanagementStore();
      // @ts-expect-error: cannot set readonly
      infomanagementStore.fruehesteSchliessungsuhrzeit = "08:00:00";

      const wahlenStore = useWahlenStore();
      wahlenStore.wahlenState.wahlen = [
        prepareWahl()
          .wahlID("123")
          .stimmzettelumschlaege({
            anzahlWaehler: 33,
            urneneroeffnungsUhrzeit: new Date("2026-01-01T08:00:00"),
          })
          .build(),
      ];

      const wrapper = _mountComponent(testPinia, true);

      await flushPromises();

      const saveButton = wrapper.findComponent(BaseButtonSave);
      mockDefinitions.postStimmzettelumschlaege.mockReturnValue(
        Promise.resolve()
      );
      router.push = vi.fn();

      await saveButton.trigger("click");

      await flushPromises();

      expect(mockDefinitions.resetAllAnwesenheiten).toHaveBeenCalled();
    });
  });
});

function _mountComponent(testPinia: TestingPinia, useTime = false) {
  return mount(TheErgebnisermittlungStimmzettelumschlaegeCard, {
    global: {
      plugins: [testPinia, vuetify],
    },
    props: {
      wahlId: "123",
      wahlbezirkId: "456",
      title: "Titel",
      useTime: useTime,
    },
  });
}

function _initWahlenStore(anzahlWaehler: number) {
  const wahlenStore = useWahlenStore();
  wahlenStore.wahlenState.wahlen = [
    prepareWahl()
      .wahlID("123")
      .stimmzettelumschlaege({ anzahlWaehler: anzahlWaehler })
      .build(),
  ];
}
