import type { Task } from "@/types/tasks/Task.ts";

import { createTestingPinia } from "@pinia/testing";
import {
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
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

import BaseOfflineLoading from "@/components/wlsComponents/BaseOfflineLoading.vue";
import vuetify from "@/plugins/vuetify.ts";
import { useTaskManagerStore } from "@/stores/taskManagerStore.ts";

describe("BaseOfflineLoading.vue", () => {
  let wrapper: VueWrapper;

  beforeAll(() => {
    createPinia();
  });

  beforeEach(() => {
    wrapper = mount(BaseOfflineLoading, {
      global: {
        plugins: [
          createTestingPinia({
            createSpy: vi.fn,
          }),
          vuetify,
        ],
      },
    });
    vi.clearAllMocks();
  });

  enableAutoUnmount(afterEach);

  describe(COMPONENT_RENDER_TESTS, () => {
    it("should_showOneTaskSuccessfulRun_when_runSuccessful", async (context) => {
      const taskManagerStore = useTaskManagerStore();
      const exampleTask: Task = {
        wahlbezirksart: undefined,
        onlyForWahlen: undefined,
        onlyForAllWVZs: undefined,
        name: "test",
        callback: () => {
          return Promise.resolve();
        },
      };

      taskManagerStore.successfullyTasks.push(exampleTask);
      taskManagerStore.numberOfTasksToRun = 1;

      await nextTick();
      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_showOneTaskFail_when_runFailed", async (context) => {
      const taskManagerStore = useTaskManagerStore();
      const exampleTask: Task = {
        wahlbezirksart: undefined,
        onlyForWahlen: undefined,
        onlyForAllWVZs: undefined,
        name: "test",
        callback: () => {
          return Promise.resolve();
        },
      };

      taskManagerStore.failedTasks.push(exampleTask);
      taskManagerStore.numberOfTasksToRun = 1;

      await nextTick();
      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_showHeadlineLoadingText_when_stillLoading", async (context) => {
      const taskManagerStore = useTaskManagerStore();
      const exampleTask: Task = {
        wahlbezirksart: undefined,
        onlyForWahlen: undefined,
        onlyForAllWVZs: undefined,
        name: "test",
        callback: () => {
          return Promise.resolve();
        },
      };

      taskManagerStore.successfullyTasks.push(exampleTask);
      taskManagerStore.numberOfTasksToRun = 5;

      await nextTick();
      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });
});
