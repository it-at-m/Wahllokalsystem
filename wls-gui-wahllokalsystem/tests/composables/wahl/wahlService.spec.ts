import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useWahlTestDataFactory } from "@tests/utils/wahl/WahlTestDataFactory.ts";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useWahlService } from "@/composables/wahl/wahlService.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const { createWahl, createWahlDTO } = useWahlTestDataFactory();

const mockDefinitions = vi.hoisted(() => ({
  getWahlen: vi.fn(),
  addNotification: vi.fn(),
  mapDtoToModel: vi.fn(),
  wahlDTOWahlartEnum: vi.fn(),
  configurationConstructor: vi.fn(),
}));

vi.mock("@/api/wls-clients/generated-basisdaten-api", () => ({
  WahlenControllerApi: class {
    getWahlen = mockDefinitions.getWahlen;
  },
  Configuration: mockDefinitions.configurationConstructor,
  WahlDTOWahlartEnum: mockDefinitions.wahlDTOWahlartEnum,
}));
vi.mock(import("@/composables/wahl/wahlMapper.ts"), () => ({
  useWahlMapper: () => ({
    toModel: mockDefinitions.mapDtoToModel,
  }),
}));

vi.mock(
  import("@/composables/userNotification/userNotificationService.ts"),
  () => ({
    useUserNotificationService: () => ({
      addNotification: mockDefinitions.addNotification,
    }),
  })
);

const { generateRandomString } = useCommonTestDataFactory();

describe("wahlService.ts", () => {
  const { getWahlen } = useWahlService();

  beforeEach(() => {
    vi.resetAllMocks();
    vi.clearAllMocks();
  });

  describe("getWahlen", () => {
    it("should_returnWahl_when_wahltagIDIsGiven", async () => {
      const wahltagID = generateRandomString(10);
      mockDefinitions.getWahlen.mockReturnValue(
        Promise.resolve({ status: 200, data: [createWahlDTO()] })
      );
      const mockedMappedWahl = createWahl();
      mockDefinitions.mapDtoToModel.mockReturnValue(mockedMappedWahl);

      const result = await getWahlen(wahltagID);

      expect(result).toEqual([mockedMappedWahl]);
      expect(mockDefinitions.getWahlen.mock.calls).toStrictEqual([[wahltagID]]);
    });

    it("should_triggerNotification_when_anExceptionOccurredDuringApiCall", async () => {
      const wahltagID = generateRandomString(10);
      mockDefinitions.getWahlen.mockRejectedValue(
        new Error("api called failed")
      );

      await expect(async () => getWahlen(wahltagID)).rejects.toThrowError();

      expect(mockDefinitions.addNotification.mock.calls.length).toStrictEqual(
        1
      );
      expect(mockDefinitions.addNotification.mock.calls[0]).toEqual([
        expect.any(String),
        UserNotificationCategoryEnum.ERROR,
      ]);
    });

    it("should_notTriggerNotification_when_anExceptionOccurredDuringApiCallAndNotificationFlagFalse", async () => {
      const wahltagID = generateRandomString(10);
      mockDefinitions.getWahlen.mockRejectedValue(
        new Error("api called failed")
      );

      await expect(async () =>
        getWahlen(wahltagID, false)
      ).rejects.toThrowError();

      expect(mockDefinitions.addNotification.mock.calls.length).toStrictEqual(
        0
      );
    });
  });
});
