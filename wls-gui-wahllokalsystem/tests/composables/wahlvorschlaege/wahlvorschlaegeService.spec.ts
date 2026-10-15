import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useWahlvorschlaegeTestDataFactory } from "@tests/utils/wahlvorschlaege/WahlvorschlaegeTestDataFactory.ts";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useWahlvorschlaegeService } from "@/composables/wahlvorschlaege/wahlvorschlaegeService.ts";

const mockDefinitions = vi.hoisted(() => ({
  getWahlvorschlaege: vi.fn(),
  toModel: vi.fn(),
  sortWahlvorschlaegeByOrdnungszahl: vi.fn(),
  sortKandidatenByListenPositionInplace: vi.fn(),
  configurationConstructor: vi.fn(),
}));

vi.mock("@/api/wls-clients/generated-basisdaten-api", () => ({
  WahlvorschlaegeControllerApi: class {
    getWahlvorschlaege = mockDefinitions.getWahlvorschlaege;
  },
  Configuration: mockDefinitions.configurationConstructor,
}));
vi.mock(
  import("@/composables/wahlvorschlaege/wahlvorschlaegeMapper.ts"),
  () => ({
    useWahlvorschlaegeMapper: () => ({
      toModel: mockDefinitions.toModel,
    }),
  })
);
vi.mock(
  import("@/composables/wahlvorschlaege/wahlvorschlagUtils.ts"),
  async (importOriginal) => {
    const original = await importOriginal();
    return {
      useWahlvorschlagUtils: () => ({
        ...original.useWahlvorschlagUtils(),
        sortWahlvorschlaegeByOrdnungszahl:
          mockDefinitions.sortWahlvorschlaegeByOrdnungszahl,
        sortKandidatenByListenPositionInplace:
          mockDefinitions.sortKandidatenByListenPositionInplace,
      }),
    };
  }
);

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
      expect(
        mockDefinitions.sortWahlvorschlaegeByOrdnungszahl.mock.calls
      ).toStrictEqual([[mockedWahlvorschlaegeModel]]);
      expect(
        mockDefinitions.sortKandidatenByListenPositionInplace.mock.calls.length
      ).toStrictEqual(mockedWahlvorschlaegeModel.wahlvorschlaege.length);
      expect(
        mockDefinitions.sortKandidatenByListenPositionInplace
      ).toHaveBeenCalledWith(...mockedWahlvorschlaegeModel.wahlvorschlaege);
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
