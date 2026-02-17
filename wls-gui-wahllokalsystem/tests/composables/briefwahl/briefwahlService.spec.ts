import type { Wahl } from "@/types/wahl/Wahl.ts";

import { useBeanstandeteWahlbriefeTestDataFactory } from "@tests/utils/briefwahl/BeanstandeteWahlbriefeTestDataFactory.ts";
import { useAxiosTestDataFactory } from "@tests/utils/common/AxiosTestDataFactory.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useWahlTestDataFactory } from "@tests/utils/wahl/WahlTestDataFactory.ts";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useBriefwahlService } from "@/composables/briefwahl/briefwahlService.ts";
import { ZurueckweisungsgrundEnum } from "@/types/briefwahl/ZurueckweisungsgrundEnum.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  getBeanstandeteWahlbriefe: vi.fn(),
  setBeanstandeteWahlbriefe: vi.fn(),
  configurationConstructor: vi.fn().mockImplementation(() => ({})),
  mapDtoToModel: vi.fn(),
  addNotification: vi.fn(),
  getWahlbriefdaten: vi.fn(),
}));

vi.mock("@/api/wls-clients/generated-briefwahl-api", () => ({
  BeanstandeteWahlbriefeControllerApi: vi.fn().mockImplementation(() => ({
    getBeanstandeteWahlbriefe: mockDefinitions.getBeanstandeteWahlbriefe,
    setBeanstandeteWahlbriefe: mockDefinitions.setBeanstandeteWahlbriefe,
  })),
  Configuration: mockDefinitions.configurationConstructor,
  WahlbriefdatenControllerApi: vi.fn().mockImplementation(() => ({
    getWahlbriefdaten: mockDefinitions.getWahlbriefdaten,
  })),
}));

vi.mock("@/composables/briefwahl/beanstandeteWahlbriefeMapper.ts", () => ({
  useBeanstandeteWahlbriefeMapper: () => ({
    toModel: mockDefinitions.mapDtoToModel,
  }),
}));

vi.mock("@/composables/userNotification/userNotificationService.ts", () => ({
  useUserNotificationService: () => ({
    addNotification: mockDefinitions.addNotification,
  }),
}));

