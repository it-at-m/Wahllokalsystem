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

const { createTaskFactoryContext } = useTasksTestDataFactory();

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
    it("should_returnOneTaskThatTriggersInit_when_called", () => {
      const tasks = unitUnderTest.createTasks(createTaskFactoryContext());
      tasks[0]?.callback();

      expect(tasks.length).toStrictEqual(1);
      expect(mockDefinitions.initEroeffnungsuhrzeit).toHaveBeenCalledTimes(1);
    });
  });
});
