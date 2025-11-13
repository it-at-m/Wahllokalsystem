import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useWahlvorschlaegeTestDataFactory } from "@tests/utils/wahlvorschlaege/WahlvorschlaegeTestDataFactory.ts";
import { createPinia, setActivePinia } from "pinia";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useWahlvorschlaegeStore } from "@/stores/wahlvorschlaegeStore.ts";

const mockDefinitions = vi.hoisted(() => ({
  getWahlvorschlaege: vi.fn(),
}));

vi.mock("@/composables/wahlvorschlaege/wahlvorschlaegeService.ts", () => ({
  useWahlvorschlaegeService: () => ({
    getWahlvorschlaege: mockDefinitions.getWahlvorschlaege,
  }),
}));

const { generateRandomString } = useCommonTestDataFactory();
const { createWahlvorschlaege, prepareWahlvorschlaege, prepareWahlvorschlag } =
  useWahlvorschlaegeTestDataFactory();

describe("wahlvorschlaegeStore.ts", () => {
  let unitUnderTest: ReturnType<typeof useWahlvorschlaegeStore>;

  beforeEach(() => {
    setActivePinia(createPinia());
    unitUnderTest = useWahlvorschlaegeStore();
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  describe("getWahlvorschlaegeByWahlIDAndWahlbezirkID", () => {
    it("should_returnUndefined_when_wahlvorschlaegeAreEmpty", () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);

      unitUnderTest.wahlvorschlaege = [];

      const wahlvorschlaege =
        unitUnderTest.getWahlvorschlaegeByWahlIDAndWahlbezirkID(
          wahlID,
          wahlbezirkID
        );

      expect(wahlvorschlaege).toStrictEqual(undefined);
    });

    it("should_returnUndefined_when_wahlvorschlaegeHaveNoMatchingEntry", () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);

      unitUnderTest.wahlvorschlaege = [
        prepareWahlvorschlaege()
          .wahlbezirkID(generateRandomString(1))
          .wahlID(generateRandomString(1))
          .build(),
      ];

      const result = unitUnderTest.getWahlvorschlaegeByWahlIDAndWahlbezirkID(
        wahlID,
        wahlbezirkID
      );

      expect(result).toStrictEqual(undefined);
    });

    it("should_returnFirstMatchingWahlvorschlaege_when_wahlvorschlaegeHaveMatchingEntry", () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const wahlvorschlaegeToFind = prepareWahlvorschlaege()
        .wahlID(wahlID)
        .wahlbezirkID(wahlbezirkID)
        .build();

      unitUnderTest.wahlvorschlaege = [
        prepareWahlvorschlaege()
          .wahlID(generateRandomString(1))
          .wahlbezirkID(generateRandomString(1))
          .build(),
        wahlvorschlaegeToFind,
        prepareWahlvorschlaege()
          .wahlID(generateRandomString(1))
          .wahlbezirkID(generateRandomString(1))
          .build(),
        prepareWahlvorschlaege()
          .wahlID(wahlID)
          .wahlbezirkID(wahlbezirkID)
          .build(),
      ];

      const result = unitUnderTest.getWahlvorschlaegeByWahlIDAndWahlbezirkID(
        wahlID,
        wahlbezirkID
      );

      expect(result).toStrictEqual(wahlvorschlaegeToFind);
    });
  });

  describe("getWahlvorschlagOrUndefinedByWahlIDWahlbezirkIDAndWahlvorschlagID", () => {
    it("should_returnUndefined_when_wahlvorschlaegeAreEmpty", () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const wahlvorschlagID = generateRandomString(10);

      unitUnderTest.wahlvorschlaege = [];

      const wahlvorschlag =
        unitUnderTest.getWahlvorschlagOrUndefinedByWahlIDWahlbezirkIDAndWahlvorschlagID(
          wahlID,
          wahlbezirkID,
          wahlvorschlagID
        );

      expect(wahlvorschlag).toStrictEqual(undefined);
    });
    it("should_returnUndefined_when_wahlvorschlaegeHaveNoMatchingEntry", () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const wahlvorschlagID = generateRandomString(10);

      unitUnderTest.wahlvorschlaege = [
        prepareWahlvorschlaege()
          .wahlbezirkID(generateRandomString(1))
          .wahlID(generateRandomString(1))
          .wahlvorschlaege([
            prepareWahlvorschlag()
              .identifikator(wahlvorschlagID + "sth more")
              .build(),
          ])
          .build(),
      ];

      const wahlvorschlag =
        unitUnderTest.getWahlvorschlagOrUndefinedByWahlIDWahlbezirkIDAndWahlvorschlagID(
          wahlID,
          wahlbezirkID,
          wahlvorschlagID
        );

      expect(wahlvorschlag).toStrictEqual(undefined);
    });
    it("should_returnFirstMatchingWahlvorschlaege_when_wahlvorschlaegeHaveMatchingEntry", () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const wahlvorschlagID = generateRandomString(10);

      const wahlvorschlagToFind = prepareWahlvorschlag()
        .identifikator(wahlvorschlagID)
        .build();
      unitUnderTest.wahlvorschlaege = [
        prepareWahlvorschlaege()
          .wahlbezirkID(wahlbezirkID)
          .wahlID(wahlID)
          .wahlvorschlaege([
            prepareWahlvorschlag()
              .identifikator(wahlvorschlagID + "sth more")
              .build(),
            wahlvorschlagToFind,
            prepareWahlvorschlag().identifikator(wahlvorschlagID).build(),
          ])
          .build(),
      ];

      const wahlvorschlag =
        unitUnderTest.getWahlvorschlagOrUndefinedByWahlIDWahlbezirkIDAndWahlvorschlagID(
          wahlID,
          wahlbezirkID,
          wahlvorschlagID
        );

      expect(wahlvorschlag).toStrictEqual(wahlvorschlagToFind);
    });
  });

  describe("loadWahlvorschlaege", () => {
    it("should_loadWahlvorschlaege_when_called", async () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const mockedWahlvorschlaegeModel = createWahlvorschlaege();

      mockDefinitions.getWahlvorschlaege.mockResolvedValue(
        mockedWahlvorschlaegeModel
      );

      await unitUnderTest.loadWahlvorschlaege(wahlID, wahlbezirkID);

      expect(mockDefinitions.getWahlvorschlaege.mock.calls).toStrictEqual([
        [wahlID, wahlbezirkID],
      ]);
      expect(unitUnderTest.wahlvorschlaege.length).toBe(1);
    });

    it("should_throwError_when_calledServiceThrowsError", async () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);

      mockDefinitions.getWahlvorschlaege.mockRejectedValue(
        new Error("service call failed")
      );

      await expect(
        async () =>
          await unitUnderTest.loadWahlvorschlaege(wahlID, wahlbezirkID)
      ).rejects.toThrow();
    });

    it("should_returnWahlvorschlaegeSortedByOrdnungszahl_when_loaded", async () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);

      const mockedWahlvorschlaegeModel = prepareWahlvorschlaege()
        .wahlID(wahlID)
        .wahlbezirkID(wahlbezirkID)
        .wahlvorschlaege([
          prepareWahlvorschlag().ordnungszahl(4).build(),
          prepareWahlvorschlag().ordnungszahl(2).build(),
          prepareWahlvorschlag().ordnungszahl(7).build(),
        ])
        .build();

      mockDefinitions.getWahlvorschlaege.mockResolvedValue(
        mockedWahlvorschlaegeModel
      );

      await unitUnderTest.loadWahlvorschlaege(wahlID, wahlbezirkID);

      const sortedWahlvorschlaegeAfterLoading = Array.from(
        // eslint-disable-next-line  @typescript-eslint/no-non-null-assertion
        unitUnderTest.wahlvorschlaege[0]!.wahlvorschlaege
      );

      expect(mockDefinitions.getWahlvorschlaege.mock.calls).toStrictEqual([
        [wahlID, wahlbezirkID],
      ]);
      expect(unitUnderTest.wahlvorschlaege.length).toBe(1);
      expect(sortedWahlvorschlaegeAfterLoading[0]?.ordnungszahl).toBe(2);
      expect(sortedWahlvorschlaegeAfterLoading[1]?.ordnungszahl).toBe(4);
      expect(sortedWahlvorschlaegeAfterLoading[2]?.ordnungszahl).toBe(7);
    });
  });
});
