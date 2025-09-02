import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useErgebnisseTestDataFactory } from "@tests/utils/ergebnismeldung/ergebnisseTestDataFactory.ts";
import { beforeEach, describe, expect, it, vi } from "vitest";

import {
  BezirkUndWahlIDStapelartDTOStapelartEnum as DtoStapelArtEnum,
  GetErgebnisseStapelartEnum,
  PostErgebnisseStapelartEnum,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import { useErgebnisService } from "@/composables/ergebnismeldung/ergebnisService.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  getErgebnisse: vi.fn(),
  postErgebnisse: vi.fn(),
  toModel: vi.fn(),
  toDto: vi.fn(),
  toGetErgebnisseStapelartEnum: vi.fn(),
  toPostErgebnisseStapelartEnum: vi.fn(),
  configurationConstructor: vi.fn().mockImplementation(() => ({})),
  bezirkUndWahlIDStapelartDTOStapelartEnum: vi.fn(),
  getErgebnisseStapelartEnum: vi.fn(),
  postErgebnisseStapelartEnum: vi.fn(),
}));

vi.mock("@/api/wls-clients/generated-ergebnismeldung-api", () => ({
  ErgebnisseControllerApi: vi.fn().mockImplementation(() => ({
    getErgebnisse: mockDefinitions.getErgebnisse,
    postErgebnisse: mockDefinitions.postErgebnisse,
  })),
  Configuration: mockDefinitions.configurationConstructor,
  BezirkUndWahlIDStapelartDTOStapelartEnum:
    mockDefinitions.bezirkUndWahlIDStapelartDTOStapelartEnum,
  GetErgebnisseStapelartEnum: mockDefinitions.getErgebnisseStapelartEnum,
  PostErgebnisseStapelartEnum: mockDefinitions.postErgebnisseStapelartEnum,
}));
vi.mock("@/composables/ergebnismeldung/ergebnisMapper.ts", () => ({
  useErgebnisMapper: () => ({
    toModel: mockDefinitions.toModel,
    toDto: mockDefinitions.toDto,
    toGetErgebnisseStapelartEnum: mockDefinitions.toGetErgebnisseStapelartEnum,
    toPostErgebnisseStapelartEnum:
      mockDefinitions.toPostErgebnisseStapelartEnum,
  }),
}));

const { generateRandomString } = useCommonTestDataFactory();
const {
  createErgebnisse,
  createErgebnisseDTO,
  prepareErgebnisse,
  prepareErgebnis,
  prepareErgebnisDTO,
  prepareErgebnisseDTO,
} = useErgebnisseTestDataFactory();

describe("ergebnisService.ts", () => {
  const { getErgebnisse, postErgebnisse } = useErgebnisService();

  beforeEach(() => {
    vi.resetAllMocks();
    vi.clearAllMocks();
  });

  describe("getErgebnisse", () => {
    it("should_returnErgebnisse_when_wahlIDWahlbezirkIdAndStapelArtGiven", async () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const stapelArtModel = StapelArtEnum.ObwA;
      const stapelArtDto = GetErgebnisseStapelartEnum.ObwA;

      const mockedErgebnisseModel = createErgebnisse();
      const mockedErgebnisseDto = createErgebnisseDTO();

      mockDefinitions.getErgebnisse.mockResolvedValue({
        status: 200,
        data: mockedErgebnisseDto,
      });
      mockDefinitions.toModel.mockReturnValue(mockedErgebnisseModel);
      mockDefinitions.toGetErgebnisseStapelartEnum.mockReturnValue(
        stapelArtDto
      );

      const result = await getErgebnisse(wahlbezirkID, wahlID, stapelArtModel);

      expect(result).toEqual(mockedErgebnisseModel);
      expect(mockDefinitions.getErgebnisse).toHaveBeenCalledWith(
        wahlbezirkID,
        wahlID,
        stapelArtDto
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

    it("should_notMapAndReturnNull_when_apiReturnsNoContent", async () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const stapelArt = StapelArtEnum.ObwA;

      mockDefinitions.getErgebnisse.mockResolvedValue({
        status: 204,
        data: undefined,
      });
      mockDefinitions.toGetErgebnisseStapelartEnum.mockReturnValue(
        GetErgebnisseStapelartEnum.ObwA
      );

      const result = await getErgebnisse(wahlbezirkID, wahlID, stapelArt);

      expect(result).toBeNull();
      expect(mockDefinitions.toModel).not.toHaveBeenCalled();
    });
  });

  describe("postErgebnisse", () => {
    it("should_sendErgebnisse_when_wahlIDWahlbezirkIdStapelArtAndModelGiven", async () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const stapelArtModel = StapelArtEnum.ObwA;
      const stapelArtPostDto = PostErgebnisseStapelartEnum.ObwA;

      const mockedErgebnisseAsModels = [
        prepareErgebnis().ergebnis(1).build(),
        prepareErgebnis().ergebnis(5).build(),
      ];
      const mockedErgebnisseAsDtos = [
        prepareErgebnisDTO().ergebnis(1).build(),
        prepareErgebnisDTO().ergebnis(5).build(),
      ];
      const ergebnisseModelToSend = prepareErgebnisse()
        .bezirkUndWahlIDStapelart({
          wahlID: wahlID,
          wahlbezirkID: wahlbezirkID,
          stapelArt: stapelArtModel,
        })
        .ergebnisse(mockedErgebnisseAsModels)
        .build();
      const mockedErgebnisseDto = prepareErgebnisseDTO()
        .bezirkUndWahlIDStapelart({
          wahlID: wahlID,
          wahlbezirkID: wahlbezirkID,
          stapelart: DtoStapelArtEnum.ObwA,
        })
        .ergebnisse(mockedErgebnisseAsDtos)
        .build();

      mockDefinitions.toPostErgebnisseStapelartEnum.mockReturnValue(
        stapelArtPostDto
      );
      mockDefinitions.toDto.mockReturnValue(mockedErgebnisseDto);
      mockDefinitions.postErgebnisse.mockResolvedValue({});

      await postErgebnisse(
        wahlbezirkID,
        wahlID,
        stapelArtModel,
        ergebnisseModelToSend
      );

      expect(mockDefinitions.postErgebnisse).toHaveBeenCalledWith(
        wahlbezirkID,
        wahlID,
        stapelArtPostDto,
        mockedErgebnisseDto
      );
      expect(mockDefinitions.toDto).toHaveBeenCalledWith(ergebnisseModelToSend);
    });

    it("should_throwError_when_apiCallFailed", async () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const stapelArt = StapelArtEnum.ObwA;
      const ergebnisse = createErgebnisse();

      mockDefinitions.postErgebnisse.mockRejectedValue(
        new Error("mocked api call failed")
      );

      await expect(
        postErgebnisse(wahlbezirkID, wahlID, stapelArt, ergebnisse)
      ).rejects.toThrowError();
    });
  });
});
