import type { TestingPinia } from "@pinia/testing";
import type { VueWrapper } from "@vue/test-utils";

import { createTestingPinia } from "@pinia/testing";
import {
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
  mockAndStubResizeObserver,
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
import { type VTab } from "vuetify/components";

import vuetify from "@/plugins/vuetify.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import BWBWahlbriefZulassungView from "@/views/wahlhandlung/BWBWahlbriefZulassungView.vue";

describe("BWBWahlbriefZulassungView", () => {
  let wrapper: VueWrapper<InstanceType<typeof BWBWahlbriefZulassungView>>;
  let pinia: TestingPinia;

  const { prepareWahl } = useWahlTestDataFactory();

  mockAndStubResizeObserver();

  vi.stubGlobal("visualViewport", new EventTarget());

  beforeAll(() => {
    createPinia();
  });

  beforeEach(async () => {
    pinia = createTestingPinia({ createSpy: vi.fn, stubActions: false });
    vi.clearAllMocks();
  });

  enableAutoUnmount(afterEach);

  describe(COMPONENT_RENDER_TESTS, () => {
    it("should_renderWithEditIcon_when_beanstandeteWahlbriefeTableNotValid", async (context) => {
      const wahlenStore = useWahlenStore();
      wahlenStore.wahlenState.wahlen = [
        prepareWahl()
          .name("Wahl1")
          .wahlID("id1")
          .beanstandeteWahlbriefe(["KEIN_ORIGINAL_SCHEIN"])
          .build(),
        prepareWahl()
          .name("Wahl2")
          .wahlID("id2")
          .beanstandeteWahlbriefe([null])
          .build(),
      ];

      wrapper = mount(BWBWahlbriefZulassungView, {
        global: {
          plugins: [pinia, vuetify],
        },
      });

      await flushPromises();

      const wahlbriefeZulassenTab = wrapper.findComponent<typeof VTab>(
        '[data-test="wahlbriefe-zulassen-tab"]'
      );

      expect(wahlbriefeZulassenTab.props("prependIcon")).toBe("$edit");
      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderWithValidIcon_when_beanstandeteWahlbriefeTableHasValidRows", async (context) => {
      const wahlenStore = useWahlenStore();
      wahlenStore.wahlenState.wahlen = [
        prepareWahl()
          .name("Wahl1")
          .wahlID("id1")
          .beanstandeteWahlbriefe(["KEIN_ORIGINAL_SCHEIN"])
          .build(),
        prepareWahl()
          .name("Wahl2")
          .wahlID("id2")
          .beanstandeteWahlbriefe(["KEIN_ORIGINAL_SCHEIN"])
          .build(),
      ];

      wrapper = mount(BWBWahlbriefZulassungView, {
        global: {
          plugins: [pinia, vuetify],
        },
      });

      await flushPromises();

      const wahlbriefeZulassenTab = wrapper.findComponent<typeof VTab>(
        '[data-test="wahlbriefe-zulassen-tab"]'
      );

      expect(wahlbriefeZulassenTab.props("prependIcon")).toBe("$valid");
      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderWithValidIcon_when_beanstandeteWahlbriefeTableHasNoRows", async (context) => {
      wrapper = mount(BWBWahlbriefZulassungView, {
        global: {
          plugins: [pinia, vuetify],
        },
      });

      await flushPromises();

      const wahlbriefeZulassenTab = wrapper.findComponent<typeof VTab>(
        '[data-test="wahlbriefe-zulassen-tab"]'
      );

      expect(wahlbriefeZulassenTab.props("prependIcon")).toBe("$valid");
      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });
});
