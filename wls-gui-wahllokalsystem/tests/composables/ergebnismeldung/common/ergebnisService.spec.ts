import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useBegruendungTestDataFactory } from "@tests/utils/ergebnismeldung/common/begruendungTestDataFactory.ts";
import { useErgebnisseTestDataFactory } from "@tests/utils/ergebnismeldung/common/ergebnisseTestDataFactory.ts";
import { useStimmzettelumschlaegeTestDataFactory } from "@tests/utils/ergebnismeldung/common/StimmzettelumschlaegeTestDataFactory.ts";
import { useWahlTestDataFactory } from "@tests/utils/wahl/WahlTestDataFactory.ts";
import { createPinia, setActivePinia } from "pinia";
import {
  afterAll,
  afterEach,
  beforeEach,
  describe,
  expect,
  it,
  vi,
} from "vitest";
import { ref } from "vue";

import {
  BezirkUndWahlIDStapelartDTOStapelartEnum as DtoStapelArtEnum,
  GetErgebnisseStapelartEnum,
  PostErgebnisseStapelartEnum,
  SendErgebnisseMeldungsartEnum,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import { useErgebnisService } from "@/composables/ergebnismeldung/common/ergebnisService.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/common/StapelArtEnum.ts";
import { MbwRoutesEnum } from "@/types/navigation/MbwRoutesEnum.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  addNotification: vi.fn(),
  getErgebnisse: vi.fn(),
  postErgebnisse: vi.fn(),
  sendErgebnisse: vi.fn(),
  toErgebnisseModel: vi.fn(),
  toErgebnisseDto: vi.fn(),
  toGetErgebnisseStapelartEnum: vi.fn(),
  toPostErgebnisseStapelartEnum: vi.fn(),
  configurationConstructor: vi.fn(),
  bezirkUndWahlIDStapelartDTOStapelartEnum: vi.fn(),
  getErgebnisseStapelartEnum: vi.fn(),
  postErgebnisseStapelartEnum: vi.fn(),
  toStimmzettelumschlaegeDto: vi.fn(),
  toStimmzettelumschlaegeModel: vi.fn(),
  postStimmzettelumschlaege: vi.fn(),
  getStimmzettelumschlaege: vi.fn(),
  getBegruendung: vi.fn(),
  postBegruendung: vi.fn(),
  toBegruendungModel: vi.fn(),
  toBegruendungDto: vi.fn(),
  setStepDone: vi.fn(),
}));

vi.mock("@/stores/workflowStore.ts", () => ({
  useWorkflowStore: () => ({
    isMbwStapelAErfasst: ref(false),
    isMbwStapelBErfasst: ref(true),
    setStepDone: mockDefinitions.setStepDone,
  }),
}));

