import type { Stimmzettel } from "@/types/dse/persistedStimmzettel/Stimmzettel.ts";

import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useStimmzettelTestDataFactory } from "@tests/utils/dse/StimmzettelTestDataFactory.ts";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useStimmzettelState } from "@/composables/dse/stimmzettelerfassung/stimmzettelState.ts";

const mockDefinitions = await vi.hoisted(async () => {
  const activatedCallbacks: (() => Promise<void> | void)[] = [];

  return {
    getStimmzettel: vi.fn(),
    saveStimmzettel: vi.fn(),
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
  import("@/composables/dse/stimmzettelerfassung/stimmzettelService.ts"),
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

describe("stimmzettelState", () => {
  const { generateRandomString } = useCommonTestDataFactory();
  const { preparePersistedStimmzettel } = useStimmzettelTestDataFactory();

  const mockedWahlId = generateRandomString(10);
  const mockedWahlbezirkId = generateRandomString(10);
  const mockedTeamId = generateRandomString(10);

  let unitUnderTest: ReturnType<typeof useStimmzettelState>;

  beforeEach(() => {
    unitUnderTest = useStimmzettelState(
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
    it("should_haveInitialSavedStimmzettelEmpty_when_initialized", () => {
      expect(unitUnderTest.savedStimmzettel.value).toStrictEqual([]);
    });

    it("should_loadStimmzettel_when_activatedAndLoadingIsSuccessful", async () => {
      const spyOnIsStimmzettelLoadingSetter = vi.spyOn(
        unitUnderTest.isStimmzettelLoading,
        "value",
        "set"
      );

      const mockedLoadedStimmzettel = [
        preparePersistedStimmzettel().build(),
        preparePersistedStimmzettel().build(),
      ];
      mockDefinitions.getStimmzettel.mockReturnValue(mockedLoadedStimmzettel);

      expect(unitUnderTest.savedStimmzettel.value).not.toStrictEqual(
        mockedLoadedStimmzettel
      );
      expect(unitUnderTest.hasStimmzettel.value).toStrictEqual(false);

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
      expect(unitUnderTest.hasStimmzettel.value).toStrictEqual(true);

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

      await expect(
        mockDefinitions.runActivatedCallbacks()
      ).rejects.toThrowError("mocked service error");

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

  describe("saveNewStimmzettel", () => {
    it("should_appendStimmzettelAndPersist_when_initialCollectionIsEmpty", async () => {
      const mockedNewStimmzettel: Stimmzettel =
        preparePersistedStimmzettel().build();

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
        preparePersistedStimmzettel().build();
      const mockedNewStimmzettel: Stimmzettel =
        preparePersistedStimmzettel().build();

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
