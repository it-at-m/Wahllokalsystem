import type { Task } from "@/types/tasks/Task.ts";

import { useIndexDBValueTestDataFactory } from "@tests/utils/indexDB/IndexDBValueTestDataFactory.ts";
import { useTasksTestDataFactory } from "@tests/utils/tasks/TasksTestDataFactory.ts";
import { spyOn } from "storybook/test";
import {
  afterAll,
  afterEach,
  beforeEach,
  describe,
  expect,
  it,
  vi,
} from "vitest";

import { useDataSyncer } from "@/composables/indexDB/dataSyncer.ts";
import { FetchStrategiesEnum } from "@/types/api/FetchStrategiesEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  getDirtyItems: vi.fn(),
  basicPostConfig: vi.fn(),
  setTasks: vi.fn(),
  runAllTasks: vi.fn(),
}));

vi.mock(import("axios"));
vi.mock(import("@/composables/indexDB/indexDB.ts"), () => ({
  useIndexDB: vi.fn().mockImplementation(() => ({
    getDirtyItems: mockDefinitions.getDirtyItems,
  })),
}));
vi.mock("@/api/axios-utils.ts", () => ({
  basicPostConfig: mockDefinitions.basicPostConfig,
}));
vi.mock(import("@/composables/tasks/taskManager.ts"), () => ({
  useTaskManager: vi.fn().mockImplementation(() => ({
    setTasks: mockDefinitions.setTasks,
    runAllTasks: mockDefinitions.runAllTasks,
  })),
}));

const { prepareIndexDBValue } = useIndexDBValueTestDataFactory();
const { createTask } = useTasksTestDataFactory();

