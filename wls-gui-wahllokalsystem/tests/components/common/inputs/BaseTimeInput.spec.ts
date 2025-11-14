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
import { useDateTimeUtils } from "@/composables/common/dateTimeUtils.ts";
import { useRules } from "@/composables/common/rules.ts";
import pinia from "@/plugins/pinia.ts";
import vuetify from "@/plugins/vuetify.ts";

const { required } = useRules();

const { createTodayWithTime } = useDateTimeUtils();
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
      const date = createTodayWithTime(input);
      await wrapper.setProps({ modelValue: date });

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
      expect(wrapper.html()).toContain(input);
    });

    it("should_renderErrorMessage_when_ruleRequiredIsViolated", async (context) => {
      const rules = [required];
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

  describe(COMPONENT_EVENT_TESTS, () => {
    it("should_updateModelValue_when_elementIsTyped", async () => {
      const input = "12:12";
      const date = createTodayWithTime(input);

      const inputTextfield = wrapper.findComponent(VTextField);
      await inputTextfield.setValue(input);

      expect(wrapper.emitted()).toHaveProperty("update:modelValue");
      expect(wrapper.emitted("update:modelValue")).toEqual([[date]]);
    });
  });
});
