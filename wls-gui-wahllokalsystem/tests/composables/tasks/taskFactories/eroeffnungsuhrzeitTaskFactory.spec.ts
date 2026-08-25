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

import { useEroeffnungsuhrzeitTaskFactory } from "@/composables/tasks/taskFactories/eroeffnungsuhrzeitTaskFactory.ts";

const mockDefinitions = vi.hoisted(() => ({
  initEroeffnungsuhrzeit: vi.fn(),
}));

vi.mock("@/stores/wahlbezirkStore.ts", () => ({
  useWahlbezirkStore: () => ({
    eroeffnungsuhrzeitActions: {
      initEroeffnungsuhrzeit: mockDefinitions.initEroeffnungsuhrzeit,
    },
  }),
}));

const { prepareTaskFactoryContext } = useTasksTestDataFactory();

describe("eroeffnungsuhrzeitTaskFactory", () => {
  let unitUnderTest: ReturnType<typeof useEroeffnungsuhrzeitTaskFactory>;

  beforeEach(() => {
    unitUnderTest = useEroeffnungsuhrzeitTaskFactory();
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  afterAll(() => {
    vi.resetAllMocks();
  });

  describe("createTasks", () => {
    describe("userHasRoleSchriftfuehrung", () => {
      it("should_returnOneTaskThatTriggersInit_when_userHasRoleSchriftfuehrung", () => {
        const tasks = unitUnderTest.createTasks(
          prepareTaskFactoryContext().isSchriftfuehrung(true).build()
        );
        tasks[0]?.callback();

        expect(tasks.length).toStrictEqual(1);
        expect(mockDefinitions.initEroeffnungsuhrzeit).toHaveBeenCalledTimes(1);
      });
    });

    describe("userHasNotRoleSchriftfuehrung", () => {
      it("should_returnEmptyList_when_userHasNotRoleSchriftfuehrung", () => {
        const tasks = unitUnderTest.createTasks(
          prepareTaskFactoryContext().isSchriftfuehrung(false).build()
        );
        expect(tasks.length).toStrictEqual(0);
      });
    });
  });
});
