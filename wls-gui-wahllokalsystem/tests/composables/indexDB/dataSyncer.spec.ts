import type { SyncronizeDataResult } from "@/types/indexDB/SyncronizeDataResult.ts";
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

const mockDefinitions = await vi.hoisted(async () => {
  const commonTestDataFactoryImport =
    await import("@tests/utils/common/CommonTestDataFactory.ts");
  const { generateRandomNumber } =
    commonTestDataFactoryImport.useCommonTestDataFactory();

  const { ref } = await import("vue");
  return {
    getDirtyItems: vi.fn(),
    basicPostConfig: vi.fn(),
    taskManager: {
      setTasks: vi.fn(),
      runAllTasks: vi.fn(),
      numberOfTasksToRun: ref(generateRandomNumber(3)),
      numberOfTasksSucceeded: ref(generateRandomNumber(3)),
      numberOfTasksFailed: ref(generateRandomNumber(3)),
    },
  };
});

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
    setTasks: mockDefinitions.taskManager.setTasks,
    runAllTasks: mockDefinitions.taskManager.runAllTasks,
    numberOfTasksToRun: mockDefinitions.taskManager.numberOfTasksToRun,
    numberOfTasksSucceeded: mockDefinitions.taskManager.numberOfTasksSucceeded,
    numberOfTasksFailed: mockDefinitions.taskManager.numberOfTasksFailed,
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
    it("should_updateDirtyTasksAfterSync_when_runAllTasksThrowError", async () => {
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
      mockDefinitions.taskManager.runAllTasks.mockRejectedValue(
        new Error("mocked task manager error")
      );

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
  });

  describe("numberOfDirtyTasksAfterSync", () => {
    it("should_returnUndefined_when_dirtyTasksAfterSyncIsNull", () => {
      unitUnderTest.dirtyTasksAfterSync.value = null;
      expect(unitUnderTest.numberOfDirtyTasksAfterSync.value).toStrictEqual(
        undefined
      );
    });
    it("should_returnUndefined_when_dirtyTasksAfterSyncIsEmpty", () => {
      unitUnderTest.dirtyTasksAfterSync.value = [];
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

    it("should_returnNull_when_syncIsAlreadyInProgress", async () => {
      const isOfflineDataSyncingSpy = spyOn(
        unitUnderTest.isOfflineDataSyncing,
        "value",
        "get"
      );
      isOfflineDataSyncingSpy.mockReturnValue(true);

      const result = await unitUnderTest.synchronizeOfflineData();
      expect(result).toStrictEqual(null);

      isOfflineDataSyncingSpy.mockRestore();
    });

    it("should_syncingTasks_when_called", async () => {
      mockDefinitions.getDirtyItems.mockResolvedValue([]);

      const result = await unitUnderTest.synchronizeOfflineData();

      expect(mockDefinitions.taskManager.setTasks).toHaveBeenCalledOnce();
      expect(mockDefinitions.taskManager.runAllTasks).toHaveBeenCalledOnce();

      const expectedResult: SyncronizeDataResult = {
        numberOfDirtyTasksRemaining: 0,
        numberOfTasksSucceeded:
          mockDefinitions.taskManager.numberOfTasksSucceeded.value,
        numberOfTasksFailed:
          mockDefinitions.taskManager.numberOfTasksFailed.value,
        numberOfTasksRan: mockDefinitions.taskManager.numberOfTasksToRun.value,
      };
      expect(result).toStrictEqual(expectedResult);
    });

    it("should_notDoAnything_when_syncIsInProgress", async () => {
      unitUnderTest.isOfflineDataSyncing.value = true;
      await unitUnderTest.synchronizeOfflineData();
      unitUnderTest.isOfflineDataSyncing.value = false;

      expect(mockDefinitions.getDirtyItems).not.toHaveBeenCalled();
      expect(mockDefinitions.taskManager.setTasks).not.toHaveBeenCalled();
      expect(mockDefinitions.taskManager.runAllTasks).not.toHaveBeenCalled();
    });

    it("should_setIsSyncingFalse_when_anErrorOccurred", async () => {
      const isOfflineDataSyncingSpy = spyOn(
        unitUnderTest.isOfflineDataSyncing,
        "value",
        "set"
      );
      mockDefinitions.getDirtyItems.mockResolvedValue([]);
      mockDefinitions.taskManager.runAllTasks.mockRejectedValueOnce(
        new Error("mocked error while running tasks")
      );

      const expectedResult: SyncronizeDataResult = {
        numberOfDirtyTasksRemaining: 0,
        numberOfTasksSucceeded:
          mockDefinitions.taskManager.numberOfTasksSucceeded.value,
        numberOfTasksFailed:
          mockDefinitions.taskManager.numberOfTasksFailed.value,
        numberOfTasksRan: mockDefinitions.taskManager.numberOfTasksToRun.value,
      };
      await expect(
        unitUnderTest.synchronizeOfflineData()
      ).resolves.toStrictEqual(expectedResult);

      expect(isOfflineDataSyncingSpy.mock.calls).toStrictEqual([
        [true],
        [false],
      ]);

      isOfflineDataSyncingSpy.mockRestore();
    });
  });
});
