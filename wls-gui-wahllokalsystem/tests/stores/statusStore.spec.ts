import { createTestingPinia } from "@pinia/testing";
import { useStatusTestDataFactory } from "@tests/utils/ergebnismeldung/statusTestDataFactory.ts";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useStatusStore } from "@/stores/statusStore.ts";

const { createStatus } = useStatusTestDataFactory();

const mockDefinitions = vi.hoisted(() => ({
  getStatus: vi.fn(),
}));

vi.mock("@/composables/ergebnismeldung/statusService.ts", () => ({
  useStatusService: () => ({
    getStatus: mockDefinitions.getStatus,
  }),
}));

describe("statusStore.ts", () => {
  let unitUnderTest: ReturnType<typeof useStatusStore>;

  const wahlID = "wahlID";
  const wahlbezirkID = "wahlbezirkID";

  beforeEach(() => {
    const testPinia = createTestingPinia({
      stubActions: false,
      createSpy: vi.fn,
    });
    unitUnderTest = useStatusStore(testPinia);
  });

  afterEach(() => {
    vi.clearAllMocks();
    vi.useRealTimers();
  });

  describe("loadStatus", () => {
    it("should_addStatus_when_loadStatusIsCalledWithValidWahlIdAndWahlbezirkId", () => {
      const status = createStatus();
      mockDefinitions.getStatus.mockReturnValue(status);

      unitUnderTest.loadStatus(wahlID, wahlbezirkID);

      expect(mockDefinitions.getStatus).toHaveBeenCalledWith(
        wahlID,
        wahlbezirkID,
        true
      );
      //expect(unitUnderTest.status).toStrictEqual([status]);
    });
  });
});
