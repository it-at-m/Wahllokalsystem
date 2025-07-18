import {
  COMPONENT_EVENT_TESTS,
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
import { enableAutoUnmount, mount, VueWrapper } from "@vue/test-utils";
import { createPinia } from "pinia";
import {
  afterEach,
  beforeAll,
  beforeEach,
  describe,
  expect,
  it,
  vi,
} from "vitest";
import { nextTick } from "vue";
import { VTextField } from "vuetify/components";

import BaseTimeInput from "@/components/common/inputs/BaseTimeInput.vue";
import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";
import pinia from "@/plugins/pinia.ts";
import vuetify from "@/plugins/vuetify.ts";
import { REQUIRED } from "@/util/rules.ts";

const { updateTimeOfDateObject } = useDateTimeFormatter();
const mockedNow = new Date();

describe("BaseTimeInput.vue", () => {
  let wrapper: VueWrapper;

  beforeAll(() => {
    createPinia();
  });

  beforeEach(() => {
    vi.useFakeTimers({
      now: mockedNow,
    });

    wrapper = mount(BaseTimeInput, {
      global: { plugins: [pinia, vuetify] },
    });
  });

  enableAutoUnmount(afterEach);
  afterEach(() => {
    vi.useRealTimers();
  });

  describe(COMPONENT_RENDER_TESTS, () => {
    it("should_renderTextFieldWithCorrectLabel_when_componentIsMounted", async (context) => {
      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderTimeInput_when_inputIsTyped", async (context) => {
      const input = "12:12";
      const currentTime = new Date("2025-06-15");
      const date = updateTimeOfDateObject(input, currentTime);
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
      await wrapper.setProps({ rules: rules, modelValue: null });
      await nextTick();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
      expect(wrapper.html()).toContain(errorMessage);
    });
  });

  describe(COMPONENT_EVENT_TESTS, () => {
    it("should_updateModelValue_when_elementIsTyped", async () => {
      const input = "12:12";
      const currentTime = new Date("2025-06-15");
      const date = updateTimeOfDateObject(input, currentTime);

      const inputTextfield = wrapper.findComponent(VTextField);
      await wrapper.setProps({
        modelValue: new Date("2025-06-15T12:12:00"),
      });
      await inputTextfield.setValue(input);

      expect(wrapper.emitted()).toHaveProperty("update:model-value");
      expect(wrapper.emitted("update:model-value")).toEqual([[date]]);
    });
  });
});
