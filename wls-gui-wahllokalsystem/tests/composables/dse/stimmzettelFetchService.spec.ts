import type { Mock } from "vitest";
import type { ComputedRef } from "vue";

import { useAxiosTestDataFactory } from "@tests/utils/common/AxiosTestDataFactory.ts";
import { useStimmzettelTestDataFactory } from "@tests/utils/dse/StimmzettelTestDataFactory.ts";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useLogging } from "@/composables/common/logging.ts";
import { useStimmzettelFetchService } from "@/composables/dse/stimmzettelFetchService.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  mapDtoToModel: vi.fn(),
  mapModelToDto: vi.fn(),
  addNotification: vi.fn(),
  configurationConstructor: vi.fn(),
  getStimmzettel: vi.fn(),
  postStimmzettel: vi.fn(),
  getAnzahlStimmzettel: vi.fn(),
}));

vi.mock(
  "@/api/wls-clients/generated-ergebnismeldung-api",
  async (importOriginal) => {
    const mod = await importOriginal();
    return {
      ...(mod as object),
      StimmzettelControllerApi: class {
        getStimmzettel = mockDefinitions.getStimmzettel;
        postStimmzettel = mockDefinitions.postStimmzettel;
        getAnzahlStimmzettel = mockDefinitions.getAnzahlStimmzettel;
      },
      Configuration: vi.fn(),
    };
  }
);

