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
const { createWahlvorschlaege } = useWahlvorschlaegeTestDataFactory();

describe("wahlvorschlaegeStore.ts", () => {
  let unitUnderTest: ReturnType<typeof useWahlvorschlaegeStore>;

  beforeEach(() => {
    setActivePinia(createPinia());
    unitUnderTest = useWahlvorschlaegeStore();
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  describe("loadWahlvorschlaege", () => {
    it("should_loadWahlvorschlaege_when_called", async () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const mockedWahlvorschlaegeModel = createWahlvorschlaege();

      mockDefinitions.getWahlvorschlaege.mockReturnValue(
        mockedWahlvorschlaegeModel
      );

      await unitUnderTest.loadWahlvorschlaege(wahlID, wahlbezirkID);

      expect(mockDefinitions.getWahlvorschlaege.mock.calls).toStrictEqual([
        [wahlID, wahlbezirkID],
      ]);
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
  });
});
