import type { TestingPinia } from "@pinia/testing";
import type { VueWrapper } from "@vue/test-utils";
import type { VIcon } from "vuetify/components";

import { createTestingPinia } from "@pinia/testing";
import {
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
  mockAndStubResizeObserver,
  stubVisualViewport,
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

import TheBeanstandeteWahlbriefeRowStatusIcon from "@/components/wahlhandlung/beanstandeteWahlbriefe/TheBeanstandeteWahlbriefeRowStatusIcon.vue";
import vuetify from "@/plugins/vuetify.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

describe("TheBeanstandeteWahlbriefeRowStatusIcon", () => {
  let wrapper: VueWrapper;
  let pinia: TestingPinia;
  const { prepareWahl } = useWahlTestDataFactory();

  mockAndStubResizeObserver();
  stubVisualViewport();

  beforeAll(() => {
    createPinia();
  });

  beforeEach(async () => {
    pinia = createTestingPinia({ createSpy: vi.fn, stubActions: false });
    vi.clearAllMocks();
  });

  enableAutoUnmount(afterEach);

  describe(COMPONENT_RENDER_TESTS, () => {
    it("should_showRowIconToBeValidAndGreen_when_rowHasValidInputs", async (context) => {
      const wahlenStore = useWahlenStore();
      wahlenStore.wahlenState.wahlen = [
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

      wrapper = mount(TheBeanstandeteWahlbriefeRowStatusIcon, {
        global: {
          plugins: [pinia, vuetify],
        },
        props: {
          index: 0,
        },
      });

      await nextTick();

      const iconRow0 = wrapper.findComponent<typeof VIcon>(
        `[data-test="rowstatus-icon-0"]`
      );

      expect(iconRow0.props("icon")).toBe("$valid");
      expect(iconRow0.props("color")).toBe("success");
      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_showRowIconToBeInvalidAndRed_when_rowHasMissingInputs", async (context) => {
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

      wrapper = mount(TheBeanstandeteWahlbriefeRowStatusIcon, {
        global: {
          plugins: [pinia, vuetify],
        },
        props: {
          index: 0,
        },
      });

      await nextTick();

      const iconRow0 = wrapper.findComponent<typeof VIcon>(
        `[data-test="rowstatus-icon-0"]`
      );

      expect(iconRow0.props("icon")).toBe("$valid");
      expect(iconRow0.props("color")).toBe("success");

      // eslint-disable-next-line  @typescript-eslint/no-non-null-assertion
      wahlenStore.wahlenState.wahlen[0]!.beanstandeteWahlbriefe[0] = null;
      await nextTick();

      expect(iconRow0.props("icon")).toBe("$edit");
      expect(iconRow0.props("color")).toBe("error");
      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });
});
