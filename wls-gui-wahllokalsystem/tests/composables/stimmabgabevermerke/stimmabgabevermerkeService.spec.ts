import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useStimmabgabevermerkeTestDataFactory } from "@tests/utils/stimmabgabevermerke/StimmabgabevermerkeTestDataFactory.ts";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useStimmabgabevermerkeService } from "@/composables/stimmabgabevermerke/stimmabgabevermerkeService.ts";

const mockDefinitions = vi.hoisted(() => ({
  getStimmabgabevermerke: vi.fn(),
  toModel: vi.fn(),
  addNotification: vi.fn(),
}));

vi.mock("@/api/wls-clients/generated-ergebnismeldung-api", () => ({
  StimmabgabevermerkeControllerApi: vi.fn().mockImplementation(() => ({
    getStimmabgabevermerke: mockDefinitions.getStimmabgabevermerke,
  })),
  StimmzettelDTOStimmzettelartEnum: vi.fn(),
  Configuration: vi.fn(),
}));
vi.mock(
  "@/composables/stimmabgabevermerke/stimmabgabevermerkeMapper.ts",
  () => ({
    useStimmabgabevermerkeMapper: () => ({
      toModel: mockDefinitions.toModel,
    }),
  })
);

vi.mock("@/composables/userNotification/userNotificationService.ts", () => ({
  useUserNotificationService: () => ({
    addNotification: mockDefinitions.addNotification,
  }),
}));

const { generateRandomString, generateRandomNumber } =
  useCommonTestDataFactory();
const { createStimmabgabevermerke, createStimmabgabevermerkeDTO } =
  useStimmabgabevermerkeTestDataFactory();

describe("stimmabgabevermerkeService.ts", () => {
  const { getStimmabgabevermerke } = useStimmabgabevermerkeService();

  beforeEach(() => {
    vi.resetAllMocks();
    vi.clearAllMocks();
  });
  describe("getStimmabgabevermerke", () => {
    it("should_returnStimmabgabevermerke_when_parameterAreGiven", async () => {
      const waehlerverzeichnisNummer = generateRandomNumber(2);
      const wahlbezirkID = generateRandomString(10);
      const mockedStimmabgabevermerke = createStimmabgabevermerke();

      mockDefinitions.getStimmabgabevermerke.mockReturnValue(
        Promise.resolve({ status: 200, data: createStimmabgabevermerkeDTO() })
      );
      mockDefinitions.toModel.mockReturnValue(mockedStimmabgabevermerke);

      const result = await getStimmabgabevermerke(
        wahlbezirkID,
        waehlerverzeichnisNummer
      );

      expect(result).toEqual(mockedStimmabgabevermerke);
    });

    it("should_triggerNotification_when_anExceptionOccurredDuringApiCall", async () => {
      const waehlerverzeichnisNummer = generateRandomNumber(2);
      const wahlbezirkID = generateRandomString(10);

      mockDefinitions.getStimmabgabevermerke.mockRejectedValue(
        new Error("api called failed")
      );

      await expect(async () =>
        getStimmabgabevermerke(wahlbezirkID, waehlerverzeichnisNummer)
      ).rejects.toThrowError();

      expect(mockDefinitions.addNotification.mock.calls[0]).toEqual([
        expect.any(String),
        "Error",
      ]);
    });

    it("should_notTriggerNotification_when_anExceptionOccurredDuringApiCall", async () => {
      const waehlerverzeichnisNummer = generateRandomNumber(2);
      const wahlbezirkID = generateRandomString(10);
      mockDefinitions.getStimmabgabevermerke.mockRejectedValue(
        new Error("api called failed")
      );

      await expect(async () =>
        getStimmabgabevermerke(wahlbezirkID, waehlerverzeichnisNummer, false)
      ).rejects.toThrowError();

      expect(mockDefinitions.addNotification.mock.calls.length).toStrictEqual(
        0
      );
    });
  });
});
