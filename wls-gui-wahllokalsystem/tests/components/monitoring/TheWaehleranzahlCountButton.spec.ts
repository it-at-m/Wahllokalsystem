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
import { createVuetify } from "vuetify";
import * as components from "vuetify/components";
import { VBtn } from "vuetify/components";
import * as directives from "vuetify/directives";

import TheWaehleranzahlCountButton from "@/components/monitoring/TheWaehleranzahlCountButton.vue";
import { useMonitoringStore } from "@/stores/monitoringStore.ts";

describe("TheWaehleranzahlCountButton", () => {
  let vuetify: ReturnType<typeof createVuetify>;
  let wrapper: VueWrapper;

  beforeEach(() => {
    vuetify = createVuetify({
      components,
      directives,
    });

    wrapper = mount(TheWaehleranzahlCountButton, {
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
    it("should_renderButtonWithCorrectLabelAndIcon_when_componentIsMounted", async (context) => {
      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderButtonWithCorrectWaehleranzahl_when_waehleranzahlIsSet", async (context) => {
      const { waehler } = storeToRefs(useMonitoringStore());
      waehler.value = 12;

      await nextTick();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });

  describe(COMPONENT_EVENT_TESTS, () => {
    it("should_callIncreaseWaehlerFunction_when_buttonClicked", async () => {
      const { increaseWaehlerByOne } = useMonitoringStore();

      const button = wrapper.findComponent(VBtn);
      await button.trigger("click");

      await nextTick();

      expect(increaseWaehlerByOne).toHaveBeenCalledOnce();
    });

    it("should_callIncreaseWaehlerFunction_when_keyupEventWithPlus", async () => {
      const { increaseWaehlerByOne } = useMonitoringStore();

      const event = new KeyboardEvent('keyup', { key: '+'});
      document.dispatchEvent(event);

      await nextTick();

      expect(increaseWaehlerByOne).toHaveBeenCalledOnce();
    });
  });
});
