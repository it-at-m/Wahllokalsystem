import { useBroadcastTestDataFactory } from "@tests/utils/broadcast/BroadcastTestDataFactory.ts";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useCommonApiUtils } from "@/composables/api/commonApiUtils.ts";
import { useBroadcastService } from "@/composables/broadcast/broadcastService.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  mapDtoToModel: vi.fn(),
  addNotification: vi.fn(),
  configurationConstructor: vi.fn().mockImplementation(() => ({})),
  deleteMessage: vi.fn(),
  getMessage: vi.fn(),
}));

vi.mock("@/api/wls-clients/generated-broadcast-api", () => ({
  BroadcastControllerApi: vi.fn().mockImplementation(() => ({
    getMessage: mockDefinitions.getMessage,
    deleteMessage: mockDefinitions.deleteMessage,
  })),
  Configuration: mockDefinitions.configurationConstructor,
}));
vi.mock("@/composables/broadcast/broadcastMapper.ts", () => ({
  useBroadcastMapper: () => ({
    dtoToModel: mockDefinitions.mapDtoToModel,
  }),
}));
vi.mock("@/composables/userNotification/userNotificationService.ts", () => ({
  useUserNotificationService: () => ({
    addNotification: mockDefinitions.addNotification,
  }),
}));

const { createBroadcastMessage, createMessageDTO } =
  useBroadcastTestDataFactory();
const { axiosConfigWrapper } = useCommonApiUtils();

describe("BroadcastService.ts", () => {
  const { getMessage, deleteMessage } = useBroadcastService();

  beforeEach(() => {
    vi.resetAllMocks();
    vi.clearAllMocks();
  });

  describe("getMessage", () => {
    it("should_returnBroadcastMessage_when_messageIsReceivedFromApi", async () => {
      const wahlbezirkID = "wahlbezirkID";

      mockDefinitions.getMessage.mockReturnValue(
        Promise.resolve({ status: 200, data: createMessageDTO() })
      );

      const mockedMappedBroadcastMessage = createBroadcastMessage();
      mockDefinitions.mapDtoToModel.mockReturnValue(
        mockedMappedBroadcastMessage
      );

      const result = await getMessage(wahlbezirkID);

      expect(result).toStrictEqual(mockedMappedBroadcastMessage);

      expect(mockDefinitions.getMessage.mock.calls.length).toStrictEqual(1);
      expect(mockDefinitions.getMessage.mock.calls).toStrictEqual([
        [wahlbezirkID, axiosConfigWrapper().requestAsOnlineOnly()],
      ]);
    });

    it("should_returnNull_when_apiReturned204", async () => {
      const wahlbezirkID = "wahlbezirkID";

      mockDefinitions.getMessage.mockReturnValue(
        Promise.resolve({ status: 204, data: null })
      );

      const result = await getMessage(wahlbezirkID);

      expect(result).toBeNull();
    });

    it("should_triggerErrorNotification_when_anExceptionOccurredDuringApiCall", async () => {
      const wahlbezirkID = "wahlbezirkID";

      mockDefinitions.getMessage.mockRejectedValue(
        new Error("api called failed")
      );

      const result = await getMessage(wahlbezirkID);

      expect(result).toBeNull();

      expect(mockDefinitions.addNotification.mock.calls.length).toStrictEqual(
        1
      );
      expect(mockDefinitions.addNotification.mock.calls[0]).toEqual([
        expect.any(String),
        UserNotificationCategoryEnum.ERROR,
      ]);
    });

    it("shouldnotTriggerErrorNotification_when_anExceptionOccurredDuringApiCallButSendNotificationIsFalse", async () => {
      const wahlbezirkID = "wahlbezirkID";

      mockDefinitions.getMessage.mockRejectedValue(
        new Error("api called failed")
      );

      const result = await getMessage(wahlbezirkID, false);

      expect(result).toBeNull();

      expect(mockDefinitions.addNotification.mock.calls.length).toStrictEqual(
        0
      );
    });
  });

  describe("deleteMessage", () => {
    it("should_returnNothing_when_requestWasProcessedSuccessfully", async () => {
      const wahlbezirkID = "wahlbezirkID";

      mockDefinitions.deleteMessage.mockReturnValue(
        Promise.resolve({ status: 200 })
      );

      await deleteMessage(wahlbezirkID);

      expect(mockDefinitions.deleteMessage.mock.calls.length).toStrictEqual(1);
      expect(mockDefinitions.deleteMessage.mock.calls).toStrictEqual([
        [wahlbezirkID, axiosConfigWrapper().requestAsOnlineOnly()],
      ]);
    });

    it("should_triggerErrorNotification_when_anExceptionOccurredDuringApiCall", async () => {
      const wahlbezirkID = "wahlbezirkID";

      mockDefinitions.deleteMessage.mockRejectedValue(
        new Error("api called failed")
      );

      await deleteMessage(wahlbezirkID);

      expect(mockDefinitions.addNotification.mock.calls.length).toStrictEqual(
        1
      );
      expect(mockDefinitions.addNotification.mock.calls[0]).toEqual([
        expect.any(String),
        UserNotificationCategoryEnum.ERROR,
      ]);
    });
  });
});
