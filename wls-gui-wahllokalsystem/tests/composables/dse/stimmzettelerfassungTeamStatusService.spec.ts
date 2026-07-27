import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { StimmzettelerfassungTeamStatusDTOStatusEnum } from "@/api/wls-clients/generated-ergebnismeldung-api";
import { useStimmzettelerfassungStatusTeamService } from "@/composables/dse/stimmzettelerfassungTeamStatusService.ts";
import { StimmzettelerfassungTeamStatusEnum } from "@/types/dse/StimmzettelerfassungTeamStatusEnum.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  getStimmzettelerfassungTeamStatus: vi.fn(),
  saveStimmzettelerfassungTeamStatus: vi.fn(),
  configurationConstructor: vi.fn(),
  getNullOn204OrElseResponseData: vi.fn(),
  addNotification: vi.fn(),
  dtoToModel: vi.fn(),
  modelToDto: vi.fn(),
  getWahlNameOrBlankStringById: vi.fn(),
}));

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

describe("stimmzettelerfassungTeamStatusService.ts", () => {
  const { loadErfassungTeamStatus, postErfassungTeamStatus } =
    useStimmzettelerfassungStatusTeamService();

  beforeEach(() => {
    vi.resetAllMocks();
    vi.clearAllMocks();
  });

  describe("loadErfassungTeamStatus", () => {
    it("should_callApiAndReturnMappedModel_and_showSuccessNotification", async () => {
      const teamID = generateRandomString(8);
      const wahlID = generateRandomString(8);
      const wahlbezirkID = generateRandomString(8);

      const mockedResponse = { status: 200, data: { status: "REGISTRIERT" } };
      mockDefinitions.getStimmzettelerfassungTeamStatus.mockResolvedValue(
        mockedResponse
      );
      mockDefinitions.getNullOn204OrElseResponseData.mockReturnValue(
        mockedResponse.data
      );

      const mappedModel = { status: "REGISTRIERT" };
      mockDefinitions.dtoToModel.mockReturnValue(mappedModel);
      mockDefinitions.getWahlNameOrBlankStringById.mockReturnValue("MBW");

      const result = await loadErfassungTeamStatus(
        teamID,
        wahlID,
        wahlbezirkID,
        true
      );

      expect(result).toEqual(mappedModel);
      expect(
        mockDefinitions.getStimmzettelerfassungTeamStatus.mock.calls
      ).toStrictEqual([[wahlID, wahlbezirkID, teamID]]);
      expect(mockDefinitions.dtoToModel.mock.calls.length).toBe(1);
      expect(mockDefinitions.addNotification.mock.calls[0]).toEqual([
        "Team-Status für MBW erfolgreich geladen.",
        UserNotificationCategoryEnum.SUCCESS,
      ]);
    });

    it("should_notShowNotification_when_sendNotificationFalse", async () => {
      const teamID = generateRandomString(8);
      const wahlID = generateRandomString(8);
      const wahlbezirkID = generateRandomString(8);

      const mockedResponse = { status: 200, data: { status: "REGISTRIERT" } };
      mockDefinitions.getStimmzettelerfassungTeamStatus.mockResolvedValue(
        mockedResponse
      );
      mockDefinitions.getNullOn204OrElseResponseData.mockReturnValue(
        mockedResponse.data
      );
      mockDefinitions.dtoToModel.mockReturnValue({ status: "REGISTRIERT" });
      mockDefinitions.getWahlNameOrBlankStringById.mockReturnValue("MBW");

      const result = await loadErfassungTeamStatus(
        teamID,
        wahlID,
        wahlbezirkID,
        false
      );

      expect(mockDefinitions.addNotification.mock.calls.length).toBe(0);
      expect(result).toEqual({ status: "REGISTRIERT" });
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
        loadErfassungTeamStatus(teamID, wahlID, wahlbezirkID, true)
      ).rejects.toThrow();

      expect(mockDefinitions.addNotification.mock.calls[0][1]).toBe(
        UserNotificationCategoryEnum.ERROR
      );
    });

    it("should_notShowErrorNotification_when_sendNotificationFalseAndApiFails", async () => {
      const teamID = generateRandomString(8);
      const wahlID = generateRandomString(8);
      const wahlbezirkID = generateRandomString(8);

      mockDefinitions.getStimmzettelerfassungTeamStatus.mockRejectedValue(
        new Error("api failed")
      );
      mockDefinitions.getWahlNameOrBlankStringById.mockReturnValue("MBW");

      await expect(
        loadErfassungTeamStatus(teamID, wahlID, wahlbezirkID, false)
      ).rejects.toThrow();

      expect(mockDefinitions.addNotification.mock.calls.length).toBe(0);
    });

    it("should_throwAndShowError_when_dtoToModelThrows", async () => {
      const teamID = generateRandomString(8);
      const wahlID = generateRandomString(8);
      const wahlbezirkID = generateRandomString(8);

      const mockedResponse = { status: 200, data: { status: "REGISTRIERT" } };
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
        loadErfassungTeamStatus(teamID, wahlID, wahlbezirkID, true)
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

      const model = { status: StimmzettelerfassungTeamStatusEnum.REGISTRIERT };
      const dto = {
        status: StimmzettelerfassungTeamStatusDTOStatusEnum.Registriert,
      };
      mockDefinitions.modelToDto.mockReturnValue(dto);
      mockDefinitions.saveStimmzettelerfassungTeamStatus.mockResolvedValue({
        status: 201,
      });
      mockDefinitions.getWahlNameOrBlankStringById.mockReturnValue("MBW");

      await postErfassungTeamStatus(wahlID, wahlbezirkID, teamID, model, true);

      expect(mockDefinitions.modelToDto.mock.calls).toStrictEqual([[model]]);
      expect(
        mockDefinitions.saveStimmzettelerfassungTeamStatus.mock.calls
      ).toStrictEqual([[wahlID, wahlbezirkID, teamID, dto]]);
      expect(mockDefinitions.addNotification.mock.calls[0]).toEqual([
        "Team-Status für MBW erfolgreich gespeichert.",
        UserNotificationCategoryEnum.SUCCESS,
      ]);
    });

    it("should_notShowSuccessNotification_when_sendNotificationFalse", async () => {
      const teamID = generateRandomString(8);
      const wahlID = generateRandomString(8);
      const wahlbezirkID = generateRandomString(8);

      const model = { status: StimmzettelerfassungTeamStatusEnum.REGISTRIERT };
      const dto = {
        status: StimmzettelerfassungTeamStatusDTOStatusEnum.Registriert,
      };
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

      const model = { status: StimmzettelerfassungTeamStatusEnum.REGISTRIERT };
      const dto = {
        status: StimmzettelerfassungTeamStatusDTOStatusEnum.Registriert,
      };
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

      const model = { status: StimmzettelerfassungTeamStatusEnum.REGISTRIERT };
      const dto = {
        status: StimmzettelerfassungTeamStatusDTOStatusEnum.Registriert,
      };
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

    it("should_handleEmptyWahlName_when_savingAndStillShowMessageWithBlank", async () => {
      const teamID = generateRandomString(8);
      const wahlID = generateRandomString(8);
      const wahlbezirkID = generateRandomString(8);

      const model = { status: StimmzettelerfassungTeamStatusEnum.REGISTRIERT };
      const dto = {
        status: StimmzettelerfassungTeamStatusDTOStatusEnum.Registriert,
      };
      mockDefinitions.modelToDto.mockReturnValue(dto);
      mockDefinitions.saveStimmzettelerfassungTeamStatus.mockResolvedValue({
        status: 201,
      });
      mockDefinitions.getWahlNameOrBlankStringById.mockReturnValue("");

      await postErfassungTeamStatus(wahlID, wahlbezirkID, teamID, model, true);

      // Erwartung: Notification-Text enthält kein 'undefined' und ist konsistent
      expect(mockDefinitions.addNotification.mock.calls[0][0]).toContain(
        "Team-Status für"
      );
    });
  });
});
