import { useAxiosTestDataFactory } from "@tests/utils/common/AxiosTestDataFactory.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useBedenklicherStimmzettelTestDataFactory } from "@tests/utils/ergebnismeldung/MBW/bedenklicherStimmzettelTestDataFactory.ts";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useBedenklicheStimmzettelService } from "@/composables/ergebnismeldung/MBW/bedenklicheStimmzettelService.ts";
import { MbwRoutesEnum } from "@/types/navigation/MbwRoutesEnum.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  toModel: vi.fn(),
  toDTO: vi.fn(),
  validityEnumToDisplayString: vi.fn(),
  supplementEnumToDisplayString: vi.fn(),
  addNotification: vi.fn(),
  getBedenklicheStimmzettelByOrderIndexAsc: vi.fn(),
  setBedenklicheStimmzettel: vi.fn(),
  configurationConstructor: vi.fn(),
  setStepDone: vi.fn(),
}));

vi.mock(
  "@/api/wls-clients/generated-ergebnismeldung-api",
  async (importOriginal) => {
    const mod = (await importOriginal()) as object;
    return {
      ...mod,
      MbwBedenklicheStimmzettelControllerApi: class {
        getBedenklicheStimmzettelByOrderIndexAsc =
          mockDefinitions.getBedenklicheStimmzettelByOrderIndexAsc;
        setBedenklicheStimmzettel = mockDefinitions.setBedenklicheStimmzettel;
      },
      Configuration: mockDefinitions.configurationConstructor,
    };
  }
);

vi.mock(
  import("@/composables/ergebnismeldung/MBW/bedenklicherStimmzettelMapper.ts"),
  () => ({
    useBedenklicherStimmzettelMapper: () => ({
      toModel: mockDefinitions.toModel,
      toDTO: mockDefinitions.toDTO,
      validityEnumToDisplayString: mockDefinitions.validityEnumToDisplayString,
      supplementEnumToDisplayString: mockDefinitions.supplementEnumToDisplayString,
    }),
  })
);
vi.mock(
  import("@/composables/userNotification/userNotificationService.ts"),
  () => ({
    useUserNotificationService: () => ({
      addNotification: mockDefinitions.addNotification,
    }),
  })
);
vi.mock("@/stores/workflowStore.ts", () => ({
  useWorkflowStore: () => ({
    setStepDone: mockDefinitions.setStepDone,
  }),
}));

const { generateRandomString } = useCommonTestDataFactory();
const { createAxiosResponse } = useAxiosTestDataFactory();
const { createBedenklicherStimmzettelDTO, createBedenklicherStimmzettel } =
  useBedenklicherStimmzettelTestDataFactory();

