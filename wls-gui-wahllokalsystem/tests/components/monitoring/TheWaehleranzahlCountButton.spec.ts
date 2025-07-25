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
import { VBtn } from "vuetify/components";

import TheWaehleranzahlCountButton from "@/components/monitoring/TheWaehleranzahlCountButton.vue";
import vuetify from "@/plugins/vuetify.ts";
import { useMonitoringStore } from "@/stores/monitoringStore.ts";

describe("TheWaehleranzahlCountButton", () => {
  let wrapper: VueWrapper;

  beforeEach(() => {
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

      const event = new KeyboardEvent("keyup", { key: "+" });
      document.dispatchEvent(event);

      await nextTick();

      expect(increaseWaehlerByOne).toHaveBeenCalledOnce();
    });

    it("should_notCallIncreaseWaehlerFunction_when_keyupEventOnTextarea", async () => {
      const { increaseWaehlerByOne } = useMonitoringStore();

      const event = new KeyboardEvent("keyup", { key: "+" });
      const textarea = document.createElement("textarea");
      textarea.dispatchEvent(event);

      await nextTick();

      expect(increaseWaehlerByOne).not.toHaveBeenCalledOnce();
    });
  });
});