vi.mock(
  "@/api/wls-clients/generated-ergebnismeldung-api",
  async (importOriginal) => {
    const mod = await importOriginal();
    return {
      ...(mod as object),
      ErgebnisseControllerApi: vi.fn().mockImplementation(
        class MockedErgebnisseControllerApi {
          getErgebnisse = mockDefinitions.getErgebnisse;
          postErgebnisse = mockDefinitions.postErgebnisse;
        } as never
      ),
      ErgebnismeldungControllerApi: vi.fn().mockImplementation(
        class MockedErgebnismeldungControllerApi {
          sendErgebnisse = mockDefinitions.sendErgebnisse;
        } as never
      ),
      Configuration: mockDefinitions.configurationConstructor,
      BezirkUndWahlIDStapelartDTOStapelartEnum:
        mockDefinitions.bezirkUndWahlIDStapelartDTOStapelartEnum,
      GetErgebnisseStapelartEnum: mockDefinitions.getErgebnisseStapelartEnum,
      PostErgebnisseStapelartEnum: mockDefinitions.postErgebnisseStapelartEnum,
      StimmzettelumschlaegeControllerApi: vi.fn().mockImplementation(
        class MockedStimmzettelumschlaegeControllerApi {
          postStimmzettelumschlaege = mockDefinitions.postStimmzettelumschlaege;
          getStimmzettelumschlaege = mockDefinitions.getStimmzettelumschlaege;
        } as never
      ),
      BegruendungControllerApi: vi.fn().mockImplementation(
        class MockedBegruendungControllerApi {
          getBegruendung = mockDefinitions.getBegruendung;
          postBegruendung = mockDefinitions.postBegruendung;
        } as never
      ),
    };
  }
);
vi.mock("@/composables/ergebnismeldung/common/ergebnisMapper.ts", () => ({
  useErgebnisMapper: () => ({
    toErgebnisseModel: mockDefinitions.toErgebnisseModel,
    toErgebnisseDto: mockDefinitions.toErgebnisseDto,
    toGetErgebnisseStapelartEnum: mockDefinitions.toGetErgebnisseStapelartEnum,
    toPostErgebnisseStapelartEnum:
      mockDefinitions.toPostErgebnisseStapelartEnum,
    toStimmzettelumschlaegeDto: mockDefinitions.toStimmzettelumschlaegeDto,
    toStimmzettelumschlaegeModel: mockDefinitions.toStimmzettelumschlaegeModel,
    toBegruendungModel: mockDefinitions.toBegruendungModel,
    toBegruendungDto: mockDefinitions.toBegruendungDto,
  }),
}));
vi.mock("@/composables/userNotification/userNotificationService.ts", () => ({
  useUserNotificationService: () => ({
    addNotification: mockDefinitions.addNotification,
  }),
}));

const { generateRandomString, generateRandomNumber } =
  useCommonTestDataFactory();
const {
  createErgebnisse,
  createErgebnisseDTO,
  prepareErgebnisse,
  prepareErgebnis,
  prepareErgebnisDTO,
  prepareErgebnisseDTO,
} = useErgebnisseTestDataFactory();
const { createStimmzettelumschlaege, createStimmzettelumschlaegeDto } =
  useStimmzettelumschlaegeTestDataFactory();
const { createWahl } = useWahlTestDataFactory();
const { createBegruendungDTO, prepareBegruendung } =
  useBegruendungTestDataFactory();