describe("bedenklicheStimmzettelService.ts", () => {
  let unitUnderTest: ReturnType<typeof useBedenklicheStimmzettelService>;

  beforeEach(() => {
    unitUnderTest = useBedenklicheStimmzettelService();
  });

  afterEach(() => {
    vi.resetAllMocks();
  });

  describe("getBedenklicheStimmzettel", () => {
    it("should_returnMappedData_when_apiReturnsData", async () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);

      const mockedApiResponse = [createBedenklicherStimmzettelDTO()];
      mockDefinitions.getBedenklicheStimmzettelByOrderIndexAsc.mockReturnValue(
        createAxiosResponse({
          status: 200,
          data: mockedApiResponse,
        })
      );

      const mockedMappedResponse = createBedenklicherStimmzettel();
      mockDefinitions.toModel.mockReturnValue(mockedMappedResponse);

      const response = await unitUnderTest.getBedenklicheStimmzettel(
        wahlID,
        wahlbezirkID
      );

      expect(response).toStrictEqual([mockedMappedResponse]);
      expect(
        mockDefinitions.getBedenklicheStimmzettelByOrderIndexAsc.mock.calls
      ).toStrictEqual([[wahlID, wahlbezirkID]]);
      expect(mockDefinitions.toModel.mock.calls).toStrictEqual([
        [mockedApiResponse[0]],
      ]);
      expect(mockDefinitions.setStepDone.mock.calls).toStrictEqual([
        [wahlID, wahlbezirkID, MbwRoutesEnum.MBW_STAPEL_E],
      ]);
      expect(mockDefinitions.addNotification.mock.calls).toEqual([
        [expect.any(String), UserNotificationCategoryEnum.SUCCESS],
      ]);
    });

    it("should_returnNull_when_apiReturnsNoContent", async () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);

      mockDefinitions.getBedenklicheStimmzettelByOrderIndexAsc.mockReturnValue(
        createAxiosResponse({
          status: 204,
        })
      );

      const response = await unitUnderTest.getBedenklicheStimmzettel(
        wahlID,
        wahlbezirkID
      );

      expect(response).toStrictEqual(null);
      expect(
        mockDefinitions.getBedenklicheStimmzettelByOrderIndexAsc.mock.calls
      ).toStrictEqual([[wahlID, wahlbezirkID]]);
      expect(mockDefinitions.setStepDone.mock.calls.length).toStrictEqual(0);
      expect(mockDefinitions.addNotification.mock.calls).toEqual([
        [expect.any(String), UserNotificationCategoryEnum.SUCCESS],
      ]);
    });

    it("should_throwError_when_apiThrowsError", async () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);

      const mockedApiError = new Error("mocked api error");
      mockDefinitions.getBedenklicheStimmzettelByOrderIndexAsc.mockRejectedValue(
        mockedApiError
      );

      await expect(
        unitUnderTest.getBedenklicheStimmzettel(wahlID, wahlbezirkID)
      ).rejects.toThrow(
        new Error(
          `Laden von bedenklichen Stimmzetteln für wahlID > ${wahlID}, wahlbezirkID > ${wahlbezirkID} fehlgeschlagen`
        )
      );

      expect(
        mockDefinitions.getBedenklicheStimmzettelByOrderIndexAsc.mock.calls
      ).toStrictEqual([[wahlID, wahlbezirkID]]);
      expect(mockDefinitions.setStepDone.mock.calls.length).toStrictEqual(0);
      expect(mockDefinitions.addNotification.mock.calls).toEqual([
        [expect.any(String), UserNotificationCategoryEnum.ERROR],
      ]);
    });

    it.each([
      { sendNotification: false, expectedCalls: 0 },
      { sendNotification: true, expectedCalls: 1 },
    ])(
      "should_sendNotificationBasedOnParameter_when_sendNotificationIs'$sendNotification'AndProcessingWasSuccessful",
      async (testcaseArguments) => {
        const mockedApiResponse = [createBedenklicherStimmzettelDTO()];
        mockDefinitions.getBedenklicheStimmzettelByOrderIndexAsc.mockReturnValue(
          createAxiosResponse({
            status: 200,
            data: mockedApiResponse,
          })
        );

        const mockedMappedResponse = createBedenklicherStimmzettel();
        mockDefinitions.toModel.mockReturnValue(mockedMappedResponse);

        await unitUnderTest.getBedenklicheStimmzettel(
          generateRandomString(10),
          generateRandomString(10),
          testcaseArguments.sendNotification
        );

        expect(mockDefinitions.addNotification.mock.calls.length).toEqual(
          testcaseArguments.expectedCalls
        );
      }
    );

    it.each([
      { sendNotification: false, expectedCalls: 0 },
      { sendNotification: true, expectedCalls: 1 },
    ])(
      "should_sendNotificationBasedOnParameter_when_sendNotificationIs'$sendNotification'AndProcessingWasNotSuccessful",
      async (testcaseArguments) => {
        mockDefinitions.getBedenklicheStimmzettelByOrderIndexAsc.mockRejectedValue(
          new Error("mocked api error")
        );

        const mockedMappedResponse = createBedenklicherStimmzettel();
        mockDefinitions.toModel.mockReturnValue(mockedMappedResponse);

        await expect(
          unitUnderTest.getBedenklicheStimmzettel(
            generateRandomString(10),
            generateRandomString(10),
            testcaseArguments.sendNotification
          )
        ).rejects.toThrow();

        expect(mockDefinitions.addNotification.mock.calls.length).toEqual(
          testcaseArguments.expectedCalls
        );
      }
    );
  });

  describe("saveBedenklicheStimmzettel", () => {
    it("should_sendDTO_when_modelIsGiven", async () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const bedenklicheStimmzettelToSave = [createBedenklicherStimmzettel()];

      const mockedMappedRequest = createBedenklicherStimmzettelDTO();
      mockDefinitions.toDTO.mockReturnValue(mockedMappedRequest);

      await unitUnderTest.saveBedenklicheStimmzettel(
        wahlID,
        wahlbezirkID,
        bedenklicheStimmzettelToSave
      );

      expect(
        mockDefinitions.setBedenklicheStimmzettel.mock.calls
      ).toStrictEqual([[wahlID, wahlbezirkID, [mockedMappedRequest]]]);
      expect(mockDefinitions.toDTO.mock.calls).toStrictEqual([
        [bedenklicheStimmzettelToSave[0]],
      ]);
      expect(mockDefinitions.setStepDone.mock.calls).toStrictEqual([
        [wahlID, wahlbezirkID, MbwRoutesEnum.MBW_STAPEL_E],
      ]);
      expect(mockDefinitions.addNotification.mock.calls).toEqual([
        [expect.any(String), UserNotificationCategoryEnum.SUCCESS],
      ]);
    });

    it("should_throwError_when_apiThrewError", async () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const bedenklicheStimmzettelToSave = [createBedenklicherStimmzettel()];

      mockDefinitions.setBedenklicheStimmzettel.mockRejectedValue(
        new Error("mocked api error")
      );

      const mockedMappedRequest = createBedenklicherStimmzettelDTO();
      mockDefinitions.toDTO.mockReturnValue(mockedMappedRequest);

      await expect(
        unitUnderTest.saveBedenklicheStimmzettel(
          wahlID,
          wahlbezirkID,
          bedenklicheStimmzettelToSave
        )
      ).rejects.toThrow(
        new Error(
          `Speichern der bedenklichen Stimmzettel fehlgeschlagen für wahlID > ${wahlID}, wahlbezirkID > ${wahlbezirkID}`
        )
      );

      expect(
        mockDefinitions.setBedenklicheStimmzettel.mock.calls
      ).toStrictEqual([[wahlID, wahlbezirkID, [mockedMappedRequest]]]);
      expect(mockDefinitions.toDTO.mock.calls).toStrictEqual([
        [bedenklicheStimmzettelToSave[0]],
      ]);
      expect(mockDefinitions.setStepDone.mock.calls.length).toStrictEqual(0);
      expect(mockDefinitions.addNotification.mock.calls).toEqual([
        [expect.any(String), UserNotificationCategoryEnum.ERROR],
      ]);
    });

    it.each([
      { sendNotification: false, expectedCalls: 0 },
      { sendNotification: true, expectedCalls: 1 },
    ])(
      "should_sendNotificationBasedOnParameter_when_sendNotificationIs'$sendNotification'AndProcessingWasSuccessful",
      async (testcaseArguments) => {
        const mockedMappedRequest = createBedenklicherStimmzettelDTO();
        mockDefinitions.toDTO.mockReturnValue(mockedMappedRequest);

        await unitUnderTest.saveBedenklicheStimmzettel(
          generateRandomString(10),
          generateRandomString(10),
          [createBedenklicherStimmzettel()],
          testcaseArguments.sendNotification
        );

        expect(mockDefinitions.addNotification.mock.calls.length).toEqual(
          testcaseArguments.expectedCalls
        );
      }
    );

    it.each([
      { sendNotification: false, expectedCalls: 0 },
      { sendNotification: true, expectedCalls: 1 },
    ])(
      "should_sendNotificationBasedOnParameter_when_sendNotificationIs'$sendNotification'AndProcessingWasNotSuccessful",
      async (testcaseArguments) => {
        mockDefinitions.setBedenklicheStimmzettel.mockRejectedValue(
          new Error("mocked api error")
        );

        const mockedMappedRequest = createBedenklicherStimmzettelDTO();
        mockDefinitions.toDTO.mockReturnValue(mockedMappedRequest);

        await expect(
          unitUnderTest.saveBedenklicheStimmzettel(
            generateRandomString(10),
            generateRandomString(10),
            [createBedenklicherStimmzettel()],
            testcaseArguments.sendNotification
          )
        ).rejects.toThrow();

        expect(mockDefinitions.addNotification.mock.calls.length).toEqual(
          testcaseArguments.expectedCalls
        );
      }
    );
  });
});
