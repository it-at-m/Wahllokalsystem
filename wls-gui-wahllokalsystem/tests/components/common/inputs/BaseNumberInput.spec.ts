import { enableAutoUnmount, mount, VueWrapper } from "@vue/test-utils";
import { createPinia } from "pinia";
import { afterEach, beforeAll, beforeEach, describe, expect, it } from "vitest";
import { createVuetify } from "vuetify";
import * as components from "vuetify/components";
import * as directives from "vuetify/directives";

import BaseNumberInput from "@/components/common/inputs/BaseNumberInput.vue";
import pinia from "@/plugins/pinia";
import { getSnapshotFilename } from "../../../utils/testutils.ts";

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
  });
});
