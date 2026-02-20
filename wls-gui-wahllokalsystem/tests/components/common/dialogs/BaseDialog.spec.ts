import {
  COMPONENT_EVENT_TESTS,
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
import { mount, VueWrapper } from "@vue/test-utils";
import { afterEach, describe, expect, it, vi } from "vitest";

import BaseDialog from "@/components/common/dialogs/BaseDialog.vue";
import vuetify from "@/plugins/vuetify.ts";

describe("BaseDialog.vue", () => {
  let wrapper: VueWrapper;
  vi.stubGlobal("visualViewport", new EventTarget());

  afterEach(() => {
    if (wrapper) wrapper.unmount();
  });

  describe(COMPONENT_RENDER_TESTS, () => {
    it("should_showBaseDialogWithConfirmButton", async (context) => {
      wrapper = setupWrapperWithConfirmButton();

      await expect(document.body.innerHTML).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_showBaseDialogWithConfirmAndCancelButton", async (context) => {
      wrapper = setupWrapperWithConfirmAndCancelButton();

      await expect(document.body.innerHTML).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });

  describe(COMPONENT_EVENT_TESTS, () => {
    it("should_sendConfirmEvent_when_confirmButtonIsClicked", async () => {
      wrapper = setupWrapperWithConfirmButton();

      await wrapper
        .findComponent('[data-test="basedialog-btn-confirm"]')
        .trigger("click");

      expect(wrapper.emitted()).toHaveProperty("confirm");
    });

    it("should_sendCancelEvent_when_cancelButtonIsClicked", async () => {
      wrapper = setupWrapperWithConfirmAndCancelButton();

      await wrapper
        .findComponent('[data-test="basedialog-btn-cancel"]')
        .trigger("click");

      expect(wrapper.emitted()).toHaveProperty("cancel");
    });
  });
});

function setupWrapperWithConfirmButton() {
  return mount(BaseDialog, {
    global: {
      plugins: [vuetify],
    },
    props: {
      visible: true,
      icon: "$information",
      dialogtitle: "Test Dialog",
      confirmtext: "Bestätigen",
    },
    slots: {
      default: "Das ist ein Test Dialog.",
    },
  });
}

function setupWrapperWithConfirmAndCancelButton() {
  return mount(BaseDialog, {
    global: {
      plugins: [vuetify],
    },
    props: {
      visible: true,
      icon: "$information",
      dialogtitle: "Test Dialog",
      confirmtext: "Bestätigen",
      canceltext: "Abbrechen",
    },
    slots: {
      default: "Das ist ein Test Dialog.",
    },
  });
}
