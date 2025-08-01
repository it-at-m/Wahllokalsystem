import type { VueWrapper } from "@vue/test-utils";

import {
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
import { enableAutoUnmount, mount } from "@vue/test-utils";
import { afterEach, beforeEach, describe, expect, it } from "vitest";

import TheUWBElectionListGroup from "@/components/navigation/TheUWBElectionListGroup.vue";
import vuetify from "@/plugins/vuetify.ts";

describe("TheUWBElectionListGroup.vue", () => {
  let wrapper: VueWrapper;

  beforeEach(() => {
    wrapper = mount(TheUWBElectionListGroup, {
      global: {
        plugins: [vuetify],
      },
    });
  });

  enableAutoUnmount(afterEach);

  describe(COMPONENT_RENDER_TESTS, () => {
    it("should_renderListGroupWithNavigationItems_when_mounted", async (context) => {
      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });
});
