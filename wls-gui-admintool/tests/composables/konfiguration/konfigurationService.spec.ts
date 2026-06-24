import type { KonfigurationDTO } from "@/api/wls-clients/generated-infomanagement-api";

import { useKonfigurationTestDataFactory } from "@tests/types/config/KonfigurationTestDataFactory.ts";
import { spyOn } from "storybook/test";
import { afterEach, describe, expect, it, vi } from "vitest";
import { ref } from "vue";

import { useKonfigurationService } from "@/composables/konfiguration/konfigurationService.ts";

const mockDefinitions = vi.hoisted(() => ({
  apiGetKonfigurations: vi.fn(),
  apiPostKonfiguration: vi.fn(),
  apiUtilsReturnUndefinedOnStatus204OrElseResponseData: vi.fn(),
  mapKonfigurationDtoToConfigParameter: vi.fn(),
  mapKonfigurationDtosToConfigParameters: vi.fn(),
  mapConfigParameterToKonfigurationSetDto: vi.fn(),
  addNotification: vi.fn(),
  konfigurationControllerApiConstructor: class {
    getKonfigurations = mockDefinitions.apiGetKonfigurations;
    postKonfiguration = mockDefinitions.apiPostKonfiguration;
  },
  infomanagementApiConfigurationConstructor: vi.fn(),
}));

vi.mock("@/api/wls-clients/generated-infomanagement-api", () => ({
  Configuration: mockDefinitions.infomanagementApiConfigurationConstructor,
  KonfigurationControllerApi:
    mockDefinitions.konfigurationControllerApiConstructor,
}));
vi.mock(
  import("@/composables/userNotification/userNotificationService.ts"),
  () => ({
    useUserNotificationService: () => ({
      addNotification: mockDefinitions.addNotification,
    }),
  })
);
vi.mock(import("@/composables/konfiguration/konfigurationMapper.ts"), () => ({
  useKonfigurationMapper: () => ({
    mapKonfigurationDtoToConfigParameter:
      mockDefinitions.mapKonfigurationDtoToConfigParameter,
    mapKonfigurationDtosToConfigParameters:
      mockDefinitions.mapKonfigurationDtosToConfigParameters,
    mapConfigParameterToKonfigurationSetDto:
      mockDefinitions.mapConfigParameterToKonfigurationSetDto,
  }),
}));
vi.mock(import("@/composables/common/apiUtils.ts"), () => ({
  useApiUtils: () => ({
    returnUndefinedOnStatus204OrElseResponseData:
      mockDefinitions.apiUtilsReturnUndefinedOnStatus204OrElseResponseData,
  }),
}));

const {
  prepareKonfigurationDto,
  prepareConfigParameter,
  prepareKonfigurationSetDto,
} = useKonfigurationTestDataFactory();

const unitUnderTest = useKonfigurationService();

