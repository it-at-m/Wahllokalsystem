import { getSnapshotFilename } from "@tests/utils/testutils.ts";
import { mount, VueWrapper } from "@vue/test-utils";
import { beforeEach, describe, expect, it } from "vitest";

import BaseButtonConfirm from "@/components/common/BaseButtonConfirm.vue";
import vuetify from "@/plugins/vuetify.ts";

describe("BaseButtonConfirm.vue", () => {
  let wrapper: VueWrapper;

  beforeEach(() => {
    wrapper = mount(BaseButtonConfirm, {
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
