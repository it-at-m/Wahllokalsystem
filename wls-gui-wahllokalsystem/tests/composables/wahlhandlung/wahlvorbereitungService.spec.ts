import type {
  UrnenwahlSchliessungsUhrzeitDTO,
  UrnenwahlSchliessungsUhrzeitWriteDTO,
} from "@/api/wls-clients/generated-wahlvorbereitung-api";
import type { Wahlvorbereitung } from "@/types/wahlhandlung/Wahlvorbereitung.ts";

import { useAxiosTestDataFactory } from "@tests/utils/common/AxiosTestDataFactory.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useWahlvorbereitungTestDataFactory } from "@tests/utils/wahlhandlung/WahlvorbereitungTestDataFactory.ts";
import { createPinia, setActivePinia } from "pinia";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useWahlvorbereitungService } from "@/composables/wahlhandlung/wahlvorbereitungService.ts";
import { useWorkflowStore } from "@/stores/workflowStore.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  addNotification: vi.fn(),
  toUrnenwahlSchliessungsuhrzeitDTO: vi.fn(),
  toUrnenwahlSchliessungsuhrzeitModel: vi.fn(),
  toEroeffnungsuhrzeitWriteDTO: vi.fn(),
  getUrnenwahlSchliessungsUhrzeit: vi.fn(),
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
  UrnenwahlSchliessungsUhrzeitControllerApi: class {
    getUrnenwahlSchliessungsUhrzeit =
      mockDefinitions.getUrnenwahlSchliessungsUhrzeit;
    postUrnenwahlSchliessungsUhrzeit =
      mockDefinitions.postUrnenwahlSchliessungsUhrzeit;
  },
  EroeffnungsUhrzeitControllerApi: class {
    postEroeffnungsuhrzeit = mockDefinitions.postEroeffnungsuhrzeit;
    getEroeffnungsuhrzeit = mockDefinitions.getEroeffnungsuhrzeit;
  },
  UrnenwahlvorbereitungControllerApi: class {
    getUrnenwahlVorbereitung = mockDefinitions.getUrnenwahlVorbereitung;
    postUrnenwahlvorbereitung = mockDefinitions.postUrnenwahlvorbereitung;
  },
  BriefwahlvorbereitungControllerApi: class {
    getBriefwahlvorbereitung = mockDefinitions.getBriefwahlvorbereitung;
    postBriefwahlvorbereitung = mockDefinitions.postBriefwahlvorbereitung;
  },
  Configuration: vi.fn(),
}));

vi.mock(
  import("@/composables/userNotification/userNotificationService.ts"),
  () => ({
    useUserNotificationService: () => ({
      addNotification: mockDefinitions.addNotification,
    }),
  })
);

