import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useBegruendungTestDataFactory } from "@tests/utils/ergebnismeldung/common/begruendungTestDataFactory.ts";
import { useStimmzettelumschlaegeTestDataFactory } from "@tests/utils/ergebnismeldung/common/StimmzettelumschlaegeTestDataFactory.ts";
import { useWahlTestDataFactory } from "@tests/utils/wahl/WahlTestDataFactory.ts";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useErgebnisermittlungService } from "@/composables/ergebnisermittlung/ergebnisermittlungService.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  postStimmzettelumschlaege: vi.fn(),
  getStimmzettelumschlaege: vi.fn(),
  getBegruendung: vi.fn(),
  postBegruendung: vi.fn(),
  addNotification: vi.fn(),
  toDto: vi.fn(),
  toModel: vi.fn(),
  toBegruendungModel: vi.fn(),
  toBegruendungDto: vi.fn(),
  toPostErgebnisseStapelartEnum: vi.fn(),
  configurationConstructor: vi.fn().mockImplementation(() => ({})),
}));

vi.mock(
  "@/api/wls-clients/generated-ergebnismeldung-api",
  async (importOriginal) => {
    const mod = await importOriginal();
    return {
      ...(mod as object),
      StimmzettelumschlaegeControllerApi: vi.fn().mockImplementation(() => ({
        postStimmzettelumschlaege: mockDefinitions.postStimmzettelumschlaege,
        getStimmzettelumschlaege: mockDefinitions.getStimmzettelumschlaege,
      })),
      BegruendungControllerApi: vi.fn().mockImplementation(() => ({
        getBegruendung: mockDefinitions.getBegruendung,
        postBegruendung: mockDefinitions.postBegruendung,
      })),
      Configuration: mockDefinitions.configurationConstructor,
    };
  }
);
vi.mock("@/composables/ergebnisermittlung/ergebnisermittlungMapper.ts", () => ({
  useErgebnisermittlungMapper: () => ({
    toDto: mockDefinitions.toDto,
    toModel: mockDefinitions.toModel,
  }),
}));
vi.mock("@/composables/ergebnismeldung/common/ergebnisMapper.ts", () => ({
  useErgebnisMapper: () => ({
    toBegruendungModel: mockDefinitions.toBegruendungModel,
    toBegruendungDto: mockDefinitions.toBegruendungDto,
    toPostErgebnisseStapelartEnum:
      mockDefinitions.toPostErgebnisseStapelartEnum,
  }),
}));
vi.mock("@/composables/userNotification/userNotificationService.ts", () => ({
  useUserNotificationService: () => ({
    addNotification: mockDefinitions.addNotification,
  }),
}));

const {
  postStimmzettelumschlaege,
  getStimmzettelumschlaege,
  getBegruendungStimmzettelumschlaege,
  postBegruendung,
} = useErgebnisermittlungService();

const { createStimmzettelumschlaege, createStimmzettelumschlaegeDto } =
  useStimmzettelumschlaegeTestDataFactory();
const { createBegruendungDTO, prepareBegruendung } =
  useBegruendungTestDataFactory();
const { generateRandomString } = useCommonTestDataFactory();
const { createWahl } = useWahlTestDataFactory();

