import type { AusdruckWriteDTO } from "@/api/wls-clients/generated-ergebnismeldung-api";

import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import {
  afterAll,
  afterEach,
  beforeEach,
  describe,
  expect,
  it,
  vi,
} from "vitest";

import { PostAusdruckMeldungsartEnum } from "@/api/wls-clients/generated-ergebnismeldung-api";
import { useAusdruckService } from "@/composables/ergebnismeldung/common/ausdruckService.ts";
import { MeldungsArtEnum } from "@/types/ergebnismeldung/common/MeldungsartEnum.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  addNotification: vi.fn(),
  mapMeldungsartEnumToDto: vi.fn(),
  mapToAusdruckWriteDTO: vi.fn(),
  postAusdruck: vi.fn(),
}));

vi.mock(
  "@/api/wls-clients/generated-ergebnismeldung-api",
  async (importOriginal) => {
    const mod = await importOriginal();
    return {
      ...(mod as object),
      AusdruckControllerApi: class {
        postAusdruck = mockDefinitions.postAusdruck;
      },
      Configuration: vi.fn(),
    };
  }
);
vi.mock(
  import("@/composables/ergebnismeldung/common/ausdruckMapper.ts"),
  () => ({
    useAusdruckMapper: () => ({
      meldungsartEnumToDto: mockDefinitions.mapMeldungsartEnumToDto,
      toAusdruckWriteDTO: mockDefinitions.mapToAusdruckWriteDTO,
    }),
  })
);
vi.mock(
  import("@/composables/userNotification/userNotificationService.ts"),
  () => ({
    useUserNotificationService: () => ({
      addNotification: mockDefinitions.addNotification,
    }),
  })
);

const { generateRandomString, getRandomItem } = useCommonTestDataFactory();

describe("ausdruckService.ts", () => {
  let unitUnderTest: ReturnType<typeof useAusdruckService>;

  beforeEach(() => {
    unitUnderTest = useAusdruckService();
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  afterAll(() => {
    vi.resetAllMocks();
  });

  describe("postAusdruck", () => {
    it("should_sendAusdruckAndNotification_when_called", async () => {
      const wahlbezirkID = generateRandomString(10);
      const wahlID = generateRandomString(10);
      const meldungsart = getRandomItem(Object.values(MeldungsArtEnum));
      const ausdruck = generateRandomString(200);

      const mockedMeldungsartMappingResult = getRandomItem(
        Object.values(PostAusdruckMeldungsartEnum)
      );
      mockDefinitions.mapMeldungsartEnumToDto.mockReturnValue(
        mockedMeldungsartMappingResult
      );

      const mockedWriteAusdruckDTO: AusdruckWriteDTO = {
        content: generateRandomString(200),
      };
      mockDefinitions.mapToAusdruckWriteDTO.mockReturnValue(
        mockedWriteAusdruckDTO
      );

      await unitUnderTest.postAusdruck(
        wahlbezirkID,
        wahlID,
        meldungsart,
        ausdruck
      );

      expect(mockDefinitions.postAusdruck.mock.calls).toStrictEqual([
        [
          wahlID,
          wahlbezirkID,
          mockedMeldungsartMappingResult,
          mockedWriteAusdruckDTO,
        ],
      ]);
      expect(mockDefinitions.addNotification.mock.calls).toStrictEqual([
        [expect.any(String), UserNotificationCategoryEnum.SUCCESS],
      ]);
    });

    it("should_sendAusdruckAndButNoNotification_when_calledWithSendNotificationFalse", async () => {
      const wahlbezirkID = generateRandomString(10);
      const wahlID = generateRandomString(10);
      const meldungsart = getRandomItem(Object.values(MeldungsArtEnum));
      const ausdruck = generateRandomString(200);

      const mockedMeldungsartMappingResult = getRandomItem(
        Object.values(PostAusdruckMeldungsartEnum)
      );
      mockDefinitions.mapMeldungsartEnumToDto.mockReturnValue(
        mockedMeldungsartMappingResult
      );

      const mockedWriteAusdruckDTO: AusdruckWriteDTO = {
        content: generateRandomString(200),
      };
      mockDefinitions.mapToAusdruckWriteDTO.mockReturnValue(
        mockedWriteAusdruckDTO
      );

      await unitUnderTest.postAusdruck(
        wahlbezirkID,
        wahlID,
        meldungsart,
        ausdruck,
        false
      );

      expect(mockDefinitions.postAusdruck.mock.calls).toStrictEqual([
        [
          wahlID,
          wahlbezirkID,
          mockedMeldungsartMappingResult,
          mockedWriteAusdruckDTO,
        ],
      ]);
      expect(mockDefinitions.addNotification.mock.calls.length).toStrictEqual(
        0
      );
    });

    it("should_throwErrorAndSendNotification_when_apiCallFailed", async () => {
      const wahlbezirkID = generateRandomString(10);
      const wahlID = generateRandomString(10);
      const meldungsart = getRandomItem(Object.values(MeldungsArtEnum));
      const ausdruck = generateRandomString(200);

      const mockedMeldungsartMappingResult = getRandomItem(
        Object.values(PostAusdruckMeldungsartEnum)
      );
      mockDefinitions.mapMeldungsartEnumToDto.mockReturnValue(
        mockedMeldungsartMappingResult
      );

      const mockedWriteAusdruckDTO: AusdruckWriteDTO = {
        content: generateRandomString(200),
      };
      mockDefinitions.mapToAusdruckWriteDTO.mockReturnValue(
        mockedWriteAusdruckDTO
      );

      const mockedApiFailure = new Error("mocked api call failed");
      mockDefinitions.postAusdruck.mockRejectedValue(mockedApiFailure);

      await expect(
        unitUnderTest.postAusdruck(wahlbezirkID, wahlID, meldungsart, ausdruck)
      ).rejects.toThrowError(new Error("post ausdruck failed"));

      expect(mockDefinitions.addNotification.mock.calls).toStrictEqual([
        [expect.any(String), UserNotificationCategoryEnum.ERROR],
      ]);
    });

    it("should_throwErrorButSendNoNotification_when_apiCallFailedAndWasCalledWithSendNotificationFalse", async () => {
      const wahlbezirkID = generateRandomString(10);
      const wahlID = generateRandomString(10);
      const meldungsart = getRandomItem(Object.values(MeldungsArtEnum));
      const ausdruck = generateRandomString(200);

      const mockedMeldungsartMappingResult = getRandomItem(
        Object.values(PostAusdruckMeldungsartEnum)
      );
      mockDefinitions.mapMeldungsartEnumToDto.mockReturnValue(
        mockedMeldungsartMappingResult
      );

      const mockedWriteAusdruckDTO: AusdruckWriteDTO = {
        content: generateRandomString(200),
      };
      mockDefinitions.mapToAusdruckWriteDTO.mockReturnValue(
        mockedWriteAusdruckDTO
      );

      const mockedApiFailure = new Error("mocked api call failed");
      mockDefinitions.postAusdruck.mockRejectedValue(mockedApiFailure);

      await expect(
        unitUnderTest.postAusdruck(
          wahlbezirkID,
          wahlID,
          meldungsart,
          ausdruck,
          false
        )
      ).rejects.toThrowError(new Error("post ausdruck failed"));

      expect(mockDefinitions.addNotification.mock.calls.length).toStrictEqual(
        0
      );
    });
  });
});
