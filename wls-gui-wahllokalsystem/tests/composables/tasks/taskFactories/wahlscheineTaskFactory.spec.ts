import type { TaskFactoryContext } from "@/composables/tasks/TaskFactoryContext.ts";

import { useTasksTestDataFactory } from "@tests/utils/tasks/TasksTestDataFactory.ts";
import { createPinia, setActivePinia } from "pinia";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useWahlscheineTaskFactory } from "@/composables/tasks/taskFactories/wahlscheineTaskFactory.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  loadWahlscheine: vi.fn(),
}));

vi.mock("@/stores/wahlscheineStore.ts", () => ({
  useWahlscheineStore: vi.fn().mockImplementation(() => ({
    loadWahlscheine: mockDefinitions.loadWahlscheine,
  })),
}));
describe("wahlscheineTaskFactory.ts", () => {
  const { prepareTaskFactoryContext, createExtendedWahlMetaData } =
    useTasksTestDataFactory();
  const { createTasks } = useWahlscheineTaskFactory();
  const wahlscheineNamePrefix = "Wahlscheine - ";

  beforeEach(() => {
    setActivePinia(createPinia());
  });

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
        .wahlbezirkArt(WahlbezirksArtEnum.BWB)
        .build();

      const result = createTasks(taskFactoryContext);

      expect(result.length).toStrictEqual(4);
      const expectedNames = [
        wahlscheineNamePrefix + extendedWahlMetaDataOne.wahlName,
        wahlscheineNamePrefix + extendedWahlMetaDataTwo.wahlName,
        wahlscheineNamePrefix + extendedWahlMetaDataThree.wahlName,
        wahlscheineNamePrefix + extendedWahlMetaDataFour.wahlName,
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
        .wahlbezirkArt(WahlbezirksArtEnum.BWB)
        .build();

      const result = createTasks(taskFactoryContext);

      expect(result.length).toStrictEqual(1);
      expect(result[0]?.name).toStrictEqual(
        wahlscheineNamePrefix + extendedWahlMetaDataOne.wahlName
      );
    });

    it("should_returnTaskListWithTwoElementsContainingTheExpectedCallbacksWithCorrectInputs_when_calledWithTwoWahldataElement", () => {
      const extendedWahlMetaDataOne = createExtendedWahlMetaData();
      const extendedWahlMetaDataTwo = createExtendedWahlMetaData();
      const taskFactoryContext: TaskFactoryContext = prepareTaskFactoryContext()
        .extendedWahlMetaData([
          extendedWahlMetaDataOne,
          extendedWahlMetaDataTwo,
        ])
        .wahlbezirkArt(WahlbezirksArtEnum.BWB)
        .build();

      mockDefinitions.loadWahlscheine.mockReturnValue(Promise.resolve());

      const result = createTasks(taskFactoryContext);

      expect(result.length).toStrictEqual(2);

      result.forEach((task) => task.callback());
      expect(mockDefinitions.loadWahlscheine).toHaveBeenCalledTimes(2);
      expect(mockDefinitions.loadWahlscheine).toHaveBeenCalledWith(
        extendedWahlMetaDataOne.wahlID,
        extendedWahlMetaDataOne.wahlbezirkID,
        false
      );
      expect(mockDefinitions.loadWahlscheine).toHaveBeenCalledWith(
        extendedWahlMetaDataTwo.wahlID,
        extendedWahlMetaDataTwo.wahlbezirkID,
        false
      );
    });

    it("should_returnNoTasks_when_calledWithWrongWahlbezirkArt", () => {
      const extendedWahlMetaDataOne = createExtendedWahlMetaData();
      const extendedWahlMetaDataTwo = createExtendedWahlMetaData();
      const taskFactoryContext: TaskFactoryContext = prepareTaskFactoryContext()
        .extendedWahlMetaData([
          extendedWahlMetaDataOne,
          extendedWahlMetaDataTwo,
        ])
        .wahlbezirkArt(WahlbezirksArtEnum.UWB)
        .build();

      mockDefinitions.loadWahlscheine.mockReturnValue(Promise.resolve());

      const result = createTasks(taskFactoryContext);

      expect(result.length).toStrictEqual(0);
    });
  });
});
