import { createTestingPinia } from "@pinia/testing";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useWahlscheineTestDataFactory } from "@tests/utils/ergebnismeldung/common/wahlscheineTestDataFactory.ts";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useWahlscheineStore } from "@/stores/wahlscheineStore.ts";

const mockDefinitions = vi.hoisted(() => ({
  getWahlscheine: vi.fn(),
  postWahlscheine: vi.fn(),
}));

vi.mock(
  import("@/composables/ergebnismeldung/common/wahlscheineService.ts"),
  () => ({
    useWahlscheineService: () => ({
      getWahlscheine: mockDefinitions.getWahlscheine,
      postWahlscheine: mockDefinitions.postWahlscheine,
    }),
  })
);

const mockedNow = new Date();

describe("wahlscheineStore.ts", () => {
  let unitUnderTest: ReturnType<typeof useWahlscheineStore>;

  const { createWahlscheine } = useWahlscheineTestDataFactory();
  const { generateRandomString } = useCommonTestDataFactory();

  beforeEach(() => {
    const testPinia = createTestingPinia({
      stubActions: false,
      createSpy: vi.fn,
    });
    unitUnderTest = useWahlscheineStore(testPinia);
    vi.useFakeTimers({
      now: mockedNow,
    });
  });

  afterEach(() => {
    vi.clearAllMocks();
    vi.useRealTimers();
  });

  describe("loadWahlscheine", () => {
    it("should_addWahlscheineToState_when_serviceReturnsData", async () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);

      const existingWahlscheine = createWahlscheine();
      unitUnderTest.wahlscheine = [existingWahlscheine];

      const mockedServiceWahlscheine = createWahlscheine();
      mockDefinitions.getWahlscheine.mockResolvedValue(
        mockedServiceWahlscheine
      );

      await unitUnderTest.loadWahlscheine(wahlID, wahlbezirkID);

      expect(unitUnderTest.wahlscheine).toStrictEqual([
        existingWahlscheine,
        mockedServiceWahlscheine,
      ]);
    });

    it("should_addEmptyWahlscheine_when_serviceReturnsNoData", async () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);

      mockDefinitions.getWahlscheine.mockResolvedValue(null);

      await unitUnderTest.loadWahlscheine(wahlID, wahlbezirkID);

      expect(unitUnderTest.wahlscheine).toStrictEqual([
        {
          bezirkUndWahlID: { wahlID, wahlbezirkID },
          stimmabgabevermerke: null,
        },
      ]);
    });

    it.each([{ sendNotification: true }, { sendNotification: false }])(
      "should_callServiceWithSendNotification$sendNotification_when_notificationParameterIsUsed",
      async (argument) => {
        const wahlID = generateRandomString(10);
        const wahlbezirkID = generateRandomString(10);

        mockDefinitions.getWahlscheine.mockResolvedValue(null);

        await unitUnderTest.loadWahlscheine(
          wahlID,
          wahlbezirkID,
          argument.sendNotification
        );

        expect(mockDefinitions.getWahlscheine.mock.calls).toStrictEqual([
          [wahlID, wahlbezirkID, argument.sendNotification],
        ]);
      }
    );
  });

  describe("saveWahlscheine", () => {
    it("should_saveWahlscheine_when_called", async () => {
      const wahlscheine = createWahlscheine();
      const wahlscheine2 = createWahlscheine();

      mockDefinitions.postWahlscheine.mockReturnValue(Promise.resolve(null));

      unitUnderTest.wahlscheine = [wahlscheine, wahlscheine2];

      await unitUnderTest.saveWahlscheine();

      expect(mockDefinitions.postWahlscheine).toHaveBeenCalledWith(
        wahlscheine.bezirkUndWahlID.wahlID,
        wahlscheine.bezirkUndWahlID.wahlbezirkID,
        wahlscheine
      );
      expect(mockDefinitions.postWahlscheine).toHaveBeenCalledWith(
        wahlscheine2.bezirkUndWahlID.wahlID,
        wahlscheine2.bezirkUndWahlID.wahlbezirkID,
        wahlscheine2
      );
    });
  });

  describe("isWahlscheineSaving", () => {
    it("should_updateIsSaving_when_sendErgebnisseIsCalled", async () => {
      const timeout = 100;
      const wahlscheine = createWahlscheine();

      mockDefinitions.postWahlscheine.mockReturnValue(
        new Promise((resolve) => {
          setTimeout(() => {
            resolve({});
          }, timeout);
        })
      );

      expect(unitUnderTest.isWahlscheineSaving).toBe(false);

      unitUnderTest.wahlscheine = [wahlscheine];

      const promise = unitUnderTest.saveWahlscheine();

      expect(unitUnderTest.isWahlscheineSaving).toBe(true);

      vi.advanceTimersByTime(timeout);
      await promise;

      expect(unitUnderTest.isWahlscheineSaving).toBe(false);
    });

    it("should_updateIsSaving_when_sendErgebnisseFails", async () => {
      const timeout = 100;
      const wahlscheine = createWahlscheine();

      mockDefinitions.postWahlscheine.mockReturnValue(
        new Promise((resolve, reject) => {
          setTimeout(() => {
            reject("Mocked API Error");
          }, timeout);
        })
      );

      expect(unitUnderTest.isWahlscheineSaving).toBe(false);

      unitUnderTest.wahlscheine = [wahlscheine];

      const promise = unitUnderTest.saveWahlscheine();

      expect(unitUnderTest.isWahlscheineSaving).toBe(true);

      vi.advanceTimersByTime(timeout);
      await promise;

      expect(unitUnderTest.isWahlscheineSaving).toBe(false);
    });
  });
});
