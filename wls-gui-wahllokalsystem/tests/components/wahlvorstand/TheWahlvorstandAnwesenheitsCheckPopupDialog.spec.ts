import type { Ref } from "vue";

import { createTestingPinia } from "@pinia/testing";
import {
  COMPONENT_EVENT_TESTS,
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
  stubVisualViewport,
} from "@tests/utils/testutils.ts";
import { flushPromises, mount, VueWrapper } from "@vue/test-utils";
import { storeToRefs } from "pinia";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { VBtn } from "vuetify/components";

import TheWahlvorstandAnwesenheitsCheckPopupDialog from "@/components/wahlvorstand/TheWahlvorstandAnwesenheitsCheckPopupDialog.vue";
import vuetify from "@/plugins/vuetify.ts";
import { useInfomanagementStore } from "@/stores/infomanagementStore.ts";

const mockDefinitions = vi.hoisted(() => ({
  resetAllAnwesenheiten: vi.fn(),
  routerPush: vi.fn(),
  setupTimer: vi.fn(),
  clearTimer: vi.fn(),
}));

let componentCallback: () => void;

vi.mock(import("@/plugins/router.ts"), async (importOriginal) => {
  const mod = await importOriginal();
  return {
    default: {
      ...mod.default,
      push: mockDefinitions.routerPush,
    },
  };
});
vi.mock(import("@/composables/scheduler/dateOfActionTimeout.ts"), () => ({
  useDateOfActionTimeout: (
    title: string,
    dateOfAction: Ref<Date | undefined>,
    callback: () => void
  ) => {
    componentCallback = callback;
    return {
      setupTimer: mockDefinitions.setupTimer,
      clearTimer: mockDefinitions.clearTimer,
    };
  },
}));

vi.mock("@/stores/wahlvorstandStore.ts", () => ({
  useWahlvorstandStore: () => ({
    resetAllAnwesenheiten: mockDefinitions.resetAllAnwesenheiten,
  }),
}));

describe("TheWahlvorstandAnwesenheitsCheckPopupDialog.vue", () => {
  let wrapper: VueWrapper;

  stubVisualViewport();

  beforeEach(() => {
    wrapper = mount(TheWahlvorstandAnwesenheitsCheckPopupDialog, {
      attachTo: document.body,
      global: {
        plugins: [
          createTestingPinia({
            createSpy: vi.fn,
            stubActions: false,
          }),
          vuetify,
        ],
      },
    });
  });

  afterEach(() => {
    if (wrapper) {
      wrapper.unmount();
    }
    document.body.innerHTML = "";
    document.head.innerHTML = "";
    vi.clearAllMocks();
    vi.resetAllMocks();
  });

  describe(COMPONENT_RENDER_TESTS, () => {
    it("should_notRenderDialog_when_timeoutCallbackWasNotTriggered", async (context) => {
      const { dateTimeToCheckAnwesenheit } = storeToRefs(
        useInfomanagementStore()
      );
      // @ts-expect-error: cannot set readonly
      dateTimeToCheckAnwesenheit.value = new Date("2026-06-26T18:21:23.123");

      await flushPromises();

      await expect(document.body.innerHTML).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderDialog_when_timeoutCallbackWasTriggered", async (context) => {
      const { dateTimeToCheckAnwesenheit } = storeToRefs(
        useInfomanagementStore()
      );
      // @ts-expect-error: cannot set readonly
      dateTimeToCheckAnwesenheit.value = new Date("2026-06-26T18:21:23.123");

      componentCallback();

      await flushPromises();

      await expect(document.body.innerHTML).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });

  describe(COMPONENT_EVENT_TESTS, () => {
    it("should_triggerResetAllAnwesenheitSwitchToWahlvorstandAndCloseDialog_when_confirmClicked", async (context) => {
      const { dateTimeToCheckAnwesenheit } = storeToRefs(
        useInfomanagementStore()
      );
      // @ts-expect-error: cannot set readonly
      dateTimeToCheckAnwesenheit.value = new Date("2026-06-26T18:21:23.123");

      componentCallback();

      // Wait for the dialog to become visible
      await flushPromises();

      const confirmButton = wrapper.findAllComponents(VBtn)[1];
      expect(confirmButton?.attributes("data-test")).toStrictEqual(
        "basedialog-btn-confirm"
      );
      await confirmButton?.trigger("click");

      await expect(document.body.innerHTML).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
      expect(mockDefinitions.routerPush.mock.calls).toStrictEqual([
        [{ name: "wahlvorstand" }],
      ]);
      expect(mockDefinitions.resetAllAnwesenheiten).toHaveBeenCalledTimes(1);
    });

    it("should_resetTimer_when_componentIsUnmounted", () => {
      const { dateTimeToCheckAnwesenheit } = storeToRefs(
        useInfomanagementStore()
      );
      // @ts-expect-error: cannot set readonly
      dateTimeToCheckAnwesenheit.value = new Date("2026-06-26T18:21:23.123");

      wrapper.unmount();

      expect(mockDefinitions.clearTimer).toHaveBeenCalledOnce();
    });

    it("should_startTimer_when_componentIsMounted", () => {
      expect(mockDefinitions.setupTimer).toHaveBeenCalledOnce();
    });
  });
});
