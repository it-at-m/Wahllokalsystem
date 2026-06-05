import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useWahlvorschlaegeTestDataFactory } from "@tests/utils/wahlvorschlaege/WahlvorschlaegeTestDataFactory.ts";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useWahlvorschlaegeService } from "@/composables/wahlvorschlaege/wahlvorschlaegeService.ts";

const mockDefinitions = vi.hoisted(() => ({
  getWahlvorschlaege: vi.fn(),
  toModel: vi.fn(),
  configurationConstructor: vi.fn(),
}));

vi.mock("@/api/wls-clients/generated-basisdaten-api", () => ({
  WahlvorschlaegeControllerApi: vi.fn().mockImplementation(
    class MockedWahlvorschlaegeControllerApi {
      getWahlvorschlaege = mockDefinitions.getWahlvorschlaege;
    } as never
  ),
  Configuration: mockDefinitions.configurationConstructor,
}));
vi.mock("@/composables/wahlvorschlaege/wahlvorschlaegeMapper.ts", () => ({
  useWahlvorschlaegeMapper: () => ({
    toModel: mockDefinitions.toModel,
  }),
}));

const { generateRandomString } = useCommonTestDataFactory();
const { createWahlvorschlaege, createWahlvorschlaegeDto } =
  useWahlvorschlaegeTestDataFactory();

describe("wahlvorschlaegeService.ts", () => {
  const { getWahlvorschlaege } = useWahlvorschlaegeService();

  beforeEach(() => {
    vi.resetAllMocks();
    vi.clearAllMocks();
  });

  describe("getWahlvorschlaege", () => {
    it("should_returnWahlvorschlaege_when_wahlIDAndWahlbezirkIdGiven", async () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);

      const mockedWahlvorschlaegeModel = createWahlvorschlaege();
      const mockedWahlvorschlaegeDto = createWahlvorschlaegeDto();

      mockDefinitions.getWahlvorschlaege.mockResolvedValue(
        Promise.resolve({ status: 200, data: mockedWahlvorschlaegeDto })
      );
      mockDefinitions.toModel.mockReturnValue(mockedWahlvorschlaegeModel);

      const result = await getWahlvorschlaege(wahlID, wahlbezirkID);

      expect(result).toEqual(mockedWahlvorschlaegeModel);
      expect(mockDefinitions.getWahlvorschlaege).toHaveBeenCalledWith(
        wahlID,
        wahlbezirkID
      );
      expect(mockDefinitions.toModel).toHaveBeenCalledWith(
        mockedWahlvorschlaegeDto
      );
    });

    it("should_throwError_when_apiCallFailed", async () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);

      mockDefinitions.getWahlvorschlaege.mockRejectedValue(
        new Error("mocked api call failed")
      );

      await expect(getWahlvorschlaege(wahlID, wahlbezirkID)).rejects.toThrow(
        "GetWahlvorschlaege failed"
      );
    });
  });
});
