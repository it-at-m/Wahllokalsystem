import { getSnapshotFilename } from "@tests/utils/testutils.ts";
import { enableAutoUnmount, mount, VueWrapper } from "@vue/test-utils";
import { createPinia } from "pinia";
import { afterEach, beforeAll, beforeEach, describe, expect, it } from "vitest";
import { nextTick } from "vue";
import { createVuetify } from "vuetify";
import * as components from "vuetify/components";
import { VTextField } from "vuetify/components";
import * as directives from "vuetify/directives";

import BaseNumberInput from "@/components/common/inputs/BaseNumberInput.vue";
import pinia from "@/plugins/pinia";
import { MAX_NUMBER, MIN_NUMBER, REQUIRED } from "@/util/rules.ts";

describe("BaseNumberInput.vue", () => {
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

    wrapper = mount(BaseNumberInput, {
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

    it("should_renderNumberInput_when_inputIsTyped", async (context) => {
      const input = 15896;
      await wrapper.setProps({ modelValue: input });

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
      expect(wrapper.html()).toContain(input);
    });

    it("should_renderErrorMessage_when_ruleRequiredIsViolated", async (context) => {
      const rules = [REQUIRED];
      const errorMessage = "Feld darf nicht leer sein.";

      await wrapper.setProps({ rules: rules, modelValue: 10 });
      await wrapper.setProps({ modelValue: null });
      await nextTick();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
      expect(wrapper.html()).toContain(errorMessage);
    });

    it("should_renderErrorMessage_when_ruleMinNumberIsViolated", async (context) => {
      const rules = [MIN_NUMBER(5)];
      const errorMessage = "Eingabe darf nicht kleiner als 5 sein.";

      await wrapper.setProps({ rules: rules, modelValue: 2 });
      await nextTick();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
      expect(wrapper.html()).toContain(errorMessage);
    });

    it("should_renderErrorMessage_when_ruleMaxNumberIsViolated", async (context) => {
      const rules = [MAX_NUMBER(5)];
      const errorMessage = "Eingabe darf nicht größer als 5 sein.";

      await wrapper.setProps({ rules: rules, modelValue: 7 });
      await nextTick();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
      expect(wrapper.html()).toContain(errorMessage);
    });
  });

  describe("behavioral logic", () => {
    describe("update:modelValue", () => {
      it("should_updateModelValue_when_elementIsTyped", async () => {
        const input = 896572;

        const textfield = wrapper.findComponent(VTextField);
        await textfield.setValue(input);

        expect(wrapper.emitted()).toHaveProperty("update:modelValue");
        expect(wrapper.emitted("update:modelValue")).toEqual([[input]]);
      });
    });
  });
});
