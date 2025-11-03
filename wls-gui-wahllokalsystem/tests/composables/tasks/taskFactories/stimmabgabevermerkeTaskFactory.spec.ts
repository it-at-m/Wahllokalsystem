import type { TaskFactoryContext } from "@/composables/tasks/TaskFactoryContext.ts";
import type { Task } from "@/types/tasks/Task.ts";

import { useTasksTestDataFactory } from "@tests/utils/tasks/TasksTestDataFactory.ts";
import { createPinia, setActivePinia } from "pinia";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useStimmabgabevermerkeTaskFactory } from "@/composables/tasks/taskFactories/stimmabgabevermerkeTaskFactory.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  loadStimmabgabevermerke: vi.fn(),
}));

vi.mock("@/stores/stimmabgabevermerkeStore.ts", () => ({
  useStimmabgabevermerkeStore: vi.fn().mockImplementation(() => ({
    loadStimmabgabevermerke: mockDefinitions.loadStimmabgabevermerke,
  })),
}));
describe("stimmabgabevermerkeFactory.ts", () => {
  const { prepareTaskFactoryContext, createExtendedWahlMetaData } =
    useTasksTestDataFactory();
  const { createTasks } = useStimmabgabevermerkeTaskFactory();

  beforeEach(() => {
    setActivePinia(createPinia());
  });

  describe("createTasks", () => {
    it("should_returnTaskListWithTwoElements_when_calledWithTwoWahldataElements", () => {
      const extendedWahlMetaDataOne = createExtendedWahlMetaData();
      const extendedWahlMetaDataTwo = createExtendedWahlMetaData();
      const taskFactoryContext: TaskFactoryContext = prepareTaskFactoryContext()
        .extendedWahlMetaData([
          extendedWahlMetaDataOne,
          extendedWahlMetaDataTwo,
        ])
        .wahlbezirkArt(WahlbezirksArtEnum.UWB)
        .build();

      const result = createTasks(taskFactoryContext);

      expect(result.length).toStrictEqual(2);
      const expectedNames = [
        `Stimmabgabevermerke-${extendedWahlMetaDataOne.wahlArt}-WVZ-${extendedWahlMetaDataOne.waehlerverzeichnisNummer}-${extendedWahlMetaDataOne.wahlnummer}`,
        `Stimmabgabevermerke-${extendedWahlMetaDataTwo.wahlArt}-WVZ-${extendedWahlMetaDataTwo.waehlerverzeichnisNummer}-${extendedWahlMetaDataTwo.wahlnummer}`,
      ];
      const resultNames = result.map((task: Task) => task.name);
      expectedNames.forEach((name) => {
        expect(resultNames).toContain(name);
      });
    });

    it("should_returnTaskListWithOneElement_when_calledWithOneWahldataElement", () => {
      const extendedWahlMetaDataOne = createExtendedWahlMetaData();
      const taskFactoryContext: TaskFactoryContext = prepareTaskFactoryContext()
        .extendedWahlMetaData([extendedWahlMetaDataOne])
        .wahlbezirkArt(WahlbezirksArtEnum.UWB)
        .build();

      const result = createTasks(taskFactoryContext);

      expect(result.length).toStrictEqual(1);
      expect(result[0]?.name).toStrictEqual(
        `Stimmabgabevermerke-${extendedWahlMetaDataOne.wahlArt}-WVZ-${extendedWahlMetaDataOne.waehlerverzeichnisNummer}-${extendedWahlMetaDataOne.wahlnummer}`
      );
    });

    it("should_returnEmptyTaskList_when_calledWithBWB", () => {
      const extendedWahlMetaDataOne = createExtendedWahlMetaData();
      const taskFactoryContext: TaskFactoryContext = prepareTaskFactoryContext()
        .extendedWahlMetaData([extendedWahlMetaDataOne])
        .wahlbezirkArt(WahlbezirksArtEnum.BWB)
        .build();

      const result = createTasks(taskFactoryContext);

      expect(result.length).toStrictEqual(0);
    });

    it("should_returnTaskListWithTwoElementsContainingTheExpectedCallbacksWithCorrectInputs_when_calledWithTwoWahldataElement", () => {
      const extendedWahlMetaDataOne = createExtendedWahlMetaData();
      const extendedWahlMetaDataTwo = createExtendedWahlMetaData();
      const taskFactoryContext: TaskFactoryContext = prepareTaskFactoryContext()
        .extendedWahlMetaData([
          extendedWahlMetaDataOne,
          extendedWahlMetaDataTwo,
        ])
        .wahlbezirkArt(WahlbezirksArtEnum.UWB)
        .build();

      mockDefinitions.loadStimmabgabevermerke.mockReturnValue(
        Promise.resolve()
      );

      const result = createTasks(taskFactoryContext);

      expect(result.length).toStrictEqual(2);

      result.forEach((task) => task.callback());
      expect(mockDefinitions.loadStimmabgabevermerke).toHaveBeenCalledTimes(2);
      expect(mockDefinitions.loadStimmabgabevermerke).toHaveBeenCalledWith(
        extendedWahlMetaDataOne.wahlbezirkID,
        extendedWahlMetaDataOne.waehlerverzeichnisNummer,
        false
      );
      expect(mockDefinitions.loadStimmabgabevermerke).toHaveBeenCalledWith(
        extendedWahlMetaDataTwo.wahlbezirkID,
        extendedWahlMetaDataTwo.waehlerverzeichnisNummer,
        false
      );
    });
  });
});
