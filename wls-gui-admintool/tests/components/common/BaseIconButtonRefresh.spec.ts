import {
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
import { mount, VueWrapper } from "@vue/test-utils";
import { beforeEach, describe, expect, it } from "vitest";

import BaseIconButtonRefresh from "@/components/common/BaseIconButtonRefresh.vue";
import vuetify from "@/plugins/vuetify";

describe("BaseIconButtonRefresh.vue", () => {
  let wrapper: VueWrapper;

  beforeEach(() => {
    wrapper = mount(BaseIconButtonRefresh, {
      global: { plugins: [vuetify] },
    });
  });

  describe(COMPONENT_RENDER_TESTS, () => {
    it("should_renderCorrectlyWithAnIcon_when_componentIsMounted", async (context) => {
      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });
});
