import type { TestingPinia } from "@pinia/testing";
import type { VueWrapper } from "@vue/test-utils";

import { createTestingPinia } from "@pinia/testing";
import {
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
import { useWahlTestDataFactory } from "@tests/utils/wahl/WahlTestDataFactory.ts";
import { enableAutoUnmount, mount } from "@vue/test-utils";
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

import TheBeanstandeterWahlbriefRow from "@/components/wahlvorbereitung/TheBeanstandeterWahlbriefRow.vue";
import vuetify from "@/plugins/vuetify.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

describe("TheBeanstandeterWahlbriefRow", () => {
  let wrapper: VueWrapper;
  let pinia: TestingPinia;
  const { prepareWahl } = useWahlTestDataFactory();

  const ResizeObserverMock = vi.fn(() => ({
    observe: vi.fn(),
    unobserve: vi.fn(),
    disconnect: vi.fn(),
  }));

  vi.stubGlobal("visualViewport", new EventTarget()); // mocks responsive layouts
  vi.stubGlobal("ResizeObserver", ResizeObserverMock); // mocks dynamic components

  beforeAll(() => {
    createPinia();
  });

  beforeEach(async () => {
    pinia = createTestingPinia({ createSpy: vi.fn, stubActions: false });
    vi.clearAllMocks();
  });

  enableAutoUnmount(afterEach);

  describe(COMPONENT_RENDER_TESTS, () => {
    it("should_onlyShowWahlscheinHeader_when_noWahlenGiven", async (context) => {
      wrapper = mount(TheBeanstandeterWahlbriefRow, {
        global: {
          plugins: [pinia, vuetify],
        },
      });

      await nextTick();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
      expect(wrapper.html()).toContain("Wahlschein");
    });

    it("should_onlyShowHeaders_when_noBeanstandeteWahlbriefeGiven", async (context) => {
      const wahlenStore = useWahlenStore();
      wahlenStore.wahlen = [
        prepareWahl()
          .name("Wahl1")
          .wahlID("id1")
          .beanstandeteWahlbriefe([])
          .build(),
        prepareWahl()
          .name("Wahl2")
          .wahlID("id2")
          .beanstandeteWahlbriefe([])
          .build(),
      ];

      wrapper = mount(TheBeanstandeterWahlbriefRow, {
        global: {
          plugins: [pinia, vuetify],
        },
      });

      await nextTick();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
      expect(wrapper.html()).toContain("Wahlschein");
      expect(wrapper.html()).toContain("Wahl1");
      expect(wrapper.html()).toContain("Wahl2");
    });

    it("should_showOneRow_when_oneZurueckweisungsgrundGiven", async (context) => {
      const wahlenStore = useWahlenStore();
      wahlenStore.wahlen = [
        prepareWahl()
          .name("Wahl1")
          .wahlID("id1")
          .beanstandeteWahlbriefe(["GEGENSTAND_IM_UMSCHLAG"])
          .build(),
        prepareWahl()
          .name("Wahl2")
          .wahlID("id2")
          .beanstandeteWahlbriefe(["GEGENSTAND_IM_UMSCHLAG"])
          .build(),
      ];

      wrapper = mount(TheBeanstandeterWahlbriefRow, {
        global: {
          plugins: [pinia, vuetify],
        },
      });

      await nextTick();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
      expect(wrapper.html()).toContain("Gegenstand im Stimmzettelumschlag");
    });

    it("should_showMultipleRows_when_multipleZurueckweisungsgruendeGiven", async (context) => {
      const wahlenStore = useWahlenStore();
      wahlenStore.wahlen = [
        prepareWahl()
          .name("Wahl1")
          .wahlID("id1")
          .beanstandeteWahlbriefe([
            "GEGENSTAND_IM_UMSCHLAG",
            "ZUGELASSEN",
            "UNTERSCHRIFT_FEHLT",
          ])
          .build(),
        prepareWahl()
          .name("Wahl2")
          .wahlID("id2")
          .beanstandeteWahlbriefe([
            "GEGENSTAND_IM_UMSCHLAG",
            "ZUGELASSEN",
            "NICHT_WAHLBERECHTIGT",
          ])
          .build(),
      ];

      wrapper = mount(TheBeanstandeterWahlbriefRow, {
        global: {
          plugins: [pinia, vuetify],
        },
      });

      await nextTick();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
      expect(wrapper.html()).toContain("Gegenstand im Stimmzettelumschlag");
      expect(wrapper.html()).toContain("Zugelassen");
      expect(wrapper.html()).toContain("Unterschrift auf Wahlschein fehlt");
      expect(wrapper.html()).toContain("Für diese Wahl nicht wahlberechtigt");
    });
  });
});
