import type { UrnenwahlSchliessungsUhrzeitWriteDTO } from "@/api/wls-clients/generated-wahlvorbereitung-api";

import { useAxiosTestDataFactory } from "@tests/utils/common/AxiosTestDataFactory.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useWahlvorbereitungTestDataFactory } from "@tests/utils/wahlhandlung/WahlvorbereitungTestDataFactory.ts";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useWahlvorbereitungService } from "@/composables/wahlhandlung/wahlvorbereitungService.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  addNotification: vi.fn(),
  toUrnenwahlSchliessungsuhrzeitDTO: vi.fn(),
  toEroeffnungsuhrzeitWriteDTO: vi.fn(),
  postUrnenwahlSchliessungsUhrzeit: vi.fn(),
  getEroeffnungsuhrzeit: vi.fn(),
  postEroeffnungsuhrzeit: vi.fn(),
  getUrnenwahlVorbereitung: vi.fn(),
  postUrnenwahlvorbereitung: vi.fn(),
  toUrnenwahlvorbereitungModel: vi.fn(),
  toUrnenwahlvorbereitungWriteDto: vi.fn(),
  getBriefwahlvorbereitung: vi.fn(),
  postBriefwahlvorbereitung: vi.fn(),
  toBriefwahlvorbereitungModel: vi.fn(),
  toBriefwahlvorbereitungWriteDto: vi.fn(),
}));

vi.mock("@/api/wls-clients/generated-wahlvorbereitung-api", () => ({
  UrnenwahlSchliessungsUhrzeitControllerApi: vi.fn().mockImplementation(() => ({
    postUrnenwahlSchliessungsUhrzeit:
      mockDefinitions.postUrnenwahlSchliessungsUhrzeit,
  })),
  EroeffnungsUhrzeitControllerApi: vi.fn().mockImplementation(() => ({
    postEroeffnungsuhrzeit: mockDefinitions.postEroeffnungsuhrzeit,
    getEroeffnungsuhrzeit: mockDefinitions.getEroeffnungsuhrzeit,
  })),
  UrnenwahlvorbereitungControllerApi: vi.fn().mockImplementation(() => ({
    getUrnenwahlVorbereitung: mockDefinitions.getUrnenwahlVorbereitung,
    postUrnenwahlvorbereitung: mockDefinitions.postUrnenwahlvorbereitung,
  })),
  BriefwahlvorbereitungControllerApi: vi.fn().mockImplementation(() => ({
    getBriefwahlvorbereitung: mockDefinitions.getBriefwahlvorbereitung,
    postBriefwahlvorbereitung: mockDefinitions.postBriefwahlvorbereitung,
  })),
  Configuration: vi.fn(),
}));

vi.mock("@/composables/userNotification/userNotificationService.ts", () => ({
  useUserNotificationService: () => ({
    addNotification: mockDefinitions.addNotification,
  }),
}));

vi.mock("@/composables/wahlhandlung/wahlvorbereitungMapper.ts", () => ({
  useWahlvorbereitungMapper: () => ({
    toUrnenwahlSchliessungsuhrzeitDTO:
      mockDefinitions.toUrnenwahlSchliessungsuhrzeitDTO,
    toEroeffnungsuhrzeitWriteDTO: mockDefinitions.toEroeffnungsuhrzeitWriteDTO,
    toUrnenwahlvorbereitungModel: mockDefinitions.toUrnenwahlvorbereitungModel,
    toUrnenwahlvorbereitungWriteDto:
      mockDefinitions.toUrnenwahlvorbereitungWriteDto,
    toBriefwahlvorbereitungModel: mockDefinitions.toBriefwahlvorbereitungModel,
    toBriefwahlvorbereitungWriteDto:
      mockDefinitions.toBriefwahlvorbereitungWriteDto,
  }),
}));

