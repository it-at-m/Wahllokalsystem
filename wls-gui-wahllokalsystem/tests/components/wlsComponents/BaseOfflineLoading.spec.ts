import { createTestingPinia } from "@pinia/testing";
import { useTasksTestDataFactory } from "@tests/utils/tasks/TasksTestDataFactory.ts";
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

  const { createTask } = useTasksTestDataFactory();

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

      taskManagerStore.successfullyTasks.push(createTask("test"));
      taskManagerStore.numberOfTasksToRun = 1;

      await nextTick();
      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_showTaskNamesInExpansionPanel_when_runSuccessful", async (context) => {
      const taskManagerStore = useTaskManagerStore();

      const taskName = "test task";
      taskManagerStore.successfullyTasks.push(createTask(taskName));
      taskManagerStore.numberOfTasksToRun = 1;

      const successExpansionPanel = wrapper.findComponent(
        '[data-test="base-progress-success"]'
      );
      const successExpansionPanelTitle = successExpansionPanel.findComponent(
        ".v-expansion-panel-title"
      );
      await successExpansionPanelTitle.trigger("click");
      const successExpansionPanelText = successExpansionPanel.findComponent(
        '[data-test="expansion-panel-tasklist"]'
      );
      await nextTick();

      expect(successExpansionPanelText.isVisible()).toBe(true);
      expect(successExpansionPanelText.text()).toContain(taskName);
      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_showOneTaskFail_when_runFailed", async (context) => {
      const taskManagerStore = useTaskManagerStore();

      taskManagerStore.failedTasks.push(createTask("test"));
      taskManagerStore.numberOfTasksToRun = 1;

      await nextTick();
      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_showTaskNamesInExpansionPanel_when_runFailed", async (context) => {
      const taskManagerStore = useTaskManagerStore();

      const taskName = "test task";
      taskManagerStore.failedTasks.push(createTask(taskName));
      taskManagerStore.numberOfTasksToRun = 1;

      const failedExpansionPanel = wrapper.findComponent(
        '[data-test="base-progress-failed"]'
      );
      const failedExpansionPanelTitle = failedExpansionPanel.findComponent(
        ".v-expansion-panel-title"
      );
      await failedExpansionPanelTitle.trigger("click");
      const failedExpansionPanelText = failedExpansionPanel.findComponent(
        '[data-test="expansion-panel-tasklist"]'
      );
      await nextTick();

      expect(failedExpansionPanelText.isVisible()).toBe(true);
      expect(failedExpansionPanelText.text()).toContain(taskName);
      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_showHeadlineLoadingText_when_stillLoading", async (context) => {
      const taskManagerStore = useTaskManagerStore();

      taskManagerStore.successfullyTasks.push(createTask("test"));
      taskManagerStore.numberOfTasksToRun = 5;

      await nextTick();
      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });
});