describe("dataSyncer.ts", () => {
  let unitUnderTest: ReturnType<typeof useDataSyncer>;

  beforeEach(() => {
    unitUnderTest = useDataSyncer();
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  afterAll(() => {
    vi.resetAllMocks();
  });

  describe("dirtyTasksAfterSync ", () => {
    it("should_setListWithTasks_when_dirtyTasksExistAfterRun", async () => {
      const dirtyTasks = [
        {
          key: "key1",
          item: prepareIndexDBValue().data("data1").timestamp(9).build(),
        },
        {
          key: "key2",
          item: prepareIndexDBValue().data("2").timestamp(10).build(),
        },
      ];
      mockDefinitions.getDirtyItems.mockResolvedValue(dirtyTasks);

      expect(unitUnderTest.dirtyTasksAfterSync.value).toStrictEqual(null);
      await unitUnderTest.synchronizeOfflineData();

      const expectedTasks = [
        { name: "key1", callback: expect.anything() },
        { name: "key2", callback: expect.anything() },
      ];
      expect(unitUnderTest.dirtyTasksAfterSync.value).toStrictEqual(
        expectedTasks
      );
    });
    it("should_setEmptyList_when_noDirtyTasksExistAfterRun", async () => {
      mockDefinitions.getDirtyItems.mockResolvedValue([]);

      unitUnderTest.dirtyTasksAfterSync.value = [createTask("task1")];

      await unitUnderTest.synchronizeOfflineData();
      expect(unitUnderTest.dirtyTasksAfterSync.value).toStrictEqual([]);
    });
  });

  describe("hasDirtyTasksAfterSync", () => {
    it("should_returnFalse_when_dirtyTasksAfterSyncIsNull", () => {
      unitUnderTest.dirtyTasksAfterSync.value = null;
      expect(unitUnderTest.hasDirtyTasksAfterSync.value).toStrictEqual(false);
    });
    it("should_returnFalse_when_dirtyTasksAfterSyncIsEmptyArray", () => {
      unitUnderTest.dirtyTasksAfterSync.value = [];
      expect(unitUnderTest.hasDirtyTasksAfterSync.value).toStrictEqual(false);
    });
    it("should_returnTrue_when_dirtyTasksAfterSyncIsArrayWithOneItem", () => {
      unitUnderTest.dirtyTasksAfterSync.value = [createTask("1")];
      expect(unitUnderTest.hasDirtyTasksAfterSync.value).toStrictEqual(true);
    });
    it("should_returnTrue_when_dirtyTasksAfterSyncIsArrayWithMultipleItems", () => {
      unitUnderTest.dirtyTasksAfterSync.value = [
        createTask("1"),
        createTask("2"),
        createTask("3"),
        createTask("4"),
        createTask("5"),
        createTask("6"),
      ];
      expect(unitUnderTest.hasDirtyTasksAfterSync.value).toStrictEqual(true);
    });
  });

  describe("numberOfDirtyTasksAfterSync", () => {
    it("should_return0_when_dirtyTasksAfterSyncIsNull", () => {
      unitUnderTest.dirtyTasksAfterSync.value = null;
      expect(unitUnderTest.numberOfDirtyTasksAfterSync.value).toStrictEqual(0);
    });
    it("should_returnLength_when_dirtyTasksAfterSyncIsNotNull", () => {
      unitUnderTest.dirtyTasksAfterSync.value = [
        createTask("1"),
        createTask("2"),
        createTask("3"),
      ];

      expect(unitUnderTest.numberOfDirtyTasksAfterSync.value).toStrictEqual(3);
    });
  });

  describe("getSyncTasks", () => {
    it("should_returnTasksForDirtyItemsSortedByTimestamp_when_dirtyItemsAreGiven", async () => {
      mockDefinitions.getDirtyItems.mockResolvedValue(
        createUnOrderedSetOfItemsToSync()
      );

      const result = await unitUnderTest.getSyncTasks();
      const taskNames = result.map((task: Task) => task.name);

      expect(taskNames).toStrictEqual([
        "key3",
        "key4",
        "key2",
        "key5",
        "key1",
        "key6",
      ]);
    });

    it("should_returnEmptyArray_when_noDirtyItemsAreGiven", async () => {
      mockDefinitions.getDirtyItems.mockResolvedValue([]);

      const result = await unitUnderTest.getSyncTasks();

      expect(result).toStrictEqual([]);
    });

    it("should_createAxiosPost_when_itemIsGiven", async () => {
      const key = "key";
      const data = "data";
      mockDefinitions.getDirtyItems.mockResolvedValue([
        { key, item: prepareIndexDBValue().data(`"${data}"`).build() },
      ]);

      const result = await unitUnderTest.getSyncTasks();
      await result[0]?.callback();

      expect(mockDefinitions.basicPostConfig.mock.calls).toStrictEqual([
        [key, FetchStrategiesEnum.STRATEGY_ONLINE_FIRST, data],
      ]);
    });

    function createUnOrderedSetOfItemsToSync() {
      return [
        {
          key: "key1",
          item: prepareIndexDBValue()
            .data("data1")
            .timestamp(undefined)
            .build(),
        },
        {
          key: "key2",
          item: prepareIndexDBValue().data("2").timestamp(10).build(),
        },
        {
          key: "key3",
          item: prepareIndexDBValue().data("3").timestamp(1).build(),
        },
        {
          key: "key4",
          item: prepareIndexDBValue().data("4").timestamp(1).build(),
        },
        {
          key: "key5",
          item: prepareIndexDBValue().data("5").timestamp(13).build(),
        },
        {
          key: "key6",
          item: prepareIndexDBValue().data("6").timestamp(undefined).build(),
        },
      ];
    }
  });

  describe("synchronizeOfflineData", () => {
    it("should_toggleOfflineDataSyncingVariable_when_called", async () => {
      const isOfflineDataSyncingSpy = spyOn(
        unitUnderTest.isOfflineDataSyncing,
        "value",
        "set"
      );
      mockDefinitions.getDirtyItems.mockResolvedValue([]);

      await unitUnderTest.synchronizeOfflineData();

      expect(isOfflineDataSyncingSpy.mock.calls).toStrictEqual([
        [true],
        [false],
      ]);

      isOfflineDataSyncingSpy.mockRestore();
    });

    it("should_syncingTasks_when_called", async () => {
      mockDefinitions.getDirtyItems.mockResolvedValue([]);

      await unitUnderTest.synchronizeOfflineData();

      expect(mockDefinitions.setTasks).toHaveBeenCalledOnce();
      expect(mockDefinitions.runAllTasks).toHaveBeenCalledOnce();
    });

    it("should_notDoAnything_when_syncIsInProgress", async () => {
      unitUnderTest.isOfflineDataSyncing.value = true;
      await unitUnderTest.synchronizeOfflineData();
      unitUnderTest.isOfflineDataSyncing.value = false;

      expect(mockDefinitions.getDirtyItems).not.toHaveBeenCalled();
      expect(mockDefinitions.setTasks).not.toHaveBeenCalled();
      expect(mockDefinitions.runAllTasks).not.toHaveBeenCalled();
    });

    it("should_setIsSyncingFalse_when_anErrorOccurred", async () => {
      const isOfflineDataSyncingSpy = spyOn(
        unitUnderTest.isOfflineDataSyncing,
        "value",
        "set"
      );
      mockDefinitions.getDirtyItems.mockResolvedValue([]);
      mockDefinitions.runAllTasks.mockRejectedValueOnce(
        new Error("mocked error while running tasks")
      );

      await expect(
        unitUnderTest.synchronizeOfflineData()
      ).resolves.toBeUndefined();

      expect(isOfflineDataSyncingSpy.mock.calls).toStrictEqual([
        [true],
        [false],
      ]);

      isOfflineDataSyncingSpy.mockRestore();
    });
  });
});
