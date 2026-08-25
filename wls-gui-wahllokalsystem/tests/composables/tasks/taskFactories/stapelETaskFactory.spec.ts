import { useTasksTestDataFactory } from "@tests/utils/tasks/TasksTestDataFactory.ts";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useStapelETaskFactory } from "@/composables/tasks/taskFactories/stapelETaskFactory.ts";
import { WahlWahlartEnum } from "@/types/wahl/WahlWahlartEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  getBedenklicheStimmzettel: vi.fn(),
}));

vi.mock(
  import("@/composables/ergebnismeldung/MBW/bedenklicheStimmzettelService.ts"),
  () => ({
    useBedenklicheStimmzettelService: () => ({
      getBedenklicheStimmzettel: mockDefinitions.getBedenklicheStimmzettel,
      saveBedenklicheStimmzettel: vi.fn(),
    }),
  })
);

const { prepareTaskFactoryContext, prepareExtendedWahlMetaData } =
  useTasksTestDataFactory();

describe("stapelETaskFactory.ts", () => {
  let unitUnderTest: ReturnType<typeof useStapelETaskFactory>;

  beforeEach(() => {
    unitUnderTest = useStapelETaskFactory();
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  describe("createTasks", () => {
    describe("userHasRoleSchriftfuehrung", () => {
      it("should_createTasksOnlyForMBWs_when_mbwsAreGiven", () => {
        const mbwMetaData1 = prepareExtendedWahlMetaData()
          .wahlArt(WahlWahlartEnum.Mbw)
          .wahlName("MBW1")
          .build();
        const mbwMetaData2 = prepareExtendedWahlMetaData()
          .wahlArt(WahlWahlartEnum.Mbw)
          .wahlName("MBW2")
          .build();
        const context = prepareTaskFactoryContext()
          .isSchriftfuehrung(true)
          .extendedWahlMetaData([
            mbwMetaData1,
            prepareExtendedWahlMetaData().wahlArt(WahlWahlartEnum.Btw).build(),
            mbwMetaData2,
          ])
          .build();

        const tasks = unitUnderTest.createTasks(context);

        expect(tasks.length).toBe(2);
        expect(tasks[0].name).toBe(`Stapel E für ${mbwMetaData1.wahlName}`);
        expect(tasks[1].name).toBe(`Stapel E für ${mbwMetaData2.wahlName}`);

        tasks.forEach((task) => task.callback());
        expect(
          mockDefinitions.getBedenklicheStimmzettel.mock.calls
        ).toStrictEqual([
          [mbwMetaData1.wahlID, mbwMetaData1.wahlbezirkID, false],
          [mbwMetaData2.wahlID, mbwMetaData2.wahlbezirkID, false],
        ]);
      });

      it("should_returnEmptyArray_when_contextIsNoElections", () => {
        const context = prepareTaskFactoryContext()
          .isSchriftfuehrung(true)
          .extendedWahlMetaData([])
          .build();

        const tasks = unitUnderTest.createTasks(context);

        expect(tasks.length).toBe(0);
      });

      it("should_returnEmptyList_when_contextHasNoMbwElections", () => {
        const context = prepareTaskFactoryContext()
          .isSchriftfuehrung(true)
          .extendedWahlMetaData([
            prepareExtendedWahlMetaData().wahlArt(WahlWahlartEnum.Btw).build(),
            prepareExtendedWahlMetaData().wahlArt(WahlWahlartEnum.Ltw).build(),
          ])
          .build();

        const tasks = unitUnderTest.createTasks(context);

        expect(tasks.length).toBe(0);
      });
    });

    describe("userHasNotRoleSchriftfuehrung", () => {
      it("should_returnEmptyList_when_called", () => {
        const context = prepareTaskFactoryContext()
          .isSchriftfuehrung(false)
          .build();

        const result = unitUnderTest.createTasks(context);
        expect(result.length).toStrictEqual(0);
      });
    });
  });
});
