import type { Stimmzettel } from "@/types/dse/persistedStimmzettel/Stimmzettel.ts";
import type { StimmzettelerfassungTeamStatus } from "@/types/dse/StimmzettelerfassungTeamStatus.ts";

import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useStimmzettelerfassungTeamStatusTestDataFactory } from "@tests/utils/dse/StimmzettelerfassungTeamStatusTestDataFactory.ts";
import { useStimmzettelTestDataFactory } from "@tests/utils/dse/StimmzettelTestDataFactory.ts";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useStimmzettelErfassungViewUtils } from "@/composables/dse/stimmzettelErfassungViewUtils.ts";
import { StimmzettelerfassungTeamStatusEnum } from "@/types/dse/StimmzettelerfassungTeamStatusEnum.ts";

const mockDefinitions = await vi.hoisted(async () => {
  const activatedCallbacks: (() => Promise<void> | void)[] = [];

  return {
    loadErfassungTeamStatus: vi.fn(),
    postErfassungTeamStatus: vi.fn(),
    getStimmzettel: vi.fn(),
    saveStimmzettel: vi.fn(),
    getEmptyStimmzettelWithStimmzettelkennung: vi.fn(),
    logError: vi.fn(),
    onActivated: vi.fn(),

    registerActivated: (cb: () => Promise<void> | void) => {
      activatedCallbacks.length = 0;
      activatedCallbacks.push(cb);
    },
    runActivatedCallbacks: async () => {
      const cbs = activatedCallbacks.splice(0, activatedCallbacks.length);
      for (const cb of cbs) {
        await cb();
      }
    },
  };
});

vi.mock("vue", async (importOriginal) => {
  const mod = (await importOriginal()) as object;
  return {
    ...mod,
    onActivated: (cb: () => Promise<void> | void) =>
      mockDefinitions.registerActivated(cb),
  };
});

vi.mock(
  import("@/composables/dse/stimmzettelerfassungTeamStatusService.ts"),
  async (importOriginal) => {
    const mod = await importOriginal();
    return {
      useStimmzettelerfassungTeamStatusService: () => ({
        ...mod.useStimmzettelerfassungTeamStatusService(),
        loadErfassungTeamStatus: mockDefinitions.loadErfassungTeamStatus,
        postErfassungTeamStatus: mockDefinitions.postErfassungTeamStatus,
      }),
    };
  }
);

vi.mock(
  import("@/composables/dse/stimmzettelService.ts"),
  async (importOriginal) => {
    const mod = await importOriginal();
    return {
      useStimmzettelService: () => ({
        ...mod.useStimmzettelService(),
        getStimmzettel: mockDefinitions.getStimmzettel,
        saveStimmzettel: mockDefinitions.saveStimmzettel,
      }),
    };
  }
);

vi.mock(
  import("@/composables/dse/stimmzettelUtils.ts"),
  async (importOriginal) => {
    const mod = await importOriginal();
    return {
      useStimmzettelUtils: () => ({
        ...mod.useStimmzettelUtils(),
        getEmptyStimmzettelWithStimmzettelkennung:
          mockDefinitions.getEmptyStimmzettelWithStimmzettelkennung,
      }),
    };
  }
);

vi.mock(import("@/composables/common/logging.ts"), async (importOriginal) => {
  const mod = await importOriginal();
  return {
    useLogging: () => ({
      ...mod.useLogging("stimmzettelErfassungViewUtils"),
      logError: mockDefinitions.logError,
    }),
  };
});

