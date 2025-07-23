import { useBeanstandeteWahlbriefeTestDataFactory } from "@tests/utils/briefwahl/BeanstandeteWahlbriefeTestDataFactory.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { describe, expect, it, vi } from "vitest";

import { useBriefwahlService } from "@/composables/briefwahl/briefwahlService.ts";

const mockDefinitions = vi.hoisted(() => ({
  getBeanstandeteWahlbriefe: vi.fn(),
  setBeanstandeteWahlbriefe: vi.fn(),
  configurationConstructor: vi.fn().mockImplementation(() => ({})),
  mapDtoToModel: vi.fn(),
  addNotification: vi.fn(),
}));

vi.mock("@/api/wls-clients/generated-briefwahl-api", () => ({
  BeanstandeteWahlbriefeControllerApi: vi.fn().mockImplementation(() => ({
    getBeanstandeteWahlbriefe: mockDefinitions.getBeanstandeteWahlbriefe,
    setBeanstandeteWahlbriefe: mockDefinitions.setBeanstandeteWahlbriefe,
  })),
  Configuration: mockDefinitions.configurationConstructor,
  WahlbriefdatenControllerApi: vi.fn(),
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

const {
  createBeanstandeteWahlbriefeDTO,
  createBeanstandeteWahlbriefe,
  createBeanstandeteWahlbriefeCreateDTO,
} = useBeanstandeteWahlbriefeTestDataFactory();
const { getBeanstandeteWahlbriefe, postBeanstandeteWahlbriefe } =
  useBriefwahlService();
const { generateRandomNumber, generateRandomString } =
  useCommonTestDataFactory();

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
    mockDefinitions.mapDtoToModel.mockReturnValue(mockedBeanstandeteWahlbriefe);

    const result = await getBeanstandeteWahlbriefe(wvzNr, wahlbezirkID);

    expect(result).toEqual(mockedBeanstandeteWahlbriefe);
  });

  it("should_triggerNotification_when_anExceptionOccurredDuringApiCall", async () => {
    const wvzNr = generateRandomNumber(1);
    const wahlbezirkID = generateRandomString(10);

    mockDefinitions.getBeanstandeteWahlbriefe.mockRejectedValue(
      new Error("mocked api call failed")
    );

    await expect(async () =>
      getBeanstandeteWahlbriefe(wvzNr, wahlbezirkID)
    ).rejects.toThrowError();

    expect(mockDefinitions.addNotification.mock.calls[0]).toEqual([
      expect.any(String),
      "Error",
    ]);
  });
});

describe("postBeanstandeteWahlbriefe", () => {
  it("should_postBeanstandeteWahlbriefe_when_calledWithValidParams", async () => {
    const wvzNr = generateRandomNumber(1);
    const wahlbezirkID = generateRandomString(10);
    const dto = createBeanstandeteWahlbriefeCreateDTO();

    mockDefinitions.setBeanstandeteWahlbriefe.mockReturnValue({});

    await postBeanstandeteWahlbriefe(dto, wahlbezirkID, wvzNr);

    expect(mockDefinitions.setBeanstandeteWahlbriefe).toHaveBeenCalledWith(
      wahlbezirkID,
      wvzNr,
      dto
    );
    expect(mockDefinitions.addNotification.mock.calls[0]).toEqual([
      expect.any(String),
      "Success",
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
      const dto = createBeanstandeteWahlbriefeCreateDTO();

      mockDefinitions.setBeanstandeteWahlbriefe.mockRejectedValueOnce(
        new Error("mocked api call failed")
      );

      await postBeanstandeteWahlbriefe(dto, wahlbezirkID, wvzNr);

      expect(mockDefinitions.setBeanstandeteWahlbriefe).toHaveBeenCalledWith(
        wahlbezirkID,
        wvzNr,
        dto
      );
      expect(mockDefinitions.addNotification.mock.calls[0]).toEqual([
        expect.any(String),
        "Error",
      ]);
    }
  );
});
