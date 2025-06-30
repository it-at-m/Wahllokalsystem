import {
  COMPONENT_EVENT_TESTS,
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
import { mount, VueWrapper } from "@vue/test-utils";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

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

  beforeEach(() => {
    wrapper = mount(BaseDialogBegruendung, {
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
  });

  afterEach(() => {
    if (wrapper) wrapper.unmount();
  });

  describe(COMPONENT_RENDER_TESTS, () => {
    it("should_showBaseDialogBegruendungWithDisabledSaveButton_when_mounted", async (context) => {
      await expect(document.body.innerHTML).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_showBaseDialogBegruendungWithEnabledSaveButton_when_begruendungIsSetAndValid", async (context) => {
      const textarea = wrapper.findComponent(
        '[data-test="basedialogbegruendung-textarea"]'
      );
      await textarea.setValue(BEGRUENDUNG);

      await expect(document.body.innerHTML).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_showBaseDialogBegruendungWithDisabledSaveButton_when_begruendungIsSetAndNotValid", async (context) => {
      const textarea = wrapper.findComponent(
        '[data-test="basedialogbegruendung-textarea"]'
      );
      await textarea.setValue("a");

      await expect(document.body.innerHTML).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });

  describe(COMPONENT_EVENT_TESTS, () => {
    it("should_sendConfirmEventWithPayload_when_confirmButtonIsClickedAndInputIsValid", async () => {
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
      await wrapper
        .findComponent('[data-test="basedialogbegruendung-btn-confirm"]')
        .trigger("click");
      expect(wrapper.emitted()).not.toHaveProperty("confirm");
    });

    it("should_sendCancelEvent_when_cancelButtonIsClicked", async () => {
      await wrapper
        .findComponent('[data-test="basedialogbegruendung-btn-cancel"]')
        .trigger("click");

      expect(wrapper.emitted()).toHaveProperty("cancel");
    });
  });
});