describe("ergebnisermittlungService", () => {
  beforeEach(() => {
    vi.resetAllMocks();
    vi.clearAllMocks();
  });

  describe("postStimmzettelumschlaege", () => {
    it("should_notCallNotificationServiceAfterSuccess_when_sendNotificationParameterIsFalse", async () => {
      const wahl = createWahl();
      const wahlbezirkID = generateRandomString(10);
      const stimmzettelumschlaege = createStimmzettelumschlaege();

      await postStimmzettelumschlaege(
        wahl,
        wahlbezirkID,
        stimmzettelumschlaege,
        "Stimmzettel",
        false
      );

      expect(mockDefinitions.addNotification.mock.calls.length).toStrictEqual(
        0
      );
      expect(mockDefinitions.toDto.mock.calls).toStrictEqual([
        [stimmzettelumschlaege, wahl.wahlID, wahlbezirkID],
      ]);
    });

    it("should_callNotificationServiceAfterSuccess_when_sendNotificationParameterIsTrue", async () => {
      const wahl = createWahl();
      const wahlbezirkID = generateRandomString(10);
      const stimmzettelumschlaege = createStimmzettelumschlaege();

      await postStimmzettelumschlaege(
        wahl,
        wahlbezirkID,
        stimmzettelumschlaege,
        "Stimmzettel",
        true
      );

      expect(mockDefinitions.addNotification.mock.calls).toEqual([
        [expect.any(String), UserNotificationCategoryEnum.SUCCESS],
      ]);
      expect(mockDefinitions.toDto.mock.calls).toStrictEqual([
        [stimmzettelumschlaege, wahl.wahlID, wahlbezirkID],
      ]);
    });

    it("should_callNotificationServiceAfterFailure_when_sendNotificationParameterIsTrue", async () => {
      const wahl = createWahl();
      const wahlbezirkID = generateRandomString(10);
      const stimmzettelumschlaege = createStimmzettelumschlaege();

      mockDefinitions.postStimmzettelumschlaege.mockRejectedValue(
        new Error("mocked api call failed")
      );

      await expect(
        postStimmzettelumschlaege(
          wahl,
          wahlbezirkID,
          stimmzettelumschlaege,
          "Stimmzettel",
          true
        )
      ).rejects.toThrow("mocked api call failed");

      expect(mockDefinitions.addNotification.mock.calls).toEqual([
        [expect.any(String), UserNotificationCategoryEnum.ERROR],
      ]);
      expect(mockDefinitions.toDto.mock.calls).toStrictEqual([
        [stimmzettelumschlaege, wahl.wahlID, wahlbezirkID],
      ]);
    });
  });

  describe("getStimmzettelumschlaege", () => {
    it("should_notCallNotificationServiceAfterSuccess_when_sendNotificationParameterIsFalse", async () => {
      const wahl = createWahl();
      const wahlbezirkID = generateRandomString(10);
      const dto = createStimmzettelumschlaegeDto();
      mockDefinitions.getStimmzettelumschlaege.mockResolvedValue({
        status: 200,
        data: dto,
      });
      const mockedStimmzettelumschlaege = createStimmzettelumschlaege();
      mockDefinitions.toModel.mockReturnValue(mockedStimmzettelumschlaege);

      const result = await getStimmzettelumschlaege(
        wahl,
        wahlbezirkID,
        "Stimmzettel",
        false
      );

      expect(mockDefinitions.addNotification.mock.calls.length).toStrictEqual(
        0
      );
      expect(mockDefinitions.toModel.mock.calls).toStrictEqual([[dto]]);
      expect(result).toStrictEqual(mockedStimmzettelumschlaege);
    });

    it("should_callNotificationServiceAfterSuccess_when_sendNotificationParameterIsTrue", async () => {
      const wahl = createWahl();
      const wahlbezirkID = generateRandomString(10);
      const dto = createStimmzettelumschlaegeDto();
      mockDefinitions.getStimmzettelumschlaege.mockResolvedValue({
        status: 200,
        data: dto,
      });
      const mockedStimmzettelumschlaege = createStimmzettelumschlaege();
      mockDefinitions.toModel.mockReturnValue(mockedStimmzettelumschlaege);

      const result = await getStimmzettelumschlaege(
        wahl,
        wahlbezirkID,
        "Stimmzettel",
        true
      );

      expect(mockDefinitions.addNotification.mock.calls).toEqual([
        [expect.any(String), UserNotificationCategoryEnum.SUCCESS],
      ]);
      expect(mockDefinitions.toModel.mock.calls).toStrictEqual([[dto]]);
      expect(result).toStrictEqual(mockedStimmzettelumschlaege);
    });

    it("should_callNotificationServiceAfterFailure_when_sendNotificationParameterIsTrue", async () => {
      const wahl = createWahl();
      const wahlbezirkID = generateRandomString(10);

      mockDefinitions.getStimmzettelumschlaege.mockRejectedValue(
        new Error("mocked api call failed")
      );

      await expect(
        getStimmzettelumschlaege(wahl, wahlbezirkID, "Stimmzettel", true)
      ).rejects.toThrow("mocked api call failed");

      expect(mockDefinitions.addNotification.mock.calls).toEqual([
        [expect.any(String), UserNotificationCategoryEnum.ERROR],
      ]);
    });

    it("should_notMapAndReturnNull_when_apiReturns204", async () => {
      const wahl = createWahl();
      const wahlbezirkID = generateRandomString(10);
      mockDefinitions.getStimmzettelumschlaege.mockResolvedValue({
        status: 204,
        data: null,
      });

      const result = await getStimmzettelumschlaege(
        wahl,
        wahlbezirkID,
        "Stimmzettel",
        false
      );

      expect(result).toBeNull();
      expect(mockDefinitions.toModel).not.toHaveBeenCalled();
      expect(mockDefinitions.addNotification).not.toHaveBeenCalled();
    });
  });

  describe("getBegruendungStimmzettelumschlaege", () => {
    it("should_notCallNotificationServiceAfterSuccess_when_sendNotificationParameterIsFalse", async () => {
      const wahl = createWahl();
      const wahlbezirkID = generateRandomString(10);

      const dto = createBegruendungDTO();
      const mockedBegruendungModel = prepareBegruendung()
        .wahlID(wahl.wahlID)
        .stapelart(StapelArtEnum.StimmzettelUmschlaege)
        .grund("grund")
        .build();

      mockDefinitions.getBegruendung.mockResolvedValue({
        status: 200,
        data: dto,
      });
      mockDefinitions.toBegruendungModel.mockReturnValue(
        mockedBegruendungModel
      );

      const result = await getBegruendungStimmzettelumschlaege(
        wahl,
        wahlbezirkID,
        "Stimmzettel",
        false
      );

      expect(mockDefinitions.getBegruendung).toHaveBeenCalledWith(
        wahlbezirkID,
        wahl.wahlID,
        StapelArtEnum.StimmzettelUmschlaege
      );
      expect(mockDefinitions.toBegruendungModel.mock.calls).toStrictEqual([
        [dto],
      ]);
      expect(mockDefinitions.addNotification.mock.calls.length).toStrictEqual(
        0
      );
      expect(result).toStrictEqual(mockedBegruendungModel);
    });

    it("should_callNotificationServiceAfterSuccess_when_sendNotificationParameterIsTrue", async () => {
      const wahl = createWahl();
      const wahlbezirkID = generateRandomString(10);

      const dto = createBegruendungDTO();
      const mockedBegruendungModel = prepareBegruendung()
        .wahlID(wahl.wahlID)
        .stapelart(StapelArtEnum.StimmzettelUmschlaege)
        .grund("grund")
        .build();

      mockDefinitions.getBegruendung.mockResolvedValue({
        status: 200,
        data: dto,
      });
      mockDefinitions.toBegruendungModel.mockReturnValue(
        mockedBegruendungModel
      );

      const result = await getBegruendungStimmzettelumschlaege(
        wahl,
        wahlbezirkID,
        "Stimmzettel",
        true
      );

      expect(mockDefinitions.getBegruendung).toHaveBeenCalledWith(
        wahlbezirkID,
        wahl.wahlID,
        StapelArtEnum.StimmzettelUmschlaege
      );
      expect(mockDefinitions.toBegruendungModel.mock.calls).toStrictEqual([
        [dto],
      ]);
      expect(mockDefinitions.addNotification.mock.calls).toEqual([
        [expect.any(String), UserNotificationCategoryEnum.SUCCESS],
      ]);
      expect(result).toStrictEqual(mockedBegruendungModel);
    });

    it("should_notMapAndReturnNull_when_apiReturns204", async () => {
      mockDefinitions.getBegruendung.mockResolvedValue({
        status: 204,
        data: null,
      });

      const result = await getBegruendungStimmzettelumschlaege(
        createWahl(),
        "wahlbezirkID",
        "Stimmzettel",
        false
      );

      expect(result).toBeNull();
      expect(mockDefinitions.toBegruendungModel).not.toHaveBeenCalled();
      expect(mockDefinitions.addNotification).not.toHaveBeenCalled();
    });

    it("should_callNotificationServiceAfterFailure_when_sendNotificationParameterIsTrue", async () => {
      mockDefinitions.getBegruendung.mockRejectedValue(
        new Error("mocked api call failed")
      );

      await expect(
        getBegruendungStimmzettelumschlaege(
          createWahl(),
          "wahlbezirkID",
          "Stimmzettel",
          true
        )
      ).rejects.toThrow("mocked api call failed");

      expect(mockDefinitions.addNotification.mock.calls).toEqual([
        [expect.any(String), UserNotificationCategoryEnum.ERROR],
      ]);
    });
  });

  describe("postBegruendung", () => {
    it("should_notCallNotificationServiceAfterSuccess_when_sendNotificationParameterIsFalse", async () => {
      const begruendung = prepareBegruendung()
        .wahlID(generateRandomString(10))
        .stapelart(StapelArtEnum.StimmzettelUmschlaege)
        .grund("grund")
        .build();
      const wahlbezirkID = generateRandomString(10);

      await postBegruendung(begruendung, wahlbezirkID, false);

      expect(mockDefinitions.addNotification.mock.calls.length).toStrictEqual(
        0
      );
      expect(mockDefinitions.toBegruendungDto.mock.calls).toStrictEqual([
        [begruendung, wahlbezirkID],
      ]);
    });

    it("should_callNotificationServiceAfterSuccess_when_sendNotificationParameterIsTrue", async () => {
      const begruendung = prepareBegruendung()
        .wahlID(generateRandomString(10))
        .stapelart(StapelArtEnum.StimmzettelUmschlaege)
        .grund("grund")
        .build();
      const wahlbezirkID = generateRandomString(10);

      await postBegruendung(begruendung, wahlbezirkID, true);

      expect(mockDefinitions.addNotification.mock.calls).toEqual([
        [expect.any(String), UserNotificationCategoryEnum.SUCCESS],
      ]);
      expect(mockDefinitions.toBegruendungDto.mock.calls).toStrictEqual([
        [begruendung, wahlbezirkID],
      ]);
    });

    it("should_callNotificationServiceAfterFailure_when_sendNotificationParameterIsTrue", async () => {
      const begruendung = prepareBegruendung()
        .wahlID(generateRandomString(10))
        .stapelart(StapelArtEnum.StimmzettelUmschlaege)
        .grund("grund")
        .build();
      const wahlbezirkID = generateRandomString(10);

      mockDefinitions.postBegruendung.mockRejectedValue(
        new Error("mocked api call failed")
      );

      await expect(
        postBegruendung(begruendung, wahlbezirkID, true)
      ).rejects.toThrow("mocked api call failed");

      expect(mockDefinitions.addNotification.mock.calls).toEqual([
        [expect.any(String), UserNotificationCategoryEnum.ERROR],
      ]);
      expect(mockDefinitions.toBegruendungDto.mock.calls).toStrictEqual([
        [begruendung, wahlbezirkID],
      ]);
    });

    it("should_notCallNotificationServiceAfterFailure_when_sendNotificationParameterIsFalse", async () => {
      const begruendung = prepareBegruendung()
        .wahlID(generateRandomString(10))
        .stapelart(StapelArtEnum.StimmzettelUmschlaege)
        .grund("grund")
        .build();
      const wahlbezirkID = generateRandomString(10);

      mockDefinitions.postBegruendung.mockRejectedValue(
        new Error("mocked api call failed")
      );

      await expect(
        postBegruendung(begruendung, wahlbezirkID, false)
      ).rejects.toThrow("mocked api call failed");

      expect(mockDefinitions.addNotification.mock.calls.length).toStrictEqual(
        0
      );
      expect(mockDefinitions.toBegruendungDto.mock.calls).toStrictEqual([
        [begruendung, wahlbezirkID],
      ]);
    });
  });
});
