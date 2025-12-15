import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useStimmabgabevermerkeTestDataFactory } from "@tests/utils/stimmabgabevermerke/StimmabgabevermerkeTestDataFactory.ts";
import { createPinia, setActivePinia } from "pinia";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useStimmabgabevermerkeService } from "@/composables/stimmabgabevermerke/stimmabgabevermerkeService.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  getStimmabgabevermerke: vi.fn(),
  toModel: vi.fn(),
  addNotification: vi.fn(),
  postStimmabgabevermerke: vi.fn(),
  toDto: vi.fn(),
}));

vi.mock(
  "@/api/wls-clients/generated-ergebnismeldung-api",
  async (importOriginal) => {
    const mod = await importOriginal();
    return {
      ...(mod as object),
      StimmzettelumschlaegeControllerApi: vi.fn(),
      BegruendungControllerApi: vi.fn(),
      StimmabgabevermerkeControllerApi: vi.fn().mockImplementation(() => ({
        getStimmabgabevermerke: mockDefinitions.getStimmabgabevermerke,
        postStimmabgabevermerke: mockDefinitions.postStimmabgabevermerke,
      })),
      StimmzettelDTOStimmzettelartEnum: vi.fn(),
      Configuration: vi.fn(),
    };
  }
);
vi.mock(
  "@/composables/stimmabgabevermerke/stimmabgabevermerkeMapper.ts",
  () => ({
    useStimmabgabevermerkeMapper: () => ({
      toModel: mockDefinitions.toModel,
      toDto: mockDefinitions.toDto,
    }),
  })
);

vi.mock("@/composables/userNotification/userNotificationService.ts", () => ({
  useUserNotificationService: () => ({
    addNotification: mockDefinitions.addNotification,
  }),
}));

const { generateRandomString, generateRandomNumber } =
  useCommonTestDataFactory();
const {
  createStimmabgabevermerke,
  createStimmabgabevermerkeDTO,
  prepareStimmabgabevermerkeDTO,
} = useStimmabgabevermerkeTestDataFactory();

describe("stimmabgabevermekerService.ts", () => {
  const { getStimmabgabevermerke, postStimmabgabevermerke } =
    useStimmabgabevermerkeService();

  beforeEach(() => {
    setActivePinia(createPinia());
    vi.resetAllMocks();
    vi.clearAllMocks();
  });

  describe("getStimmabgabevermerke", () => {
    it("should_returnStimmabgabevermerke_when_parameterAreGiven", async () => {
      const waehlerverzeichnisNummer = generateRandomNumber(2);
      const wahlbezirkID = generateRandomString(10);
      const mockedStimmabgabevermerke = createStimmabgabevermerke();

      mockDefinitions.getStimmabgabevermerke.mockReturnValue(
        Promise.resolve({ status: 200, data: createStimmabgabevermerkeDTO() })
      );
      mockDefinitions.toModel.mockReturnValue(mockedStimmabgabevermerke);

      const result = await getStimmabgabevermerke(
        wahlbezirkID,
        waehlerverzeichnisNummer
      );

      expect(result).toEqual(mockedStimmabgabevermerke);
    });

    it("should_triggerNotification_when_anExceptionOccurredDuringApiCall", async () => {
      const waehlerverzeichnisNummer = generateRandomNumber(2);
      const wahlbezirkID = generateRandomString(10);

      mockDefinitions.getStimmabgabevermerke.mockRejectedValue(
        new Error("api called failed")
      );

      await expect(async () =>
        getStimmabgabevermerke(wahlbezirkID, waehlerverzeichnisNummer)
      ).rejects.toThrowError();

      expect(mockDefinitions.addNotification.mock.calls[0]).toEqual([
        expect.any(String),
        UserNotificationCategoryEnum.ERROR,
      ]);
    });

    it("should_notTriggerNotification_when_anExceptionOccurredDuringApiCall", async () => {
      const waehlerverzeichnisNummer = generateRandomNumber(2);
      const wahlbezirkID = generateRandomString(10);
      mockDefinitions.getStimmabgabevermerke.mockRejectedValue(
        new Error("api called failed")
      );

      await expect(async () =>
        getStimmabgabevermerke(wahlbezirkID, waehlerverzeichnisNummer, false)
      ).rejects.toThrowError();

      expect(mockDefinitions.addNotification.mock.calls.length).toStrictEqual(
        0
      );
    });
  });

  describe("postStimmabgabevermerke", () => {
    it("should_sendStimmabgabevermerke_when_noErrorAppear", async () => {
      const stimmabgabevermerk = createStimmabgabevermerke();
      const stimmabgabevermerkeDTO = prepareStimmabgabevermerkeDTO()
        .wahlbezirkID(stimmabgabevermerk.wahlbezirkID)
        .waehlerverzeichnisNummer(stimmabgabevermerk.waehlerverzeichnisNummer)
        .build();

      mockDefinitions.postStimmabgabevermerke.mockReturnValue(
        Promise.resolve({ status: 200 })
      );

      mockDefinitions.toDto.mockReturnValue(stimmabgabevermerkeDTO);

      await postStimmabgabevermerke(
        stimmabgabevermerk.wahlbezirkID,
        stimmabgabevermerk.waehlerverzeichnisNummer,
        stimmabgabevermerk
      );

      expect(mockDefinitions.postStimmabgabevermerke).toHaveBeenCalledWith(
        stimmabgabevermerk.wahlbezirkID,
        stimmabgabevermerk.waehlerverzeichnisNummer,
        stimmabgabevermerkeDTO
      );
      expect(mockDefinitions.addNotification.mock.calls).toEqual([
        [expect.any(String), UserNotificationCategoryEnum.SUCCESS],
      ]);
    });

    it("should_throwError_when_postStimmabgabevermerkeFailed", async () => {
      const stimmabgabevermerk = createStimmabgabevermerke();

      mockDefinitions.postStimmabgabevermerke.mockRejectedValue(
        new Error("api called failed")
      );

      await expect(
        postStimmabgabevermerke(
          stimmabgabevermerk.wahlbezirkID,
          stimmabgabevermerk.waehlerverzeichnisNummer,
          stimmabgabevermerk
        )
      ).rejects.toThrow("Post Stimmabgabevermerke Failed");

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
