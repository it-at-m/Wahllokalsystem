import { useAxiosTestDataFactory } from "@tests/utils/common/AxiosTestDataFactory.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useStimmzettelerfassungTeamStatusTestDataFactory } from "@tests/utils/dse/StimmzettelerfassungTeamStatusTestDataFactory.ts";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useStimmzettelerfassungTeamStatusService } from "@/composables/dse/stimmzettelerfassungTeamStatusService.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const mockDefinitions = vi.hoisted(() => {
  const sentinelAxiosConfig = { sentinel: "config" };

  return {
    getStimmzettelerfassungTeamStatus: vi.fn(),
    saveStimmzettelerfassungTeamStatus: vi.fn(),
    configurationConstructor: vi.fn(),
    getNullOn204OrElseResponseData: vi.fn(),
    addNotification: vi.fn(),
    dtoToModel: vi.fn(),
    modelToDto: vi.fn(),
    getWahlNameOrBlankStringById: vi.fn(),
    requestAsOnlineOnly: vi.fn(() => sentinelAxiosConfig),
    sentinelAxiosConfig,
  };
});

vi.mock("@/api/wls-clients/generated-ergebnismeldung-api", () => ({
  StimmzettelerfassungTeamStatusControllerApi: class {
    getStimmzettelerfassungTeamStatus =
      mockDefinitions.getStimmzettelerfassungTeamStatus;
    saveStimmzettelerfassungTeamStatus =
      mockDefinitions.saveStimmzettelerfassungTeamStatus;
  },
  Configuration: mockDefinitions.configurationConstructor,
  // Export the DTO enum used in tests
  StimmzettelerfassungTeamStatusDTOStatusEnum: {
    Registriert: "Registriert",
  },
}));

vi.mock("@/composables/api/commonApiUtils.ts", () => ({
  useCommonApiUtils: () => ({
    getNullOn204OrElseResponseData:
      mockDefinitions.getNullOn204OrElseResponseData,
    axiosConfigWrapper: () => ({
      requestAsOnlineOnly: mockDefinitions.requestAsOnlineOnly,
    }),
  }),
}));

vi.mock("@/composables/userNotification/userNotificationService.ts", () => ({
  useUserNotificationService: () => ({
    addNotification: mockDefinitions.addNotification,
  }),
}));

vi.mock("@/composables/dse/stimmzettelerfassungTeamStatusMapper.ts", () => ({
  useStimmzettelerfassungTeamStatusMapper: () => ({
    dtoToModel: mockDefinitions.dtoToModel,
    modelToDto: mockDefinitions.modelToDto,
  }),
}));

vi.mock("@/stores/wahlenStore.ts", () => ({
  useWahlenStore: () => ({
    wahlenActions: {
      getWahlNameOrBlankStringById:
        mockDefinitions.getWahlNameOrBlankStringById,
    },
  }),
}));

const { generateRandomString } = useCommonTestDataFactory();
const { createAxiosResponse } = useAxiosTestDataFactory();
const {
  createStimmzettelerfassungTeamStatusDTOData,
  createStimmzettelerfassungTeamStatusDtoEnumValue,
  createStimmzettelerfassungTeamStatusModel,
} = useStimmzettelerfassungTeamStatusTestDataFactory();

