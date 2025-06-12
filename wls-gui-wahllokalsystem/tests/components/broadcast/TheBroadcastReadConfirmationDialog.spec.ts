import type { VueWrapper } from "@vue/test-utils";

import { createTestingPinia } from "@pinia/testing";
import {
  COMPONENT_EVENT_TESTS,
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
import { mount } from "@vue/test-utils";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { nextTick } from "vue";

import TheBroadcastReadConfirmationDialog from "@/components/broadcast/TheBroadcastReadConfirmationDialog.vue";
import vuetify from "@/plugins/vuetify.ts";
import { useBroadcastStore } from "@/stores/broadcastStore.ts";

describe("TheBroadcastReadConfirmationDialog.vue", () => {
  let wrapper: VueWrapper<
    InstanceType<typeof TheBroadcastReadConfirmationDialog>
  >;
  vi.stubGlobal("visualViewport", new EventTarget());

  beforeEach(() => {
    wrapper = mount(TheBroadcastReadConfirmationDialog, {
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
    document.body.innerHTML = "";
    document.head.innerHTML = "";

    vi.resetAllMocks();
    vi.clearAllMocks();
  });

  describe(COMPONENT_RENDER_TESTS, () => {
    it("should_renderDialog_when_broadcastMessageIsGiven", async (context) => {
      const broadcastStore = useBroadcastStore();
      // @ts-expect-error: cannot set readonly
      broadcastStore.currentBroadcastNachricht = "testing the broadcast dialog";

      await nextTick();

      await expect(document.body.innerHTML).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderDialogWithOkButtonEnabled_when_checkboxWasSelected", async (context) => {
      const broadcastStore = useBroadcastStore();
      // @ts-expect-error: cannot set readonly
      broadcastStore.currentBroadcastNachricht = "testing the broadcast dialog";

      await nextTick();

      const markAsReadCheckbox = wrapper.findComponent(
        '[data-test="checkbox-mark-as-read"]'
      );
      await markAsReadCheckbox.setValue(true);

      await expect(document.body.innerHTML).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_notRenderDialog_when_noBroadcastMessageIsGiven", async () => {
      const broadcastStore = useBroadcastStore();
      broadcastStore.currentBroadcastNachricht = null;

      await nextTick();

      expect(document.body.innerHTML).toStrictEqual("");
    });
  });

  describe(COMPONENT_EVENT_TESTS, () => {
    it("should_markActiveMessageAsRead_when_okWasClicked", async () => {
      const broadcastStore = useBroadcastStore();
      // @ts-expect-error: cannot set readonly
      broadcastStore.currentBroadcastNachricht = "testing the broadcast dialog";

      await nextTick();

      const markAsReadCheckbox = wrapper.findComponent(
        '[data-test="checkbox-mark-as-read"]'
      );
      await markAsReadCheckbox.setValue(true);

      const okButton = wrapper.findComponent('[data-test="button-ok"]');
      await okButton.trigger("click");

      expect(
        broadcastStore.markMessageAsReadAndLoadNextMessage
      ).toHaveBeenCalledTimes(1);
    });
  });
});