vi.mock(import("@/composables/dse/stimmzettelMapper.ts"), () => ({
  useStimmzettelMapper: () => ({
    toModel: mockDefinitions.mapDtoToModel,
    toDTO: mockDefinitions.mapModelToDto,
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

const { createAxiosResponse } = useAxiosTestDataFactory();
const { createStimmzettelOfTeamDTO, createPersistedStimmzettel } =
  useStimmzettelTestDataFactory();

const { logDebug } = useLogging("stimmzettelFetchService.spec.ts");

describe("stimmzettelFetchService.ts", () => {
  const wahlID = "wahlID";
  const wahlbezirkID = "wahlbezirkID";
  const teamID = "teamID";

  let unitUnderTest: ReturnType<typeof useStimmzettelFetchService>;

  beforeEach(() => {
    unitUnderTest = useStimmzettelFetchService();
  });

  afterEach(() => {
    vi.resetAllMocks();
    vi.clearAllMocks();
    vi.useRealTimers();
  });

  describe("isLoadingStimmzettel", () => {
    it("should_returnFalse_when_instantiated", () => {
      expect(unitUnderTest.isLoadingStimmzettel.value).toStrictEqual(false);
    });

    it.each([
      {
        isApiCallSuccessful: true,
        testcaseSuffix: "apiWasSuccessful",
      },
      {
        isApiCallSuccessful: false,
        testcaseSuffix: "apiWasNotSuccessful",
      },
    ])("should_updateState_when_$testcaseSuffix", async (testcaseArguments) => {
      await _runLoadingTestcase(
        testcaseArguments.isApiCallSuccessful,
        mockDefinitions.getStimmzettel,
        () => [createStimmzettelOfTeamDTO],
        () => unitUnderTest.loadStimmzettel(wahlID, wahlbezirkID, teamID),
        unitUnderTest.isLoadingStimmzettel
      );
    });
  });

  describe("isSavingStimmzettel", () => {
    it("should_returnFalse_when_instantiated", () => {
      expect(unitUnderTest.isSavingStimmzettel.value).toStrictEqual(false);
    });

    it.each([
      {
        isApiCallSuccessful: true,
        testcaseSuffix: "apiWasSuccessful",
      },
      {
        isApiCallSuccessful: false,
        testcaseSuffix: "apiWasNotSuccessful",
      },
    ])("should_updateState_when_$testcaseSuffix", async (testcaseArguments) => {
      await _runLoadingTestcase(
        testcaseArguments.isApiCallSuccessful,
        mockDefinitions.postStimmzettel,
        () => null,
        () => unitUnderTest.saveStimmzettel(wahlID, wahlbezirkID, teamID, []),
        unitUnderTest.isSavingStimmzettel
      );
    });
  });

  describe("isLoadingAnzahlStimmzettel", () => {
    it("should_returnFalse_when_instantiated", () => {
      expect(unitUnderTest.isLoadingAnzahlStimmzettel.value).toStrictEqual(
        false
      );
    });

    it.each([
      {
        isApiCallSuccessful: true,
        testcaseSuffix: "apiWasSuccessful",
      },
      {
        isApiCallSuccessful: false,
        testcaseSuffix: "apiWasNotSuccessful",
      },
    ])("should_updateState_when_$testcaseSuffix", async (testcaseArguments) => {
      await _runLoadingTestcase(
        testcaseArguments.isApiCallSuccessful,
        mockDefinitions.getAnzahlStimmzettel,
        () => 0,
        () => unitUnderTest.loadAnzahlStimmzettel(wahlID, wahlbezirkID),
        unitUnderTest.isLoadingAnzahlStimmzettel
      );
    });
  });

  describe("loadStimmzettel", () => {
    it("should_returnStimmzettelList_when_responseIsReceivedFromApi", async () => {
      const dto1 = createStimmzettelOfTeamDTO();
      const dto2 = createStimmzettelOfTeamDTO();

      mockDefinitions.getStimmzettel.mockReturnValue(
        Promise.resolve({
          status: 200,
          data: [dto1, dto2],
        })
      );

      const model1 = createPersistedStimmzettel();
      const model2 = createPersistedStimmzettel();

      mockDefinitions.mapDtoToModel
        .mockReturnValueOnce(model1)
        .mockReturnValueOnce(model2);

      await unitUnderTest.loadStimmzettel(wahlID, wahlbezirkID, teamID);

      expect(unitUnderTest.latestStimmzettelState.value).toStrictEqual([
        model1,
        model2,
      ]);

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

      await unitUnderTest.loadStimmzettel(wahlID, wahlbezirkID, teamID);

      expect(unitUnderTest.latestStimmzettelState.value).toStrictEqual([]);
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

      await unitUnderTest.loadStimmzettel(wahlID, wahlbezirkID, teamID);

      expect(unitUnderTest.latestStimmzettelState.value).toStrictEqual([]);
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
        unitUnderTest.loadStimmzettel(wahlID, wahlbezirkID, teamID)
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
        unitUnderTest.loadStimmzettel(wahlID, wahlbezirkID, teamID, false)
      ).rejects.toThrowError();

      expect(mockDefinitions.addNotification.mock.calls.length).toStrictEqual(
        0
      );
    });
  });

  describe("saveStimmzettel", () => {
    it("should_sendDTOAndUpdateLatestStimmzettel_when_modelIsGivenAndApiCallWasSuccessful", async () => {
      const wahlID = "wahlID";
      const wahlbezirkID = "wahlbezirkID";
      const teamID = "teamID";
      const stimmzettel = [createPersistedStimmzettel()];

      const mockedDto = createStimmzettelOfTeamDTO();

      mockDefinitions.mapModelToDto.mockReturnValue(mockedDto);

      expect(unitUnderTest.latestStimmzettelState.value).toStrictEqual(
        undefined
      );
      await unitUnderTest.saveStimmzettel(
        wahlID,
        wahlbezirkID,
        teamID,
        stimmzettel
      );

      expect(unitUnderTest.latestStimmzettelState.value).toStrictEqual(
        stimmzettel
      );
      expect(mockDefinitions.postStimmzettel.mock.calls).toStrictEqual([
        [wahlID, wahlbezirkID, teamID, [mockedDto]],
      ]);
      expect(mockDefinitions.mapModelToDto.mock.calls).toStrictEqual([
        stimmzettel,
      ]);
      expect(mockDefinitions.addNotification.mock.calls).toEqual([
        [expect.any(String), UserNotificationCategoryEnum.SUCCESS],
      ]);
    });

    it("should_triggerErrorNotification_when_anExceptionOccurredDuringApiCall", async () => {
      const wahlID = "wahlID";
      const wahlbezirkID = "wahlbezirkID";
      const teamID = "teamID";
      const stimmzettel = [createPersistedStimmzettel()];

      const spyOnLatestStimmzettelState = vi.spyOn(
        unitUnderTest.latestStimmzettelState,
        "value",
        "set"
      );

      mockDefinitions.postStimmzettel.mockRejectedValue(
        new Error("api call failed")
      );

      await expect(async () =>
        unitUnderTest.saveStimmzettel(wahlID, wahlbezirkID, teamID, stimmzettel)
      ).rejects.toThrowError();

      expect(mockDefinitions.addNotification.mock.calls.length).toStrictEqual(
        1
      );
      expect(mockDefinitions.addNotification.mock.calls[0]).toEqual([
        "Speichern der Stimmzettel ist fehlgeschlagen",
        UserNotificationCategoryEnum.ERROR,
      ]);
      expect(spyOnLatestStimmzettelState).not.toHaveBeenCalled();
    });

    it("should_notTriggerErrorNotification_when_anExceptionOccurredDuringApiCallButSendNotificationIsFalse", async () => {
      const wahlID = "wahlID";
      const wahlbezirkID = "wahlbezirkID";
      const teamID = "teamID";
      const stimmzettel = [createPersistedStimmzettel()];

      mockDefinitions.postStimmzettel.mockRejectedValue(
        new Error("api call failed")
      );

      await expect(async () =>
        unitUnderTest.saveStimmzettel(
          wahlID,
          wahlbezirkID,
          teamID,
          stimmzettel,
          false
        )
      ).rejects.toThrowError();

      expect(mockDefinitions.addNotification.mock.calls.length).toStrictEqual(
        0
      );
    });
  });

  describe("loadAnzahlStimmzettel", () => {
    it("should_returnAnzahlStimmzettel_when_called", async () => {
      const wahlID = "wahlID";
      const wahlbezirkID = "wahlbezirkID";
      const anzahlStimmzettel = 5;

      mockDefinitions.getAnzahlStimmzettel.mockResolvedValue({
        status: 200,
        data: anzahlStimmzettel,
      });

      await unitUnderTest.loadAnzahlStimmzettel(wahlID, wahlbezirkID);

      expect(unitUnderTest.lastLoadedAnzahlStimmzettel.value).toStrictEqual(
        anzahlStimmzettel
      );
    });

    it("should_triggerErrorNotification_when_anExceptionOccurredDuringApiCall", async () => {
      const wahlID = "wahlID";
      const wahlbezirkID = "wahlbezirkID";

      mockDefinitions.getAnzahlStimmzettel.mockRejectedValue(
        new Error("api call failed")
      );

      await expect(async () =>
        unitUnderTest.loadAnzahlStimmzettel(wahlID, wahlbezirkID)
      ).rejects.toThrowError();

      expect(mockDefinitions.addNotification.mock.calls.length).toStrictEqual(
        1
      );
      expect(mockDefinitions.addNotification.mock.calls[0]).toEqual([
        "Abrufen der Anzahl der Stimmzettel ist fehlgeschlagen",
        UserNotificationCategoryEnum.ERROR,
      ]);
    });

    it("should_notTriggerErrorNotification_when_anExceptionOccurredDuringApiCallButSendNotificationIsFalse", async () => {
      const wahlID = "wahlID";
      const wahlbezirkID = "wahlbezirkID";

      mockDefinitions.getAnzahlStimmzettel.mockRejectedValue(
        new Error("api call failed")
      );

      await expect(async () =>
        unitUnderTest.loadAnzahlStimmzettel(wahlID, wahlbezirkID, false)
      ).rejects.toThrowError();

      expect(mockDefinitions.addNotification.mock.calls.length).toStrictEqual(
        0
      );
    });
  });

  async function _runLoadingTestcase(
    isApiCallSuccessful: boolean,
    apiMock: Mock,
    successDataProducer: () => unknown,
    functionUpdatingLoadingState: (...args: unknown[]) => Promise<unknown>,
    loadingRef: ComputedRef<boolean>
  ) {
    vi.useFakeTimers();
    const timeout = 1000;

    apiMock.mockImplementation(
      () =>
        new Promise((resolve, reject) => {
          setTimeout(() => {
            if (isApiCallSuccessful) {
              resolve(
                createAxiosResponse({
                  status: 200,
                  data: successDataProducer(),
                })
              );
            } else {
              reject(new Error("mocked api error"));
            }
          }, timeout);
        })
    );

    expect(loadingRef.value).toStrictEqual(false);
    const promise = functionUpdatingLoadingState();
    expect(loadingRef.value).toStrictEqual(true);

    vi.advanceTimersByTime(timeout);
    try {
      await promise;
    } catch (error) {
      logDebug("caught error during test. its fine", error);
    }

    expect(loadingRef.value).toStrictEqual(false);
  }
});
