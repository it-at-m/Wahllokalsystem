import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { createPinia, setActivePinia } from "pinia";
import { afterAll, beforeEach, describe, expect, it, vi } from "vitest";

import { useNachlieferungsbezirkeService } from "@/composables/basisdaten/nachlieferungsbezirkeService.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  addNotification: vi.fn(),
  configurationConstructor: vi.fn(),
  isNachlieferungsbezirk: vi.fn(),
  setNachlieferungsbezirk: vi.fn(),
}));

vi.mock("@/api/wls-clients/generated-basisdaten-api", () => ({
  NachlieferungsbezirkeControllerApi: class {
    isNachlieferungsbezirk = mockDefinitions.isNachlieferungsbezirk;
  },
  Configuration: mockDefinitions.configurationConstructor,
}));
vi.mock(
  import("@/composables/userNotification/userNotificationService.ts"),
  () => ({
    useUserNotificationService: () => ({
      addNotification: mockDefinitions.addNotification,
    }),
  })
);
vi.mock("@/stores/userStore.ts", () => ({
  useUserStore: () => ({
    setNachlieferungsbezirk: mockDefinitions.setNachlieferungsbezirk,
  }),
}));

describe("nachlieferungsbezirkeService.ts", () => {
  const { generateRandomString } = useCommonTestDataFactory();

  let unitUnderTest: ReturnType<typeof useNachlieferungsbezirkeService>;

  beforeEach(() => {
    setActivePinia(createPinia());
    unitUnderTest = useNachlieferungsbezirkeService();
    globalThis.URL.createObjectURL = vi.fn();
    globalThis.URL.revokeObjectURL = vi.fn();
    vi.clearAllMocks();
  });

  afterAll(() => {
    vi.resetAllMocks();
  });

  describe("isNachlieferungsbezirk", () => {
    it("should_setIsNachlieferungsbezirk_when_apiCallSucceeded", async () => {
      const wahltagID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const mockedResult = false;

      mockDefinitions.isNachlieferungsbezirk.mockReturnValue({
        status: 200,
        data: mockedResult,
      });

      await unitUnderTest.isNachlieferungsbezirk(wahltagID, wahlbezirkID);

      expect(mockDefinitions.isNachlieferungsbezirk).toHaveBeenCalledWith(
        wahltagID,
        wahlbezirkID
      );
      expect(mockDefinitions.setNachlieferungsbezirk).toHaveBeenCalledWith(
        mockedResult
      );
    });

    it("should_addNotification_when_apiCallFailed", async () => {
      const wahltagID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);

      const apiCallError = new Error(
        "mocked api call is nachlieferungsbezirk failed"
      );
      mockDefinitions.isNachlieferungsbezirk.mockRejectedValue(apiCallError);

      await expect(
        unitUnderTest.isNachlieferungsbezirk(wahltagID, wahlbezirkID)
      ).rejects.toThrow(apiCallError);

      expect(mockDefinitions.addNotification.mock.calls).toEqual([
        [expect.any(String), UserNotificationCategoryEnum.ERROR],
      ]);
    });
  });
});
