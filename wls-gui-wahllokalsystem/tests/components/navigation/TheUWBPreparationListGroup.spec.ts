import type { VueWrapper } from "@vue/test-utils";

import {
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
import { enableAutoUnmount, mount } from "@vue/test-utils";
import { afterEach, beforeEach, describe, expect, it } from "vitest";

import TheUWBPreparationListGroup from "@/components/navigation/TheUWBPreparationListGroup.vue";
import vuetify from "@/plugins/vuetify.ts";

describe("TheUWBPreparationListGroup.vue", () => {
  let wrapper: VueWrapper;

  beforeEach(() => {
    wrapper = mount(TheUWBPreparationListGroup, {
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
