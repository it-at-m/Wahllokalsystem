import type { Wahl } from "@/types/wahl/Wahl.ts";
import type { VueWrapper } from "@vue/test-utils";

import { createTestingPinia } from "@pinia/testing";
import {
  COMPONENT_EVENT_TESTS,
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
import { useWahlTestDataFactory } from "@tests/utils/wahl/WahlTestDataFactory.ts";
import { mount } from "@vue/test-utils";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import TheBeanstandeteWahlbriefeBeschlussergebnis from "@/components/wahlvorbereitung/beanstandeteWahlbriefe/TheBeanstandeteWahlbriefeBeschlussergebnis.vue";
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

  const { prepareWahl } = useWahlTestDataFactory();

  const wahlen: Wahl[] = [
    prepareWahl()
      .beanstandeteWahlbriefe(["KEIN_ORIGINAL_SCHEIN", "ZUGELASSEN"])
      .build(),
    prepareWahl().beanstandeteWahlbriefe(["ZUGELASSEN", "ZUGELASSEN"]).build(),
  ];

  beforeEach(() => {
    const testPinia = createTestingPinia({
      createSpy: vi.fn,
    });

    const wahlenStore = useWahlenStore(testPinia);
    wahlenStore.wahlen = wahlen;

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
    it("should_renderWahlbriefZulassungBeschlussergebnis_when_componentIsMounted", async (context) => {
      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });

  describe(COMPONENT_EVENT_TESTS, () => {
    it("should_calculateSums_when_componentIsMounted", async () => {
      expect(wrapper.vm.sumGueltig).toEqual([1, 2]);
      expect(wrapper.vm.sumUngueltig).toEqual([1, 0]);
      wrapper.vm.ungueltigeeinzelsummen.forEach((row) => {
        if (row.grund === "KEIN_ORIGINAL_SCHEIN") {
          expect(row.ungueltig).toEqual([1, 0]);
        } else {
          expect(row.ungueltig).toEqual([0, 0]);
        }
      });
    });
  });
});
