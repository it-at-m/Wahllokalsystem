import type { VueWrapper } from "@vue/test-utils";
import type { Ref } from "vue";

import { useTasksTestDataFactory } from "@tests/utils/tasks/TasksTestDataFactory.ts";
import {
  COMPONENT_EVENT_TESTS,
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
import { mount } from "@vue/test-utils";
import { createPinia, setActivePinia } from "pinia";
import {
  afterAll,
  afterEach,
  beforeEach,
  describe,
  expect,
  it,
  vi,
} from "vitest";
import { ref } from "vue";

import OfflineSyncer from "@/components/wlsComponents/OfflineSyncer.vue";
import vuetify from "@/plugins/vuetify.ts";

const mockDefinitions = vi.hoisted(() => ({
  taskManager: {
    numberOfTasksFinished: undefined as Ref<number> | undefined, //i cant use ref because its hoisted
    numberOfTasksToRun: undefined as Ref<number> | undefined,
    setTasks: vi.fn(),
    runAllTasks: vi.fn(),
  },
  getSyncTasks: vi.fn(),
}));

vi.mock("@/composables/tasks/taskManager.ts", () => ({
  useTaskManager: vi.fn().mockImplementation(() => mockDefinitions.taskManager),
}));
vi.mock("@/composables/indexDB/dataSyncer.ts", () => ({
  useDataSyncer: vi.fn().mockImplementation(() => ({
    getSyncTasks: mockDefinitions.getSyncTasks,
  })),
}));

const { createTask } = useTasksTestDataFactory();

describe("OfflineSyncer", () => {
  let wrapper: VueWrapper;

  vi.stubGlobal("visualViewport", new EventTarget());
  const ResizeObserverMock = vi.fn(() => ({
    observe: vi.fn(),
    unobserve: vi.fn(),
    disconnect: vi.fn(),
  }));
  vi.stubGlobal("ResizeObserver", ResizeObserverMock);

  beforeEach(() => {
    mockDefinitions.taskManager.numberOfTasksFinished = ref(0);
    mockDefinitions.taskManager.numberOfTasksToRun = ref(0);
    setActivePinia(createPinia());
    wrapper = mount(OfflineSyncer, {
      global: {
        plugins: [vuetify],
      },
    });
  });

  afterEach(() => {
    vi.clearAllMocks();
    document.body.innerHTML = "";
    document.head.innerHTML = "";
  });

  afterAll(() => {
    vi.resetAllMocks();
  });

  describe(COMPONENT_RENDER_TESTS, () => {
    it("should_renderActivatorButton_when_mounted", async (context) => {
      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderMenu_when_refreshWasClicked", async (context) => {
      const syncButton = wrapper.findComponent(
        '[data-test="button-sync-offline-data"]'
      );
      // eslint-disable-next-line  @typescript-eslint/no-non-null-assertion
      mockDefinitions.taskManager.numberOfTasksToRun!.value = 10;
      // eslint-disable-next-line  @typescript-eslint/no-non-null-assertion
      mockDefinitions.taskManager.numberOfTasksFinished!.value = 2;
      await syncButton.trigger("click");

      await expect(document.body.innerHTML).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });

  describe(COMPONENT_EVENT_TESTS, () => {
    it("should_triggerRunAllTasks_when_syncButtonIsClicked", async () => {
      const syncButton = wrapper.findComponent(
        '[data-test="button-sync-offline-data"]'
      );

      const mockedTasks = [createTask("task1"), createTask("task2")];
      mockDefinitions.getSyncTasks.mockReturnValue(
        Promise.resolve(mockedTasks)
      );

      await syncButton.trigger("click");

      expect(mockDefinitions.taskManager.setTasks.mock.calls).toStrictEqual([
        [mockedTasks],
      ]);
      expect(mockDefinitions.taskManager.runAllTasks).toHaveBeenCalledTimes(1);
    });
  });
});