describe("stimmzettelErfassungViewUtils.ts", () => {
  const { generateRandomString, generateRandomNumber } =
    useCommonTestDataFactory();
  const { prepareStimmzettel } = useStimmzettelTestDataFactory();
  const { createStimmzettelerfassungTeamStatusModel } =
    useStimmzettelerfassungTeamStatusTestDataFactory();

  const mockedWahlId = generateRandomString(10);
  const mockedWahlbezirkId = generateRandomString(10);
  const mockedTeamId = generateRandomString(10);

  let unitUnderTest: ReturnType<typeof useStimmzettelErfassungViewUtils>;

  beforeEach(() => {
    unitUnderTest = useStimmzettelErfassungViewUtils(
      mockedWahlId,
      mockedWahlbezirkId,
      mockedTeamId
    );
  });

  afterEach(() => {
    vi.resetAllMocks();
    vi.clearAllMocks();
  });

  describe("initial state", () => {
    it("should_haveInitialDialogVisibilityFalse_when_initialized", () => {
      expect(unitUnderTest.isKennungsDialogVisible.value).toBe(false);
      expect(unitUnderTest.isErfassungsDialogVisible.value).toBe(false);
    });

    it("should_haveInitialTeamStatusAndStatusLoading_when_initialized", () => {
      expect(unitUnderTest.teamStatus.value).toBeNull();
      expect(unitUnderTest.isStatusLoading.value).toBe(false);
    });

    it("should_haveInitialSavedStimmzettelEmpty_when_initialized", () => {
      expect(unitUnderTest.savedStimmzettel.value).toStrictEqual([]);
    });
  });

  describe("onActivated", () => {
    const NO_NOTIFICATION = false;

    it("should_loadTeamStatus_when_activatedLoadIsSuccessful", async () => {
      const spyOnIsStatusLoadingSetter = vi.spyOn(
        unitUnderTest.isStatusLoading,
        "value",
        "set"
      );

      const mockedRepsonse = createStimmzettelerfassungTeamStatusModel();
      mockDefinitions.loadErfassungTeamStatus.mockResolvedValueOnce(
        mockedRepsonse
      );

      expect(unitUnderTest.isStatusLoading.value).toStrictEqual(false);
      expect(unitUnderTest.teamStatus.value).toStrictEqual(null);

      await mockDefinitions.runActivatedCallbacks();

      expect(mockDefinitions.loadErfassungTeamStatus.mock.calls).toStrictEqual([
        [mockedWahlId, mockedWahlbezirkId, mockedTeamId, NO_NOTIFICATION],
      ]);
      expect(spyOnIsStatusLoadingSetter.mock.calls).toStrictEqual([
        [true],
        [false],
      ]);
      expect(unitUnderTest.teamStatus.value).toStrictEqual(mockedRepsonse);

      spyOnIsStatusLoadingSetter.mockRestore();
    });

    it("should_loadTeamStatusButKeepDefaultValue_when_activatedLoadIsFails", async () => {
      const spyOnIsStatusLoadingSetter = vi.spyOn(
        unitUnderTest.isStatusLoading,
        "value",
        "set"
      );

      mockDefinitions.loadErfassungTeamStatus.mockRejectedValue(
        new Error("mocked service error")
      );

      expect(unitUnderTest.isStatusLoading.value).toStrictEqual(false);
      expect(unitUnderTest.teamStatus.value).toStrictEqual(null);

      await mockDefinitions.runActivatedCallbacks();

      expect(mockDefinitions.loadErfassungTeamStatus.mock.calls).toStrictEqual([
        [mockedWahlId, mockedWahlbezirkId, mockedTeamId, NO_NOTIFICATION],
      ]);
      expect(spyOnIsStatusLoadingSetter.mock.calls).toStrictEqual([
        [true],
        [false],
      ]);
      expect(unitUnderTest.teamStatus.value).toStrictEqual(null);

      spyOnIsStatusLoadingSetter.mockRestore();
    });

    it("should_loadStimmzettel_when_activatedAndLoadingIsSuccessful", async () => {
      const spyOnIsStimmzettelLoadingSetter = vi.spyOn(
        unitUnderTest.isStimmzettelLoading,
        "value",
        "set"
      );

      const mockedLoadedStimmzettel = [
        prepareStimmzettel().build(),
        prepareStimmzettel().build(),
      ];
      mockDefinitions.getStimmzettel.mockReturnValue(mockedLoadedStimmzettel);

      expect(unitUnderTest.savedStimmzettel.value).not.toStrictEqual(
        mockedLoadedStimmzettel
      );

      await mockDefinitions.runActivatedCallbacks();

      expect(unitUnderTest.savedStimmzettel.value).toStrictEqual(
        mockedLoadedStimmzettel
      );
      expect(mockDefinitions.getStimmzettel.mock.calls).toStrictEqual([
        [mockedWahlId, mockedWahlbezirkId, mockedTeamId],
      ]);
      expect(spyOnIsStimmzettelLoadingSetter.mock.calls).toStrictEqual([
        [true],
        [false],
      ]);

      spyOnIsStimmzettelLoadingSetter.mockRestore();
    });

    it("should_loadStimmzettel_when_activatedAndLoadingFailed", async () => {
      const spyOnIsStimmzettelLoadingSetter = vi.spyOn(
        unitUnderTest.isStimmzettelLoading,
        "value",
        "set"
      );

      mockDefinitions.getStimmzettel.mockRejectedValue(
        new Error("mocked service error")
      );
      expect(unitUnderTest.savedStimmzettel.value).toStrictEqual([]);

      await mockDefinitions.runActivatedCallbacks();

      expect(unitUnderTest.savedStimmzettel.value).toStrictEqual([]);
      expect(mockDefinitions.getStimmzettel.mock.calls).toStrictEqual([
        [mockedWahlId, mockedWahlbezirkId, mockedTeamId],
      ]);
      expect(spyOnIsStimmzettelLoadingSetter.mock.calls).toStrictEqual([
        [true],
        [false],
      ]);

      spyOnIsStimmzettelLoadingSetter.mockRestore();
    });
  });

  describe("startNewEmptyStimmzettelWithStimmzettelkennung", () => {
    it("should_setActiveStimmzettel_when_calledWithKennung", () => {
      const mockedKennung = generateRandomNumber(3);
      const mockedEmptyStimmzettel: Stimmzettel = prepareStimmzettel()
        .stimmzettelkennung(mockedKennung)
        .build();

      mockDefinitions.getEmptyStimmzettelWithStimmzettelkennung.mockReturnValue(
        mockedEmptyStimmzettel
      );

      unitUnderTest.startNewEmptyStimmzettelWithStimmzettelkennung(
        mockedKennung
      );

      expect(
        mockDefinitions.getEmptyStimmzettelWithStimmzettelkennung
      ).toHaveBeenCalledWith(mockedKennung);
      expect(unitUnderTest.activeStimmzettel.value).toStrictEqual(
        mockedEmptyStimmzettel
      );
    });
  });

  describe("sendStatusInBearbeitung", () => {
    it("should_sendStatusInBearbeitungAndUpdateInternalState_when_sendingWasSuccessful", async () => {
      mockDefinitions.postErfassungTeamStatus.mockResolvedValue(undefined);

      await unitUnderTest.sendStatusInBearbeitung();

      const expectedStatusToSend =
        StimmzettelerfassungTeamStatusEnum.IN_BEARBEITUNG;
      expect(mockDefinitions.postErfassungTeamStatus).toHaveBeenCalledWith(
        mockedWahlId,
        mockedWahlbezirkId,
        mockedTeamId,
        { status: expectedStatusToSend },
        false
      );

      const mockedExpectedTeamStatusModel: StimmzettelerfassungTeamStatus =
        createStimmzettelerfassungTeamStatusModel(expectedStatusToSend);
      expect(unitUnderTest.teamStatus.value).toStrictEqual(
        mockedExpectedTeamStatusModel
      );
    });

    it("should_throwErrorAndNotUpdateTeamStatus_when_serviceCallFailed", async () => {
      const mockedPostApiError = new Error("mocked post error");
      mockDefinitions.postErfassungTeamStatus.mockRejectedValue(
        mockedPostApiError
      );

      await expect(
        unitUnderTest.sendStatusInBearbeitung()
      ).rejects.toThrowError(mockedPostApiError);

      const expectedStatusToSend =
        StimmzettelerfassungTeamStatusEnum.IN_BEARBEITUNG;
      expect(mockDefinitions.postErfassungTeamStatus).toHaveBeenCalledWith(
        mockedWahlId,
        mockedWahlbezirkId,
        mockedTeamId,
        { status: expectedStatusToSend },
        false
      );
      expect(unitUnderTest.teamStatus.value).toBeNull();
    });
  });

  describe("sendStatusUnterbrochen", () => {
    it("should_sendStatusUnterbrochenAndUpdateInternalState_when_sendingWasSuccessful", async () => {
      mockDefinitions.postErfassungTeamStatus.mockResolvedValue(undefined);

      await unitUnderTest.sendStatusUnterbrochen();

      const expectedStatusToSend =
        StimmzettelerfassungTeamStatusEnum.UNTERBROCHEN;
      expect(mockDefinitions.postErfassungTeamStatus).toHaveBeenCalledWith(
        mockedWahlId,
        mockedWahlbezirkId,
        mockedTeamId,
        { status: expectedStatusToSend },
        false
      );

      const mockedExpectedTeamStatusModel: StimmzettelerfassungTeamStatus =
        createStimmzettelerfassungTeamStatusModel(expectedStatusToSend);
      expect(unitUnderTest.teamStatus.value).toStrictEqual(
        mockedExpectedTeamStatusModel
      );
    });

    it("should_throwErrorAndNotUpdateTeamStatus_when_serviceCallFailed", async () => {
      const mockedPostApiError = new Error("mocked post error");
      mockDefinitions.postErfassungTeamStatus.mockRejectedValue(
        mockedPostApiError
      );

      await expect(unitUnderTest.sendStatusUnterbrochen()).rejects.toThrowError(
        mockedPostApiError
      );

      const expectedStatusToSend =
        StimmzettelerfassungTeamStatusEnum.UNTERBROCHEN;
      expect(mockDefinitions.postErfassungTeamStatus).toHaveBeenCalledWith(
        mockedWahlId,
        mockedWahlbezirkId,
        mockedTeamId,
        { status: expectedStatusToSend },
        false
      );
      expect(unitUnderTest.teamStatus.value).toBeNull();
    });
  });

  describe("saveNewStimmzettel", () => {
    it("should_appendStimmzettelAndPersist_when_initialCollectionIsEmpty", async () => {
      const mockedNewStimmzettel: Stimmzettel = prepareStimmzettel().build();

      mockDefinitions.saveStimmzettel.mockResolvedValue(undefined);

      await unitUnderTest.saveNewStimmzettel(mockedNewStimmzettel);

      expect(mockDefinitions.saveStimmzettel).toHaveBeenCalledWith(
        mockedWahlId,
        mockedWahlbezirkId,
        mockedTeamId,
        [mockedNewStimmzettel]
      );
      expect(unitUnderTest.savedStimmzettel.value).toStrictEqual([
        mockedNewStimmzettel,
      ]);
    });

    it("should_appendStimmzettelToExistingCollectionAndPersist_when_collectionAlreadyContainsItems", async () => {
      const mockedExistingStimmzettel: Stimmzettel =
        prepareStimmzettel().build();
      const mockedNewStimmzettel: Stimmzettel = prepareStimmzettel().build();

      mockDefinitions.saveStimmzettel.mockResolvedValue(undefined);

      await unitUnderTest.saveNewStimmzettel(mockedExistingStimmzettel);
      await unitUnderTest.saveNewStimmzettel(mockedNewStimmzettel);

      const mockedLastSaveCall =
        mockDefinitions.saveStimmzettel.mock.calls.at(-1) ?? [];
      const mockedSavedCollection = mockedLastSaveCall[3] as Stimmzettel[];

      expect(mockedSavedCollection).toStrictEqual([
        mockedExistingStimmzettel,
        mockedNewStimmzettel,
      ]);
      expect(unitUnderTest.savedStimmzettel.value).toStrictEqual([
        mockedExistingStimmzettel,
        mockedNewStimmzettel,
      ]);
    });
  });
});
