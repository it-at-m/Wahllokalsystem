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

import { useWaehlverzeichnisTaskFactory } from "@/composables/tasks/taskFactories/waehlverzeichnisTaskFactory.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  loadPflegeWaehlerverzeichnis: vi.fn(),
}));

vi.mock("@/stores/wahlbezirkStore.ts", () => ({
  useWahlbezirkStore: vi.fn().mockImplementation(() => ({
    pflegeWaehlerverzeichnisActions: {
      loadPflegeWaehlerverzeichnis:
        mockDefinitions.loadPflegeWaehlerverzeichnis,
    },
  })),
}));

describe("waehlverzeichnisTaskFactory.ts", () => {
  const { prepareTaskFactoryContext } = useTasksTestDataFactory();

  let unitUnderTest: ReturnType<typeof useWaehlverzeichnisTaskFactory>;

  beforeEach(() => {
    unitUnderTest = useWaehlverzeichnisTaskFactory();
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  afterAll(() => {
    vi.resetAllMocks();
  });

  describe("createTasks", () => {
    it("should_returnTask_when_wahlbezirksArtIsUWB", async () => {
      const taskFactoryContext = prepareTaskFactoryContext()
        .wahlbezirkArt(WahlbezirksArtEnum.UWB)
        .build();

      const result = unitUnderTest.createTasks(taskFactoryContext);
      await result[0]?.callback();

      expect(result.length).toStrictEqual(1);
      expect(result[0]?.name).toStrictEqual("Wählerverzeichnis");
      expect(
        mockDefinitions.loadPflegeWaehlerverzeichnis.mock.calls
      ).toStrictEqual([[false]]);
    });

    it("should_returnNoTask_when_wahlbezirksArtIsBWB", async () => {
      const taskFactoryContext = prepareTaskFactoryContext()
        .wahlbezirkArt(WahlbezirksArtEnum.BWB)
        .build();

      const result = unitUnderTest.createTasks(taskFactoryContext);

      expect(result).toStrictEqual([]);
    });
  });
});
