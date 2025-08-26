import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useWahlvorschlaegeTestDataFactory } from "@tests/utils/wahlvorschlaege/WahlvorschlaegeTestDataFactory.ts";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useWahlvorschlaegeService } from "@/composables/wahlvorschlaege/wahlvorschlaegeService.ts";

const mockDefinitions = vi.hoisted(() => ({
  getWahlvorschlaege: vi.fn(),
  toModel: vi.fn(),
  configurationConstructor: vi.fn().mockImplementation(() => ({})),
}));

vi.mock("@/api/wls-clients/generated-basisdaten-api", () => ({
  WahlvorschlaegeControllerApi: vi.fn().mockImplementation(() => ({
    getWahlvorschlaege: mockDefinitions.getWahlvorschlaege,
  })),
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

      mockDefinitions.getWahlvorschlaege.mockResolvedValue(
        Promise.resolve({ status: 200, data: createWahlvorschlaegeDto() })
      );
      mockDefinitions.toModel.mockReturnValue(mockedWahlvorschlaegeModel);

      const result = await getWahlvorschlaege(wahlID, wahlbezirkID);

      expect(result).toEqual(mockedWahlvorschlaegeModel);
    });
  });
});