describe("briefwahlService.ts", () => {
  const {
    createBeanstandeteWahlbriefeDTO,
    createBeanstandeteWahlbriefe,
    prepareBeanstandeteWahlbriefeCreateDTO,
  } = useBeanstandeteWahlbriefeTestDataFactory();
  const {
    getBeanstandeteWahlbriefe,
    postBeanstandeteWahlbriefe,
    getWahlbriefdaten,
  } = useBriefwahlService();
  const { generateRandomNumber, generateRandomString } =
    useCommonTestDataFactory();
  const { prepareWahl } = useWahlTestDataFactory();
  const { createAxiosResponse } = useAxiosTestDataFactory();

  beforeEach(() => {
    vi.resetAllMocks();
    vi.clearAllMocks();
  });

  describe("getBeanstandeteWahlbriefe", () => {
    it("should_returnBeanstandeteWahlbriefe_when_calledWithValidParams", async () => {
      const wvzNr = generateRandomNumber(1);
      const wahlbezirkID = generateRandomString(10);
      const mockedBeanstandeteWahlbriefe = createBeanstandeteWahlbriefe();

      mockDefinitions.getBeanstandeteWahlbriefe.mockReturnValue(
        Promise.resolve({
          status: 200,
          data: createBeanstandeteWahlbriefeDTO(),
        })
      );
      mockDefinitions.mapDtoToModel.mockReturnValue(
        mockedBeanstandeteWahlbriefe
      );

      const result = await getBeanstandeteWahlbriefe(wvzNr, wahlbezirkID);

      expect(result).toEqual(mockedBeanstandeteWahlbriefe);
      expect(mockDefinitions.addNotification.mock.calls).toEqual([
        [expect.any(String), UserNotificationCategoryEnum.SUCCESS],
      ]);
    });

    it("should_triggerNotification_when_anExceptionOccurredDuringApiCallAndSendNotificationIsTrue", async () => {
      const wvzNr = generateRandomNumber(1);
      const wahlbezirkID = generateRandomString(10);

      mockDefinitions.getBeanstandeteWahlbriefe.mockRejectedValue(
        new Error("mocked api call failed")
      );

      await expect(async () =>
        getBeanstandeteWahlbriefe(wvzNr, wahlbezirkID, true)
      ).rejects.toThrowError();

      expect(mockDefinitions.addNotification.mock.calls[0]).toEqual([
        expect.any(String),
        UserNotificationCategoryEnum.ERROR,
      ]);
    });

    it("should_notTriggerNotification_when_sendNotificationIsFalse", async () => {
      const wvzNr = generateRandomNumber(1);
      const wahlbezirkID = generateRandomString(10);

      mockDefinitions.getBeanstandeteWahlbriefe.mockRejectedValue(
        new Error("mocked api call failed")
      );

      await expect(async () =>
        getBeanstandeteWahlbriefe(wvzNr, wahlbezirkID, false)
      ).rejects.toThrowError();

      expect(mockDefinitions.addNotification.mock.calls.length).toStrictEqual(
        0
      );
    });
  });

  describe("postBeanstandeteWahlbriefe", () => {
    it("should_postBeanstandeteWahlbriefe_when_calledWithValidParams", async () => {
      const wvzNr = generateRandomNumber(1);
      const wahlbezirkID = generateRandomString(10);
      const beanstandeteWahlbriefe = [ZurueckweisungsgrundEnum.Zugelassen];
      const wahl = prepareWahl()
        .beanstandeteWahlbriefe(beanstandeteWahlbriefe)
        .waehlerverzeichnisNummer(wvzNr)
        .build();
      const mockedWahlenGroupedByWvzNr = new Map<number, Wahl[]>([
        [wvzNr, [wahl]],
      ]);
      const dto = prepareBeanstandeteWahlbriefeCreateDTO()
        .beanstandeteWahlbriefe({
          [wahl.wahlID]: beanstandeteWahlbriefe,
        })
        .build();

      mockDefinitions.setBeanstandeteWahlbriefe.mockReturnValue({});

      await postBeanstandeteWahlbriefe(
        mockedWahlenGroupedByWvzNr,
        wahlbezirkID
      );

      expect(mockDefinitions.setBeanstandeteWahlbriefe).toHaveBeenCalledWith(
        wahlbezirkID,
        wvzNr,
        dto
      );
      expect(mockDefinitions.addNotification.mock.calls[0]).toEqual([
        expect.any(String),
        UserNotificationCategoryEnum.SUCCESS,
      ]);
    });

    it.each([
      {
        when: "wahlbezirkIdEmpty",
        wvzNr: generateRandomNumber(1),
        wahlbezirkID: "",
      },
      {
        when: "wahlbezirkIdBlank",
        wvzNr: generateRandomNumber(1),
        wahlbezirkID: "  ",
      },
    ])(
      "should_showUserNotification_when_$when",
      async ({ wvzNr, wahlbezirkID }) => {
        const beanstandeteWahlbriefe = [ZurueckweisungsgrundEnum.Zugelassen];
        const wahl = prepareWahl()
          .beanstandeteWahlbriefe(beanstandeteWahlbriefe)
          .waehlerverzeichnisNummer(wvzNr)
          .build();
        const mockedWahlenGroupedByWvzNr = new Map<number, Wahl[]>([
          [wvzNr, [wahl]],
        ]);
        const dto = prepareBeanstandeteWahlbriefeCreateDTO()
          .beanstandeteWahlbriefe({
            [wahl.wahlID]: beanstandeteWahlbriefe,
          })
          .build();

        mockDefinitions.setBeanstandeteWahlbriefe.mockRejectedValueOnce(
          new Error("mocked api call failed")
        );

        await expect(async () =>
          postBeanstandeteWahlbriefe(mockedWahlenGroupedByWvzNr, wahlbezirkID)
        ).rejects.toThrowError();

        expect(mockDefinitions.setBeanstandeteWahlbriefe).toHaveBeenCalledWith(
          wahlbezirkID,
          wvzNr,
          dto
        );
        expect(mockDefinitions.addNotification.mock.calls).toEqual([
          [expect.any(String), UserNotificationCategoryEnum.ERROR],
        ]);
      }
    );
  });

  describe("getWahlbriefdaten", () => {
    it("should_returnWahlbriefdatenWithUndefinedValues_when-apiReturned204", async () => {
      const wahlbezirkID = generateRandomString(10);
      mockDefinitions.getWahlbriefdaten.mockReturnValue(
        createAxiosResponse({ status: 204, data: "" })
      );

      const expectedResult = {
        wahlbriefe: undefined,
        verzeichnisseUngueltige: undefined,
        nachtraege: undefined,
        nachtraeglichUeberbrachte: undefined,
        zeitNachtraeglichUeberbrachte: undefined,
      };
      const result = await getWahlbriefdaten(wahlbezirkID);

      expect(result).toStrictEqual(expectedResult);
    });
  });
});
