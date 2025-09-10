import type { PflegeWaehlerverzeichnis } from "@/types/wahlbezirk/PflegeWaehlerverzeichnis.ts";

import { useAxiosTestDataFactory } from "@tests/utils/common/AxiosTestDataFactory.ts";
import { usePflegeWaehlerverzeichnisTestDataFactory } from "@tests/utils/wahlhandlung/PflegeWaehlerverzeichnisTestDataFactory.ts";
import { afterEach, describe, expect, it, vi } from "vitest";

import { useWaehlerverzeichnisService } from "@/composables/wahlhandlung/waehlerverzeichnisService.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  addNotification: vi.fn(),
  configurationConstructor: vi.fn().mockImplementation(() => ({})),
  getWaehlerverzeichnis: vi.fn(),
  postWaehlerverzeichnis: vi.fn(),
  mapToPflegeWaehlerverzeichnis: vi.fn(),
  mapToWaehlerverzeichnisWriteDTO: vi.fn(),
}));
vi.mock("@/api/wls-clients/generated-wahlvorbereitung-api", () => ({
  WaehlerverzeichnisControllerApi: vi.fn().mockImplementation(() => ({
    getWaehlerverzeichnis: mockDefinitions.getWaehlerverzeichnis,
    postWaehlerverzeichnis: mockDefinitions.postWaehlerverzeichnis,
  })),
  Configuration: mockDefinitions.configurationConstructor,
}));
vi.mock("@/composables/userNotification/userNotificationService.ts", () => ({
  useUserNotificationService: () => ({
    addNotification: mockDefinitions.addNotification,
  }),
}));
vi.mock("@/composables/wahlhandlung/waehlerverzeichnisMapper.ts", () => ({
  useWaehlerverzeichnisMapper: () => ({
    toPflegeWaehlerverzeichnis: mockDefinitions.mapToPflegeWaehlerverzeichnis,
    toWaehlerverzeichnisWriteDTO:
      mockDefinitions.mapToWaehlerverzeichnisWriteDTO,
  }),
}));

const { createAxiosResponse } = useAxiosTestDataFactory();
const { createPflegeWaehlerverzeichnis, createWaehlerverzeichnisWriteDTO } =
  usePflegeWaehlerverzeichnisTestDataFactory();

