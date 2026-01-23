import type { VueWrapper } from "@vue/test-utils";

import { createTestingPinia } from "@pinia/testing";
import {
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
import { enableAutoUnmount, mount } from "@vue/test-utils";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import TheBWBElectionListGroup from "@/components/navigation/TheBWBElectionListGroup.vue";
import vuetify from "@/plugins/vuetify.ts";
import { useStatusStore } from "@/stores/statusStore.ts";

describe("TheBWBElectionListGroup.vue", () => {
  let wrapper: VueWrapper;

  beforeEach(() => {
    createTestingPinia({
      createSpy: vi.fn,
    });
    useStatusStore().isWahlvorstandErfasst = true;
    useStatusStore().isWahlumgebungErfasst = true;
    wrapper = mount(TheBWBElectionListGroup, {
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
