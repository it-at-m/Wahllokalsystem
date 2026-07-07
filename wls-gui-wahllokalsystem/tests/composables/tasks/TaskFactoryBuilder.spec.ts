import { useTasksTestDataFactory } from "@tests/utils/tasks/TasksTestDataFactory.ts";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useTaskFactoryBuilder } from "@/composables/tasks/TaskFactoryBuilder.ts";

const { prepareTaskFactoryContext, createTask } = useTasksTestDataFactory();

describe("TaskFactoryBuilder.ts", () => {
  let unitUnderTest: ReturnType<typeof useTaskFactoryBuilder>;

  beforeEach(() => {
    unitUnderTest = useTaskFactoryBuilder();
  });

  describe("whenUserIsSchriftfuehrung", () => {
    it("should_createTaskFactoryThatCallsTaskCreatorFunction_when_userIsSchriftfuehrung", () => {
      const context = prepareTaskFactoryContext()
        .isSchriftfuehrung(true)
        .build();
      const mockedCreatedTasks = [createTask("task1"), createTask("task2")];
      const taskCreatorFunctionMock = vi
        .fn()
        .mockImplementation(() => mockedCreatedTasks);

      const result = unitUnderTest.whenUserIsSchriftfuehrung(
        taskCreatorFunctionMock
      );
      const createdTasks = result.createTasks(context);

      expect(createdTasks).toStrictEqual(mockedCreatedTasks);
      expect(taskCreatorFunctionMock).toHaveBeenCalledOnce();
      expect(taskCreatorFunctionMock).toHaveBeenCalledWith(context);
    });

    it("should_createTaskFactoryThatNotCallsTaskCreatorFunction_when_userIsNotSchriftfuehrung", () => {
      const context = prepareTaskFactoryContext()
        .isSchriftfuehrung(false)
        .build();
      const taskCreatorFunctionMock = vi.fn();

      const result = unitUnderTest.whenUserIsSchriftfuehrung(
        taskCreatorFunctionMock
      );
      const createdTasks = result.createTasks(context);

      expect(createdTasks).toHaveLength(0);
      expect(taskCreatorFunctionMock).not.toHaveBeenCalled();
    });
  });
});
