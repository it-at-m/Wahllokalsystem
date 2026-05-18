import { createTestingPinia } from "@pinia/testing";
import { useTasksTestDataFactory } from "@tests/utils/tasks/TasksTestDataFactory.ts";
import {
  COMPONENT_EVENT_TESTS,
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
import { createRouter, createWebHistory } from "vue-router";

import BaseOfflineLoading from "@/components/wlsComponents/BaseOfflineLoading.vue";
import { ROUTE_WAHLVORSTAND, ROUTES_HOME } from "@/constants.ts";
import vuetify from "@/plugins/vuetify.ts";
import { useInitTaskManagerStore } from "@/stores/initTaskManagerStore.ts";
import { useWorkflowStore } from "@/stores/workflowStore.ts";
import HomeView from "@/views/HomeView.vue";
import WahlvorstandAnwesenheitView from "@/views/WahlvorstandAnwesenheitView.vue";

describe("BaseOfflineLoading.vue", () => {
  let wrapper: VueWrapper;

  const { createTask } = useTasksTestDataFactory();

  beforeAll(() => {
    createPinia();
  });

  const router = createRouter({
    history: createWebHistory(),
    routes: [
      {
        path: "/",
        name: ROUTES_HOME,
        component: HomeView,
        meta: {},
      },
      {
        path: "/wahlvorstand",
        name: ROUTE_WAHLVORSTAND,
        component: WahlvorstandAnwesenheitView,
        meta: {},
      },
    ],
  });

  beforeEach(() => {
    wrapper = mount(BaseOfflineLoading, {
      global: {
        plugins: [
          createTestingPinia({
            createSpy: vi.fn,
          }),
          vuetify,
          router,
        ],
      },
    });
    vi.clearAllMocks();
  });

  enableAutoUnmount(afterEach);

  describe(COMPONENT_RENDER_TESTS, () => {
    it("should_showOneTaskSuccessfulRun_when_runSuccessful", async (context) => {
      const taskManagerStore = useInitTaskManagerStore();

      taskManagerStore.successfullyTasks.push(createTask("test"));
      // @ts-expect-error: cannot set readonly
      taskManagerStore.numberOfTasksToRun = 1;
      // @ts-expect-error: cannot set readonly
      taskManagerStore.hasTasksToRun = true;

      await nextTick();
      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_showTaskNamesInExpansionPanel_when_runSuccessful", async (context) => {
      const taskManagerStore = useInitTaskManagerStore();

      const taskName = "test task";
      taskManagerStore.successfullyTasks.push(createTask(taskName));
      // @ts-expect-error: cannot set readonly
      taskManagerStore.numberOfTasksToRun = 1;
      // @ts-expect-error: cannot set readonly
      taskManagerStore.hasTasksToRun = true;

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
      const taskManagerStore = useInitTaskManagerStore();

      taskManagerStore.failedTasks.push(createTask("test"));
      // @ts-expect-error: cannot set readonly
      taskManagerStore.numberOfTasksToRun = 1;
      // @ts-expect-error: cannot set readonly
      taskManagerStore.hasTasksToRun = true;

      await nextTick();
      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_showTaskNamesInExpansionPanel_when_runFailed", async (context) => {
      const taskManagerStore = useInitTaskManagerStore();

      const taskName = "test task";
      taskManagerStore.failedTasks.push(createTask(taskName));
      // @ts-expect-error: cannot set readonly
      taskManagerStore.numberOfTasksToRun = 1;
      // @ts-expect-error: cannot set readonly
      taskManagerStore.hasTasksToRun = true;

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
      const taskManagerStore = useInitTaskManagerStore();

      taskManagerStore.successfullyTasks.push(createTask("test"));
      // @ts-expect-error: cannot set readonly
      taskManagerStore.numberOfTasksToRun = 5;
      // @ts-expect-error: cannot set readonly
      taskManagerStore.hasTasksToRun = true;

      await nextTick();
      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_showEnabledRefreshButton_when_tasksFailed", async () => {
      const taskManagerStore = useInitTaskManagerStore();
      // @ts-expect-error: cannot set readonly
      taskManagerStore.numberOfTasksFailed = 1;

      await nextTick();

      const refreshButton = wrapper.find('[data-test="refresh-button"]');
      expect(refreshButton.element.hasAttribute("disabled")).toStrictEqual(
        false
      );
    });

    it("should_showDisabledRefreshButton_when_noTasksFailed", async () => {
      const taskManagerStore = useInitTaskManagerStore();
      // @ts-expect-error: cannot set readonly
      taskManagerStore.numberOfTasksFailed = 0;

      await nextTick();

      const refreshButton = wrapper.find('[data-test="refresh-button"]');
      expect(refreshButton.element.hasAttribute("disabled")).toStrictEqual(
        true
      );
    });
  });

  describe(COMPONENT_EVENT_TESTS, () => {
    it("should_triggerRouting_when_allTasksRunSuccessfullyAndTestdruckIsPrinted", async () => {
      const pushMock = vi.fn();
      vi.spyOn(router, "push").mockImplementation(pushMock);

      const workflowStore = useWorkflowStore();
      const taskManagerStore = useInitTaskManagerStore();
      // @ts-expect-error: cannot set readonly
      taskManagerStore.hasAllTasksRunSuccessfully = false;

      await nextTick();

      expect(pushMock).not.toHaveBeenCalledOnce();

      // @ts-expect-error: cannot set readonly
      taskManagerStore.hasAllTasksRunSuccessfully = true;
      workflowStore.isTestseiteGedruckt = true;

      await nextTick();

      expect(pushMock).toHaveBeenCalledWith({ name: ROUTE_WAHLVORSTAND });
    });

    it("should_callOnRefreshClicked_when_refreshButtonIsClicked", async () => {
      const taskManagerStore = useInitTaskManagerStore();
      // @ts-expect-error: cannot set readonly
      taskManagerStore.numberOfTasksFailed = 1;

      await nextTick();

      const refreshButton = wrapper.find('[data-test="refresh-button"]');
      await refreshButton.trigger("click");

      expect(taskManagerStore.rerunFailedTasks).toHaveBeenCalled();
    });

    it("should_disableWeiterButton_when_hasNoTasksToRun", async () => {
      const taskManagerStore = useInitTaskManagerStore();
      // @ts-expect-error: cannot set readonly
      taskManagerStore.numberOfTasksToRun = 0;
      // @ts-expect-error: cannot set readonly
      taskManagerStore.hasTasksToRun = false;

      await nextTick();

      const weiterButton = wrapper.find('[data-test="weiter-button"]');
      expect(weiterButton.element.hasAttribute("disabled")).toStrictEqual(true);
    });
  });
});
