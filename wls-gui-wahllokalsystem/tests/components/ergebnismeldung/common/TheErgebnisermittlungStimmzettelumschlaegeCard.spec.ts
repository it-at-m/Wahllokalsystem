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
import vuetify from "@/plugins/vuetify.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

const mockDefinitions = vi.hoisted(() => ({
  postStimmzettelumschlaege: vi.fn(),
}));

vi.mock(
  "@/composables/ergebnisermittlung/ergebnisermittlungService.ts",
  () => ({
    useErgebnisermittlungService: () => ({
      postStimmzettelumschlaege: mockDefinitions.postStimmzettelumschlaege,
    }),
  })
);

describe("TheErgebnisermittlungStimmzettelumschlaegeCard.vue", () => {
  let testPinia: TestingPinia;

  const { prepareWahl } = useWahlTestDataFactory();

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

  enableAutoUnmount(afterEach);

  describe(COMPONENT_RENDER_TESTS, () => {
    it("should_renderWithEnabledSaveButton_when_anzahlIsZeroAndUseTimeIsFalse", async (context) => {
      const wahlenStore = useWahlenStore();
      wahlenStore.wahlenState.wahlen = [
        prepareWahl()
          .wahlID("123")
          .stimmzettelumschlaege({ anzahlWaehler: 0 })
          .build(),
      ];

      const wrapper = mount(TheErgebnisermittlungStimmzettelumschlaegeCard, {
        global: {
          plugins: [testPinia, vuetify],
        },
        props: {
          wahlId: "123",
          title: "Titel",
        },
      });

      await flushPromises(); //update databinding and keep button disabled

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderWithDisabledSaveButton_when_invalidAnzahlIsEnteredAndUseTimeIsFalse", async (context) => {
      const wahlenStore = useWahlenStore();
      wahlenStore.wahlenState.wahlen = [
        prepareWahl()
          .wahlID("123")
          .stimmzettelumschlaege({ anzahlWaehler: -1 })
          .build(),
      ];

      const wrapper = mount(TheErgebnisermittlungStimmzettelumschlaegeCard, {
        global: {
          plugins: [testPinia, vuetify],
        },
        props: {
          wahlId: "123",
          title: "Titel",
        },
      });

      await flushPromises(); //update databinding and keep button disabled

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderWithDisabledSaveButton_when_anzahlExceedsMaximumAndUseTimeIsFalse", async (context) => {
      const wahlenStore = useWahlenStore();
      wahlenStore.wahlenState.wahlen = [
        prepareWahl()
          .wahlID("123")
          .stimmzettelumschlaege({ anzahlWaehler: 10000 })
          .build(),
      ];

      const wrapper = mount(TheErgebnisermittlungStimmzettelumschlaegeCard, {
        global: {
          plugins: [testPinia, vuetify],
        },
        props: {
          wahlId: "123",
          title: "Titel",
        },
      });

      await flushPromises();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderWithEnabledSaveButton_when_validAnzahlIsEnteredAndUseTimeIsFalse", async (context) => {
      const wahlenStore = useWahlenStore();
      wahlenStore.wahlenState.wahlen = [
        prepareWahl()
          .wahlID("123")
          .stimmzettelumschlaege({ anzahlWaehler: 33 })
          .build(),
      ];

      const wrapper = mount(TheErgebnisermittlungStimmzettelumschlaegeCard, {
        global: {
          plugins: [testPinia, vuetify],
        },
        props: {
          wahlId: "123",
          title: "Titel",
        },
      });

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

      const wrapper = mount(TheErgebnisermittlungStimmzettelumschlaegeCard, {
        global: {
          plugins: [testPinia, vuetify],
        },
        props: {
          wahlId: "123",
          title: "Titel",
        },
      });

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

      const wrapper = mount(TheErgebnisermittlungStimmzettelumschlaegeCard, {
        global: {
          plugins: [testPinia, vuetify],
        },
        props: {
          wahlId: "123",
          title: "Titel",
        },
      });

      const anzahlWaehler = wrapper.findComponent(VNumberInput);

      await anzahlWaehler.setValue(33);

      expect(
        wahlenStore.wahlenState.wahlen[0]?.stimmzettelumschlaege.anzahlWaehler
      ).toBe(33);
    });

    it("should_callSaveStimmzettelumschlaege_when_saveButtonIsClickedAndUseTimeIsFalse", async () => {
      const wahlenStore = useWahlenStore();
      wahlenStore.wahlenState.wahlen = [
        prepareWahl()
          .wahlID("123")
          .stimmzettelumschlaege({ anzahlWaehler: 33 })
          .build(),
      ];

      const wrapper = mount(TheErgebnisermittlungStimmzettelumschlaegeCard, {
        global: {
          plugins: [testPinia, vuetify],
        },
        props: {
          wahlId: "123",
          title: "Titel",
        },
      });

      await flushPromises();

      const saveButton = wrapper.findComponent(BaseButtonSave);
      mockDefinitions.postStimmzettelumschlaege.mockReturnValue(
        Promise.resolve()
      );
      await saveButton.trigger("click");

      expect(mockDefinitions.postStimmzettelumschlaege).toHaveBeenCalled();
    });
  });
});
