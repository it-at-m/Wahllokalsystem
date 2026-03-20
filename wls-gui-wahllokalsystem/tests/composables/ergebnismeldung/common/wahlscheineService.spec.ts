import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useWahlscheineTestDataFactory } from "@tests/utils/ergebnismeldung/common/wahlscheineTestDataFactory.ts";
import { createPinia, setActivePinia } from "pinia";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useWahlscheineService } from "@/composables/ergebnismeldung/common/wahlscheineService.ts";
import { useWorkflowStore } from "@/stores/workflowStore.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const { createWahlscheine, prepareWahlscheineDTO } =
  useWahlscheineTestDataFactory();
const { generateRandomString } = useCommonTestDataFactory();
const mockDefinitions = vi.hoisted(() => ({
  getWahlscheine: vi.fn(),
  postWahlscheine: vi.fn(),
  configurationConstructor: vi.fn(),
  addNotification: vi.fn(),
  mapDtoToModel: vi.fn(),
  mapModelToDto: vi.fn(),
}));

vi.mock(
  "@/api/wls-clients/generated-ergebnismeldung-api",
  async (importOriginal) => {
    const mod = await importOriginal();
    return {
      ...(mod as object),
      WahlscheineControllerApi: vi.fn().mockImplementation(() => ({
        getWahlscheine: mockDefinitions.getWahlscheine,
        postWahlscheine: mockDefinitions.postWahlscheine,
      })),
      Configuration: mockDefinitions.configurationConstructor,
    };
  }
);

vi.mock("@/composables/userNotification/userNotificationService.ts", () => ({
  useUserNotificationService: () => ({
    addNotification: mockDefinitions.addNotification,
  }),
}));

vi.mock("@/composables/ergebnismeldung/common/wahlscheineMapper.ts", () => ({
  useWahlscheineMapper: () => ({
    toModel: mockDefinitions.mapDtoToModel,
    toDto: mockDefinitions.mapModelToDto,
  }),
}));

describe("wahlscheineService.ts", () => {
  const { getWahlscheine, postWahlscheine } = useWahlscheineService();
  beforeEach(() => {
    setActivePinia(createPinia());
    vi.resetAllMocks();
    vi.clearAllMocks();
  });

  describe("getWahlscheine", () => {
    it("should_returnWahlscheine_when_calledWithValidParameters", async () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const mockedWahlscheine = createWahlscheine();
      mockDefinitions.getWahlscheine.mockReturnValue(
        Promise.resolve({
          status: 200,
          data: createWahlscheine(),
        })
      );
      mockDefinitions.mapDtoToModel.mockReturnValue(mockedWahlscheine);

      expect(useWorkflowStore().isAnzahlWahlscheineErfasst).toBe(false);

      const result = await getWahlscheine(wahlID, wahlbezirkID);

      expect(useWorkflowStore().isAnzahlWahlscheineErfasst).toBe(true);
      expect(result).toEqual(mockedWahlscheine);
    });

    it("should_returnNull_when_apiReturned204", async () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      mockDefinitions.getWahlscheine.mockReturnValue(
        Promise.resolve({
          status: 204,
          data: {},
        })
      );

      const result = await getWahlscheine(wahlID, wahlbezirkID);

      expect(useWorkflowStore().isAnzahlWahlscheineErfasst).toBe(false);
      expect(result).toEqual(null);
    });

    it("should_triggerNotification_when_anExceptionOccurredDuringApiCall", async () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const mockedWahlscheine = createWahlscheine();
      mockDefinitions.getWahlscheine.mockRejectedValue(
        new Error("api called failed")
      );

      mockDefinitions.mapDtoToModel.mockReturnValue(mockedWahlscheine);

      await expect(async () =>
        getWahlscheine(wahlID, wahlbezirkID)
      ).rejects.toThrowError();
      expect(useWorkflowStore().isAnzahlWahlscheineErfasst).toBe(false);
      expect(mockDefinitions.addNotification.mock.calls[0]).toEqual([
        expect.any(String),
        UserNotificationCategoryEnum.ERROR,
      ]);
    });

    it("should_notTriggerNotification_when_anExceptionOccurredDuringApiCallAndNotificationIsDisabled", async () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);

      mockDefinitions.getWahlscheine.mockRejectedValue(
        new Error("api called failed")
      );

      await expect(async () =>
        getWahlscheine(wahlID, wahlbezirkID, false)
      ).rejects.toThrowError();
      expect(useWorkflowStore().isAnzahlWahlscheineErfasst).toBe(false);
      expect(mockDefinitions.addNotification.mock.calls.length).toStrictEqual(
        0
      );
    });
  });

  describe("postWahlscheine", () => {
    it("should_sendWahlscheine_when_noErrorAppear", async () => {
      const wahlschein = createWahlscheine();
      const wahlscheinDTO = prepareWahlscheineDTO()
        .bezirkUndWahlID(wahlschein.bezirkUndWahlID)
        .stimmabgabevermerke(wahlschein.stimmabgabevermerke as number)
        .build();

      mockDefinitions.postWahlscheine.mockReturnValue(
        Promise.resolve({ status: 200 })
      );

      mockDefinitions.mapModelToDto.mockReturnValue(wahlscheinDTO);

      expect(useWorkflowStore().isAnzahlWahlscheineErfasst).toBe(false);

      await postWahlscheine(
        wahlschein.bezirkUndWahlID.wahlID,
        wahlschein.bezirkUndWahlID.wahlbezirkID,
        wahlschein
      );

      expect(useWorkflowStore().isAnzahlWahlscheineErfasst).toBe(true);
      expect(mockDefinitions.postWahlscheine).toHaveBeenCalledWith(
        wahlschein.bezirkUndWahlID.wahlID,
        wahlschein.bezirkUndWahlID.wahlbezirkID,
        wahlscheinDTO
      );
      expect(mockDefinitions.mapModelToDto).toHaveBeenCalledWith(wahlschein);
      expect(mockDefinitions.addNotification.mock.calls).toEqual([
        [expect.any(String), UserNotificationCategoryEnum.SUCCESS],
      ]);
    });

    it("should_throwError_when_postWahlscheineFailed", async () => {
      const wahlschein = createWahlscheine();

      mockDefinitions.postWahlscheine.mockRejectedValue(
        new Error("api called failed")
      );

      await expect(
        postWahlscheine(
          wahlschein.bezirkUndWahlID.wahlID,
          wahlschein.bezirkUndWahlID.wahlbezirkID,
          wahlschein
        )
      ).rejects.toThrow("Post Wahlscheine Failed");
      expect(useWorkflowStore().isAnzahlWahlscheineErfasst).toBe(false);
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