describe("ergebnisService.ts", () => {
  const {
    getErgebnisse,
    postErgebnisse,
    postSchnellmeldung,
    postStimmzettelumschlaege,
    getStimmzettelumschlaege,
    getBegruendungStimmzettelumschlaege,
    postBegruendung,
    postNiederschrift,
  } = useErgebnisService();

  beforeEach(() => {
    setActivePinia(createPinia());
  });

  afterEach(() => {
    vi.resetAllMocks();
  });

  afterAll(() => {
    vi.resetAllMocks();
  });

  describe("getErgebnisse", () => {
    it("should_returnErgebnisse_when_wahlIDWahlbezirkIdAndStapelArtGiven", async () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const stapelArtModel = StapelArtEnum.ObwA;
      const stapelArtDto = GetErgebnisseStapelartEnum.ObwA;

      const mockedErgebnisseModel = createErgebnisse();
      const mockedErgebnisseDto = createErgebnisseDTO();

      mockDefinitions.getErgebnisse.mockResolvedValue({
        status: 200,
        data: mockedErgebnisseDto,
      });
      mockDefinitions.toErgebnisseModel.mockReturnValue(mockedErgebnisseModel);
      mockDefinitions.toGetErgebnisseStapelartEnum.mockReturnValue(
        stapelArtDto
      );

      const result = await getErgebnisse(wahlbezirkID, wahlID, stapelArtModel);

      expect(result).toEqual(mockedErgebnisseModel);
      expect(mockDefinitions.getErgebnisse).toHaveBeenCalledWith(
        wahlbezirkID,
        wahlID,
        stapelArtDto
      );
      expect(mockDefinitions.toErgebnisseModel).toHaveBeenCalledWith(
        mockedErgebnisseDto
      );
    });

    it("should_throwError_when_apiCallFailed", async () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const stapelArt = StapelArtEnum.ObwA;

      mockDefinitions.getErgebnisse.mockRejectedValue(
        new Error("mocked api call failed")
      );

      await expect(
        getErgebnisse(wahlbezirkID, wahlID, stapelArt)
      ).rejects.toThrowError();
    });

    it("should_notMapAndReturnNull_when_apiReturnsNoContent", async () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const stapelArt = StapelArtEnum.ObwA;

      mockDefinitions.getErgebnisse.mockResolvedValue({
        status: 204,
        data: undefined,
      });
      mockDefinitions.toGetErgebnisseStapelartEnum.mockReturnValue(
        GetErgebnisseStapelartEnum.ObwA
      );

      const result = await getErgebnisse(wahlbezirkID, wahlID, stapelArt);

      expect(result).toBeNull();
      expect(mockDefinitions.toErgebnisseModel).not.toHaveBeenCalled();
    });

    it("should_setStepDone_when_wahlIDWahlbezirkIdAndStapelArtMbwDUngueltigGiven", async () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const stapelArtModel = StapelArtEnum.MbwDUngueltig;
      const stapelArtDto = GetErgebnisseStapelartEnum.MbwDUngueltig;

      const mockedErgebnisseModel = createErgebnisse();
      const mockedErgebnisseDto = createErgebnisseDTO();

      mockDefinitions.getErgebnisse.mockResolvedValue({
        status: 200,
        data: mockedErgebnisseDto,
      });
      mockDefinitions.toErgebnisseModel.mockReturnValue(mockedErgebnisseModel);
      mockDefinitions.toGetErgebnisseStapelartEnum.mockReturnValue(
        stapelArtDto
      );

      const result = await getErgebnisse(wahlbezirkID, wahlID, stapelArtModel);

      expect(result).toEqual(mockedErgebnisseModel);
      expect(mockDefinitions.getErgebnisse).toHaveBeenCalledWith(
        wahlbezirkID,
        wahlID,
        stapelArtDto
      );
      expect(mockDefinitions.toErgebnisseModel).toHaveBeenCalledWith(
        mockedErgebnisseDto
      );
      expect(mockDefinitions.setStepDone).toHaveBeenCalledWith(
        wahlID,
        wahlbezirkID,
        MbwRoutesEnum.MBW_STAPEL_D_UNGUELTIG
      );
    });

    it("should_returnErgebnisseAndSetStepDoneForStapelAB_when_wahlIDWahlbezirkIdAndStapelArtMbwAGiven", async () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const stapelArtModel = StapelArtEnum.MbwA;
      const stapelArtDto = GetErgebnisseStapelartEnum.MbwA;

      const mockedErgebnisseModel = createErgebnisse();
      const mockedErgebnisseDto = createErgebnisseDTO();

      mockDefinitions.getErgebnisse.mockResolvedValue({
        status: 200,
        data: mockedErgebnisseDto,
      });
      mockDefinitions.toErgebnisseModel.mockReturnValue(mockedErgebnisseModel);
      mockDefinitions.toGetErgebnisseStapelartEnum.mockReturnValue(
        stapelArtDto
      );

      const result = await getErgebnisse(wahlbezirkID, wahlID, stapelArtModel);

      expect(result).toEqual(mockedErgebnisseModel);
      expect(mockDefinitions.getErgebnisse).toHaveBeenCalledWith(
        wahlbezirkID,
        wahlID,
        stapelArtDto
      );
      expect(mockDefinitions.toErgebnisseModel).toHaveBeenCalledWith(
        mockedErgebnisseDto
      );
      expect(mockDefinitions.setStepDone).toHaveBeenCalledWith(
        wahlID,
        wahlbezirkID,
        MbwRoutesEnum.MBW_STAPEL_A_AND_B
      );
    });

    it("should_returnErgebnisseAndNotSetStepDone_when_wahlIDWahlbezirkIdAndStapelArtMbwBGiven", async () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const stapelArtModel = StapelArtEnum.MbwB;
      const stapelArtDto = GetErgebnisseStapelartEnum.MbwB;

      const mockedErgebnisseModel = createErgebnisse();
      const mockedErgebnisseDto = createErgebnisseDTO();

      mockDefinitions.getErgebnisse.mockResolvedValue({
        status: 200,
        data: mockedErgebnisseDto,
      });
      mockDefinitions.toErgebnisseModel.mockReturnValue(mockedErgebnisseModel);
      mockDefinitions.toGetErgebnisseStapelartEnum.mockReturnValue(
        stapelArtDto
      );

      const result = await getErgebnisse(wahlbezirkID, wahlID, stapelArtModel);

      expect(result).toEqual(mockedErgebnisseModel);
      expect(mockDefinitions.getErgebnisse).toHaveBeenCalledWith(
        wahlbezirkID,
        wahlID,
        stapelArtDto
      );
      expect(mockDefinitions.toErgebnisseModel).toHaveBeenCalledWith(
        mockedErgebnisseDto
      );
      expect(mockDefinitions.setStepDone).not.toHaveBeenCalledWith(
        wahlID,
        wahlbezirkID,
        MbwRoutesEnum.MBW_STAPEL_A_AND_B
      );
    });
  });

  describe("postErgebnisse", () => {
    it("should_sendErgebnisse_when_wahlIDWahlbezirkIdStapelArtAndModelGiven", async () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const stapelArtModel = StapelArtEnum.ObwA;
      const stapelArtPostDto = PostErgebnisseStapelartEnum.ObwA;

      const mockedErgebnisseAsModels = [
        prepareErgebnis().ergebnis(1).build(),
        prepareErgebnis().ergebnis(5).build(),
      ];
      const mockedErgebnisseAsDtos = [
        prepareErgebnisDTO().ergebnis(1).build(),
        prepareErgebnisDTO().ergebnis(5).build(),
      ];
      const ergebnisseModelToSend = prepareErgebnisse()
        .bezirkUndWahlIDStapelart({
          wahlID: wahlID,
          wahlbezirkID: wahlbezirkID,
          stapelArt: stapelArtModel,
        })
        .ergebnisse(mockedErgebnisseAsModels)
        .build();
      const mockedErgebnisseDto = prepareErgebnisseDTO()
        .bezirkUndWahlIDStapelart({
          wahlID: wahlID,
          wahlbezirkID: wahlbezirkID,
          stapelart: DtoStapelArtEnum.ObwA,
        })
        .ergebnisse(mockedErgebnisseAsDtos)
        .build();

      mockDefinitions.toPostErgebnisseStapelartEnum.mockReturnValue(
        stapelArtPostDto
      );
      mockDefinitions.toErgebnisseDto.mockReturnValue(mockedErgebnisseDto);
      mockDefinitions.postErgebnisse.mockResolvedValue({});

      await postErgebnisse(
        wahlbezirkID,
        wahlID,
        stapelArtModel,
        ergebnisseModelToSend
      );

      expect(mockDefinitions.postErgebnisse).toHaveBeenCalledWith(
        wahlbezirkID,
        wahlID,
        stapelArtPostDto,
        mockedErgebnisseDto
      );
      expect(mockDefinitions.toErgebnisseDto).toHaveBeenCalledWith(
        ergebnisseModelToSend
      );
    });

    it("should_throwError_when_apiCallFailed", async () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const stapelArt = StapelArtEnum.ObwA;
      const ergebnisse = createErgebnisse();

      mockDefinitions.postErgebnisse.mockRejectedValue(
        new Error("mocked api call failed")
      );

      await expect(
        postErgebnisse(wahlbezirkID, wahlID, stapelArt, ergebnisse)
      ).rejects.toThrowError();
    });
  });

  describe("postSchnellmeldung", () => {
    it.each([
      [true, true],
      [false, false],
    ])(
      "should_callClientAndSendSuccessNotification%s_when_clientCallWasSuccessfulAndSendNotificationIs%s",
      async (sendNotificationParameter, sendNotificationCallIsExpected) => {
        const wahlID = generateRandomString(10);
        const wahlbezirkID = generateRandomString(10);
        const hauptwahlbezirkID = generateRandomString(10);
        const waehlerverzeichnisNummer = generateRandomNumber(2);

        await postSchnellmeldung(
          wahlID,
          wahlbezirkID,
          hauptwahlbezirkID,
          waehlerverzeichnisNummer,
          sendNotificationParameter
        );

        expect(mockDefinitions.sendErgebnisse.mock.calls).toStrictEqual([
          [
            wahlID,
            wahlbezirkID,
            waehlerverzeichnisNummer,
            SendErgebnisseMeldungsartEnum.V3,
            hauptwahlbezirkID,
          ],
        ]);

        if (sendNotificationCallIsExpected) {
          expect(mockDefinitions.addNotification).toHaveBeenCalledTimes(1);
          expect(mockDefinitions.addNotification.mock.calls).toEqual([
            [expect.any(String), UserNotificationCategoryEnum.SUCCESS],
          ]);
        } else {
          expect(mockDefinitions.addNotification).toHaveBeenCalledTimes(0);
        }
      }
    );

    it.each([
      [true, true],
      [false, false],
    ])(
      "should_throwError_when_apiCallFailed",
      async (sendNotificationParameter, sendNotificationCallIsExpected) => {
        const wahlID = generateRandomString(10);
        const wahlbezirkID = generateRandomString(10);
        const hauptwahlbezirkID = generateRandomString(10);
        const waehlerverzeichnisNummer = generateRandomNumber(2);

        const mockedApiError = new Error("mocked api call failed");
        mockDefinitions.sendErgebnisse.mockRejectedValue(mockedApiError);

        await expect(
          postSchnellmeldung(
            wahlID,
            wahlbezirkID,
            hauptwahlbezirkID,
            waehlerverzeichnisNummer,
            sendNotificationParameter
          )
        ).rejects.toThrowError(mockedApiError);

        if (sendNotificationCallIsExpected) {
          expect(mockDefinitions.addNotification).toHaveBeenCalledTimes(1);
          expect(mockDefinitions.addNotification.mock.calls).toEqual([
            [expect.any(String), UserNotificationCategoryEnum.ERROR],
          ]);
        } else {
          expect(mockDefinitions.addNotification).toHaveBeenCalledTimes(0);
        }
      }
    );
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
      expect(
        mockDefinitions.toStimmzettelumschlaegeDto.mock.calls
      ).toStrictEqual([[stimmzettelumschlaege, wahl.wahlID, wahlbezirkID]]);
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
      expect(
        mockDefinitions.toStimmzettelumschlaegeDto.mock.calls
      ).toStrictEqual([[stimmzettelumschlaege, wahl.wahlID, wahlbezirkID]]);
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
      expect(
        mockDefinitions.toStimmzettelumschlaegeDto.mock.calls
      ).toStrictEqual([[stimmzettelumschlaege, wahl.wahlID, wahlbezirkID]]);
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
      mockDefinitions.toStimmzettelumschlaegeModel.mockReturnValue(
        mockedStimmzettelumschlaege
      );

      const result = await getStimmzettelumschlaege(
        wahl,
        wahlbezirkID,
        "Stimmzettel",
        false
      );

      expect(mockDefinitions.addNotification.mock.calls.length).toStrictEqual(
        0
      );
      expect(
        mockDefinitions.toStimmzettelumschlaegeModel.mock.calls
      ).toStrictEqual([[dto]]);
      expect(result).toStrictEqual(mockedStimmzettelumschlaege);
      expect(mockDefinitions.setStepDone).toHaveBeenCalledWith(
        wahl.wahlID,
        wahlbezirkID,
        MbwRoutesEnum.MBW_AUSZAEHLUNG_STIMMZETTEL
      );
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
      mockDefinitions.toStimmzettelumschlaegeModel.mockReturnValue(
        mockedStimmzettelumschlaege
      );

      const result = await getStimmzettelumschlaege(
        wahl,
        wahlbezirkID,
        "Stimmzettel",
        true
      );

      expect(mockDefinitions.addNotification.mock.calls).toEqual([
        [expect.any(String), UserNotificationCategoryEnum.SUCCESS],
      ]);
      expect(
        mockDefinitions.toStimmzettelumschlaegeModel.mock.calls
      ).toStrictEqual([[dto]]);
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
      expect(
        mockDefinitions.toStimmzettelumschlaegeModel
      ).not.toHaveBeenCalled();
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

  describe("postNiederschrift", () => {
    it("should_callClientAndSendingNotification_when_sendNotificationIsTrueAndCallSuccessful", async () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const hauptwahlbezirkID = generateRandomString(10);
      const waehlerverzeichnisNummer = generateRandomNumber(2);

      await postNiederschrift(
        wahlID,
        wahlbezirkID,
        waehlerverzeichnisNummer,
        hauptwahlbezirkID,
        true
      );

      expect(mockDefinitions.sendErgebnisse.mock.calls).toStrictEqual([
        [
          wahlID,
          wahlbezirkID,
          waehlerverzeichnisNummer,
          SendErgebnisseMeldungsartEnum.V1,
          hauptwahlbezirkID,
        ],
      ]);
      expect(mockDefinitions.addNotification).toHaveBeenCalledTimes(1);
      expect(mockDefinitions.addNotification.mock.calls).toEqual([
        [expect.any(String), UserNotificationCategoryEnum.SUCCESS],
      ]);
    });

    it("should_callClientWithoutSendingNotification_when_sendNotificationIsFalseAndCallSuccessful", async () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const hauptwahlbezirkID = generateRandomString(10);
      const waehlerverzeichnisNummer = generateRandomNumber(2);

      await postNiederschrift(
        wahlID,
        wahlbezirkID,
        waehlerverzeichnisNummer,
        hauptwahlbezirkID,
        false
      );

      expect(mockDefinitions.sendErgebnisse.mock.calls).toStrictEqual([
        [
          wahlID,
          wahlbezirkID,
          waehlerverzeichnisNummer,
          SendErgebnisseMeldungsartEnum.V1,
          hauptwahlbezirkID,
        ],
      ]);
      expect(mockDefinitions.addNotification).toHaveBeenCalledTimes(0);
    });

    it("should_callNotificationServiceAfterFailure_when_sendNotificationIsTrueAndCallFails", async () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const hauptwahlbezirkID = generateRandomString(10);
      const waehlerverzeichnisNummer = generateRandomNumber(2);
      const mockedApiError = new Error("mocked api call failed");

      mockDefinitions.sendErgebnisse.mockRejectedValue(mockedApiError);

      await expect(
        postNiederschrift(
          wahlID,
          wahlbezirkID,
          waehlerverzeichnisNummer,
          hauptwahlbezirkID,
          true
        )
      ).rejects.toThrowError(mockedApiError);

      expect(mockDefinitions.addNotification).toHaveBeenCalledTimes(1);
      expect(mockDefinitions.addNotification.mock.calls).toEqual([
        [expect.any(String), UserNotificationCategoryEnum.ERROR],
      ]);
    });

    it("should_notCallNotificationServiceAfterFailure_when_sendNotificationIsFalseAndCallFails", async () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const hauptwahlbezirkID = generateRandomString(10);
      const waehlerverzeichnisNummer = generateRandomNumber(2);
      const mockedApiError = new Error("mocked api call failed");

      mockDefinitions.sendErgebnisse.mockRejectedValue(mockedApiError);

      await expect(
        postNiederschrift(
          wahlID,
          wahlbezirkID,
          waehlerverzeichnisNummer,
          hauptwahlbezirkID,
          false
        )
      ).rejects.toThrowError(mockedApiError);
      expect(mockDefinitions.addNotification).toHaveBeenCalledTimes(0);
    });
  });
});
