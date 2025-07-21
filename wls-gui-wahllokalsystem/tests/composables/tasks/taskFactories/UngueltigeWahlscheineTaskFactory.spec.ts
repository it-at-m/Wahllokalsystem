import type { TaskFactoryContext } from "@/composables/tasks/TaskFactoryContext.ts";

import { useTasksTestDataFactory } from "@tests/utils/tasks/TasksTestDataFactory.ts";
import { createPinia, setActivePinia } from "pinia";
import { beforeEach, describe, expect, it } from "vitest";

import { useUngueltigeWahlscheineTaskFactory } from "@/composables/tasks/taskFactories/UngueltigeWahlscheineTaskFactory.ts";
import { WahlWahlartEnum } from "@/types/wahl/WahlWahlartEnum.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

describe("UngueltigeWahlscheineTaskFactory.ts", () => {
  const { prepareExtendedWahlMetaData, prepareTaskFactoryContext } =
    useTasksTestDataFactory();
  const { createTasks } = useUngueltigeWahlscheineTaskFactory();

  beforeEach(() => {
    setActivePinia(createPinia());
  });

  describe("createTasks", () => {
    it.each([
      WahlWahlartEnum.Btw,
      WahlWahlartEnum.Beb,
      WahlWahlartEnum.Euw,
      WahlWahlartEnum.Obw,
      WahlWahlartEnum.Srw,
      WahlWahlartEnum.Baw,
    ])(
      "should_returnTaskListWithOneElement_when_calledWithCorrectWahlArt(%s)AndCorrectWahlbezirkArt(UWB)",
      (input) => {
        const extendedWahlMetaData = prepareExtendedWahlMetaData()
          .wahlart(input)
          .build();
        const taskFactoryContext: TaskFactoryContext =
          prepareTaskFactoryContext()
            .extendedWahlMetaData([extendedWahlMetaData])
            .wahlbezirkArt(WahlbezirksArtEnum.UWB)
            .build();

        const result = createTasks(taskFactoryContext);

        expect(result.length).toStrictEqual(1);
      }
    );

    it.each([
      WahlWahlartEnum.Btw,
      WahlWahlartEnum.Beb,
      WahlWahlartEnum.Euw,
      WahlWahlartEnum.Obw,
      WahlWahlartEnum.Srw,
      WahlWahlartEnum.Baw,
    ])(
      "should_returnTaskListWithZeroElements_when_calledWithCorrectWahlArt(%s)AndWrongWahlbezirkArt(BWB)",
      (input) => {
        const extendedWahlMetaData = prepareExtendedWahlMetaData()
          .wahlart(input)
          .build();
        const taskFactoryContext: TaskFactoryContext =
          prepareTaskFactoryContext()
            .extendedWahlMetaData([extendedWahlMetaData])
            .wahlbezirkArt(WahlbezirksArtEnum.BWB)
            .build();

        const result = createTasks(taskFactoryContext);

        expect(result.length).toStrictEqual(0);
      }
    );

    it.each([
      WahlWahlartEnum.Bzw,
      WahlWahlartEnum.Ltw,
      WahlWahlartEnum.Mbw,
      WahlWahlartEnum.Svw,
      WahlWahlartEnum.Ve,
    ])(
      "should_returnTaskListWithZeroElements_when_calledWithWrongWahlArt(%s)AndCorrectWahlbezirkArt(UWB)",
      (input) => {
        const extendedWahlMetaData = prepareExtendedWahlMetaData()
          .wahlart(input)
          .build();
        const taskFactoryContext: TaskFactoryContext =
          prepareTaskFactoryContext()
            .extendedWahlMetaData([extendedWahlMetaData])
            .wahlbezirkArt(WahlbezirksArtEnum.UWB)
            .build();

        const result = createTasks(taskFactoryContext);

        expect(result.length).toStrictEqual(0);
      }
    );

    it.each([
      WahlWahlartEnum.Bzw,
      WahlWahlartEnum.Ltw,
      WahlWahlartEnum.Mbw,
      WahlWahlartEnum.Svw,
      WahlWahlartEnum.Ve,
    ])(
      "should_returnTaskListWithZeroElements_when_calledWithWrongWahlArt(%s)AndWrongWahlbezirkArt(BWB)",
      (input) => {
        const extendedWahlMetaData = prepareExtendedWahlMetaData()
          .wahlart(input)
          .build();
        const taskFactoryContext: TaskFactoryContext =
          prepareTaskFactoryContext()
            .extendedWahlMetaData([extendedWahlMetaData])
            .wahlbezirkArt(WahlbezirksArtEnum.BWB)
            .build();

        const result = createTasks(taskFactoryContext);

        expect(result.length).toStrictEqual(0);
      }
    );

    it("should_returnOneTask_when_calledWithMultipleWahlMetaDataObjectsWhichContainOnlyCorrectWahlArt", () => {
      const extendedWahlMetaDataOne = prepareExtendedWahlMetaData()
        .wahlart(WahlWahlartEnum.Beb)
        .build();
      const extendedWahlMetaDataTwo = prepareExtendedWahlMetaData()
        .wahlart(WahlWahlartEnum.Btw)
        .build();
      const extendedWahlMetaDataThree = prepareExtendedWahlMetaData()
        .wahlart(WahlWahlartEnum.Euw)
        .build();
      const extendedWahlMetaDataFour = prepareExtendedWahlMetaData()
        .wahlart(WahlWahlartEnum.Srw)
        .build();

      const taskFactoryContext: TaskFactoryContext = prepareTaskFactoryContext()
        .extendedWahlMetaData([
          extendedWahlMetaDataOne,
          extendedWahlMetaDataTwo,
          extendedWahlMetaDataThree,
          extendedWahlMetaDataFour,
        ])
        .wahlbezirkArt(WahlbezirksArtEnum.UWB)
        .build();

      const result = createTasks(taskFactoryContext);

      expect(result.length).toStrictEqual(1);
    });

    it("should_returnZeroTasks_when_calledWithMultipleWahlMetaDataObjectsWhichContainOnlyWrongWahlArt", () => {
      const extendedWahlMetaDataOne = prepareExtendedWahlMetaData()
        .wahlart(WahlWahlartEnum.Bzw)
        .build();
      const extendedWahlMetaDataTwo = prepareExtendedWahlMetaData()
        .wahlart(WahlWahlartEnum.Ve)
        .build();
      const extendedWahlMetaDataThree = prepareExtendedWahlMetaData()
        .wahlart(WahlWahlartEnum.Svw)
        .build();
      const extendedWahlMetaDataFour = prepareExtendedWahlMetaData()
        .wahlart(WahlWahlartEnum.Mbw)
        .build();

      const taskFactoryContext: TaskFactoryContext = prepareTaskFactoryContext()
        .extendedWahlMetaData([
          extendedWahlMetaDataOne,
          extendedWahlMetaDataTwo,
          extendedWahlMetaDataThree,
          extendedWahlMetaDataFour,
        ])
        .wahlbezirkArt(WahlbezirksArtEnum.UWB)
        .build();

      const result = createTasks(taskFactoryContext);

      expect(result.length).toStrictEqual(0);
    });

    it("should_returnZeroTasks_when_calledWithMultipleWahlMetaDataObjectsWhichContainWrongAndCorrectWahlArt", () => {
      const extendedWahlMetaDataOne = prepareExtendedWahlMetaData()
        .wahlart(WahlWahlartEnum.Btw)
        .build();
      const extendedWahlMetaDataTwo = prepareExtendedWahlMetaData()
        .wahlart(WahlWahlartEnum.Beb)
        .build();
      const extendedWahlMetaDataThree = prepareExtendedWahlMetaData()
        .wahlart(WahlWahlartEnum.Svw)
        .build();
      const extendedWahlMetaDataFour = prepareExtendedWahlMetaData()
        .wahlart(WahlWahlartEnum.Mbw)
        .build();

      const taskFactoryContext: TaskFactoryContext = prepareTaskFactoryContext()
        .extendedWahlMetaData([
          extendedWahlMetaDataOne,
          extendedWahlMetaDataTwo,
          extendedWahlMetaDataThree,
          extendedWahlMetaDataFour,
        ])
        .wahlbezirkArt(WahlbezirksArtEnum.UWB)
        .build();

      const result = createTasks(taskFactoryContext);

      expect(result.length).toStrictEqual(1);
    });
  });
});
