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
import { VTextField } from "vuetify/components";

import BaseDateInput from "@/components/common/inputs/BaseDateInput.vue";
import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";
import pinia from "@/plugins/pinia.ts";
import vuetify from "@/plugins/vuetify.ts";

const { updateDateOfDateObject } = useDateTimeFormatter();
const mockedNow = new Date();

describe("BaseDateInput.vue", () => {
  let wrapper: VueWrapper;

  beforeAll(() => {
    createPinia();
  });

  beforeEach(() => {
    vi.useFakeTimers({
      now: mockedNow,
    });

    wrapper = mount(BaseDateInput, {
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

    it("should_renderDateInput_when_inputIsTyped", async (context) => {
      const input = "2025-07-01";
      const currentDate = new Date("2025-06-02");
      const date = updateDateOfDateObject(input, currentDate);
      await wrapper.setProps({ modelValue: date });

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
      expect(wrapper.html()).toContain(input);
    });
  });

  describe(COMPONENT_EVENT_TESTS, () => {
    it("should_updateModelValue_when_elementIsTyped", async () => {
      const input = "2025-07-01";
      const currentDate = new Date("2025-06-15");
      currentDate.setHours(10, 10);
      const date = updateDateOfDateObject(input, currentDate);

      const inputTextfield = wrapper.findComponent(VTextField);
      await wrapper.setProps({
        modelValue: new Date("2025-06-15T10:10:00"),
      });
      await inputTextfield.setValue(input);

      expect(wrapper.emitted()).toHaveProperty("update:model-value");
      expect(wrapper.emitted("update:model-value")).toEqual([[date]]);
    });
  });
});
