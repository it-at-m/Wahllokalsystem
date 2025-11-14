import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useKopfdatenTestDataFactory } from "@tests/utils/kopfdaten/KopfdatenTestDataFactory.ts";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useKopfdatenService } from "@/composables/kopfdaten/kopfdatenService.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const { createKopfdaten, createKopfdatenDto } = useKopfdatenTestDataFactory();

const mockDefinitions = vi.hoisted(() => ({
  getKopfdaten: vi.fn(),
  mapDtoToModel: vi.fn(),
  addNotification: vi.fn(),
  KopfdatenDTOStimmzettelgebietsartEnum: vi.fn(),
  configurationConstructor: vi.fn().mockImplementation(() => ({})),
}));

vi.mock("@/api/wls-clients/generated-basisdaten-api", () => ({
  KopfdatenControllerApi: vi.fn().mockImplementation(() => ({
    getKopfdaten: mockDefinitions.getKopfdaten,
  })),
  KopfdatenDTOStimmzettelgebietsartEnum: vi.fn(),
  Configuration: mockDefinitions.configurationConstructor,
}));

vi.mock("@/composables/kopfdaten/kopfdatenMapper.ts", () => ({
  useKopfdatenMapper: () => ({
    toModel: mockDefinitions.mapDtoToModel,
  }),
  KopfdatenDTOStimmzettelgebietsartEnum:
    mockDefinitions.KopfdatenDTOStimmzettelgebietsartEnum,
}));

vi.mock("@/composables/userNotification/userNotificationService.ts", () => ({
  useUserNotificationService: () => ({
    addNotification: mockDefinitions.addNotification,
  }),
}));

const { generateRandomString } = useCommonTestDataFactory();

describe("KopfdatenService.ts", () => {
  const { getKopfdaten } = useKopfdatenService();

  beforeEach(() => {
    vi.resetAllMocks();
    vi.clearAllMocks();
  });

  describe("getKopfdaten", () => {
    it("should_returnKopfdaten_when_parameterAreGiven", async () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const mockedKopfdaten = createKopfdaten();

      mockDefinitions.getKopfdaten.mockReturnValue(
        Promise.resolve({ status: 200, data: createKopfdatenDto() })
      );
      mockDefinitions.mapDtoToModel.mockReturnValue(mockedKopfdaten);

      const result = await getKopfdaten(wahlID, wahlbezirkID);

      expect(result).toEqual(mockedKopfdaten);
    });
  });

  it("should_triggerNotification_when_anExceptionOccurredDuringApiCall", async () => {
    const wahlID = generateRandomString(10);
    const wahlbezirkID = generateRandomString(10);

    mockDefinitions.getKopfdaten.mockRejectedValue(
      new Error("api called failed")
    );

    await expect(async () =>
      getKopfdaten(wahlID, wahlbezirkID)
    ).rejects.toThrowError();

    expect(mockDefinitions.addNotification.mock.calls[0]).toEqual([
      expect.any(String),
      UserNotificationCategoryEnum.ERROR,
    ]);
  });

  it("should_notTriggerNotification_when_anExceptionOccurredDuringApiCall", async () => {
    const wahlID = generateRandomString(10);
    const wahlbezirkID = generateRandomString(10);

    mockDefinitions.getKopfdaten.mockRejectedValue(
      new Error("api called failed")
    );

    await expect(async () =>
      getKopfdaten(wahlID, wahlbezirkID, false)
    ).rejects.toThrowError();

    expect(mockDefinitions.addNotification.mock.calls.length).toStrictEqual(0);
  });
});
