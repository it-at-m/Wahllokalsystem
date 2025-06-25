import {
  COMPONENT_EVENT_TESTS,
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
import { mount, VueWrapper } from "@vue/test-utils";
import { afterEach, describe, expect, it, vi } from "vitest";

import BaseDialogBegruendung from "@/components/common/dialogs/BaseDialogBegruendung.vue";
import vuetify from "@/plugins/vuetify.ts";

describe("BaseDialogBegruendung.vue", () => {
  let wrapper: VueWrapper;
  vi.stubGlobal("visualViewport", new EventTarget());
  const ResizeObserverMock = vi.fn(() => ({
    observe: vi.fn(),
    unobserve: vi.fn(),
    disconnect: vi.fn(),
  }));
  vi.stubGlobal("ResizeObserver", ResizeObserverMock);

  const BEGRUENDUNG = "Begründung";

  afterEach(() => {
    if (wrapper) wrapper.unmount();
  });

  describe(COMPONENT_RENDER_TESTS, () => {
    it("should_showBaseDialogBegruendung_WithDisabledSaveButton", async (context) => {
      wrapper = setupWrapperBaseDialogBegruendung();

      await expect(document.body.innerHTML).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_showBaseDialogBegruendung_WithEnabledSaveButton", async (context) => {
      wrapper = setupWrapperBaseDialogBegruendung();

      const textarea = wrapper.findComponent(
        '[data-test="basedialogbegruendung-textarea"]'
      );
      await textarea.setValue(BEGRUENDUNG);

      await expect(document.body.innerHTML).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });

  describe(COMPONENT_EVENT_TESTS, () => {
    it("should_sendConfirmEventWithPayload_when_confirmButtonIsClickedAndInputIsValid", async () => {
      wrapper = setupWrapperBaseDialogBegruendung();

      const textarea = wrapper.findComponent(
        '[data-test="basedialogbegruendung-textarea"]'
      );
      await textarea.setValue(BEGRUENDUNG);

      await wrapper
        .findComponent('[data-test="basedialogbegruendung-btn-confirm"]')
        .trigger("click");

      expect(wrapper.emitted()).toHaveProperty("confirm");
      const confirmEventPayload = wrapper.emitted("confirm") as string[];
      expect(confirmEventPayload).toHaveLength(1);
      expect(confirmEventPayload[0]).toEqual([BEGRUENDUNG]);
    });

    it("should_notSendConfirmEvent_when_inputIsInvalid", async () => {
      wrapper = setupWrapperBaseDialogBegruendung();

      await wrapper
        .findComponent('[data-test="basedialogbegruendung-btn-confirm"]')
        .trigger("click");
      expect(wrapper.emitted()).not.toHaveProperty("confirm");
    });

    it("should_sendCancelEvent_when_cancelButtonIsClicked", async () => {
      wrapper = setupWrapperBaseDialogBegruendung();

      await wrapper
        .findComponent('[data-test="basedialogbegruendung-btn-cancel"]')
        .trigger("click");

      expect(wrapper.emitted()).toHaveProperty("cancel");
    });
  });
});

function setupWrapperBaseDialogBegruendung() {
  return mount(BaseDialogBegruendung, {
    global: {
      plugins: [vuetify],
    },
    props: {
      visible: true,
      dialogtitle: "Abweichung erfordert Begründung",
      label: "Bitte begründen Sie hier die Abweichung",
    },
    slots: {
      default: "Es wurde eine Abweichung erkannt.",
    },
  });
}
