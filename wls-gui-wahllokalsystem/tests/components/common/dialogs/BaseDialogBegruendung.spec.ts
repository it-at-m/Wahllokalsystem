import {
  COMPONENT_EVENT_TESTS,
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
import { mount, VueWrapper } from "@vue/test-utils";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import BaseDialogBegruendung from "@/components/common/dialogs/BaseDialogBegruendung.vue";
import pinia from "@/plugins/pinia.ts";
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

  beforeEach(() => {
    wrapper = mount(BaseDialogBegruendung, {
      global: {
        plugins: [vuetify, pinia],
      },
      props: {
        visible: true,
        dialogtitle: "Abweichung erfordert Begründung",
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
    it("should_showBaseDialogBegruendung_when_mounted", async (context) => {
      await expect(document.body.innerHTML).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });

  describe(COMPONENT_EVENT_TESTS, () => {
    it("should_sendConfirm_when_confirmButtonIsClicked", async () => {
      await wrapper
        .findComponent('[data-test="basedialogbegruendung-btn-confirm"]')
        .trigger("click");

      expect(wrapper.emitted()).toHaveProperty("confirm");
    });

    it("should_disableSaveButton_when_propertyIsUsed", async () => {
      const dialogWithDisabledButton = mount(BaseDialogBegruendung, {
        global: {
          plugins: [vuetify],
        },
        props: {
          visible: true,
          dialogtitle: "Abweichung erfordert Begründung",
          isSaveDisabled: true,
        },
        slots: {
          default: "Es wurde eine Abweichung erkannt.",
        },
      });
      const saveButton = dialogWithDisabledButton.findComponent(BaseButtonSave);
      expect(saveButton.element.hasAttribute("disabled")).toStrictEqual(true);

      dialogWithDisabledButton.unmount();
    });

    it("should_sendCancelEvent_when_cancelButtonIsClicked", async () => {
      await wrapper
        .findComponent('[data-test="basedialogbegruendung-btn-cancel"]')
        .trigger("click");

      expect(wrapper.emitted()).toHaveProperty("cancel");
    });
  });
});
