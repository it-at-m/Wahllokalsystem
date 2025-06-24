import type { UrnenwahlSchliessungsUhrzeitWriteDTO } from "@/api/wls-clients/generated-wahlvorbereitung-api";

import { useWahlvorbereitungTestDataFactory } from "@tests/utils/wahlvorbereitung/WahlvorbereitungTestDataFactory.ts";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useWahlvorbereitungService } from "@/composables/wahlvorbereitung/wahlvorbereitungService.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  addNotification: vi.fn(),
  toUrnenwahlSchliessungsuhrzeitDTO: vi.fn(),
  toEroeffnungsuhrzeitWriteDTO: vi.fn(),
  postUrnenwahlSchliessungsUhrzeit: vi.fn(),
  postEroeffnungsuhrzeit: vi.fn(),
  getUrnenwahlVorbereitung: vi.fn(),
  postUrnenwahlvorbereitung: vi.fn(),
  toUrnenwahlvorbereitungModel: vi.fn(),
  toUrnenwahlvorbereitungWriteDto: vi.fn(),
}));

vi.mock("@/api/wls-clients/generated-wahlvorbereitung-api", () => ({
  UrnenwahlSchliessungsUhrzeitControllerApi: vi.fn().mockImplementation(() => ({
    postUrnenwahlSchliessungsUhrzeit:
      mockDefinitions.postUrnenwahlSchliessungsUhrzeit,
  })),
  EroeffnungsUhrzeitControllerApi: vi.fn().mockImplementation(() => ({
    postEroeffnungsuhrzeit: mockDefinitions.postEroeffnungsuhrzeit,
  })),
  UrnenwahlvorbereitungControllerApi: vi.fn().mockImplementation(() => ({
    getUrnenwahlVorbereitung: mockDefinitions.getUrnenwahlVorbereitung,
    postUrnenwahlvorbereitung: mockDefinitions.postUrnenwahlvorbereitung,
  })),
  Configuration: vi.fn(),
}));

vi.mock("@/composables/userNotification/userNotificationService.ts", () => ({
  useUserNotificationService: () => ({
    addNotification: mockDefinitions.addNotification,
  }),
}));

vi.mock("@/composables/wahlvorbereitung/wahlvorbereitungMapper.ts", () => ({
  useWahlvorbereitungMapper: () => ({
    toUrnenwahlSchliessungsuhrzeitDTO:
      mockDefinitions.toUrnenwahlSchliessungsuhrzeitDTO,
    toEroeffnungsuhrzeitWriteDTO: mockDefinitions.toEroeffnungsuhrzeitWriteDTO,
    toUrnenwahlvorbereitungModel: mockDefinitions.toUrnenwahlvorbereitungModel,
    toUrnenwahlvorbereitungWriteDto:
      mockDefinitions.toUrnenwahlvorbereitungWriteDto,
  }),
}));

const {
  postUrnenwahlSchliessungsuhrzeit,
  postEroeffnungsuhrzeit,
  getUrnenwahlvorbereitung,
  postUrnenwahlvorbereitung,
} = useWahlvorbereitungService();
const {
  createEroeffnungsUhrzeitWriteDTO,
  createUrnenwahlvorbereitungWriteDTO,
  createUrnenwahlvorbereitung,
  createUrnenwahlvorbereitungDTO,
} = useWahlvorbereitungTestDataFactory();

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
});
