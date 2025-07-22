import { createTestingPinia } from "@pinia/testing";
import {
  COMPONENT_EVENT_TESTS,
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
import { mount, VueWrapper } from "@vue/test-utils";
import { storeToRefs } from "pinia";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { nextTick } from "vue";

import TheBedenklicheWahlbriefeBeschlussergebnis from "@/components/wahlvorbereitung/TheBedenklicheWahlbriefeBeschlussergebnis.vue";
import { useMonitoringStore } from "@/stores/monitoringStore.ts";
import vuetify from "@/plugins/vuetify.ts";

describe("TheBedenklicheWahlbriefeBeschlussergebnis", () => {
  let wrapper: VueWrapper;

  beforeEach(() => {
    wrapper = mount(TheBedenklicheWahlbriefeBeschlussergebnis, {
      global: {
        plugins: [
          createTestingPinia({
            createSpy: vi.fn,
          }),
          vuetify,
        ],
      },
    });
  });

  afterEach(() => {
    wrapper.unmount();

    vi.resetAllMocks();
    vi.clearAllMocks();
  });

  describe(COMPONENT_RENDER_TESTS, () => {
    it("should_renderWahlbriefZulassungBeschlussergebnis_when_componentIsMounted", async (context) => {
      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderWahlbriefZulassungBeschlussergebnis_when_dataInStoreIsEdited", async (context) => {
        await expect(wrapper.html()).toMatchFileSnapshot(
          getSnapshotFilename(context)
        );
      });
  });

  describe(COMPONENT_EVENT_TESTS, () => {
    it("should_calculateSummeGueltigerWahlbriefe_when_componentIsMounted", async () => {
      const wrapper2 = initWrapper();
      nextTick();
      expect(wrapper2.vm.sumGueltig).toEqual([1, 1, 2]);
    });
  });
});

function initWrapper() {
  return mount(TheBedenklicheWahlbriefeBeschlussergebnis, {
    global: {
      plugins: [
        createTestingPinia({
          createSpy: vi.fn,
        }),
        vuetify,
      ],
    },
  });
}
