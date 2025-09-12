import { createTestingPinia } from "@pinia/testing";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useWahlscheineTestDataFactory } from "@tests/utils/ergebnismeldung/wahlscheineTestDataFactory.ts";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useWahlscheineStore } from "@/stores/wahlscheineStore.ts";

const mockDefinitions = vi.hoisted(() => ({
  getWahlscheine: vi.fn(),
  postWahlscheine: vi.fn(),
}));

vi.mock("@/composables/ergebnismeldung/wahlscheineService.ts", () => ({
  useStimmabgabevermerkeService: () => ({
    getWahlscheine: mockDefinitions.getWahlscheine,
    postWahlscheine: mockDefinitions.postWahlscheine,
  }),
}));

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
  });

  describe("loadWahlscheine", () => {
    it("should_addWahlscheineeToState_when_serviceReturnsData", async () => {
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
  });

  describe("saveWahlscheine", () => {
    it("should_saveWahlscheine_when_called", async () => {
      const wahlscheine = createWahlscheine();

      mockDefinitions.postWahlscheine.mockReturnValue(Promise.resolve(null));

      unitUnderTest.wahlscheine = [wahlscheine];

      await unitUnderTest.saveWahlscheine();

      expect(mockDefinitions.postWahlscheine).toHaveBeenCalledWith(
        wahlscheine.bezirkUndWahlID.wahlID,
        wahlscheine.bezirkUndWahlID.wahlbezirkID,
        wahlscheine
      );
    });
  });
});
