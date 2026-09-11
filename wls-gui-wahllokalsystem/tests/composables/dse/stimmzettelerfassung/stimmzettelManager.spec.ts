import type { ManagedStimmzettel } from "@/composables/dse/stimmzettelerfassung/managedStimmzettel.ts";
import type { Stimmzettel } from "@/types/dse/stimmzettelerfassung/Stimmzettel.ts";
import type { Stimmzettel as PersistedStimmzettel } from "@/types/dse/persistedStimmzettel/Stimmzettel.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";
import type { Ref } from "vue";

import { useStimmzettelTestDataFactory } from "@tests/utils/dse/StimmzettelTestDataFactory.ts";
import { useWahlvorschlaegeTestDataFactory } from "@tests/utils/wahlvorschlaege/WahlvorschlaegeTestDataFactory.ts";
import { afterEach, describe, expect, it, vi } from "vitest";
import { computed } from "vue";

import { useStimmzettelManager } from "@/composables/dse/stimmzettelerfassung/stimmzettelManager.ts";
import { CommandExecutionError } from "@/types/dse/error/CommandExecutionError.ts";
import { UnsupportedCommandError } from "@/types/dse/error/UnsupportedCommandError.ts";
import { StimmzettelGueltigkeitEnum } from "@/types/dse/persistedStimmzettel/StimmzettelGueltigkeitEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  handlerOneCanHandle: vi.fn(),
  handlerTwoCanHandle: vi.fn(),
  handlerOneHandleOrThrow: vi.fn(),
  handlerTwoHandleOrThrow: vi.fn(),
  mangedStimmzettel: {
    kandidatAddEinzelstimmenOrThrow: vi.fn(),
    resetChangeHistory: vi.fn(),
  },
}));

vi.mock(
  "@/composables/dse/stimmzettelerfassung/command/commandHandlers.ts",
  () => {
    const handlers = [
      {
        canHandle: mockDefinitions.handlerOneCanHandle,
        handleOrThrow: mockDefinitions.handlerOneHandleOrThrow,
      },
      {
        canHandle: mockDefinitions.handlerTwoCanHandle,
        handleOrThrow: mockDefinitions.handlerTwoHandleOrThrow,
      },
    ];
    return { COMMAND_HANDLERS: handlers };
  }
);
vi.mock("@/composables/dse/stimmzettelerfassung/managedStimmzettel.ts", () => ({
  useBearbeitenDialogStimmzettelUtils: (
    stimmzettel: Ref<Stimmzettel>,
    wahlID: string
  ) => {
    return {
      kandidatAddEinzelstimmenOrThrow:
        mockDefinitions.mangedStimmzettel.kandidatAddEinzelstimmenOrThrow,
      changeHistory: {
        reset: mockDefinitions.mangedStimmzettel.resetChangeHistory,
      },
      stimmzettel,
      wahlID,
    };
  },
}));

const { prepareWahlvorschlag, prepareKandidat } =
  useWahlvorschlaegeTestDataFactory();
const {
  preparePersistedStimmzettel,
  preparePersistedStimmzettelWahlvorschlag,
  preparePersistedStimmzettelKandidat,
} = useStimmzettelTestDataFactory();

