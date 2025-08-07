import type { InfomanagementConfigParameter } from "@/types/config/InfomanagementConfigParameter.ts";

import {
  COMPONENT_EVENT_TESTS,
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
import { mount, VueWrapper } from "@vue/test-utils";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { nextTick } from "vue";
import { VTextField } from "vuetify/components";

import BaseDialogConfigParameterDisplay from "@/components/config-parameter/BaseDialogConfigParameterDisplay.vue";
import vuetify from "@/plugins/vuetify.ts";

const configParameter = {
  name: "Willkommenstext",
  beschreibung: "Begrüßungstext auf der Anmeldemaske",
  wert: "Herzlich willkommen zur Wahl!",
  defaultValue: "Herzlich willkommen zur Testwahl!",
} as InfomanagementConfigParameter;

describe("BaseDialogConfigParameterDisplay.vue", () => {
  let wrapper: VueWrapper<any>;

  vi.stubGlobal("visualViewport", new EventTarget());

  beforeEach(() => {
    wrapper = setupWrapper();
  });

  afterEach(() => {
    cleanUpWrapper(wrapper);
  });

  describe(COMPONENT_RENDER_TESTS, () => {
    it("should_renderDialog_when_showWasCalled", async (context) => {
      wrapper.vm.showDialog();

      await wrapper.vm.$nextTick();

      await expect(document.body.innerHTML).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_hideDialog_when_hideWasCalledAfterShow", async (context) => {
      wrapper.vm.showDialog();
      await nextTick();

      wrapper.vm.showDialog();
      await wrapper.vm.$nextTick();
      wrapper.vm.hideDialog();
      await wrapper.vm.$nextTick();

      await expect(document.body.innerHTML).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });

  describe(COMPONENT_EVENT_TESTS, () => {
    describe("cancelEdit", () => {
      it("should_emitCancelEditConfigParameterName_when_CancelButtonIsClicked", async () => {
        wrapper.vm.showDialog();
        await wrapper.vm.$nextTick();

        const input = wrapper.findComponent(VTextField);
        await input.setValue("Mock Wert");

        const confirmEditWrapper = wrapper.findComponent({
          name: "VConfirmEdit",
        });

        const cancelBtn = confirmEditWrapper.find(
          '[data-test="cancel-edit-button"]'
        );
        await cancelBtn.trigger("click");
        await nextTick();

        const emitted = wrapper.emitted("cancelEdit");
        expect(emitted).toBeTruthy();
        expect(emitted?.[0][0]).toEqual("Kein Payload, Keine Änderung");
      });
    });

    describe("commitEdit", () => {
      it("should_emitCommitEditConfigParameterValue_when_CommitButtonIsClicked", async () => {
        wrapper.vm.showDialog();
        await wrapper.vm.$nextTick();

        const input = wrapper.findComponent(VTextField);
        await input.setValue("Mock Wert");
        await wrapper.vm.$nextTick();

        expect(wrapper.vm.isChanged).toBe(true);

        const confirmEditWrapper = wrapper.findComponent({
          name: "VConfirmEdit",
        });

        const confirmBtn = confirmEditWrapper.find(
          '[data-test="commit-edit-button"]'
        );
        await confirmBtn.trigger("click");
        await nextTick();

        const emitted = wrapper.emitted("commitEdit");
        expect(emitted).toBeTruthy();
        expect(emitted[0][0]).toContain("Mock Wert");
      });

      it("should_showDefaultValue_when_resetButtonIsClicked_And_emitCommitEditConfigParameterValue_when_CommitButtonIsClicked", async () => {
        wrapper.vm.showDialog();
        await wrapper.vm.$nextTick();

        const input = wrapper.findComponent(VTextField);
        await input.setValue("Mock Wert");

        const confirmEditWrapper = wrapper.findComponent({
          name: "VConfirmEdit",
        });

        const resetBtn = confirmEditWrapper.find(
          '[data-test = "reset-button"]'
        );
        expect(resetBtn.exists()).toBe(true);

        await resetBtn.trigger("click");
        await wrapper.vm.$nextTick();

        expect(wrapper.vm.isChanged).toBe(true);
        expect(wrapper.vm.model).toBe(configParameter.defaultValue);

        const confirmBtn = confirmEditWrapper.find(
          '[data-test="commit-edit-button"]'
        );
        await confirmBtn.trigger("click");
        await nextTick();

        const emitted = wrapper.emitted("commitEdit");
        expect(emitted).toBeTruthy();
        expect(emitted?.[0][0]).toContain(configParameter.defaultValue);
      });
    });
  });
});

function setupWrapper(
  overrides?: Partial<{ configParameter: InfomanagementConfigParameter }>
) {
  return mount(BaseDialogConfigParameterDisplay, {
    props: { configParameter: overrides?.configParameter ?? configParameter },
    global: { plugins: [vuetify] },
    attachTo: document.body,
  });
}

function cleanUpWrapper(wrapper: VueWrapper) {
  wrapper.unmount();
  document.body.innerHTML = "";
  document.head.innerHTML = "";
}
