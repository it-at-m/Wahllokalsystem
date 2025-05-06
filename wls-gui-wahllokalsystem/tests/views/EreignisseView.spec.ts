import { createTestingPinia } from "@pinia/testing";
import { mount, VueWrapper } from "@vue/test-utils";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { nextTick } from "vue";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import vuetify from "@/plugins/vuetify";
import { useEreignisStore } from "@/stores/ereignisStore.ts";
import EreignisseView from "@/views/EreignisseView.vue";

describe("TheEreignisseView", () => {
  let wrapper: VueWrapper<InstanceType<typeof EreignisseView>>;

  beforeEach(() => {
    wrapper = mount(EreignisseView, {
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
    vi.clearAllMocks();
  });

  describe("visual logic", () => {
    it("should_renderSaveButtonEnabled_when_noEintraegeIsTrueAndKeineEreignisseFlagsAreValid", async () => {
      const ereignisStore = useEreignisStore();

      // @ts-expect-error: cannot set readonly
      ereignisStore.areKeineEreignisseFlagsValid = true;
      // @ts-expect-error: cannot set readonly
      ereignisStore.hasEintraege = true;

      await nextTick();

      const saveButton = wrapper.findComponent(BaseButtonSave);
      expect(saveButton.element.hasAttribute("disabled")).toStrictEqual(false);
    });

    it("should_renderSaveButtonDisabled_when_noEintraegeIsFalseAndKeineEreignisseFlagsAreValid", async () => {
      const ereignisStore = useEreignisStore();

      // @ts-expect-error: cannot set readonly
      ereignisStore.areKeineEreignisseFlagsValid = true;
      // @ts-expect-error: cannot set readonly
      ereignisStore.hasEintraege = false;

      await nextTick();

      const saveButton = wrapper.findComponent(BaseButtonSave);
      expect(saveButton.element.hasAttribute("disabled")).toStrictEqual(true);
    });

    it("should_renderSaveButtonDisabled_when_noEintraegeIsTrueAndKeineEreignisseFlagsAreInvalid", async () => {
      const ereignisStore = useEreignisStore();

      // @ts-expect-error: cannot set readonly
      ereignisStore.areKeineEreignisseFlagsValid = false;
      // @ts-expect-error: cannot set readonly
      ereignisStore.hasEintraege = true;

      await nextTick();

      const saveButton = wrapper.findComponent(BaseButtonSave);
      expect(saveButton.element.hasAttribute("disabled")).toStrictEqual(true);
    });
  });
});