describe("stimmzettelManager.ts", () => {
  const dummyWahlvorschlag: Wahlvorschlag = {
    identifikator: "id",
    ordnungszahl: 1,
    kurzname: "KV",
    erhaeltStimmen: true,
    kandidaten: [],
  };

  afterEach(() => {
    vi.resetAllMocks();
    vi.clearAllMocks();
  });

  describe("parseCommandOrThrowError", () => {
    it("should_callFirstHandlerThatCanHandleCommand_when_commandIsSupported", () => {
      const command = "101+1";

      mockDefinitions.handlerOneCanHandle.mockReturnValue(true);
      mockDefinitions.handlerTwoCanHandle.mockReturnValue(false);

      const { parseCommandOrThrowError } = useStimmzettelManager(
        computed(() => 1),
        [dummyWahlvorschlag],
        "wahl-1",
        "team A"
      );

      parseCommandOrThrowError(command);

      expect(mockDefinitions.handlerOneCanHandle).toHaveBeenCalledTimes(1);
      expect(mockDefinitions.handlerOneCanHandle.mock.calls[0]).toStrictEqual([
        command,
      ]);

      expect(mockDefinitions.handlerOneHandleOrThrow).toHaveBeenCalledTimes(1);
      const callArgs = mockDefinitions.handlerOneHandleOrThrow.mock.calls[0];
      expect(callArgs[0]).toBe(command);
      expect(
        (callArgs[1] as ManagedStimmzettel).kandidatAddEinzelstimmenOrThrow
      ).toBeDefined();

      expect(mockDefinitions.handlerTwoCanHandle).not.toHaveBeenCalled();
      expect(mockDefinitions.handlerTwoHandleOrThrow).not.toHaveBeenCalled();
    });

    it("should_throwUnsupportedCommandError_when_noHandlerCanHandleCommand", () => {
      const command = "UNKNOWN";

      mockDefinitions.handlerOneCanHandle.mockReturnValue(false);
      mockDefinitions.handlerTwoCanHandle.mockReturnValue(false);

      const { parseCommandOrThrowError } = useStimmzettelManager(
        computed(() => 1),
        [dummyWahlvorschlag],
        "wahl-1",
        "team A"
      );

      expect(() => parseCommandOrThrowError(command)).toThrow(
        UnsupportedCommandError
      );

      expect(mockDefinitions.handlerOneHandleOrThrow).not.toHaveBeenCalled();
      expect(mockDefinitions.handlerTwoHandleOrThrow).not.toHaveBeenCalled();
    });

    it("should_rethrowCommandExecutionError_when_handlerThrowsCommandExecutionError", () => {
      const command = "101+1";
      const error = new CommandExecutionError(command);

      mockDefinitions.handlerOneCanHandle.mockReturnValue(true);
      mockDefinitions.handlerOneHandleOrThrow.mockImplementation(() => {
        throw error;
      });

      const { parseCommandOrThrowError } = useStimmzettelManager(
        computed(() => 1),
        [dummyWahlvorschlag],
        "wahl-1",
        "team A"
      );

      expect(() => parseCommandOrThrowError(command)).toThrow(
        CommandExecutionError
      );

      expect(mockDefinitions.handlerOneCanHandle).toHaveBeenCalledTimes(1);
      expect(mockDefinitions.handlerOneHandleOrThrow).toHaveBeenCalledTimes(1);
      expect(mockDefinitions.handlerTwoHandleOrThrow).not.toHaveBeenCalled();
    });
  });

  describe("startNewStimmzettel", () => {
    it("should_setNewStimmzettelAndResetHistory_when_called", () => {
      const unitUnderTest = useStimmzettelManager(
        computed(() => 1),
        [dummyWahlvorschlag],
        "wahl-1",
        "team A"
      );

      const stimmzettelBeforeStartNewOne =
        unitUnderTest.bearbeitenDialogStimmzettelUtils.stimmzettel.value;
      stimmzettelBeforeStartNewOne.invalideVotes = 20;

      unitUnderTest.startNewStimmzettel();

      expect(
        unitUnderTest.bearbeitenDialogStimmzettelUtils.stimmzettel.value
      ).not.toStrictEqual(stimmzettelBeforeStartNewOne);
      expect(
        unitUnderTest.bearbeitenDialogStimmzettelUtils.stimmzettel.value
      ).not.toBe(stimmzettelBeforeStartNewOne);
      expect(
        mockDefinitions.mangedStimmzettel.resetChangeHistory
      ).toHaveBeenCalledTimes(1);
    });
  });

  describe("setActiveStimmzettelWhenEditing", () => {
    it("should_updateManagedBearbeitenDialogStimmzettel_when_calledWithPersistedActiveStimmzettel", () => {
      const stimmzettelKennung = 42;
      const teamID = "team-1";
      const wahlvorschlagID = "wv-1";
      const kandidatID = "k-1";

      const wahlvorschlaege: Wahlvorschlag[] = [
        prepareWahlvorschlag()
          .identifikator(wahlvorschlagID)
          .kandidaten([
            prepareKandidat()
              .identifikator(kandidatID)
              .anzahlNennungen(1)
              .build(),
          ])
          .build(),
      ];

      const {
        bearbeitenDialogStimmzettelUtils,
        setActiveStimmzettelWhenEditing,
        stimmzettelBeforeEdit,
      } = useStimmzettelManager(
        computed(() => stimmzettelKennung),
        wahlvorschlaege,
        "wahl-1",
        teamID
      );

      const managedBefore = bearbeitenDialogStimmzettelUtils.stimmzettel.value;
      expect(managedBefore.invalideVotes).toBe(0);
      expect(managedBefore.wahlvorschlaege[0].selected).toBe(false);

      const managedBeforeKandidat =
        managedBefore.wahlvorschlaege[0].kandidaten[0];
      expect(managedBeforeKandidat.einzelstimmen).toBeNull();
      expect(managedBeforeKandidat.ungueltigeStimmen).toBeNull();
      expect(managedBeforeKandidat.reststimmen).toBeNull();
      expect(managedBeforeKandidat.durchgestrichen).toBe(false);

      const activeStimmzettelToBeSet: PersistedStimmzettel =
        preparePersistedStimmzettel()
          .stimmzettelkennung(stimmzettelKennung)
          .teamID(teamID)
          .invalideVotes(3)
          .wahlvorschlaege([
            preparePersistedStimmzettelWahlvorschlag()
              .wahlvorschlagID(wahlvorschlagID)
              .selected(true)
              .kandidaten([
                preparePersistedStimmzettelKandidat()
                  .kandidatId(kandidatID)
                  .nennung(1)
                  .votesByVoter(5)
                  .invalidVotes(1)
                  .votesByWahlvorschlag(2)
                  .isDiscarded(true)
                  .build(),
              ])
              .build(),
          ])
          .build();

      expect(stimmzettelBeforeEdit.value).toBeNull();
      setActiveStimmzettelWhenEditing(activeStimmzettelToBeSet);

      expect(stimmzettelBeforeEdit.value).toStrictEqual(
        activeStimmzettelToBeSet
      );
      const managedAfter = bearbeitenDialogStimmzettelUtils.stimmzettel.value;
      expect(managedAfter.invalideVotes).toBe(3);
      expect(managedAfter.wahlvorschlaege[0].selected).toBe(true);

      const managedAfterKandidat =
        managedAfter.wahlvorschlaege[0].kandidaten[0];
      expect(managedAfterKandidat.einzelstimmen).toBe(5);
      expect(managedAfterKandidat.ungueltigeStimmen).toBe(1);
      expect(managedAfterKandidat.reststimmen).toBe(2);
      expect(managedAfterKandidat.durchgestrichen).toBe(true);
    });
  });

  describe("hasStimmzettelBeenEdited", () => {
    const stimmzettelKennung = 101;
    const teamID = "team-x";
    const wahlvorschlagID = "wv-x";
    const kandidatID = "k-x";
    const wahlID = "wahl-1";

    const wahlvorschlaege: Wahlvorschlag[] = [
      prepareWahlvorschlag()
        .identifikator(wahlvorschlagID)
        .kandidaten([
          prepareKandidat()
            .identifikator(kandidatID)
            .anzahlNennungen(1)
            .build(),
        ])
        .build(),
    ];

    const persistedStimmzettelBeforeEdit: PersistedStimmzettel =
      preparePersistedStimmzettel()
        .stimmzettelkennung(stimmzettelKennung)
        .teamID(teamID)
        .gueltigkeit(StimmzettelGueltigkeitEnum.Valid)
        .invalideVotes(0)
        .wahlvorschlaege([])
        .wahlvorstandBeschlussvorschlag([])
        .systemBeschlussvorschlag([])
        .beschlussfassung(null)
        .build();

    it("should_returnTrue_whenStimmzettelToCompareHaveDifferentValues", () => {
      const {
        bearbeitenDialogStimmzettelUtils,
        setActiveStimmzettelWhenEditing,
        hasStimmzettelBeenEdited,
        stimmzettelBeforeEdit,
      } = useStimmzettelManager(
        computed(() => stimmzettelKennung),
        wahlvorschlaege,
        wahlID,
        teamID
      );

      expect(stimmzettelBeforeEdit.value).toBeNull();

      setActiveStimmzettelWhenEditing(persistedStimmzettelBeforeEdit);

      expect(stimmzettelBeforeEdit.value).toStrictEqual(
        persistedStimmzettelBeforeEdit
      );
      expect(hasStimmzettelBeenEdited.value).toBe(false);

      const managed = bearbeitenDialogStimmzettelUtils.stimmzettel.value;
      managed.wahlvorschlaege[0].kandidaten[0].einzelstimmen = 5;

      expect(hasStimmzettelBeenEdited.value).toBe(true);
    });

    it("should_returnFalse_whenStimmzettelToCompareHaveSameValues", () => {
      const {
        setActiveStimmzettelWhenEditing,
        hasStimmzettelBeenEdited,
        stimmzettelBeforeEdit,
      } = useStimmzettelManager(
        computed(() => stimmzettelKennung),
        wahlvorschlaege,
        wahlID,
        teamID
      );

      expect(stimmzettelBeforeEdit.value).toBeNull();

      setActiveStimmzettelWhenEditing(persistedStimmzettelBeforeEdit);

      expect(stimmzettelBeforeEdit.value).toStrictEqual(
        persistedStimmzettelBeforeEdit
      );
      expect(hasStimmzettelBeenEdited.value).toBe(false);
    });

    it("should_returnFalse_whenStimmzettelToCompareIsNull", () => {
      const { hasStimmzettelBeenEdited, stimmzettelBeforeEdit } =
        useStimmzettelManager(
          computed(() => stimmzettelKennung),
          wahlvorschlaege,
          wahlID,
          teamID
        );

      expect(stimmzettelBeforeEdit.value).toBeNull();
      expect(hasStimmzettelBeenEdited.value).toBe(false);
    });
  });
});
