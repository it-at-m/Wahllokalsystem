import { useWahlbezirkTestDataFactory } from "@tests/utils/wahlbezirk/WahlbezirkTestDataFactory.ts";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useUngueltigeWahlscheineService } from "@/composables/basisdaten/ungueltigeWahlscheineService.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  addNotification: vi.fn(),
  getUngueltigeWahlscheine: vi.fn(),
  configurationConstructor: vi.fn().mockImplementation(() => ({})),
  mapToModel: vi.fn(),
}));

vi.mock("@/api/wls-clients/generated-basisdaten-api", () => ({
  UngueltigeWahlscheineControllerApi: vi.fn().mockImplementation(() => ({
    getUngueltigeWahlscheine: mockDefinitions.getUngueltigeWahlscheine,
  })),
  Configuration: mockDefinitions.configurationConstructor,
}));
vi.mock("@/composables/userNotification/userNotificationService.ts", () => ({
  useUserNotificationService: () => ({
    addNotification: mockDefinitions.addNotification,
  }),
}));
vi.mock("@/composables/basisdaten/ungueltigerWahlscheinMapper.ts", () => ({
  useUngueltigerWahlscheinMapper: () => ({
    toModel: mockDefinitions.mapToModel,
  }),
}));

const { createUngueltigerWahlschein } = useWahlbezirkTestDataFactory();

describe("ungueltigeWahlscheineService.ts", () => {
  const { getUngueltigeWahlscheine } = useUngueltigeWahlscheineService();

  beforeEach(() => {
    vi.resetAllMocks();
    vi.clearAllMocks();
  });

  describe("getUngueltigeWahlscheine", () => {
    it("should_returnListOfUngueltigeWahlscheine_when_apiCalledSucceeded", async () => {
      const wahltagID = "wahltagID";
      const wahlbezirksArt = WahlbezirksArtEnum.BWB;

      const mockedUngueltigeWahlscheineApiResponseData = "c;s;v";
      mockDefinitions.getUngueltigeWahlscheine.mockReturnValue({
        status: 200,
        data: mockedUngueltigeWahlscheineApiResponseData,
      });

      const mockedMapperResponse = [
        createUngueltigerWahlschein(),
        createUngueltigerWahlschein(),
      ];
      mockDefinitions.mapToModel.mockReturnValue(mockedMapperResponse);

      const result = await getUngueltigeWahlscheine(wahltagID, wahlbezirksArt);

      expect(result).toStrictEqual(mockedMapperResponse);
      expect(mockDefinitions.mapToModel.mock.calls).toStrictEqual([
        [mockedUngueltigeWahlscheineApiResponseData],
      ]);
      expect(mockDefinitions.getUngueltigeWahlscheine.mock.calls).toStrictEqual(
        [[wahltagID, wahlbezirksArt]]
      );
      expect(mockDefinitions.addNotification.mock.calls).toHaveLength(0);
    });

    it("should_addNotification_when_apiCallFailed", async () => {
      const wahltagID = "wahltagID";
      const wahlbezirksArt = WahlbezirksArtEnum.BWB;

      const apiCallError = new Error(
        "mocked api call get ungueltige Wahlscheine failed"
      );
      mockDefinitions.getUngueltigeWahlscheine.mockRejectedValue(apiCallError);

      await expect(
        getUngueltigeWahlscheine(wahltagID, wahlbezirksArt)
      ).rejects.toThrow(apiCallError);

      expect(mockDefinitions.mapToModel.mock.calls).toHaveLength(0);
      expect(mockDefinitions.getUngueltigeWahlscheine.mock.calls).toStrictEqual(
        [[wahltagID, wahlbezirksArt]]
      );
      expect(mockDefinitions.addNotification.mock.calls).toEqual([
        [expect.any(String), "Error"],
      ]);
    });

    it("should_notAddNotification_when_apiCallFailedButSendNotificationParameterIsFalse", async () => {
      const wahltagID = "wahltagID";
      const wahlbezirksArt = WahlbezirksArtEnum.BWB;

      const apiCallError = new Error(
        "mocked api call get ungueltige Wahlscheine failed"
      );
      mockDefinitions.getUngueltigeWahlscheine.mockRejectedValue(apiCallError);

      await expect(
        getUngueltigeWahlscheine(wahltagID, wahlbezirksArt, false)
      ).rejects.toThrow(apiCallError);

      expect(mockDefinitions.mapToModel.mock.calls).toHaveLength(0);
      expect(mockDefinitions.getUngueltigeWahlscheine.mock.calls).toStrictEqual(
        [[wahltagID, wahlbezirksArt]]
      );
      expect(mockDefinitions.addNotification.mock.calls).toHaveLength(0);
    });
  });
});
