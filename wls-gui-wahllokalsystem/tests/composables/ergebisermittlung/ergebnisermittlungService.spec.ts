import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useStimmzettelumschlaegeTestDataFactory } from "@tests/utils/ergebnisermittlung/StimmzettelumschlaegeTestDataFactory.ts";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useErgebnisermittlungService } from "@/composables/ergebnisermittlung/ergebnisermittlungService.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  postStimmzettelumschlaege: vi.fn(),
  addNotification: vi.fn(),
  toDto: vi.fn(),
  configurationConstructor: vi.fn().mockImplementation(() => ({})),
}));

vi.mock("@/api/wls-clients/generated-ergebnismeldung-api", () => ({
  StimmzettelumschlaegeControllerApi: vi.fn().mockImplementation(() => ({
    postStimmzettelumschlaege: mockDefinitions.postStimmzettelumschlaege,
  })),
  Configuration: mockDefinitions.configurationConstructor,
}));
vi.mock("@/composables/ergebnisermittlung/ergebnisermittlungMapper.ts", () => ({
  useErgebnisermittlungMapper: () => ({
    toDto: mockDefinitions.toDto,
  }),
}));

vi.mock("@/composables/userNotification/userNotificationService.ts", () => ({
  useUserNotificationService: () => ({
    addNotification: mockDefinitions.addNotification,
  }),
}));

const { saveStimmzettelumschlaege } = useErgebnisermittlungService();

const { createStimmzettelumschlaege } =
  useStimmzettelumschlaegeTestDataFactory();

const { generateRandomString } = useCommonTestDataFactory();

describe("ergebnisermittlungService", () => {
  beforeEach(() => {
    vi.resetAllMocks();
    vi.clearAllMocks();
  });

  describe("saveStimmzettelumschlaege", () => {
    it("should_notCallNotificationServiceAfterSuccess_when_sendNotificationParameterIsFalse", async () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const stimmzettelumschlaege = createStimmzettelumschlaege();

      await saveStimmzettelumschlaege(
        wahlID,
        wahlbezirkID,
        stimmzettelumschlaege,
        false
      );

      expect(mockDefinitions.addNotification.mock.calls.length).toStrictEqual(
        0
      );
      expect(mockDefinitions.toDto.mock.calls).toStrictEqual([
        [stimmzettelumschlaege, wahlID, wahlbezirkID],
      ]);
    });

    it("should_callNotificationServiceAfterSuccess_when_sendNotificationParameterIsTrue", async () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const stimmzettelumschlaege = createStimmzettelumschlaege();

      await saveStimmzettelumschlaege(
        wahlID,
        wahlbezirkID,
        stimmzettelumschlaege,
        true
      );

      expect(mockDefinitions.addNotification.mock.calls).toEqual([
        [expect.any(String), UserNotificationCategoryEnum.SUCCESS],
      ]);
      expect(mockDefinitions.toDto.mock.calls).toStrictEqual([
        [stimmzettelumschlaege, wahlID, wahlbezirkID],
      ]);
    });

    it("should_callNotificationServiceAfterFailure_when_sendNotificationParameterIsTrue", async () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const stimmzettelumschlaege = createStimmzettelumschlaege();

      mockDefinitions.postStimmzettelumschlaege.mockRejectedValue(
        new Error("mocked api call failed")
      );

      await expect(
        saveStimmzettelumschlaege(
          wahlID,
          wahlbezirkID,
          stimmzettelumschlaege,
          true
        )
      ).rejects.toThrow("mocked api call failed");

      expect(mockDefinitions.addNotification.mock.calls).toEqual([
        [expect.any(String), UserNotificationCategoryEnum.ERROR],
      ]);
      expect(mockDefinitions.toDto.mock.calls).toStrictEqual([
        [stimmzettelumschlaege, wahlID, wahlbezirkID],
      ]);
    });
  });
});