const {
  postUrnenwahlSchliessungsuhrzeit,
  getEroeffnungsuhrzeit,
  postEroeffnungsuhrzeit,
  getUrnenwahlvorbereitung,
  postUrnenwahlvorbereitung,
  postBriefwahlvorbereitung,
} = useWahlvorbereitungService();
const {
  createEroeffnungsUhrzeitDTO,
  createEroeffnungsUhrzeitWriteDTO,
  createUrnenwahlvorbereitungWriteDTO,
  createUrnenwahlvorbereitung,
  createUrnenwahlvorbereitungDTO,
  createWahlvorbereitung,
  createBriefwahlvorbereitungDTO,
} = useWahlvorbereitungTestDataFactory();
const { generateRandomString } = useCommonTestDataFactory();
const { createAxiosResponse } = useAxiosTestDataFactory();

describe("wahlvorbereitungService", () => {
  beforeEach(() => {
    vi.resetAllMocks();
    vi.clearAllMocks();
  });

  describe("postUrnenwahlSchliessungsuhrzeit", () => {
    it("should_throwErrorAndCallNotificationService_when_apiCallFails", async () => {
      const wahlbezirkID = "wahlbezirkID";
      const schliessungsuhrzeit = "2025-04-29T12:12:42";

      const utcDate = new Date(schliessungsuhrzeit);
      const expectedDate = new Date(utcDate.getTime());

      const mockedSchliessungsuhrzeitAsDTO: UrnenwahlSchliessungsUhrzeitWriteDTO =
        { schliessungsuhrzeit };
      mockDefinitions.toUrnenwahlSchliessungsuhrzeitDTO.mockReturnValue(
        mockedSchliessungsuhrzeitAsDTO
      );

      mockDefinitions.postUrnenwahlSchliessungsUhrzeit.mockRejectedValue(
        new Error("API Error")
      );

      await expect(
        postUrnenwahlSchliessungsuhrzeit(
          wahlbezirkID,
          new Date(schliessungsuhrzeit)
        )
      ).rejects.toThrow("API Error");

      expect(mockDefinitions.addNotification.mock.calls).toEqual([
        [expect.any(String), UserNotificationCategoryEnum.ERROR],
      ]);
      expect(
        mockDefinitions.toUrnenwahlSchliessungsuhrzeitDTO.mock.calls
      ).toStrictEqual([[expectedDate]]);
    });
  });

  describe("getEroeffnungsuhrzeit", () => {
    it("should_returnEroeffnunguhrzeit_when_apiReturnsData", async () => {
      const wahlbezirkID = generateRandomString(10);

      const mockedApiResponseData = createEroeffnungsUhrzeitDTO();
      mockDefinitions.getEroeffnungsuhrzeit.mockReturnValue(
        createAxiosResponse({ status: 200, data: mockedApiResponseData })
      );

      const result = await getEroeffnungsuhrzeit(wahlbezirkID);

      expect(result?.getTime()).toStrictEqual(
        new Date(mockedApiResponseData.eroeffnungsuhrzeit).getTime()
      );
      expect(mockDefinitions.getEroeffnungsuhrzeit.mock.calls).toStrictEqual([
        [wahlbezirkID],
      ]);
    });

    it("should_returnNull_when_apiReturnsNoData", async () => {
      const wahlbezirkID = generateRandomString(10);

      mockDefinitions.getEroeffnungsuhrzeit.mockReturnValue(
        createAxiosResponse({ status: 204, data: null })
      );

      const result = await getEroeffnungsuhrzeit(wahlbezirkID);

      expect(result).toBeNull();
    });

    it("should_throwErrorAndSendNotification_when_apiFailed", async () => {
      const wahlbezirkID = generateRandomString(10);

      const mockedApiError = new Error("mocked api call failed");
      mockDefinitions.getEroeffnungsuhrzeit.mockRejectedValue(mockedApiError);

      await expect(getEroeffnungsuhrzeit(wahlbezirkID)).rejects.toThrow(
        mockedApiError
      );
      expect(mockDefinitions.addNotification.mock.calls).toStrictEqual([
        [expect.any(String), UserNotificationCategoryEnum.ERROR],
      ]);
    });

    it("should_throwErrorAndSendNoNotification_when_apiFailedAndNotificationFlagIsFalse", async () => {
      const wahlbezirkID = generateRandomString(10);

      const mockedApiError = new Error("mocked api call failed");
      mockDefinitions.getEroeffnungsuhrzeit.mockRejectedValue(mockedApiError);

      await expect(getEroeffnungsuhrzeit(wahlbezirkID, false)).rejects.toThrow(
        mockedApiError
      );
      expect(mockDefinitions.addNotification.mock.calls).toStrictEqual([]);
    });
  });

  describe("postEroeffnungsuhrzeit", () => {
    it("should_callNotificationServiceWithSuccess_when_apiCallSucceeded", async () => {
      const wahlbezirkID = "wahlbezirkID";
      const uhrzeit = new Date();

      const mappedUhrzeitToDto = createEroeffnungsUhrzeitWriteDTO();
      mockDefinitions.toEroeffnungsuhrzeitWriteDTO.mockReturnValue(
        mappedUhrzeitToDto
      );

      await postEroeffnungsuhrzeit(wahlbezirkID, uhrzeit);

      expect(mockDefinitions.addNotification.mock.calls).toStrictEqual([
        [expect.any(String), UserNotificationCategoryEnum.SUCCESS],
      ]);
      expect(mockDefinitions.postEroeffnungsuhrzeit.mock.calls).toStrictEqual([
        [wahlbezirkID, mappedUhrzeitToDto],
      ]);
    });

    it("should_throwApiErrorAndCallNotificationServiceWithError_when_apiCallFailed", async () => {
      const wahlbezirkID = "wahlbezirkID";
      const uhrzeit = new Date();

      const mappedUhrzeitToDto = createEroeffnungsUhrzeitWriteDTO();
      mockDefinitions.toEroeffnungsuhrzeitWriteDTO.mockReturnValue(
        mappedUhrzeitToDto
      );

      const mockedApiError = new Error("mocked api call failed");
      mockDefinitions.postEroeffnungsuhrzeit.mockRejectedValue(mockedApiError);

      await expect(
        postEroeffnungsuhrzeit(wahlbezirkID, uhrzeit)
      ).rejects.toThrow(mockedApiError);

      expect(mockDefinitions.addNotification.mock.calls).toStrictEqual([
        [expect.any(String), UserNotificationCategoryEnum.ERROR],
      ]);
      expect(mockDefinitions.postEroeffnungsuhrzeit.mock.calls).toStrictEqual([
        [wahlbezirkID, mappedUhrzeitToDto],
      ]);
    });
  });

  describe("getUrnenwahlvorbereitung", () => {
    it("should_returnUrnenwahlvorbereitung_when_apiCallSucceeded", async () => {
      const wahlbezirkID = "wahlbezirkID1";
      const expectedUrnenwahlvorbereitung = createUrnenwahlvorbereitung();

      mockDefinitions.toUrnenwahlvorbereitungModel.mockReturnValue(
        expectedUrnenwahlvorbereitung
      );
      // Mock the API call to return a response with the expected data
      mockDefinitions.getUrnenwahlVorbereitung.mockResolvedValue(
        createUrnenwahlvorbereitungDTO()
      );

      const result = await getUrnenwahlvorbereitung(wahlbezirkID);

      expect(result).toEqual(expectedUrnenwahlvorbereitung);
    });

    it("should_throwErrorAndCallNotificationService_when_apiCallFails", async () => {
      const wahlbezirkID = "wahlbezirkID1";

      const mockedApiError = new Error("API Error");
      mockDefinitions.getUrnenwahlVorbereitung.mockRejectedValue(
        mockedApiError
      );

      await expect(getUrnenwahlvorbereitung(wahlbezirkID)).rejects.toThrow(
        "API Error"
      );

      expect(mockDefinitions.addNotification.mock.calls).toEqual([
        [
          "Fehler beim Laden der Urnenwahlvorbereitung.",
          UserNotificationCategoryEnum.ERROR,
        ],
      ]);
    });
  });

  describe("postUrnenwahlvorbereitung", () => {
    it("should_callNotificationServiceWithSuccess_when_apiCallSucceeded", async () => {
      const wahlbezirkID = "wahlbezirkID";
      const urnenwahlvorbereitung = createUrnenwahlvorbereitung();
      const mappedUrnenwahlvorbereitungDto = createUrnenwahlvorbereitungDTO();

      mockDefinitions.toUrnenwahlvorbereitungWriteDto.mockReturnValue(
        mappedUrnenwahlvorbereitungDto
      );

      await postUrnenwahlvorbereitung(wahlbezirkID, urnenwahlvorbereitung);

      expect(mockDefinitions.addNotification.mock.calls).toStrictEqual([
        [
          "Urnenwahlvorbereitung erfolgreich gespeichert.",
          UserNotificationCategoryEnum.SUCCESS,
        ],
      ]);
      expect(
        mockDefinitions.postUrnenwahlvorbereitung.mock.calls
      ).toStrictEqual([[wahlbezirkID, mappedUrnenwahlvorbereitungDto]]);
    });

    it("should_throwApiErrorAndCallNotificationServiceWithError_when_apiCallFailed", async () => {
      const wahlbezirkID = "wahlbezirkID";
      const urnenwahlvorbereitung = createUrnenwahlvorbereitung();
      const mappedUrnenwahlvorbereitungWriteDto =
        createUrnenwahlvorbereitungWriteDTO();

      mockDefinitions.toUrnenwahlvorbereitungWriteDto.mockReturnValue(
        mappedUrnenwahlvorbereitungWriteDto
      );

      const mockedApiError = new Error("mocked api call failed");
      mockDefinitions.postUrnenwahlvorbereitung.mockRejectedValue(
        mockedApiError
      );

      await expect(
        postUrnenwahlvorbereitung(wahlbezirkID, urnenwahlvorbereitung)
      ).rejects.toThrow(mockedApiError);

      expect(mockDefinitions.addNotification.mock.calls).toStrictEqual([
        [
          "Speichern der Urnenwahlvorbereitung fehlgeschlagen.",
          UserNotificationCategoryEnum.ERROR,
        ],
      ]);
    });
  });

  describe("postBriefwahlvorbereitung", () => {
    it("should_callNotificationServiceWithSuccess_when_apiCallSucceeded", async () => {
      const wahlbezirkID = "wahlbezirkID";
      const briefwahlvorbereitung = createWahlvorbereitung();
      const mappedBriefwahlvorbereitungDto = createBriefwahlvorbereitungDTO();

      mockDefinitions.toBriefwahlvorbereitungWriteDto.mockReturnValue(
        mappedBriefwahlvorbereitungDto
      );

      await postBriefwahlvorbereitung(wahlbezirkID, briefwahlvorbereitung);

      expect(mockDefinitions.addNotification.mock.calls).toStrictEqual([
        [
          "Briefwahlvorbereitung erfolgreich gespeichert.",
          UserNotificationCategoryEnum.SUCCESS,
        ],
      ]);
      expect(
        mockDefinitions.postBriefwahlvorbereitung.mock.calls
      ).toStrictEqual([[wahlbezirkID, mappedBriefwahlvorbereitungDto]]);
    });

    it("should_throwApiErrorAndCallNotificationServiceWithError_when_apiCallFailed", async () => {
      const wahlbezirkID = "wahlbezirkID";
      const briefwahlvorbereitung = createWahlvorbereitung();
      const mappedBriefwahlvorbereitungWriteDto =
        createBriefwahlvorbereitungDTO();

      mockDefinitions.toBriefwahlvorbereitungWriteDto.mockReturnValue(
        mappedBriefwahlvorbereitungWriteDto
      );

      const mockedApiError = new Error("mocked api call failed");
      mockDefinitions.postBriefwahlvorbereitung.mockRejectedValue(
        mockedApiError
      );

      await expect(
        postBriefwahlvorbereitung(wahlbezirkID, briefwahlvorbereitung)
      ).rejects.toThrow(mockedApiError);

      expect(mockDefinitions.addNotification.mock.calls).toStrictEqual([
        [
          "Speichern der Briefwahlvorbereitung fehlgeschlagen.",
          UserNotificationCategoryEnum.ERROR,
        ],
      ]);
    });
  });
});
