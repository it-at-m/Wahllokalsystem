import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { createPinia, setActivePinia } from "pinia";
import { afterAll, beforeEach, describe, expect, it, vi } from "vitest";

import { useNachlieferungsbezirkeService } from "@/composables/basisdaten/nachlieferungsbezirkeService.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  addNotification: vi.fn(),
  configurationConstructor: vi.fn(),
  isNachlieferungsbezirk: vi.fn(),
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
    it("should_returnBool_when_apiCallSucceeded", async () => {
      const wahltagID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const mockedResult = false;

      mockDefinitions.isNachlieferungsbezirk.mockReturnValue({
        status: 200,
        data: mockedResult,
      });

      const result = await unitUnderTest.loadIsNachlieferungsbezirk(
        wahltagID,
        wahlbezirkID
      );

      expect(mockDefinitions.isNachlieferungsbezirk).toHaveBeenCalledWith(
        wahltagID,
        wahlbezirkID
      );
      expect(result).toStrictEqual(mockedResult);
    });

    it("should_addNotification_when_apiCallFailed", async () => {
      const wahltagID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);

      const apiCallError = new Error(
        "mocked api call is nachlieferungsbezirk failed"
      );
      mockDefinitions.isNachlieferungsbezirk.mockRejectedValue(apiCallError);

      await expect(
        unitUnderTest.loadIsNachlieferungsbezirk(wahltagID, wahlbezirkID)
      ).rejects.toThrow(apiCallError);

      expect(mockDefinitions.addNotification.mock.calls).toEqual([
        [expect.any(String), UserNotificationCategoryEnum.ERROR],
      ]);
    });
  });
});
