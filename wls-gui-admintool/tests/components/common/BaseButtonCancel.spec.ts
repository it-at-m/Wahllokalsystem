import { getSnapshotFilename } from "@tests/utils/testutils.ts";
import { mount, VueWrapper } from "@vue/test-utils";
import { beforeEach, describe, expect, it } from "vitest";

import BaseButtonCancel from "@/components/common/BaseButtonCancel.vue";
import vuetify from "@/plugins/vuetify.ts";

describe("BaseButtonCancel.vue", () => {
  let wrapper: VueWrapper;

  beforeEach(() => {
    wrapper = mount(BaseButtonCancel, {
      global: { plugins: [vuetify] },
    });
  });

  describe("visual logic", () => {
    it("should_renderCorrectly_when_componentIsMounted", async (context) => {
      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });
});
