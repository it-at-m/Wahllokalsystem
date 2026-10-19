import { useDseStimmzettelTestDataFactory } from "@tests/utils/dse/DseStimmzettelTestDataFactory.ts";
import { mount } from "@vue/test-utils";
import { afterEach, describe, expect, it, vi } from "vitest";
import { nextTick } from "vue";

import TheStimmzettelContent from "@/components/dse/stimmzettelerfassung/TheStimmzettelContent.vue";

describe("TheStimmzettelContent", () => {
  afterEach(() => {
    vi.unstubAllGlobals();
  });

  describe("COMPONENT_RENDER_TESTS", () => {
    it("should_preloadOneWahlvorschlagPerIdleCallback_when_mounted", async () => {
      let idleCallback: IdleRequestCallback | undefined;
      vi.stubGlobal(
        "requestIdleCallback",
        vi.fn((callback: IdleRequestCallback) => {
          idleCallback = callback;
          return 1;
        })
      );
      const { createDseWahlvorschlag } = useDseStimmzettelTestDataFactory();

      const wrapper = mount(TheStimmzettelContent, {
        props: {
          activeWahlvorschlagId: null,
          activeKandidat: null,
          wahlvorschlaege: [createDseWahlvorschlag(), createDseWahlvorschlag()],
        },
        global: {
          stubs: {
            "v-slide-group": { template: "<div><slot /></div>" },
            "v-slide-group-item": { template: "<div><slot /></div>" },
            "base-wahlvorschlag-card": true,
            "v-progress-linear": true,
          },
        },
      });

      expect(wrapper.findAll("base-wahlvorschlag-card-stub")).toHaveLength(0);
      expect(wrapper.text()).toContain(
        "Stimmzettel wird vorbereitet (Wahlvorschlag 0 von 2)"
      );

      await idleCallback?.({ didTimeout: false, timeRemaining: () => 50 });

      expect(wrapper.findAll("base-wahlvorschlag-card-stub")).toHaveLength(1);
      expect(wrapper.text()).toContain(
        "Stimmzettel wird vorbereitet (Wahlvorschlag 1 von 2)"
      );

      await idleCallback?.({ didTimeout: false, timeRemaining: () => 50 });
      await nextTick();

      expect(wrapper.findAll("base-wahlvorschlag-card-stub")).toHaveLength(2);
      expect(wrapper.text()).not.toContain("Stimmzettel wird vorbereitet");
    });
  });
});
