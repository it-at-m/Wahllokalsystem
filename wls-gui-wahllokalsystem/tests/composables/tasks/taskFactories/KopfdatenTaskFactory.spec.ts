import type { TaskFactoryContext } from "@/composables/tasks/TaskFactoryContext.ts";

import { useTasksTestDataFactory } from "@tests/utils/tasks/TasksTestDataFactory.ts";
import { createPinia, setActivePinia } from "pinia";
import { beforeEach, describe, expect, it } from "vitest";

import { useKopfdatenTaskFactory } from "@/composables/tasks/taskFactories/KopfdatenTaskFactory.ts";

describe("WahlvorstandTaskFactory.ts", () => {
  const { prepareTaskFactoryContext, createExtendedWahlMetaData } =
    useTasksTestDataFactory();
  const { createTasks } = useKopfdatenTaskFactory();
  const kopfdatenNamePrefix = "Kopfdaten - ";

  beforeEach(() => {
    setActivePinia(createPinia());
  });

  describe("createTasks", () => {
    it("should_returnTaskListWithFourElements_when_calledWithCorrectParametersAndFourWahldataElements", () => {
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
        kopfdatenNamePrefix + extendedWahlMetaDataOne.wahlname,
        kopfdatenNamePrefix + extendedWahlMetaDataTwo.wahlname,
        kopfdatenNamePrefix + extendedWahlMetaDataThree.wahlname,
        kopfdatenNamePrefix + extendedWahlMetaDataFour.wahlname,
      ];
      const resultNames = result.map((task) => task.name);
      expectedNames.forEach((name) => {
        expect(resultNames).toContain(name);
      });
    });

    it("should_returnTaskListWithOneElement_when_calledWithCorrectParametersAndOneWahldataElement", () => {
      const extendedWahlMetaDataOne = createExtendedWahlMetaData();
      const taskFactoryContext: TaskFactoryContext = prepareTaskFactoryContext()
        .extendedWahlMetaData([extendedWahlMetaDataOne])
        .build();

      const result = createTasks(taskFactoryContext);

      expect(result.length).toStrictEqual(1);
      expect(result[0].name).toStrictEqual(
        kopfdatenNamePrefix + extendedWahlMetaDataOne.wahlname
      );
    });
  });
});
