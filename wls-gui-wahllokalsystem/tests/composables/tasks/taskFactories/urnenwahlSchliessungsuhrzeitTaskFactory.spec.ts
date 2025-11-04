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

import { useUrnenwahlSchliessungsuhrzeitTaskFactory } from "@/composables/tasks/taskFactories/urnenwahlSchliessungsuhrzeitTaskFactory.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  initSchliessungsuhrzeit: vi.fn(),
}));

vi.mock("@/stores/wahlbezirkStore.ts", () => ({
  useWahlbezirkStore: () => ({
    schliessungsuhrzeitActions: {
      initSchliessungsuhrzeit: mockDefinitions.initSchliessungsuhrzeit,
    },
  }),
}));

const { prepareTaskFactoryContext } = useTasksTestDataFactory();

describe("urnenwahlSchliessungsuhrzeitTaskFactory", () => {
  let unitUnderTest: ReturnType<
    typeof useUrnenwahlSchliessungsuhrzeitTaskFactory
  >;

  beforeEach(() => {
    unitUnderTest = useUrnenwahlSchliessungsuhrzeitTaskFactory();
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  afterAll(() => {
    vi.resetAllMocks();
  });

  describe("createTasks", () => {
    it("should_createOneTask_when_wahlbezirkArtIsUWB", () => {
      const result = unitUnderTest.createTasks(
        prepareTaskFactoryContext()
          .wahlbezirkArt(WahlbezirksArtEnum.UWB)
          .build()
      );

      expect(result.length).toStrictEqual(1);
    });

    it("should_create0ZeroTasks_when_wahlbezirkArtIsBWBW", () => {
      const result = unitUnderTest.createTasks(
        prepareTaskFactoryContext()
          .wahlbezirkArt(WahlbezirksArtEnum.BWB)
          .build()
      );

      expect(result.length).toStrictEqual(0);
    });

    it("should_callsInitFunction_when_createdTasksCallbackIsUsed", () => {
      unitUnderTest
        .createTasks(
          prepareTaskFactoryContext()
            .wahlbezirkArt(WahlbezirksArtEnum.UWB)
            .build()
        )[0]
        ?.callback();

      expect(mockDefinitions.initSchliessungsuhrzeit).toHaveBeenCalledTimes(1);
    });
  });
});
