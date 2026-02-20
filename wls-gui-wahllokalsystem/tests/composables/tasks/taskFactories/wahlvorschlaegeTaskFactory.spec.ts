import type { TaskFactoryContext } from "@/composables/tasks/TaskFactoryContext.ts";

import { useTasksTestDataFactory } from "@tests/utils/tasks/TasksTestDataFactory.ts";
import { createPinia, setActivePinia } from "pinia";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useWahlvorschlaegeTaskFactory } from "@/composables/tasks/taskFactories/wahlvorschlaegeTaskFactory.ts";
import { WahlWahlartEnum } from "@/types/wahl/WahlWahlartEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  loadWahlvorschlaege: vi.fn(),
}));

vi.mock("@/stores/wahlvorschlaegeStore.ts", () => ({
  useWahlvorschlaegeStore: vi.fn().mockImplementation(() => ({
    loadWahlvorschlaege: mockDefinitions.loadWahlvorschlaege,
  })),
}));

describe("wahlvorschlaegeTaskFactory.ts", () => {
  const {
    prepareTaskFactoryContext,
    createExtendedWahlMetaData,
    prepareExtendedWahlMetaData,
  } = useTasksTestDataFactory();
  const { createTasks } = useWahlvorschlaegeTaskFactory();
  const wahlvorschlaegeNamePrefix = "Wahlvorschläge - ";

  beforeEach(() => {
    setActivePinia(createPinia());
  });

  describe("createTasks", () => {
    it("should_returnTaskListForAllWahlenExceptVeAndBeb_when_called", () => {
      const extendedWahlMetaDataOBW = prepareExtendedWahlMetaData()
        .wahlArt(WahlWahlartEnum.Obw)
        .build();
      const extendedWahlMetaDataEUW = prepareExtendedWahlMetaData()
        .wahlArt(WahlWahlartEnum.Euw)
        .build();
      const extendedWahlMetaDataVE = prepareExtendedWahlMetaData()
        .wahlArt(WahlWahlartEnum.Ve)
        .build();
      const extendedWahlMetaDataBEB = prepareExtendedWahlMetaData()
        .wahlArt(WahlWahlartEnum.Beb)
        .build();
      const taskFactoryContext: TaskFactoryContext = prepareTaskFactoryContext()
        .extendedWahlMetaData([
          extendedWahlMetaDataOBW,
          extendedWahlMetaDataEUW,
          extendedWahlMetaDataVE,
          extendedWahlMetaDataBEB,
        ])
        .build();

      const expectedTaskNames = [
        wahlvorschlaegeNamePrefix + extendedWahlMetaDataOBW.wahlName,
        wahlvorschlaegeNamePrefix + extendedWahlMetaDataEUW.wahlName,
      ];

      const result = createTasks(taskFactoryContext);
      const resultNames = result.map((task) => task.name);

      expect(result.length).toStrictEqual(2);
      expectedTaskNames.forEach((name) => {
        expect(resultNames).toContain(name);
      });
    });

    it("should_returnTaskListWithOneElement_when_calledWithOneWahldataElement", () => {
      const extendedWahlMetaData = createExtendedWahlMetaData();
      const taskFactoryContext: TaskFactoryContext = prepareTaskFactoryContext()
        .extendedWahlMetaData([extendedWahlMetaData])
        .build();

      const result = createTasks(taskFactoryContext);

      expect(result.length).toStrictEqual(1);
      expect(result[0]?.name).toStrictEqual(
        wahlvorschlaegeNamePrefix + extendedWahlMetaData.wahlName
      );
    });

    it("should_returnTasksWithExpectedCallbacks_when_calledForObwAndEuw", () => {
      const extendedWahlMetaDataOBW = prepareExtendedWahlMetaData()
        .wahlArt(WahlWahlartEnum.Obw)
        .build();
      const extendedWahlMetaDataEUW = prepareExtendedWahlMetaData()
        .wahlArt(WahlWahlartEnum.Euw)
        .build();
      const taskFactoryContext: TaskFactoryContext = prepareTaskFactoryContext()
        .extendedWahlMetaData([
          extendedWahlMetaDataOBW,
          extendedWahlMetaDataEUW,
        ])
        .build();

      mockDefinitions.loadWahlvorschlaege.mockReturnValue(Promise.resolve());

      const result = createTasks(taskFactoryContext);

      expect(result.length).toStrictEqual(2);

      result.forEach((task) => task.callback());
      expect(mockDefinitions.loadWahlvorschlaege).toHaveBeenCalledTimes(2);
      expect(mockDefinitions.loadWahlvorschlaege).toHaveBeenCalledWith(
        extendedWahlMetaDataOBW.wahlID,
        extendedWahlMetaDataOBW.wahlbezirkID
      );
      expect(mockDefinitions.loadWahlvorschlaege).toHaveBeenCalledWith(
        extendedWahlMetaDataEUW.wahlID,
        extendedWahlMetaDataEUW.wahlbezirkID
      );
    });
  });
});
