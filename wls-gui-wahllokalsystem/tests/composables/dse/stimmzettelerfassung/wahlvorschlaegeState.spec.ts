import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useWahlvorschlaegeTestDataFactory } from "@tests/utils/wahlvorschlaege/WahlvorschlaegeTestDataFactory.ts";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useWahlvorschlaegeState } from "@/composables/dse/stimmzettelerfassung/wahlvorschlaegeState.ts";

const mockDefinitions = await vi.hoisted(async () => {
  const activatedCallbacks: (() => Promise<void> | void)[] = [];

  return {
    loadWahlvorschlaege: vi.fn(),
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
  import("@/composables/wahlvorschlaege/wahlvorschlaegeService.ts"),
  () => ({
    useWahlvorschlaegeService: () => ({
      getWahlvorschlaege: mockDefinitions.loadWahlvorschlaege,
    }),
  })
);

describe("wahlvorschlaegeState", () => {
  const { generateRandomString } = useCommonTestDataFactory();
  const { prepareWahlvorschlaege, createWahlvorschlag } =
    useWahlvorschlaegeTestDataFactory();

  const mockedWahlId = generateRandomString(10);
  const mockedWahlbezirkId = generateRandomString(10);

  let unitUnderTest: ReturnType<typeof useWahlvorschlaegeState>;

  beforeEach(() => {
    unitUnderTest = useWahlvorschlaegeState(mockedWahlId, mockedWahlbezirkId);
  });

  afterEach(() => {
    vi.resetAllMocks();
    vi.clearAllMocks();
  });

  describe("initial state", () => {
    it("should_loadWahlvorschlaege_when_activatedAndLoadingIsSuccessful", async () => {
      const spyOnIsWahlvorschlaegeLoading = vi.spyOn(
        unitUnderTest.isWahlvorschlaegeLoading,
        "value",
        "set"
      );

      const mockedLoadedWahlvorschlaege = prepareWahlvorschlaege()
        .wahlvorschlaege([createWahlvorschlag(), createWahlvorschlag()])
        .build();
      mockDefinitions.loadWahlvorschlaege.mockReturnValue(
        mockedLoadedWahlvorschlaege
      );
      expect(unitUnderTest.wahlvorschlaege.value).toStrictEqual([]);

      await mockDefinitions.runActivatedCallbacks();

      expect(unitUnderTest.wahlvorschlaege.value).toStrictEqual(
        mockedLoadedWahlvorschlaege.wahlvorschlaege
      );
      expect(mockDefinitions.loadWahlvorschlaege.mock.calls).toStrictEqual([
        [mockedWahlId, mockedWahlbezirkId],
      ]);
      expect(spyOnIsWahlvorschlaegeLoading.mock.calls).toStrictEqual([
        [true],
        [false],
      ]);

      spyOnIsWahlvorschlaegeLoading.mockRestore();
    });

    it("should_loadWahlvorschlaege_when_activatedAndLoadingFailed", async () => {
      const spyOnIsWahlvorschlaegeLoading = vi.spyOn(
        unitUnderTest.isWahlvorschlaegeLoading,
        "value",
        "set"
      );

      mockDefinitions.loadWahlvorschlaege.mockRejectedValue(
        new Error("mocked service error")
      );
      expect(unitUnderTest.wahlvorschlaege.value).toStrictEqual([]);

      await expect(
        mockDefinitions.runActivatedCallbacks()
      ).rejects.toThrowError("mocked service error");

      expect(unitUnderTest.wahlvorschlaege.value).toStrictEqual([]);
      expect(mockDefinitions.loadWahlvorschlaege.mock.calls).toStrictEqual([
        [mockedWahlId, mockedWahlbezirkId],
      ]);
      expect(spyOnIsWahlvorschlaegeLoading.mock.calls).toStrictEqual([
        [true],
        [false],
      ]);

      spyOnIsWahlvorschlaegeLoading.mockRestore();
    });
  });
});
