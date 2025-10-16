import { useTasksTestDataFactory } from "@tests/utils/tasks/TasksTestDataFactory.ts";
import {
  afterAll,
  afterEach,
  beforeEach,
  describe,
  expect,
  it,
  vi,
} from "vitest";

import { useAWerteTaskFactory } from "@/composables/tasks/taskFactories/aWerteTaskFactory.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  getAWerte: vi.fn(),
}));

vi.mock("@/composables/ergebnismeldung/aWerteService.ts", () => ({
  useAWerteService: vi.fn().mockImplementation(() => ({
    getAWerte: mockDefinitions.getAWerte,
  })),
}));

const { createExtendedWahlMetaData, prepareTaskFactoryContext } =
  useTasksTestDataFactory();

describe("aWerteTaskFactory.ts", () => {
  let unitUnderTest: ReturnType<typeof useAWerteTaskFactory>;

  beforeEach(() => {
    unitUnderTest = useAWerteTaskFactory();
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  afterAll(() => {
    vi.resetAllMocks();
  });

  describe("createTasks", () => {
    it("should_returnTaskForEachWahlbezirk_when_wahlbezirkArtIsUWB", () => {
      const factoryContext = prepareTaskFactoryContext()
        .wahlbezirkArt(WahlbezirksArtEnum.UWB)
        .extendedWahlMetaData([
          createExtendedWahlMetaData(),
          createExtendedWahlMetaData(),
        ])
        .build();

      const result = unitUnderTest.createTasks(factoryContext);

      expect(result.length).toStrictEqual(
        factoryContext.extendedWahlMetaData.length
      );
      expect(result[0]?.name).toContain(
        factoryContext.extendedWahlMetaData[0]?.wahlName
      );
      expect(result[1]?.name).toContain(
        factoryContext.extendedWahlMetaData[1]?.wahlName
      );

      result.forEach((task) => task.callback());
      expect(mockDefinitions.getAWerte.mock.calls).toStrictEqual([
        [factoryContext.extendedWahlMetaData[0]?.wahlbezirkID, false],
        [factoryContext.extendedWahlMetaData[1]?.wahlbezirkID, false],
      ]);
    });

    it("should_returnNoTask_when_wahlbezirkArtIsBWB", () => {
      const factoryContext = prepareTaskFactoryContext()
        .wahlbezirkArt(WahlbezirksArtEnum.BWB)
        .extendedWahlMetaData([
          createExtendedWahlMetaData(),
          createExtendedWahlMetaData(),
        ])
        .build();

      const result = unitUnderTest.createTasks(factoryContext);

      expect(result.length).toStrictEqual(0);
    });
  });
});