describe("stimmzettelerfassungTeamStatusService.ts", () => {
  const { isSaving, loadErfassungTeamStatus, postErfassungTeamStatus } =
    useStimmzettelerfassungTeamStatusService();

  beforeEach(() => {
    vi.useFakeTimers({});
  });

  afterEach(() => {
    vi.resetAllMocks();
    vi.clearAllMocks();
    vi.useRealTimers();
  });

  describe("isSaving", () => {
    it("should_updateIsSaving_when_functionIsCalled", async () => {
      const teamID = generateRandomString(8);
      const wahlID = generateRandomString(8);
      const wahlbezirkID = generateRandomString(8);

      const model = createStimmzettelerfassungTeamStatusModel();
      const dto = createStimmzettelerfassungTeamStatusDTOData();
      mockDefinitions.modelToDto.mockReturnValue(dto);

      const timeout = 100;
      mockDefinitions.saveStimmzettelerfassungTeamStatus.mockReturnValue(
        new Promise((resolve) => {
          setTimeout(() => {
            resolve(createAxiosResponse({ status: 201 }));
          }, timeout);
        })
      );

      expect(isSaving.value).toBe(false);
      const servicePromise = postErfassungTeamStatus(
        wahlID,
        wahlbezirkID,
        teamID,
        model,
        true
      );
      expect(isSaving.value).toBe(true);

      vi.advanceTimersByTime(timeout);
      await servicePromise;
      expect(isSaving.value).toBe(false);
    });
  });

  describe("loadErfassungTeamStatus", () => {
    it("should_callApiAndReturnMappedModel_and_showSuccessNotification", async () => {
      const teamID = generateRandomString(8);
      const wahlID = generateRandomString(8);
      const wahlbezirkID = generateRandomString(8);

      const mockedResponse = createAxiosResponse({
        status: 200,
        data: createStimmzettelerfassungTeamStatusDtoEnumValue(),
      });

      mockDefinitions.getStimmzettelerfassungTeamStatus.mockResolvedValue(
        mockedResponse
      );
      mockDefinitions.getNullOn204OrElseResponseData.mockReturnValue(
        mockedResponse.data
      );

      const mappedModel = createStimmzettelerfassungTeamStatusModel();
      mockDefinitions.dtoToModel.mockReturnValue(mappedModel);
      mockDefinitions.getWahlNameOrBlankStringById.mockReturnValue("MBW");

      const result = await loadErfassungTeamStatus(
        wahlID,
        wahlbezirkID,
        teamID,
        true
      );

      expect(result).toEqual(mappedModel);
      expect(
        mockDefinitions.getStimmzettelerfassungTeamStatus.mock.calls
      ).toStrictEqual([[wahlID, wahlbezirkID, teamID]]);
      expect(mockDefinitions.dtoToModel.mock.calls.length).toBe(1);
      expect(mockDefinitions.addNotification.mock.calls[0]).toEqual([
        "Status 'REGISTRIERT' für MBW erfolgreich geladen.",
        UserNotificationCategoryEnum.SUCCESS,
      ]);
    });

    it("should_notShowNotification_when_sendNotificationIsFalse", async () => {
      const teamID = generateRandomString(8);
      const wahlID = generateRandomString(8);
      const wahlbezirkID = generateRandomString(8);

      const mockedResponse = createAxiosResponse({
        status: 200,
        data: createStimmzettelerfassungTeamStatusDtoEnumValue(),
      });
      mockDefinitions.getStimmzettelerfassungTeamStatus.mockResolvedValue(
        mockedResponse
      );
      mockDefinitions.getNullOn204OrElseResponseData.mockReturnValue(
        mockedResponse.data
      );
      mockDefinitions.dtoToModel.mockReturnValue(
        createStimmzettelerfassungTeamStatusModel()
      );
      mockDefinitions.getWahlNameOrBlankStringById.mockReturnValue("MBW");

      const result = await loadErfassungTeamStatus(
        wahlID,
        wahlbezirkID,
        teamID,
        false
      );

      expect(mockDefinitions.addNotification.mock.calls.length).toBe(0);
      expect(result).toEqual(createStimmzettelerfassungTeamStatusModel());
    });

    it("should_showErrorNotificationAndThrow_when_apiFails", async () => {
      const teamID = generateRandomString(8);
      const wahlID = generateRandomString(8);
      const wahlbezirkID = generateRandomString(8);

      mockDefinitions.getStimmzettelerfassungTeamStatus.mockRejectedValue(
        new Error("api failed")
      );
      mockDefinitions.getWahlNameOrBlankStringById.mockReturnValue("MBW");

      await expect(
        loadErfassungTeamStatus(wahlID, wahlbezirkID, teamID, true)
      ).rejects.toThrow();

      expect(mockDefinitions.addNotification.mock.calls[0][1]).toBe(
        UserNotificationCategoryEnum.ERROR
      );
    });

    it("should_notShowErrorNotification_when_sendNotificationIsFalseAndApiFails", async () => {
      const teamID = generateRandomString(8);
      const wahlID = generateRandomString(8);
      const wahlbezirkID = generateRandomString(8);

      mockDefinitions.getStimmzettelerfassungTeamStatus.mockRejectedValue(
        new Error("api failed")
      );
      mockDefinitions.getWahlNameOrBlankStringById.mockReturnValue("MBW");

      await expect(
        loadErfassungTeamStatus(wahlID, wahlbezirkID, teamID, false)
      ).rejects.toThrow();

      expect(mockDefinitions.addNotification.mock.calls.length).toBe(0);
    });

    it("should_throwAndShowError_when_dtoToModelThrows", async () => {
      const teamID = generateRandomString(8);
      const wahlID = generateRandomString(8);
      const wahlbezirkID = generateRandomString(8);

      const mockedResponse = createAxiosResponse({
        status: 200,
        data: createStimmzettelerfassungTeamStatusDtoEnumValue(),
      });
      mockDefinitions.getStimmzettelerfassungTeamStatus.mockResolvedValue(
        mockedResponse
      );
      mockDefinitions.getNullOn204OrElseResponseData.mockReturnValue(
        mockedResponse.data
      );
      mockDefinitions.dtoToModel.mockImplementation(() => {
        throw new Error("mapping failed");
      });
      mockDefinitions.getWahlNameOrBlankStringById.mockReturnValue("MBW");

      await expect(
        loadErfassungTeamStatus(wahlID, wahlbezirkID, teamID, true)
      ).rejects.toThrow("Get Team-Status für MBW failed.");
      expect(mockDefinitions.addNotification.mock.calls[0][1]).toBe(
        UserNotificationCategoryEnum.ERROR
      );
    });
  });

  describe("postErfassungTeamStatus", () => {
    it("should_callSaveApi_withMappedDto_and_showSuccessNotification", async () => {
      const teamID = generateRandomString(8);
      const wahlID = generateRandomString(8);
      const wahlbezirkID = generateRandomString(8);

      const model = createStimmzettelerfassungTeamStatusModel();
      const dto = createStimmzettelerfassungTeamStatusDTOData();
      mockDefinitions.modelToDto.mockReturnValue(dto);
      mockDefinitions.saveStimmzettelerfassungTeamStatus.mockResolvedValue({
        status: 201,
      });
      mockDefinitions.getWahlNameOrBlankStringById.mockReturnValue("MBW");

      await postErfassungTeamStatus(wahlID, wahlbezirkID, teamID, model, true);

      expect(mockDefinitions.requestAsOnlineOnly).toHaveBeenCalledTimes(1);
      expect(mockDefinitions.modelToDto.mock.calls).toStrictEqual([[model]]);
      expect(
        mockDefinitions.saveStimmzettelerfassungTeamStatus.mock.calls
      ).toStrictEqual([
        [
          wahlID,
          wahlbezirkID,
          teamID,
          dto,
          mockDefinitions.sentinelAxiosConfig,
        ],
      ]);
      expect(mockDefinitions.addNotification.mock.calls[0]).toEqual([
        "Team-Status für MBW erfolgreich gespeichert.",
        UserNotificationCategoryEnum.SUCCESS,
      ]);
    });

    it("should_notShowSuccessNotification_when_sendNotificationFalse", async () => {
      const teamID = generateRandomString(8);
      const wahlID = generateRandomString(8);
      const wahlbezirkID = generateRandomString(8);

      const model = createStimmzettelerfassungTeamStatusModel();
      const dto = createStimmzettelerfassungTeamStatusDTOData();
      mockDefinitions.modelToDto.mockReturnValue(dto);
      mockDefinitions.saveStimmzettelerfassungTeamStatus.mockResolvedValue({
        status: 201,
      });
      mockDefinitions.getWahlNameOrBlankStringById.mockReturnValue("MBW");

      await postErfassungTeamStatus(wahlID, wahlbezirkID, teamID, model, false);

      expect(mockDefinitions.addNotification.mock.calls.length).toBe(0);
    });

    it("should_showErrorNotificationAndThrow_when_apiFails", async () => {
      const teamID = generateRandomString(8);
      const wahlID = generateRandomString(8);
      const wahlbezirkID = generateRandomString(8);

      const model = createStimmzettelerfassungTeamStatusModel();
      const dto = createStimmzettelerfassungTeamStatusDTOData();
      mockDefinitions.modelToDto.mockReturnValue(dto);
      mockDefinitions.saveStimmzettelerfassungTeamStatus.mockRejectedValue(
        new Error("api failed")
      );
      mockDefinitions.getWahlNameOrBlankStringById.mockReturnValue("MBW");

      await expect(
        postErfassungTeamStatus(wahlID, wahlbezirkID, teamID, model, true)
      ).rejects.toThrow();

      expect(mockDefinitions.addNotification.mock.calls[0][1]).toBe(
        UserNotificationCategoryEnum.ERROR
      );
    });

    it("should_notShowErrorNotification_when_sendNotificationFalseAndApiFails", async () => {
      const teamID = generateRandomString(8);
      const wahlID = generateRandomString(8);
      const wahlbezirkID = generateRandomString(8);

      const model = createStimmzettelerfassungTeamStatusModel();
      const dto = createStimmzettelerfassungTeamStatusDTOData();
      mockDefinitions.modelToDto.mockReturnValue(dto);
      mockDefinitions.saveStimmzettelerfassungTeamStatus.mockRejectedValue(
        new Error("api failed")
      );
      mockDefinitions.getWahlNameOrBlankStringById.mockReturnValue("MBW");

      await expect(
        postErfassungTeamStatus(wahlID, wahlbezirkID, teamID, model, false)
      ).rejects.toThrow();

      expect(mockDefinitions.addNotification.mock.calls.length).toBe(0);
    });
  });
});
