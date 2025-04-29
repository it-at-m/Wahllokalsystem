import { getSnapshotFilename } from "@tests/utils/testutils.ts";
import { enableAutoUnmount, mount, VueWrapper } from "@vue/test-utils";
import { createPinia } from "pinia";
import { afterEach, beforeAll, beforeEach, describe, expect, it } from "vitest";
import { nextTick } from "vue";
import { createVuetify } from "vuetify";
import * as components from "vuetify/components";
import { VTextField } from "vuetify/components";
import * as directives from "vuetify/directives";

import BaseTimeInput from "@/components/common/inputs/BaseTimeInput.vue";
import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";
import pinia from "@/plugins/pinia.ts";
import { REQUIRED } from "@/util/rules.ts";

const { getDateFromTimeString } = useDateTimeFormatter();

describe("BaseTimeInput.vue", () => {
  let vuetify: ReturnType<typeof createVuetify>;
  let wrapper: VueWrapper;

  beforeAll(() => {
    createPinia();
  });

  beforeEach(() => {
    vuetify = createVuetify({
      components,
      directives,
    });

    wrapper = mount(BaseTimeInput, {
      global: { plugins: [pinia, vuetify] },
    });
  });

  enableAutoUnmount(afterEach);

  describe("visual logic", () => {
    it("should_renderTextFieldWithCorrectLabel_when_componentIsMounted", async (context) => {
      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderTimeInput_when_inputIsTyped", async (context) => {
      const input = "12:12";
      const date = getDateFromTimeString(input);
      await wrapper.setProps({ modelValue: date });

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
      expect(wrapper.html()).toContain(input);
    });

    it("should_renderErrorMessage_when_ruleRequiredIsViolated", async (context) => {
      const rules = [REQUIRED];
      const errorMessage = "Feld darf nicht leer sein.";

      await wrapper.setProps({ rules: rules, modelValue: new Date() });
      await wrapper.setProps({ modelValue: null });
      await nextTick();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
      expect(wrapper.html()).toContain(errorMessage);
    });
  });

  describe("behavioral logic", () => {
    it("should_updateModelValue_when_elementIsTyped", async () => {
      const input = "12:12";
      const date = getDateFromTimeString(input);

      const inputTextfield = wrapper.findComponent(VTextField);
      await inputTextfield.setValue(input);

      expect(wrapper.emitted()).toHaveProperty("update:modelValue");
      expect(wrapper.emitted("update:modelValue")).toEqual([[date]]);
    });
  });
});
