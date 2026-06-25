import type { VueWrapper } from "@vue/test-utils";

import { createTestingPinia } from "@pinia/testing";
import {
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
import { enableAutoUnmount, mount } from "@vue/test-utils";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import TheAppSupportMenu from "@/components/basisdaten/TheAppSupportMenu.vue";
import vuetify from "@/plugins/vuetify.ts";

describe("TheAppSupportMenu.vue", () => {
  let wrapper: VueWrapper<InstanceType<typeof TheAppSupportMenu>>;

  beforeEach(() => {
    wrapper = mount(TheAppSupportMenu, {
      global: {
        plugins: [
          createTestingPinia({
            createSpy: vi.fn,
          }),
          vuetify,
        ],
      },
    });
  });

  enableAutoUnmount(afterEach);

  describe(COMPONENT_RENDER_TESTS, () => {
    it("should_renderIcon_when_mounted", async (context) => {
      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });
});
