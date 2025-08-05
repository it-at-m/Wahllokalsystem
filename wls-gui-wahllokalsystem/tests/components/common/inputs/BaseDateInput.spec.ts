import type { VueWrapper } from "@vue/test-utils";

import {
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
import { mount } from "@vue/test-utils";
import { beforeEach, describe, expect, it } from "vitest";
import { VTextField } from "vuetify/components";

import BaseDateInput from "@/components/common/inputs/BaseDateInput.vue";
import vuetify from "@/plugins/vuetify.ts";

describe("BaseDateInput.vue", (): void => {
  let wrapper: VueWrapper;

  beforeEach(() => {
    wrapper = mount(BaseDateInput, {
      global: {
        plugins: [vuetify],
      },
    });
  });

  describe(COMPONENT_RENDER_TESTS, () => {
    it("should_renderEmpty_when_noValueIsSet", async (context) => {
      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderValue_when_valueIsSet", async (context) => {
      const textfieldForDate = wrapper.findComponent(VTextField);
      await textfieldForDate.setValue("2025-08-05");

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderError_when_noValueIsSetAndIsValidated", async (context) => {
      const textfieldForDate = wrapper.findComponent(VTextField);
      await textfieldForDate.setValue(null);

      await textfieldForDate.vm.validate();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });
});
