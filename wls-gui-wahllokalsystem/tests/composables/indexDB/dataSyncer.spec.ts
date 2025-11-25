import type { Task } from "@/types/tasks/Task.ts";

import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useIndexDBValueTestDataFactory } from "@tests/utils/indexDB/IndexDBValueTestDataFactory.ts";
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
}));

vi.mock("axios");
vi.mock("@/composables/indexDB/indexDB.ts", () => ({
  useIndexDB: vi.fn().mockImplementation(() => ({
    getDirtyItems: mockDefinitions.getDirtyItems,
  })),
}));
vi.mock("@/api/axios-utils.ts", () => ({
  basicPostConfig: mockDefinitions.basicPostConfig,
}));

const { prepareIndexDBValue } = useIndexDBValueTestDataFactory();
const { generateRandomNumber } = useCommonTestDataFactory();

describe("dataSyncer.ts", () => {
  let unitUnderTest: ReturnType<typeof useDataSyncer>;

  const mockKey: CryptoKey = {
    algorithm: { name: "AES-GCM", length: 256 },
    extractable: true,
    type: "secret",
    usages: ["encrypt", "decrypt"],
  } as CryptoKey;
  const mockIV = new Uint8Array(new ArrayBuffer(generateRandomNumber(1)));

  beforeEach(() => {
    unitUnderTest = useDataSyncer();
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  afterAll(() => {
    vi.resetAllMocks();
  });

  describe("getSyncTasks", () => {
    it("should_returnTasksForDirtyItemsSortedByTimestamp_when_dirtyItemsAreGiven", async () => {
      mockDefinitions.getDirtyItems.mockResolvedValue(
        createUnOrderedSetOfItemsToSync()
      );

      const result = await unitUnderTest.getSyncTasks(mockKey, mockIV);
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

      const result = await unitUnderTest.getSyncTasks(mockKey, mockIV);

      expect(result).toStrictEqual([]);
    });

    it("should_createAxiosPost_when_itemIsGiven", async () => {
      const key = "key";
      const data = "data";
      mockDefinitions.getDirtyItems.mockResolvedValue([
        { key, item: prepareIndexDBValue().data(`"${data}"`).build() },
      ]);

      const result = await unitUnderTest.getSyncTasks(mockKey, mockIV);
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
});
