import type { ExtendedWahlMetaData } from "@/composables/tasks/ExtendedWahlMetaData.ts";
import type { TaskFactoryContext } from "@/composables/tasks/TaskFactoryContext.ts";

import { useTasksTestDataFactory } from "@tests/utils/tasks/TasksTestDataFactory.ts";
import { createPinia, setActivePinia } from "pinia";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useErgebnisseTaskFactory } from "@/composables/tasks/taskFactories/ergebnisseTaskFactory.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/common/StapelArtEnum.ts";
import { WahlWahlartEnum } from "@/types/wahl/WahlWahlartEnum.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  loadErgebnisseByStapelArt: vi.fn(),
}));

vi.mock("@/stores/ergebnismeldungStore.ts", () => ({
  useErgebnismeldungStore: vi.fn().mockImplementation(() => ({
    loadErgebnisseByStapelArt: mockDefinitions.loadErgebnisseByStapelArt,
  })),
}));

describe("ergebnisseTaskFactory.ts", () => {
  const { prepareTaskFactoryContext, prepareExtendedWahlMetaData } =
    useTasksTestDataFactory();
  const { createTasks } = useErgebnisseTaskFactory();
  const stapelPrefix = "Stapel - ";

  beforeEach(() => {
    setActivePinia(createPinia());
  });

  afterEach(() => {
    vi.clearAllMocks();
    vi.resetAllMocks();
  });

  describe("createTasks", () => {
    describe("userHasRoleSchriftfuehrung", () => {
      it.each([
        prepareExtendedWahlMetaData().wahlArt(WahlWahlartEnum.Obw).build(),
        prepareExtendedWahlMetaData().wahlArt(WahlWahlartEnum.Srw).build(),
        prepareExtendedWahlMetaData().wahlArt(WahlWahlartEnum.Baw).build(),
      ])("should_returnTaskListFor%s_when_called", (extendedWahlMetaData) => {
        const taskFactoryContext: TaskFactoryContext =
          prepareTaskFactoryContext()
            .extendedWahlMetaData([extendedWahlMetaData])
            .isSchriftfuehrung(true)
            .build();

        const stapelForWahl = Object.values(StapelArtEnum).filter((value) =>
          value.includes(extendedWahlMetaData.wahlArt)
        );

        const expectedTaskNames: string[] = [];

        stapelForWahl.forEach((stapel) => {
          expectedTaskNames.push(
            stapelPrefix + stapel + " für " + extendedWahlMetaData.wahlName
          );
        });

        const result = createTasks(taskFactoryContext);
        const resultNames = result.map((task) => task.name);

        expect(result.length).toStrictEqual(stapelForWahl.length);
        expectedTaskNames.forEach((name) => {
          expect(resultNames).toContain(name);
        });
      });

      it.each([
        prepareExtendedWahlMetaData().wahlArt(WahlWahlartEnum.Obw).build(),
        prepareExtendedWahlMetaData().wahlArt(WahlWahlartEnum.Srw).build(),
        prepareExtendedWahlMetaData().wahlArt(WahlWahlartEnum.Baw).build(),
      ])(
        "should_returnTasksWithExpectedCallbacks_when_calledFor%s",
        async (extendedWahlMetaData) => {
          const taskFactoryContext: TaskFactoryContext =
            prepareTaskFactoryContext()
              .extendedWahlMetaData([extendedWahlMetaData])
              .isSchriftfuehrung(true)
              .build();

          const stapelForWahl = Object.values(StapelArtEnum).filter((value) =>
            value.includes(extendedWahlMetaData.wahlArt)
          );

          mockDefinitions.loadErgebnisseByStapelArt.mockReturnValue(
            Promise.resolve()
          );

          const result = createTasks(taskFactoryContext);
          await Promise.all(result.map((task) => task.callback()));

          expect(result.length).toStrictEqual(stapelForWahl.length);
          expect(
            mockDefinitions.loadErgebnisseByStapelArt
          ).toHaveBeenCalledTimes(stapelForWahl.length);
          stapelForWahl.forEach((stapel) => {
            expect(
              mockDefinitions.loadErgebnisseByStapelArt
            ).toHaveBeenCalledWith(extendedWahlMetaData.wahlID, stapel, false);
          });
        }
      );

      it("should_returnEmptyTaskListForWahlenNotInObwSrwBaw_when_called", () => {
        const extendedWahlMetaDataForAllWahlenExceptObwSrwBaw: ExtendedWahlMetaData[] =
          [];
        Object.values(WahlWahlartEnum).forEach((value) => {
          if (value !== "OBW" && value !== "BAW" && value !== "SRW") {
            extendedWahlMetaDataForAllWahlenExceptObwSrwBaw.push(
              prepareExtendedWahlMetaData().wahlArt(value).build()
            );
          }
        });

        const taskFactoryContext: TaskFactoryContext =
          prepareTaskFactoryContext()
            .isSchriftfuehrung(true)
            .extendedWahlMetaData(
              extendedWahlMetaDataForAllWahlenExceptObwSrwBaw
            )
            .build();

        const result = createTasks(taskFactoryContext);

        expect(result.length).toStrictEqual(0);
      });
    });

    it("should_skipStapelartObwBLeer_when_usersWahlbezirksArtIsUwb", async () => {
      const wahlMetaDataOBW = prepareExtendedWahlMetaData()
        .wahlArt(WahlWahlartEnum.Obw)
        .build();
      const taskFactoryContext = prepareTaskFactoryContext()
        .extendedWahlMetaData([wahlMetaDataOBW])
        .isSchriftfuehrung(true)
        .build();
      taskFactoryContext.wahlbezirkArt = WahlbezirksArtEnum.UWB;

      const result = createTasks(taskFactoryContext);

      const generatedTaskListNames = result.map((t) => t.name);
      expect(
        generatedTaskListNames.some((name) => name.includes("OBW_B_LEER"))
      ).toBe(false);
    });
  });

  describe("userHasNotRoleSchriftfuehrung", () => {
    it("should_returnEmptyTaskList_when_userHasNotRoleSchriftfuehrungEvenWahlartenAreGiven", () => {
      const taskFactoryContext = prepareTaskFactoryContext()
        .isSchriftfuehrung(false)
        .extendedWahlMetaData([
          prepareExtendedWahlMetaData().wahlArt(WahlWahlartEnum.Btw).build(),
        ])
        .build();

      const result = createTasks(taskFactoryContext);
      expect(result.length).toStrictEqual(0);
    });
  });
});
