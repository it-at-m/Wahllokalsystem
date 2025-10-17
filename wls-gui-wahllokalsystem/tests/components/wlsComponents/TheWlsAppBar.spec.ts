import type { VueWrapper } from "@vue/test-utils";

import { createTestingPinia } from "@pinia/testing";
import {
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
import { enableAutoUnmount, mount } from "@vue/test-utils";
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
import { h, nextTick } from "vue";
import { VApp } from "vuetify/components";

import TheWlsAppBar from "@/components/wlsComponents/TheWlsAppBar.vue";
import vuetify from "@/plugins/vuetify.ts";
import { useTaskManagerStore } from "@/stores/taskManagerStore.ts";

describe("TheWlsAppBar.vue", () => {
  let wrapper: VueWrapper;
  const mockedDate = new Date("2025-05-23T07:30:00");

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

  enableAutoUnmount(afterEach);
  afterEach(() => {
    vi.useRealTimers();
  });

  describe(COMPONENT_RENDER_TESTS, () => {
    it("should_renderNavigationDrawerIcon_when_initializationOfTaskHasCompletelyRun", async (context) => {
      const taskManagerStore = useTaskManagerStore();
      // @ts-expect-error: cannot set readonly
      taskManagerStore.hasAllTasksRunSuccessfully = true;
      // @ts-expect-error: cannot set readonly
      taskManagerStore.hasTasksToRun = true;

      await nextTick();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderNavigationDrawerIcon_when_initializationOfTaskHasNotCompletelyRun", async (context) => {
      const taskManagerStore = useTaskManagerStore();
      // @ts-expect-error: cannot set readonly
      taskManagerStore.hasAllTasksRunSuccessfully = false;
      // @ts-expect-error: cannot set readonly
      taskManagerStore.hasTasksToRun = true;

      await nextTick();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });
});
