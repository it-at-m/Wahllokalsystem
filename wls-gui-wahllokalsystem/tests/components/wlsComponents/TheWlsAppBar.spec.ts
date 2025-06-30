import type { VueWrapper } from "@vue/test-utils";

import { createTestingPinia } from "@pinia/testing";
import {
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
import { useUserTestDataFactory } from "@tests/utils/user/UserTestDataFactory.ts";
import { mount } from "@vue/test-utils";
import { createPinia } from "pinia";
import { beforeAll, beforeEach, describe, expect, it, vi } from "vitest";
import { h, nextTick } from "vue";
import { VApp } from "vuetify/components";

import TheWlsAppBar from "@/components/wlsComponents/TheWlsAppBar.vue";
import vuetify from "@/plugins/vuetify.ts";
import { useTaskManagerStore } from "@/stores/taskManagerStore.ts";
import { useUserStore } from "@/stores/userStore.ts";

describe("TheWlsAppBar.vue", () => {
  let wrapper: VueWrapper;
  const mockedDate = new Date("2024-12-17T03:24:00");

  const ResizeObserverMock = vi.fn(() => ({
    observe: vi.fn(),
    unobserve: vi.fn(),
    disconnect: vi.fn(),
  }));
  vi.stubGlobal("ResizeObserver", ResizeObserverMock);

  beforeAll(() => {
    createPinia();
  });

  beforeEach(() => {
    vi.setSystemTime(mockedDate);
    wrapper = mount(VApp, {
      global: {
        plugins: [
          createTestingPinia({
            createSpy: vi.fn,
          }),
          vuetify,
        ],
      },
      slots: {
        default: h(TheWlsAppBar),
      },
    });
  });

  describe(COMPONENT_RENDER_TESTS, () => {
    it("when_initializationOfTaskHasCompletelyRun_then_renderNavigationDrawerIcon", async (context) => {
      const userStore = useUserStore();
      const user = useUserTestDataFactory().prepareUser().build();
      userStore.setUser(user);
      const taskManagerStore = useTaskManagerStore();
      taskManagerStore.hasInitializationOfTasksCompletelyRun = true;

      await nextTick();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("when_initializationOfTaskHasNotCompletelyRun_then_dontRenderNavigationDrawerIcon", async (context) => {
      const userStore = useUserStore();
      const user = useUserTestDataFactory().prepareUser().build();
      userStore.setUser(user);
      const taskManagerStore = useTaskManagerStore();
      taskManagerStore.hasInitializationOfTasksCompletelyRun = false;

      await nextTick();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });
});
