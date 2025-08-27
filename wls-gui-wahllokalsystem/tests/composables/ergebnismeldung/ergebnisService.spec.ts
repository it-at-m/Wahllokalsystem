import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useErgebnisseTestDataFactory } from "@tests/utils/ergebnismeldung/ergebnisseTestDataFactory.ts";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useErgebnisService } from "@/composables/ergebnismeldung/ergebnisService.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  getErgebnisse: vi.fn(),
  toModel: vi.fn(),
  configurationConstructor: vi.fn().mockImplementation(() => ({})),
  bezirkUndWahlIDStapelartDTOStapelartEnum: vi.fn(),
}));

vi.mock("@/api/wls-clients/generated-ergebnismeldung-api", () => ({
  ErgebnisseControllerApi: vi.fn().mockImplementation(() => ({
    getErgebnisse: mockDefinitions.getErgebnisse,
  })),
  Configuration: mockDefinitions.configurationConstructor,
  BezirkUndWahlIDStapelartDTOStapelartEnum:
    mockDefinitions.bezirkUndWahlIDStapelartDTOStapelartEnum,
}));
vi.mock("@/composables/ergebnismeldung/ergebnisMapper.ts", () => ({
  useErgebnisMapper: () => ({
    toModel: mockDefinitions.toModel,
  }),
}));

const { generateRandomString } = useCommonTestDataFactory();
const { createErgebnisse, createErgebnisseDTO } =
  useErgebnisseTestDataFactory();

describe("ergebnisService.ts", () => {
  const { getErgebnisse } = useErgebnisService();

  beforeEach(() => {
    vi.resetAllMocks();
    vi.clearAllMocks();
  });

  describe("getErgebnisse", () => {
    it("should_returnErgebnisse_when_wahlIDWahlbezirkIdAndStapelArtGiven", async () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const stapelArt = StapelArtEnum.ObwA;

      const mockedErgebnisseModel = createErgebnisse();
      const mockedErgebnisseDto = createErgebnisseDTO();

      mockDefinitions.getErgebnisse.mockResolvedValue(
        Promise.resolve({ status: 200, data: mockedErgebnisseDto })
      );
      mockDefinitions.toModel.mockReturnValue(mockedErgebnisseModel);

      const result = await getErgebnisse(wahlbezirkID, wahlID, stapelArt);

      expect(result).toEqual(mockedErgebnisseModel);
      expect(mockDefinitions.getErgebnisse).toHaveBeenCalledWith(
        wahlbezirkID,
        wahlID,
        stapelArt
      );
      expect(mockDefinitions.toModel).toHaveBeenCalledWith(mockedErgebnisseDto);
    });

    it("should_throwError_when_apiCallFailed", async () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const stapelArt = StapelArtEnum.ObwA;

      mockDefinitions.getErgebnisse.mockRejectedValue(
        new Error("mocked api call failed")
      );

      await expect(
        getErgebnisse(wahlbezirkID, wahlID, stapelArt)
      ).rejects.toThrowError();
    });
  });
});
