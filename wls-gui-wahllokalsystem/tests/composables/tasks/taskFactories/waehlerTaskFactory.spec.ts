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

import { useWaehlerTaskFactory } from "@/composables/tasks/taskFactories/waehlerTaskFactory.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  loadWaehler: vi.fn(),
}));

vi.mock("@/stores/monitoringStore.ts", () => ({
  useMonitoringStore: vi.fn().mockImplementation(() => ({
    loadWaehler: mockDefinitions.loadWaehler,
  })),
}));

describe("waehlerTaskFactory.ts", () => {
  const { prepareTaskFactoryContext } = useTasksTestDataFactory();

  let unitUnderTest: ReturnType<typeof useWaehlerTaskFactory>;

  beforeEach(() => {
    unitUnderTest = useWaehlerTaskFactory();
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  afterAll(() => {
    vi.resetAllMocks();
  });
  describe("createTasks", () => {
    describe("userHasRoleSchriftfuehrung", () => {
      it("should_returnTask_when_wahlbezirksArtIsUWB", async () => {
        const taskFactoryContext = prepareTaskFactoryContext()
          .isSchriftfuehrung(true)
          .wahlbezirkArt(WahlbezirksArtEnum.UWB)
          .build();

        const result = unitUnderTest.createTasks(taskFactoryContext);
        await result[0]?.callback();

        expect(result.length).toStrictEqual(1);
        expect(result[0]?.name).toStrictEqual("Wahlbeteiligung");
        expect(mockDefinitions.loadWaehler.mock.calls).toStrictEqual([[]]);
      });

      it("should_returnNoTask_when_wahlbezirksArtIsBWB", () => {
        const taskFactoryContext = prepareTaskFactoryContext()
          .isSchriftfuehrung(true)
          .wahlbezirkArt(WahlbezirksArtEnum.BWB)
          .build();

        const result = unitUnderTest.createTasks(taskFactoryContext);

        expect(result).toStrictEqual([]);
      });
    });

    describe("userHasNotRoleSchriftfuehrung", () => {
      it("should_returnNoTask_when_called", () => {
        const taskFactoryContext = prepareTaskFactoryContext()
          .isSchriftfuehrung(false)
          .build();

        const result = unitUnderTest.createTasks(taskFactoryContext);
        expect(result.length).toStrictEqual(0);
      });
    });
  });
});
