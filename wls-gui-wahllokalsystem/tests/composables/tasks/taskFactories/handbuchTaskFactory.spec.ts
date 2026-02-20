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

import { useHandbuchTaskFactory } from "@/composables/tasks/taskFactories/handbuchTaskFactory.ts";

const mockDefinitions = vi.hoisted(() => ({
  getHandbuch: vi.fn(),
}));

vi.mock("@/composables/basisdaten/handbuchService.ts", () => ({
  useHandbuchService: vi.fn().mockImplementation(() => ({
    getHandbuch: mockDefinitions.getHandbuch,
  })),
}));

const { createTaskFactoryContext } = useTasksTestDataFactory();

describe("handbuchTaskFactory.ts", () => {
  let unitUnderTest: ReturnType<typeof useHandbuchTaskFactory>;

  beforeEach(() => {
    unitUnderTest = useHandbuchTaskFactory();
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  afterAll(() => {
    vi.resetAllMocks();
  });

  describe("createTasks", () => {
    it("should_addOneTaskToLoadHandbuch_when_called", () => {
      const result = unitUnderTest.createTasks(createTaskFactoryContext());
      result[0]?.callback();

      expect(result.length).toStrictEqual(1);
      expect(result[0]?.name).toStrictEqual("Handbuch");
      expect(mockDefinitions.getHandbuch).toHaveBeenCalled();
    });
  });
});