vi.mock(import("@/composables/wahlhandlung/wahlvorbereitungMapper.ts"), () => ({
  useWahlvorbereitungMapper: () => ({
    toUrnenwahlSchliessungsuhrzeitModel:
      mockDefinitions.toUrnenwahlSchliessungsuhrzeitModel,
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
  getUrnenwahlSchliessungsUhrzeit,
  postUrnenwahlSchliessungsuhrzeit,
  getEroeffnungsuhrzeit,
  postEroeffnungsuhrzeit,
  getUrnenwahlvorbereitung,
  getBriefwahlvorbereitung,
  postUrnenwahlvorbereitung,
  postBriefwahlvorbereitung,
} = useWahlvorbereitungService();
const {
  createEroeffnungsUhrzeitDTO,
  createEroeffnungsUhrzeitWriteDTO,
  creteUrnenwahlSchliessungsuhrzeit,
  createUrnenwahlvorbereitungWriteDTO,
  createUrnenwahlvorbereitung,
  createUrnenwahlvorbereitungDTO,
  createWahlvorbereitung,
  createBriefwahlvorbereitungDTO,
  prepareEroeffnungsuhrzeitDTO,
} = useWahlvorbereitungTestDataFactory();
const { generateRandomString, generateRandomDateTimeAsString } =
  useCommonTestDataFactory();
const { createAxiosResponse } = useAxiosTestDataFactory();

describe("wahlvorbereitungService", () => {
  beforeEach(() => {
    setActivePinia(createPinia());

    vi.resetAllMocks();
    vi.clearAllMocks();
  });

  describe("getUrnenwahlSchliessungsUhrzeit", () => {
    it("should_returnMappedApiResponse_when_apiCallWasSuccessful", async () => {
      const wahlbezirkID = "wahlbezirkID";

      const mockedApiResponseData: UrnenwahlSchliessungsUhrzeitDTO = {
        wahlbezirkID,
        schliessungsuhrzeit: generateRandomDateTimeAsString(),
      };
      mockDefinitions.getUrnenwahlSchliessungsUhrzeit.mockResolvedValue(
        createAxiosResponse({ status: 200, data: mockedApiResponseData })
      );

      const mockedMappingResult = creteUrnenwahlSchliessungsuhrzeit();
      mockDefinitions.toUrnenwahlSchliessungsuhrzeitModel.mockResolvedValue(
        mockedMappingResult
      );

      expect(useWorkflowStore().isStimmabgabeErfasst).toBe(false);

      const result = await getUrnenwahlSchliessungsUhrzeit(wahlbezirkID);

      expect(useWorkflowStore().isStimmabgabeErfasst).toBe(true);
      expect(result).toBe(mockedMappingResult);
      expect(
        mockDefinitions.getUrnenwahlSchliessungsUhrzeit
      ).toHaveBeenCalledWith(wahlbezirkID);
      expect(
        mockDefinitions.toUrnenwahlSchliessungsuhrzeitModel
      ).toHaveBeenCalledWith(mockedApiResponseData);
    });

    it("should_returnNull_when_apiReturns204", async () => {
      const wahlbezirkID = "wahlbezirkID";

      mockDefinitions.getUrnenwahlSchliessungsUhrzeit.mockResolvedValue(
        createAxiosResponse({ status: 204, data: {} })
      );

      const result = await getUrnenwahlSchliessungsUhrzeit(wahlbezirkID);

      expect(useWorkflowStore().isStimmabgabeErfasst).toBe(false);
      expect(result).toBeNull();
    });

    it.each([true, false])(
      "should_throwErrorAndSendNotification=%b_when_apiCallFailedAndSendNotification",
      async (sendNotificationParameter) => {
        const wahlbezirkID = "wahlbezirkID";
        const mockedApiError = new Error("mocked api call failed");
        mockDefinitions.getUrnenwahlSchliessungsUhrzeit.mockRejectedValue(
          mockedApiError
        );

        await expect(
          getUrnenwahlSchliessungsUhrzeit(
            wahlbezirkID,
            sendNotificationParameter
          )
        ).rejects.toThrow(mockedApiError);
        expect(useWorkflowStore().isStimmabgabeErfasst).toBe(false);
        expect(
          mockDefinitions.getUrnenwahlSchliessungsUhrzeit
        ).toHaveBeenCalledWith(wahlbezirkID);
        expect(mockDefinitions.addNotification.mock.calls.length).toStrictEqual(
          sendNotificationParameter ? 1 : 0
        );
      }
    );
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
      expect(useWorkflowStore().isStimmabgabeErfasst).toBe(false);
      expect(mockDefinitions.addNotification.mock.calls).toEqual([
        [expect.any(String), UserNotificationCategoryEnum.ERROR],
      ]);
      expect(
        mockDefinitions.toUrnenwahlSchliessungsuhrzeitDTO.mock.calls
      ).toStrictEqual([[expectedDate]]);
    });
  });

  describe("getEroeffnungsuhrzeit", () => {
    it("should_returnEroeffnungsuhrzeit_when_apiReturnsData", async () => {
      const wahlbezirkID = generateRandomString(10);

      const mockedApiResponseData = createEroeffnungsUhrzeitDTO();
      mockDefinitions.getEroeffnungsuhrzeit.mockReturnValue(
        createAxiosResponse({ status: 200, data: mockedApiResponseData })
      );

      expect(useWorkflowStore().isWahleroeffnungErfasst).toBe(false);

      const result = await getEroeffnungsuhrzeit(wahlbezirkID);

      expect(useWorkflowStore().isWahleroeffnungErfasst).toBe(true);
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

      expect(useWorkflowStore().isWahleroeffnungErfasst).toBe(false);
      expect(result).toBeNull();
    });

    it("should_returnNull_when_apiReturnsInvalidDate", async () => {
      const wahlbezirkID = generateRandomString(10);

      const mockedApiResponseData = prepareEroeffnungsuhrzeitDTO()
        .eroeffnungsuhrzeit("invalid date")
        .build();
      mockDefinitions.getEroeffnungsuhrzeit.mockReturnValue(
        createAxiosResponse({ status: 200, data: mockedApiResponseData })
      );

      const result = await getEroeffnungsuhrzeit(wahlbezirkID);

      expect(result).toBeNull();
      expect(mockDefinitions.getEroeffnungsuhrzeit.mock.calls).toStrictEqual([
        [wahlbezirkID],
      ]);
    });

    it("should_throwErrorAndSendNotification_when_apiFailed", async () => {
      const wahlbezirkID = generateRandomString(10);

      const mockedApiError = new Error("mocked api call failed");
      mockDefinitions.getEroeffnungsuhrzeit.mockRejectedValue(mockedApiError);

      await expect(getEroeffnungsuhrzeit(wahlbezirkID)).rejects.toThrow(
        mockedApiError
      );
      expect(useWorkflowStore().isWahleroeffnungErfasst).toBe(false);
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
      expect(useWorkflowStore().isWahleroeffnungErfasst).toBe(false);
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

      expect(useWorkflowStore().isWahleroeffnungErfasst).toBe(false);

      await postEroeffnungsuhrzeit(wahlbezirkID, uhrzeit);

      expect(useWorkflowStore().isWahleroeffnungErfasst).toBe(true);
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
      expect(useWorkflowStore().isWahleroeffnungErfasst).toBe(false);
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
      mockDefinitions.getUrnenwahlVorbereitung.mockResolvedValue(
        createAxiosResponse({
          status: 200,
          data: createUrnenwahlvorbereitungDTO(),
        })
      );
      expect(useWorkflowStore().isWahlumgebungErfasst).toBe(false);

      const result = await getUrnenwahlvorbereitung(wahlbezirkID);

      expect(useWorkflowStore().isWahlumgebungErfasst).toBe(true);
      expect(result).toEqual(expectedUrnenwahlvorbereitung);
    });

    it("should_returnNull_when_apiReturns204", async () => {
      const wahlbezirkID = "wahlbezirkID1";

      mockDefinitions.getUrnenwahlVorbereitung.mockResolvedValue(
        createAxiosResponse({
          status: 204,
        })
      );

      const result = await getUrnenwahlvorbereitung(wahlbezirkID);

      expect(useWorkflowStore().isWahlumgebungErfasst).toBe(false);
      expect(result).toBeNull();
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
      expect(useWorkflowStore().isWahlumgebungErfasst).toBe(false);
      expect(mockDefinitions.addNotification.mock.calls).toEqual([
        [
          "Fehler beim Laden der Urnenwahlvorbereitung.",
          UserNotificationCategoryEnum.ERROR,
        ],
      ]);
    });

    it("should_notCallNotificationServiceAfterFailure_when_sendNotificationParameterIsFalse", async () => {
      const wahlbezirkID = "wahlbezirkID1";

      const mockedApiError = new Error("API Error");
      mockDefinitions.getUrnenwahlVorbereitung.mockRejectedValue(
        mockedApiError
      );

      await expect(
        getUrnenwahlvorbereitung(wahlbezirkID, false)
      ).rejects.toThrow("API Error");
      expect(useWorkflowStore().isWahlumgebungErfasst).toBe(false);
      expect(mockDefinitions.addNotification.mock.calls.length).toStrictEqual(
        0
      );
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

      expect(useWorkflowStore().isWahlumgebungErfasst).toBe(false);

      await postUrnenwahlvorbereitung(wahlbezirkID, urnenwahlvorbereitung);

      expect(useWorkflowStore().isWahlumgebungErfasst).toBe(true);
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
      expect(useWorkflowStore().isWahlumgebungErfasst).toBe(false);
      expect(mockDefinitions.addNotification.mock.calls).toStrictEqual([
        [
          "Speichern der Urnenwahlvorbereitung fehlgeschlagen.",
          UserNotificationCategoryEnum.ERROR,
        ],
      ]);
    });
  });

  describe("getBriefwahlvorbereitung", () => {
    it("should_returnBriefwahlvorbereitung_when_apiCallSucceeded", async () => {
      const wahlbezirkID = "wahlbezirkID1";
      const expectedBriefwahlvorbereitung = createWahlvorbereitung();

      mockDefinitions.toBriefwahlvorbereitungModel.mockReturnValue(
        expectedBriefwahlvorbereitung
      );
      mockDefinitions.getBriefwahlvorbereitung.mockResolvedValue(
        createAxiosResponse({
          status: 200,
          data: createBriefwahlvorbereitungDTO(),
        })
      );

      expect(useWorkflowStore().isWahlumgebungErfasst).toBe(false);

      const result = await getBriefwahlvorbereitung(wahlbezirkID);

      expect(useWorkflowStore().isWahlumgebungErfasst).toBe(true);
      expect(result).toEqual(expectedBriefwahlvorbereitung);
    });

    it("should_throwErrorAndCallNotificationService_when_apiCallFails", async () => {
      const wahlbezirkID = "wahlbezirkID1";

      const mockedApiError = new Error("API Error");
      mockDefinitions.getBriefwahlvorbereitung.mockRejectedValue(
        mockedApiError
      );

      await expect(getBriefwahlvorbereitung(wahlbezirkID)).rejects.toThrow(
        "API Error"
      );
      expect(useWorkflowStore().isWahlumgebungErfasst).toBe(false);
      expect(mockDefinitions.addNotification.mock.calls).toEqual([
        [
          "Fehler beim Laden der Briefwahlvorbereitung.",
          UserNotificationCategoryEnum.ERROR,
        ],
      ]);
    });

    it("should_returnDefaultWahlvorbereitung_when_apiReturns204", async () => {
      const wahlbezirkID = "wahlbezirkID1";
      mockDefinitions.getBriefwahlvorbereitung.mockReturnValue(
        createAxiosResponse({ status: 204, data: "" })
      );

      const result = await getBriefwahlvorbereitung(wahlbezirkID);

      const expectedResult: Wahlvorbereitung = {
        wahlbezirkID,
        urneVersiegelt: false,
        urnenAnzahl: [],
      };
      expect(result).toStrictEqual(expectedResult);
      expect(useWorkflowStore().isWahlumgebungErfasst).toBe(false);
    });

    it("should_notCallNotificationServiceAfterFailure_when_sendNotificationParameterIsFalse", async () => {
      const wahlbezirkID = "wahlbezirkID1";

      const mockedApiError = new Error("API Error");
      mockDefinitions.getBriefwahlvorbereitung.mockRejectedValue(
        mockedApiError
      );

      await expect(
        getBriefwahlvorbereitung(wahlbezirkID, false)
      ).rejects.toThrow("API Error");
      expect(useWorkflowStore().isWahlumgebungErfasst).toBe(false);
      expect(mockDefinitions.addNotification.mock.calls.length).toStrictEqual(
        0
      );
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

      expect(useWorkflowStore().isWahlumgebungErfasst).toBe(false);

      await postBriefwahlvorbereitung(wahlbezirkID, briefwahlvorbereitung);

      expect(useWorkflowStore().isWahlumgebungErfasst).toBe(true);
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
      expect(useWorkflowStore().isWahlumgebungErfasst).toBe(false);
      expect(mockDefinitions.addNotification.mock.calls).toStrictEqual([
        [
          "Speichern der Briefwahlvorbereitung fehlgeschlagen.",
          UserNotificationCategoryEnum.ERROR,
        ],
      ]);
    });
  });
});
