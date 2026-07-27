import { useStimmzettelTestDataFactory } from "@tests/utils/dse/StimmzettelTestDataFactory.ts";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useStimmzettelService } from "@/composables/dse/stimmzettelService.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const { createStimmzettelOfTeamDTO, createStimmzettel } =
  useStimmzettelTestDataFactory();

const mockDefinitions = vi.hoisted(() => ({
  mapDtoToModel: vi.fn(),
  addNotification: vi.fn(),
  configurationConstructor: vi.fn(),
  getStimmzettel: vi.fn(),
}));

vi.mock(
  "@/api/wls-clients/generated-ergebnismeldung-api",
  async (importOriginal) => {
    const mod = await importOriginal();
    return {
      ...(mod as object),
      StimmzettelControllerApi: class {
        getStimmzettel = mockDefinitions.getStimmzettel;
      },
      Configuration: vi.fn(),
    };
  }
);

vi.mock(import("@/composables/dse/stimmzettelMapper.ts"), () => ({
  useStimmzettelMapper: () => ({
    toModel: mockDefinitions.mapDtoToModel,
  }),
}));

vi.mock(
  import("@/composables/userNotification/userNotificationService.ts"),
  () => ({
    useUserNotificationService: () => ({
      addNotification: mockDefinitions.addNotification,
    }),
  })
);

describe("stimmzettelService.ts", () => {
  const { getStimmzettel } = useStimmzettelService();

  beforeEach(() => {
    vi.resetAllMocks();
    vi.clearAllMocks();
  });

  describe("getStimmzettel", () => {
    it("should_returnStimmzettelList_when_responseIsReceivedFromApi", async () => {
      const wahlID = "wahlID";
      const wahlbezirkID = "wahlbezirkID";
      const teamID = "teamID";

      const dto1 = createStimmzettelOfTeamDTO();
      const dto2 = createStimmzettelOfTeamDTO();

      mockDefinitions.getStimmzettel.mockReturnValue(
        Promise.resolve({
          status: 200,
          data: [dto1, dto2],
        })
      );

      const model1 = createStimmzettel();
      const model2 = createStimmzettel();

      mockDefinitions.mapDtoToModel
        .mockReturnValueOnce(model1)
        .mockReturnValueOnce(model2);

      const result = await getStimmzettel(wahlID, wahlbezirkID, teamID);

      expect(result).toStrictEqual([model1, model2]);

      expect(mockDefinitions.getStimmzettel.mock.calls.length).toStrictEqual(1);
      expect(mockDefinitions.getStimmzettel.mock.calls[0]).toStrictEqual([
        wahlID,
        wahlbezirkID,
        teamID,
      ]);

      expect(mockDefinitions.mapDtoToModel.mock.calls).toStrictEqual([
        [dto1],
        [dto2],
      ]);
    });

    it("should_returnEmptyArray_when_apiReturned204", async () => {
      const wahlID = "wahlID";
      const wahlbezirkID = "wahlbezirkID";
      const teamID = "teamID";

      mockDefinitions.getStimmzettel.mockReturnValue(
        Promise.resolve({
          status: 204,
          data: null,
        })
      );

      const result = await getStimmzettel(wahlID, wahlbezirkID, teamID);

      expect(result).toStrictEqual([]);
    });

    it("should_returnEmptyArray_when_apiReturnedEmptyListWithStatus200", async () => {
      const wahlID = "wahlID";
      const wahlbezirkID = "wahlbezirkID";
      const teamID = "teamID";

      mockDefinitions.getStimmzettel.mockReturnValue(
        Promise.resolve({
          status: 200,
          data: [],
        })
      );

      const result = await getStimmzettel(wahlID, wahlbezirkID, teamID);

      expect(result).toStrictEqual([]);
      expect(mockDefinitions.mapDtoToModel).not.toHaveBeenCalled();
    });

    it("should_triggerErrorNotification_when_anExceptionOccurredDuringApiCall", async () => {
      const wahlID = "wahlID";
      const wahlbezirkID = "wahlbezirkID";
      const teamID = "teamID";

      mockDefinitions.getStimmzettel.mockRejectedValue(
        new Error("api call failed")
      );

      await expect(async () =>
        getStimmzettel(wahlID, wahlbezirkID, teamID)
      ).rejects.toThrowError();

      expect(mockDefinitions.addNotification.mock.calls.length).toStrictEqual(
        1
      );
      expect(mockDefinitions.addNotification.mock.calls[0]).toEqual([
        "Abrufen der Stimmzettel ist fehlgeschlagen",
        UserNotificationCategoryEnum.ERROR,
      ]);
    });

    it("should_notTriggerErrorNotification_when_anExceptionOccurredDuringApiCallButSendNotificationIsFalse", async () => {
      const wahlID = "wahlID";
      const wahlbezirkID = "wahlbezirkID";
      const teamID = "teamID";

      mockDefinitions.getStimmzettel.mockRejectedValue(
        new Error("api call failed")
      );

      await expect(async () =>
        getStimmzettel(wahlID, wahlbezirkID, teamID, false)
      ).rejects.toThrowError();

      expect(mockDefinitions.addNotification.mock.calls.length).toStrictEqual(
        0
      );
    });
  });
});