describe("konfigurationService.ts", () => {
  afterEach(() => {
    vi.clearAllMocks();
    vi.resetAllMocks();
  });

  describe("getKonfigurations", () => {
    it("should_returnMappedConfigParameters_when_apiReturnedData", async () => {
      const { mockedDtos, mockedConfigParameters } =
        useMockSetupForSuccessfulLoad();

      const result = await unitUnderTest.getKonfigurations();

      expect(result).toStrictEqual(mockedConfigParameters);
      expect(
        mockDefinitions.mapKonfigurationDtosToConfigParameters
      ).toHaveBeenCalledWith(mockedDtos);
    });

    it("should_changeTheValueOfLoading_when_loadingIsNotUndefined", async () => {
      useMockSetupForSuccessfulLoad();

      const loadingRef = ref(false);
      const spyOnValueSetterOfRef = spyOn(loadingRef, "value", "set");

      await unitUnderTest.getKonfigurations(loadingRef);

      expect(spyOnValueSetterOfRef.mock.calls).toStrictEqual([[true], [false]]);

      spyOnValueSetterOfRef.mockRestore();
    });

    it("should_returnEmptyArray_when_anExceptionOccurred", async () => {
      mockDefinitions.apiGetKonfigurations.mockRejectedValue(
        new Error("api call failed")
      );

      const result = await unitUnderTest.getKonfigurations();

      expect(result).toStrictEqual([]);
    });

    it("should_returnEmptyArray_when_apiReturned204WithUndefined", async () => {
      mockDefinitions.apiGetKonfigurations.mockReturnValue(
        Promise.resolve({ status: 204, data: undefined })
      );
      mockDefinitions.apiUtilsReturnUndefinedOnStatus204OrElseResponseData.mockReturnValue(
        undefined
      );

      const result = await unitUnderTest.getKonfigurations();

      expect(result).toStrictEqual([]);
      expect(
        mockDefinitions.mapKonfigurationDtosToConfigParameters
      ).not.toHaveBeenCalled();
    });

    it("should_triggerToastyWithError_when_anExceptionOccurred", async () => {
      mockDefinitions.apiGetKonfigurations.mockRejectedValue(
        new Error("api call failed")
      );

      await unitUnderTest.getKonfigurations();

      expect(mockDefinitions.addNotification.mock.calls[0]).toEqual([
        expect.any(String),
        "Error",
      ]);
    });

    it("should_useInfomanagementUrl_when_initializingKonfigurationControllerApi", () => {
      useKonfigurationService();

      const configurationConstructorParameter =
        mockDefinitions.infomanagementApiConfigurationConstructor.mock
          .calls[0]?.[0];

      expect(configurationConstructorParameter["basePath"]).toStrictEqual(
        "/api/infomanagement-service"
      );
    });
  });

  describe("saveKonfiguration", () => {
    it("should_postMappedDtoWithKey_when_called", async () => {
      const configParameter = prepareConfigParameter()
        .name("WILLKOMMENSTEXT")
        .build();
      const mappedSetDto = prepareKonfigurationSetDto().build();
      mockDefinitions.mapConfigParameterToKonfigurationSetDto.mockReturnValue(
        mappedSetDto
      );
      mockDefinitions.apiPostKonfiguration.mockResolvedValue({ status: 200 });

      const result = await unitUnderTest.saveKonfiguration(configParameter);

      expect(result).toStrictEqual(true);
      expect(
        mockDefinitions.mapConfigParameterToKonfigurationSetDto
      ).toHaveBeenCalledWith(configParameter);
      expect(mockDefinitions.apiPostKonfiguration).toHaveBeenCalledWith(
        "WILLKOMMENSTEXT",
        mappedSetDto
      );
    });

    it("should_triggerToastyWithSuccess_when_saveSucceeded", async () => {
      const configParameter = prepareConfigParameter().build();
      mockDefinitions.mapConfigParameterToKonfigurationSetDto.mockReturnValue(
        prepareKonfigurationSetDto().build()
      );
      mockDefinitions.apiPostKonfiguration.mockResolvedValue({ status: 200 });

      await unitUnderTest.saveKonfiguration(configParameter);

      expect(mockDefinitions.addNotification.mock.calls[0]).toEqual([
        expect.any(String),
        "Success",
      ]);
    });

    it("should_returnFalseAndTriggerToastyWithError_when_saveFailed", async () => {
      const configParameter = prepareConfigParameter().build();
      mockDefinitions.mapConfigParameterToKonfigurationSetDto.mockReturnValue(
        prepareKonfigurationSetDto().build()
      );
      mockDefinitions.apiPostKonfiguration.mockRejectedValue(
        new Error("api call failed")
      );

      const result = await unitUnderTest.saveKonfiguration(configParameter);

      expect(result).toStrictEqual(false);
      expect(mockDefinitions.addNotification.mock.calls[0]).toEqual([
        expect.any(String),
        "Error",
      ]);
    });
  });
});

function useMockSetupForSuccessfulLoad() {
  const mockedDtos: KonfigurationDTO[] = [
    prepareKonfigurationDto().build(),
    prepareKonfigurationDto().build(),
  ];
  const mockedConfigParameters = [
    prepareConfigParameter().build(),
    prepareConfigParameter().build(),
  ];

  mockDefinitions.apiGetKonfigurations.mockReturnValue(
    Promise.resolve({ status: 200, data: mockedDtos })
  );
  mockDefinitions.apiUtilsReturnUndefinedOnStatus204OrElseResponseData.mockReturnValue(
    mockedDtos
  );
  mockDefinitions.mapKonfigurationDtosToConfigParameters.mockReturnValue(
    mockedConfigParameters
  );

  return {
    mockedDtos,
    mockedConfigParameters,
  };
}
