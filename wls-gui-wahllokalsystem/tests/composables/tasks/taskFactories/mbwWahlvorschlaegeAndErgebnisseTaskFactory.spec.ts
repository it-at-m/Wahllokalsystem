import { useErgebnisseTestDataFactory } from "@tests/utils/ergebnismeldung/common/ergebnisseTestDataFactory.ts";
import { useTasksTestDataFactory } from "@tests/utils/tasks/TasksTestDataFactory.ts";
import { useWahlvorschlaegeTestDataFactory } from "@tests/utils/wahlvorschlaege/WahlvorschlaegeTestDataFactory.ts";
import {
  afterAll,
  afterEach,
  beforeEach,
  describe,
  expect,
  it,
  vi,
} from "vitest";

import { useMBWWahlvorschlaegeAndErgebnisseTaskFactory } from "@/composables/tasks/taskFactories/mbwWahlvorschlaegeAndErgebnisseTaskFactory.ts";
import { getStapelForWahlart } from "@/types/ergebnismeldung/common/StapelArtEnum.ts";
import { MbwRoutesEnum } from "@/types/navigation/MbwRoutesEnum.ts";
import { WahlWahlartEnum } from "@/types/wahl/WahlWahlartEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  getErgebnisseByWahlIdAndStapelartOrUndefined: vi.fn(),
  getWahlvorschlaegeByWahlIDAndWahlbezirkID: vi.fn(),
  loadErgebnisseByStapelArt: vi.fn(),
  loadWahlvorschlaege: vi.fn(),
  setStepDone: vi.fn(),
}));

vi.mock("@/stores/ergebnismeldungStore.ts", () => ({
  useErgebnismeldungStore: () => ({
    getErgebnisseByWahlIdAndStapelartOrUndefined:
      mockDefinitions.getErgebnisseByWahlIdAndStapelartOrUndefined,
    loadErgebnisseByStapelArt: mockDefinitions.loadErgebnisseByStapelArt,
  }),
}));
vi.mock("@/stores/wahlvorschlaegeStore.ts", () => ({
  useWahlvorschlaegeStore: () => ({
    getWahlvorschlaegeByWahlIDAndWahlbezirkID:
      mockDefinitions.getWahlvorschlaegeByWahlIDAndWahlbezirkID,
    loadWahlvorschlaege: mockDefinitions.loadWahlvorschlaege,
  }),
}));
vi.mock("@/stores/workflowStore.ts", () => ({
  useWorkflowStore: () => ({
    setStepDone: mockDefinitions.setStepDone,
  }),
}));

const { prepareTaskFactoryContext, prepareExtendedWahlMetaData } =
  useTasksTestDataFactory();
const { prepareErgebnisse, createErgebnis } = useErgebnisseTestDataFactory();
const { prepareWahlvorschlaege, prepareWahlvorschlag, createKandidat } =
  useWahlvorschlaegeTestDataFactory();

