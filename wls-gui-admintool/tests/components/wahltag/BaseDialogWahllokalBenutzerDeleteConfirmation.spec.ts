import {
  COMPONENT_EVENT_TESTS,
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
import { mount, VueWrapper } from "@vue/test-utils";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { nextTick } from "vue";
import { VTextField } from "vuetify/components";

import BaseButtonCancel from "@/components/common/BaseButtonCancel.vue";
import BaseButtonConfirm from "@/components/common/BaseButtonConfirm.vue";
import BaseDialogWahllokalBenutzerDeleteConfirmation from "@/components/wahltag/BaseDialogWahllokalBenutzerDeleteConfirmation.vue";
import vuetify from "@/plugins/vuetify.ts";

describe("BaseDialogWahllokalBenutzerDeleteConfirmation.vue", () => {
  let wrapper: VueWrapper<
    InstanceType<typeof BaseDialogWahllokalBenutzerDeleteConfirmation>
  >;
  vi.stubGlobal("visualViewport", new EventTarget());

  beforeEach(() => {
    wrapper = setupWrapper();
  });

  afterEach(() => {
    cleanUpWrapper(wrapper);
  });

  describe(COMPONENT_RENDER_TESTS, () => {
    it("should_renderNothingVisible_when_mountedUnchanged", async (context) => {
      await expect(document.body.innerHTML).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderDialog_when_showWasCalled", async (context) => {
      wrapper.vm.showDialog();

      await wrapper.vm.$nextTick();

      await expect(document.body.innerHTML).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_hideDialog_when_hideWasCalledAfterShow", async (context) => {
      wrapper.vm.showDialog();
      await wrapper.vm.$nextTick();
      wrapper.vm.hideDialog();
      await wrapper.vm.$nextTick();

      await expect(document.body.innerHTML).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderValues_when_propertiesAreSet", async (context) => {
      cleanUpWrapper(wrapper);
      wrapper = setupWrapper({
        requiredConfirmText: "requiredConfirmText",
      });

      wrapper.vm.showDialog();
      await wrapper.vm.$nextTick();

      await expect(document.body.innerHTML).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_enableConfirmButton_when_requiredConfirmTextIsEntered", async () => {
      const confirmText = "confirmText";
      await wrapper.setProps({
        requiredConfirmText: confirmText,
      });
      wrapper.vm.showDialog();
      await wrapper.vm.$nextTick();

      const confirmButton = wrapper.findComponent(BaseButtonConfirm);
      expect(confirmButton.element.hasAttribute("disabled")).toStrictEqual(
        true
      );

      const confirmTextField = wrapper.findComponent(VTextField);
      await confirmTextField.setValue(confirmText);

      expect(confirmButton.element.hasAttribute("disabled")).toStrictEqual(
        false
      );
    });
  });

  describe(COMPONENT_EVENT_TESTS, () => {
    describe("confirmDelete", () => {
      it("should_emitConfirmDelete_when_confirmButtonClicked", async () => {
        wrapper.vm.showDialog();
        await wrapper.vm.$nextTick();

        const confirmButton = wrapper.findComponent(BaseButtonConfirm);
        confirmButton.element.dispatchEvent(new Event("click"));

        await nextTick();

        expect(wrapper.emitted("confirmDelete")).toEqual([[]]);
      });
    });

    describe("cancelDelete", () => {
      it("should_emitCancelDelete_when_cancelButtonClicked", async () => {
        wrapper.vm.showDialog();
        await wrapper.vm.$nextTick();

        const cancelButton = wrapper.findComponent(BaseButtonCancel);
        await cancelButton.trigger("click");

        expect(wrapper.emitted("cancelDelete")).toEqual([[]]);
      });
    });
  });
});

function setupWrapper(props?: Record<string, unknown>) {
  return mount(BaseDialogWahllokalBenutzerDeleteConfirmation, {
    attachTo: document.body,
    props: props,
    global: { plugins: [vuetify] },
  });
}

function cleanUpWrapper(wrapper: VueWrapper) {
  wrapper.unmount();
  document.body.innerHTML = "";
  document.head.innerHTML = "";
}