describe("waehlerverzeichnisService.ts", () => {
  const unitUnderTest = useWaehlerverzeichnisService();

  afterEach(() => {
    vi.clearAllMocks();
    vi.resetAllMocks();
  });

  describe("createDefaultPflegeWaehlerverzeichnis", () => {
    it("should_returnPflegeWaehlerverzeichnisWithDefaultValues_when_called", () => {
      const result = unitUnderTest.createDefaultPflegeWaehlerverzeichnis();

      const expectedResult: PflegeWaehlerverzeichnis = {
        nachtraeglicheBerichtigung: false,
        waehlerverzeichnisUnchanged: true,
        mitteilungUeberUngueltigeWahlscheineErhalten: true,
      };
      expect(result).toStrictEqual(expectedResult);
    });
  });

  describe("getWaehlerverzeichnis", () => {
    it("should_returnMappedResponseData_when_responseHasData", async () => {
      const wahlbezirkID = "wahlbezirkID";
      const wvzNummer = 1;

      const mockedApiResponse = createAxiosResponse({
        status: 200,
        data: "responseString",
      });
      mockDefinitions.getWaehlerverzeichnis.mockResolvedValue(
        mockedApiResponse
      );
      const mockedMappedApiResponseData = createPflegeWaehlerverzeichnis();
      mockDefinitions.mapToPflegeWaehlerverzeichnis.mockReturnValue(
        mockedMappedApiResponseData
      );

      const result = await unitUnderTest.getWaehlerverzeichnis(
        wahlbezirkID,
        wvzNummer
      );

      expect(result).toStrictEqual(mockedMappedApiResponseData);
      expect(mockDefinitions.getWaehlerverzeichnis.mock.calls).toStrictEqual([
        [wahlbezirkID, wvzNummer],
      ]);
      expect(
        mockDefinitions.mapToPflegeWaehlerverzeichnis.mock.calls
      ).toStrictEqual([[mockedApiResponse.data]]);
    });

    it("should_returnDefaultResponseData_when_apiReturnsWithStatus204", async () => {
      const wahlbezirkID = "wahlbezirkID";
      const wvzNummer = 1;

      const mockedApiResponse = createAxiosResponse({
        status: 204,
      });
      mockDefinitions.getWaehlerverzeichnis.mockResolvedValue(
        mockedApiResponse
      );

      const result = await unitUnderTest.getWaehlerverzeichnis(
        wahlbezirkID,
        wvzNummer
      );

      expect(result).toStrictEqual(
        unitUnderTest.createDefaultPflegeWaehlerverzeichnis()
      );
      expect(mockDefinitions.getWaehlerverzeichnis.mock.calls).toStrictEqual([
        [wahlbezirkID, wvzNummer],
      ]);
      expect(
        mockDefinitions.mapToPflegeWaehlerverzeichnis.mock.calls.length
      ).toStrictEqual(0);
    });

    it("should_rethrowErrorAndSendNotification_when_apiThrowsError", async () => {
      const wahlbezirkID = "wahlbezirkID";
      const wvzNummer = 1;

      const mockedApiError = new Error("mocked api error");
      mockDefinitions.getWaehlerverzeichnis.mockRejectedValue(mockedApiError);

      await expect(
        unitUnderTest.getWaehlerverzeichnis(wahlbezirkID, wvzNummer)
      ).rejects.toThrowError(mockedApiError);
      expect(
        mockDefinitions.mapToPflegeWaehlerverzeichnis.mock.calls.length
      ).toStrictEqual(0);
      expect(mockDefinitions.addNotification.mock.calls).toEqual([
        [expect.any(String), UserNotificationCategoryEnum.ERROR],
      ]);
    });

    it("should_rethrowErrorButSendNoNotification_when_apiThrowsErrorAndSendNotificationIsFalse", async () => {
      const wahlbezirkID = "wahlbezirkID";
      const wvzNummer = 1;

      const mockedApiError = new Error("mocked api error");
      mockDefinitions.getWaehlerverzeichnis.mockRejectedValue(mockedApiError);

      await expect(
        unitUnderTest.getWaehlerverzeichnis(wahlbezirkID, wvzNummer, false)
      ).rejects.toThrowError(mockedApiError);
      expect(
        mockDefinitions.mapToPflegeWaehlerverzeichnis.mock.calls.length
      ).toStrictEqual(0);
      expect(mockDefinitions.addNotification.mock.calls.length).toStrictEqual(
        0
      );
    });
  });

  describe("postWaehlerverzeichnis", () => {
    it("should_callApiAndSendSuccessNotification_when_apiFinishedSuccessfully", async () => {
      const wahlbezirkID = "wahlbezirkID";
      const wvzNummer = 1;
      const pflegeWaehlerverzeichnis = createPflegeWaehlerverzeichnis();

      const mockedMappedRequestBody = createWaehlerverzeichnisWriteDTO();
      mockDefinitions.mapToWaehlerverzeichnisWriteDTO.mockReturnValue(
        mockedMappedRequestBody
      );

      await unitUnderTest.postWaehlerverzeichnis(
        wahlbezirkID,
        wvzNummer,
        pflegeWaehlerverzeichnis
      );

      expect(
        mockDefinitions.mapToWaehlerverzeichnisWriteDTO.mock.calls
      ).toStrictEqual([[pflegeWaehlerverzeichnis]]);
      expect(mockDefinitions.postWaehlerverzeichnis.mock.calls).toStrictEqual([
        [wahlbezirkID, wvzNummer, mockedMappedRequestBody],
      ]);
      expect(mockDefinitions.addNotification.mock.calls).toEqual([
        [expect.any(String), UserNotificationCategoryEnum.SUCCESS],
      ]);
    });

    it("should_callApiButNotSendSuccessNotification_when_apiFinishedSuccessfullyButSendNotificationIsFalse", async () => {
      const wahlbezirkID = "wahlbezirkID";
      const wvzNummer = 1;
      const pflegeWaehlerverzeichnis = createPflegeWaehlerverzeichnis();

      const mockedMappedRequestBody = createWaehlerverzeichnisWriteDTO();
      mockDefinitions.mapToWaehlerverzeichnisWriteDTO.mockReturnValue(
        mockedMappedRequestBody
      );

      await unitUnderTest.postWaehlerverzeichnis(
        wahlbezirkID,
        wvzNummer,
        pflegeWaehlerverzeichnis,
        false
      );

      expect(
        mockDefinitions.mapToWaehlerverzeichnisWriteDTO.mock.calls
      ).toStrictEqual([[pflegeWaehlerverzeichnis]]);
      expect(mockDefinitions.postWaehlerverzeichnis.mock.calls).toStrictEqual([
        [wahlbezirkID, wvzNummer, mockedMappedRequestBody],
      ]);
      expect(mockDefinitions.addNotification.mock.calls.length).toStrictEqual(
        0
      );
    });

    it("should_callApiAndSendErrorNotification_when_apiFinishedWithError", async () => {
      const wahlbezirkID = "wahlbezirkID";
      const wvzNummer = 1;
      const pflegeWaehlerverzeichnis = createPflegeWaehlerverzeichnis();

      const mockedMappedRequestBody = createWaehlerverzeichnisWriteDTO();
      mockDefinitions.mapToWaehlerverzeichnisWriteDTO.mockReturnValue(
        mockedMappedRequestBody
      );

      const mockedApiCallError = new Error("mocked api call error");
      mockDefinitions.postWaehlerverzeichnis.mockRejectedValue(
        mockedApiCallError
      );

      await expect(
        unitUnderTest.postWaehlerverzeichnis(
          wahlbezirkID,
          wvzNummer,
          pflegeWaehlerverzeichnis
        )
      ).rejects.toThrow(mockedApiCallError);

      expect(mockDefinitions.addNotification.mock.calls).toEqual([
        [expect.any(String), UserNotificationCategoryEnum.ERROR],
      ]);
    });

    it("should_callApiButNotSendErrorNotification_when_apiFinishedWithErrorButSendNotificationIsFalse", async () => {
      const wahlbezirkID = "wahlbezirkID";
      const wvzNummer = 1;
      const pflegeWaehlerverzeichnis = createPflegeWaehlerverzeichnis();

      const mockedMappedRequestBody = createWaehlerverzeichnisWriteDTO();
      mockDefinitions.mapToWaehlerverzeichnisWriteDTO.mockReturnValue(
        mockedMappedRequestBody
      );

      const mockedApiCallError = new Error("mocked api call error");
      mockDefinitions.postWaehlerverzeichnis.mockRejectedValue(
        mockedApiCallError
      );

      await expect(
        unitUnderTest.postWaehlerverzeichnis(
          wahlbezirkID,
          wvzNummer,
          pflegeWaehlerverzeichnis,
          false
        )
      ).rejects.toThrow(mockedApiCallError);

      expect(mockDefinitions.addNotification.mock.calls.length).toStrictEqual(
        0
      );
    });
  });
});