describe("mbwWahlvorschlaegeAndErgebnisseTaskFactory.ts", () => {
  let unitUnderTest: ReturnType<
    typeof useMBWWahlvorschlaegeAndErgebnisseTaskFactory
  >;

  beforeEach(() => {
    unitUnderTest = useMBWWahlvorschlaegeAndErgebnisseTaskFactory();
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  afterAll(() => {
    vi.resetAllMocks();
  });

  describe("createTasks", () => {
    it("should_createTasksOnlyForMBW_when_contextWithMBWIsGiven", () => {
      const extendedWahlMetaData = Object.values(WahlWahlartEnum).map(
        (wahlArt) => prepareExtendedWahlMetaData().wahlArt(wahlArt).build()
      );
      const mbwMetaData = extendedWahlMetaData.find(
        (metaData) => metaData.wahlArt === WahlWahlartEnum.Mbw
      );
      const taskFactoryContext = prepareTaskFactoryContext()
        .extendedWahlMetaData(extendedWahlMetaData)
        .build();
      const result = unitUnderTest.createTasks(taskFactoryContext);

      expect(result.length).toStrictEqual(1);
      expect(result[0].name).toStrictEqual(
        `Wahlvorschläge und Ergebnisse - ${mbwMetaData?.wahlName}`
      );
    });

    it("should_createNoTasks_when_contextWithoutMBWIsGiven", () => {
      const extendedWahlMetaData = Object.values(WahlWahlartEnum)
        .filter((wahlArt) => wahlArt !== WahlWahlartEnum.Mbw)
        .map((wahlArt) =>
          prepareExtendedWahlMetaData().wahlArt(wahlArt).build()
        );
      const taskFactoryContext = prepareTaskFactoryContext()
        .extendedWahlMetaData(extendedWahlMetaData)
        .build();
      const result = unitUnderTest.createTasks(taskFactoryContext);

      expect(result.length).toStrictEqual(0);
    });

    it("should_createNoTasks_when_contextIsEmpty", () => {
      const taskFactoryContext = prepareTaskFactoryContext()
        .extendedWahlMetaData([])
        .build();
      const result = unitUnderTest.createTasks(taskFactoryContext);

      expect(result.length).toStrictEqual(0);
    });

    it("should_loadWahlvorschlaegeAndErgebnisse_when_mbwIsGiven", async () => {
      const mbwMetaData = prepareExtendedWahlMetaData()
        .wahlArt(WahlWahlartEnum.Mbw)
        .build();
      const taskToRun = unitUnderTest.createTasks(
        prepareTaskFactoryContext().extendedWahlMetaData([mbwMetaData]).build()
      )[0];

      mockDefinitions.loadErgebnisseByStapelArt.mockImplementation(() =>
        Promise.resolve()
      );
      mockDefinitions.loadWahlvorschlaege.mockImplementation(() =>
        Promise.resolve()
      );

      await taskToRun.callback();

      expect(mockDefinitions.loadWahlvorschlaege.mock.calls).toStrictEqual([
        [mbwMetaData.wahlID, mbwMetaData.wahlbezirkID],
      ]);

      const mbwStapel = getStapelForWahlart(mbwMetaData.wahlArt);
      mbwStapel.forEach((stapel) => {
        expect(mockDefinitions.loadErgebnisseByStapelArt).toHaveBeenCalledWith(
          mbwMetaData.wahlID,
          stapel,
          false
        );
      });
      expect(
        mockDefinitions.loadErgebnisseByStapelArt.mock.calls.length
      ).toStrictEqual(mbwStapel.length);
    });

    it("should_setStepDone_when_ergebnisseForAllKandidatenExist", async () => {
      const mbwMetaData = prepareExtendedWahlMetaData()
        .wahlArt(WahlWahlartEnum.Mbw)
        .build();
      const taskToRun = unitUnderTest.createTasks(
        prepareTaskFactoryContext().extendedWahlMetaData([mbwMetaData]).build()
      )[0];

      mockDefinitions.loadErgebnisseByStapelArt.mockImplementation(() =>
        Promise.resolve()
      );
      mockDefinitions.loadWahlvorschlaege.mockImplementation(() =>
        Promise.resolve()
      );

      const mockedErgebnisse = prepareErgebnisse()
        .ergebnisse([createErgebnis(), createErgebnis(), createErgebnis()])
        .build();
      mockDefinitions.getErgebnisseByWahlIdAndStapelartOrUndefined.mockReturnValue(
        mockedErgebnisse
      );

      const mockeWahlvorschlaege = prepareWahlvorschlaege()
        .wahlvorschlaege([
          prepareWahlvorschlag()
            .kandidaten([createKandidat(), createKandidat()])
            .build(),
          prepareWahlvorschlag().kandidaten([createKandidat()]).build(),
        ])
        .build();
      mockDefinitions.getWahlvorschlaegeByWahlIDAndWahlbezirkID.mockReturnValue(
        mockeWahlvorschlaege
      );

      await taskToRun.callback();

      expect(mockDefinitions.setStepDone.mock.calls).toStrictEqual([
        [
          mbwMetaData.wahlID,
          mbwMetaData.wahlbezirkID,
          MbwRoutesEnum.MBW_STAPEL_BC,
        ],
      ]);
    });

    it("should_notSetStepDone_when_ergebnisseNotForAllKandidatenExist", async () => {
      const mbwMetaData = prepareExtendedWahlMetaData()
        .wahlArt(WahlWahlartEnum.Mbw)
        .build();
      const taskToRun = unitUnderTest.createTasks(
        prepareTaskFactoryContext().extendedWahlMetaData([mbwMetaData]).build()
      )[0];

      mockDefinitions.loadErgebnisseByStapelArt.mockImplementation(() =>
        Promise.resolve()
      );
      mockDefinitions.loadWahlvorschlaege.mockImplementation(() =>
        Promise.resolve()
      );

      const mockedErgebnisse = prepareErgebnisse()
        .ergebnisse([createErgebnis(), createErgebnis(), createErgebnis()])
        .build();
      mockDefinitions.getErgebnisseByWahlIdAndStapelartOrUndefined.mockReturnValue(
        mockedErgebnisse
      );

      const mockeWahlvorschlaege = prepareWahlvorschlaege()
        .wahlvorschlaege([
          prepareWahlvorschlag().kandidaten([createKandidat()]).build(),
          prepareWahlvorschlag().kandidaten([createKandidat()]).build(),
        ])
        .build();
      mockDefinitions.getWahlvorschlaegeByWahlIDAndWahlbezirkID.mockReturnValue(
        mockeWahlvorschlaege
      );

      await taskToRun.callback();

      expect(mockDefinitions.setStepDone.mock.calls.length).toStrictEqual(0);
    });
  });
});
