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
import TheErgebnisermittlungStimmzettelumschlaegeCard from "@/components/ergebnisermittlung/TheErgebnisermittlungStimmzettelumschlaegeCard.vue";
import vuetify from "@/plugins/vuetify.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

const mockDefinitions = vi.hoisted(() => ({
  saveStimmzettelumschlaege: vi.fn(),
}));

vi.mock(
  "@/composables/ergebnisermittlung/ergebnisermittlungService.ts",
  () => ({
    useErgebnisermittlungService: () => ({
      saveStimmzettelumschlaege: mockDefinitions.saveStimmzettelumschlaege,
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
    it("should_renderWithDisabledSaveButton_when_anzahlIsZero", async (context) => {
      const wahlenStore = useWahlenStore();
      wahlenStore.wahlen = [
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
        },
      });

      await flushPromises(); //update databinding and keep button disabled

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderWithDisabledSaveButton_when_invalidAnzahlIsEntered", async (context) => {
      const wahlenStore = useWahlenStore();
      wahlenStore.wahlen = [
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
        },
      });

      await flushPromises(); //update databinding and keep button disabled

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderWithDisabledSaveButton_when_anzahlExceedsMaximum", async (context) => {
      const wahlenStore = useWahlenStore();
      wahlenStore.wahlen = [
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
        },
      });

      await flushPromises();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderWithEnabledSaveButton_when_validAnzahlIsEntered", async (context) => {
      const wahlenStore = useWahlenStore();
      wahlenStore.wahlen = [
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
        },
      });

      await flushPromises(); //update databinding and keep button disabled

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderWithSaveButtonInLoadingState_when_isSavingIsTrue", async (context) => {
      const wahlenStore = useWahlenStore();
      wahlenStore.wahlen = [
        prepareWahl()
          .wahlID("123")
          .stimmzettelumschlaege({ anzahlWaehler: 33 })
          .build(),
      ];
      wahlenStore.isStimmzettelumschlaegeSaving = true;

      const wrapper = mount(TheErgebnisermittlungStimmzettelumschlaegeCard, {
        global: {
          plugins: [testPinia, vuetify],
        },
        props: {
          wahlId: "123",
        },
      });

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });

  describe(COMPONENT_EVENT_TESTS, () => {
    it("should_updateWahlenInStore_when_validAnzahlIsEntered", async () => {
      const wahlenStore = useWahlenStore();
      wahlenStore.wahlen = [
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
        },
      });

      const anzahlWaehler = wrapper.findComponent(VNumberInput);

      await anzahlWaehler.setValue(33);

      expect(wahlenStore.wahlen[0].stimmzettelumschlaege.anzahlWaehler).toBe(
        33
      );
    });

    it("should_callSaveStimmzettelumschlaege_when_saveButtonIsClicked", async () => {
      const wahlenStore = useWahlenStore();
      wahlenStore.wahlen = [
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
        },
      });

      await flushPromises();

      const saveButton = wrapper.findComponent(BaseButtonSave);
      await saveButton.trigger("click");

      const saveButton = wrapper.findComponent(BaseButtonSave);
      mockDefinitions.saveStimmzettelumschlaege.mockReturnValue(
        Promise.resolve()
      );
      await saveButton.trigger("click");

      expect(mockDefinitions.saveStimmzettelumschlaege).toHaveBeenCalled();
    });
  });
});
