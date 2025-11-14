import type { Wahl } from "@/types/wahl/Wahl.ts";
import type { TestingPinia } from "@pinia/testing";
import type { VueWrapper } from "@vue/test-utils";

import { createTestingPinia } from "@pinia/testing";
import {
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
import { useWahlTestDataFactory } from "@tests/utils/wahl/WahlTestDataFactory.ts";
import { flushPromises, mount } from "@vue/test-utils";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import TheBeanstandeteWahlbriefeBeschlussergebnis from "@/components/wahlhandlung/beanstandeteWahlbriefe/TheBeanstandeteWahlbriefeBeschlussergebnis.vue";
import vuetify from "@/plugins/vuetify.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { ZurueckweisungsgrundEnum } from "@/types/briefwahl/ZurueckweisungsgrundEnum.ts";

declare module "@vue/runtime-core" {
  interface ZurueckweisungRow {
    ungueltig: number[];
    grund: ZurueckweisungsgrundEnum;
  }

  interface ComponentCustomProperties {
    sumGueltig: number[];
    sumUngueltig: number[];
    ungueltigeeinzelsummen: ZurueckweisungRow[];
  }
}

describe("TheBeanstandeteWahlbriefeBeschlussergebnis", () => {
  let wrapper: VueWrapper;
  let testPinia: TestingPinia;

  const { prepareWahl } = useWahlTestDataFactory();

  const wahlen: Wahl[] = [
    prepareWahl()
      .name("wahl1")
      .beanstandeteWahlbriefe(["KEIN_ORIGINAL_SCHEIN", "ZUGELASSEN"])
      .build(),
    prepareWahl()
      .name("wahl2")
      .beanstandeteWahlbriefe(["ZUGELASSEN", "ZUGELASSEN"])
      .build(),
  ];

  beforeEach(() => {
    testPinia = createTestingPinia({
      createSpy: vi.fn,
    });

    wrapper = mount(TheBeanstandeteWahlbriefeBeschlussergebnis, {
      global: {
        plugins: [testPinia, vuetify],
      },
    });
  });

  afterEach(() => {
    wrapper.unmount();
  });

  describe(COMPONENT_RENDER_TESTS, () => {
    it("should_renderWahlbriefZulassungBeschlussergebnis_when_componentIsMountedWithoutBeanstandeteWahlbriefe", async (context) => {
      const wahlenStore = useWahlenStore(testPinia);
      wahlenStore.wahlenState.wahlen = [
        prepareWahl().name("wahl").beanstandeteWahlbriefe([]).build(),
      ];

      await flushPromises();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderWahlbriefZulassungBeschlussergebnis_when_componentIsMountedWithBeanstandeteWahlbriefe", async (context) => {
      const wahlenStore = useWahlenStore(testPinia);
      wahlenStore.wahlenState.wahlen = wahlen;

      await flushPromises();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });
});
