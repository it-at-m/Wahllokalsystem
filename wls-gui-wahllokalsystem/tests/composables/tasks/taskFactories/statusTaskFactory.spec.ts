import type { TaskFactoryContext } from "@/composables/tasks/TaskFactoryContext.ts";

import { useTasksTestDataFactory } from "@tests/utils/tasks/TasksTestDataFactory.ts";
import { describe, expect, it, vi } from "vitest";

import { useStatusTaskFactory } from "@/composables/tasks/taskFactories/statusTaskFactory.ts";

const mockDefinitions = vi.hoisted(() => ({
  loadStatus: vi.fn(),
}));

vi.mock("@/stores/statusStore.ts", () => ({
  useStatusStore: vi.fn().mockImplementation(() => ({
    loadStatus: mockDefinitions.loadStatus,
  })),
}));

describe("statusTaskFactory.ts", () => {
  const { prepareTaskFactoryContext, createExtendedWahlMetaData } =
    useTasksTestDataFactory();
  const { createTasks } = useStatusTaskFactory();
  const statusNamePrefix = "Druckstatus - ";

  describe("createTasks", () => {
    it("should_returnTaskListWithFourElements_when_calledWithFourWahldataElements", () => {
      const extendedWahlMetaDataOne = createExtendedWahlMetaData();
      const extendedWahlMetaDataTwo = createExtendedWahlMetaData();
      const extendedWahlMetaDataThree = createExtendedWahlMetaData();
      const extendedWahlMetaDataFour = createExtendedWahlMetaData();
      const taskFactoryContext: TaskFactoryContext = prepareTaskFactoryContext()
        .extendedWahlMetaData([
          extendedWahlMetaDataOne,
          extendedWahlMetaDataTwo,
          extendedWahlMetaDataThree,
          extendedWahlMetaDataFour,
        ])
        .build();

      const result = createTasks(taskFactoryContext);

      expect(result.length).toStrictEqual(4);
      const expectedNames = [
        statusNamePrefix + extendedWahlMetaDataOne.wahlName,
        statusNamePrefix + extendedWahlMetaDataTwo.wahlName,
        statusNamePrefix + extendedWahlMetaDataThree.wahlName,
        statusNamePrefix + extendedWahlMetaDataFour.wahlName,
      ];
      const resultNames = result.map((task) => task.name);
      expectedNames.forEach((name) => {
        expect(resultNames).toContain(name);
      });
    });

    it("should_returnTaskListWithOneElement_when_calledWithOneWahldataElement", () => {
      const extendedWahlMetaDataOne = createExtendedWahlMetaData();
      const taskFactoryContext: TaskFactoryContext = prepareTaskFactoryContext()
        .extendedWahlMetaData([extendedWahlMetaDataOne])
        .build();

      const result = createTasks(taskFactoryContext);

      expect(result.length).toStrictEqual(1);
      expect(result[0]?.name).toStrictEqual(
        statusNamePrefix + extendedWahlMetaDataOne.wahlName
      );
    });

    it("should_returnEmptyTaskList_when_calledWithNoWahldataElement", () => {
      const taskFactoryContext: TaskFactoryContext = prepareTaskFactoryContext()
        .extendedWahlMetaData([])
        .build();
      const result = createTasks(taskFactoryContext);

      expect(result.length).toStrictEqual(0);
    });
  });
});
