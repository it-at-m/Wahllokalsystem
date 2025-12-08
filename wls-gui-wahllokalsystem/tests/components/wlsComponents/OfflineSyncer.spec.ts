import type { VueWrapper } from "@vue/test-utils";
import type { Ref } from "vue";

import {
  COMPONENT_EVENT_TESTS,
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
import { mount } from "@vue/test-utils";
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
  dataSyncer: {
    isOfflineDataSyncing: undefined as Ref<boolean> | undefined,
    synchronizeOfflineData: vi.fn(),
  },
}));

vi.mock("@/composables/tasks/taskManager.ts", () => ({
  useTaskManager: vi.fn().mockImplementation(() => mockDefinitions.taskManager),
}));
vi.mock("@/composables/indexDB/dataSyncer.ts", () => ({
  useDataSyncer: vi.fn().mockImplementation(() => ({
    getSyncTasks: mockDefinitions.getSyncTasks,
  })),
}));
vi.mock("@/stores/dataSyncStore.ts", () => ({
  useDataSyncStore: vi
    .fn()
    .mockImplementation(() => mockDefinitions.dataSyncer),
}));

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
    mockDefinitions.dataSyncer.isOfflineDataSyncing = ref(false);
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
    it("should_triggerSynchronizeOfflineData_when_syncButtonIsClicked", async () => {
      const syncButton = wrapper.findComponent(
        '[data-test="button-sync-offline-data"]'
      );

      mockDefinitions.dataSyncer.synchronizeOfflineData.mockResolvedValue(
        Promise.resolve()
      );
      await syncButton.trigger("click");

      expect(
        mockDefinitions.dataSyncer.synchronizeOfflineData
      ).toHaveBeenCalledTimes(1);
    });

    it("should_notTriggerSynchronizeOfflineData_when_syncButtonIsClickedAndValueIsAlreadySyncing", async () => {
      // eslint-disable-next-line  @typescript-eslint/no-non-null-assertion
      mockDefinitions.dataSyncer.isOfflineDataSyncing!.value = true;
      const syncButton = wrapper.findComponent(
        '[data-test="button-sync-offline-data"]'
      );

      mockDefinitions.dataSyncer.synchronizeOfflineData.mockResolvedValue(
        Promise.resolve()
      );
      await syncButton.trigger("click");

      expect(
        mockDefinitions.dataSyncer.synchronizeOfflineData
      ).toHaveBeenCalledTimes(0);
    });
  });
});
