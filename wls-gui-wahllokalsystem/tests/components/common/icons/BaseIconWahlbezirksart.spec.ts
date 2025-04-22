import { createTestingPinia } from "@pinia/testing";
import { getSnapshotFilename } from "@tests/utils/testutils.ts";
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
import { nextTick } from "vue";

import BaseIconWahlbezirksart from "@/components/common/icons/BaseIconWahlbezirksart.vue";
import vuetify from "@/plugins/vuetify.ts";
import { useUserStore } from "@/stores/user";
import User from "@/types/User.ts";

describe("BaseIconWahlbezirksart.vue", () => {
  let wrapper: VueWrapper;

  beforeAll(() => {
    createPinia();
  });

  beforeEach(() => {
    wrapper = mount(BaseIconWahlbezirksart, {
      global: {
        plugins: [
          createTestingPinia({
            createSpy: vi.fn,
            stubActions: false,
          }),
          vuetify,
        ],
      },
    });
    vi.clearAllMocks();
  });

  enableAutoUnmount(afterEach);

  describe("visual logic", () => {
    it("should_displayUWB_when_storeVariableIsUWB", async (context) => {
      const userStore = useUserStore();
      const u = new User();
      u.wahlbezirksArt = "UWB";
      userStore.setUser(u);

      await wrapper.vm.$nextTick();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_displayBWB_when_storeVariableIsBWB", async (context) => {
      const userStore = useUserStore();
      const u = new User();
      u.wahlbezirksArt = "BWB";
      userStore.setUser(u);

      await nextTick();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_displayBWB_when_storeVariableIsUndefined", async (context) => {
      const userStore = useUserStore();
      const u = new User();
      u.wahlbezirksArt = undefined;
      userStore.setUser(u);

      await nextTick();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });
});
