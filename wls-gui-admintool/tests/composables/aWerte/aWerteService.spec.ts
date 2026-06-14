import { useAWerteTestDataFactory } from "@tests/types/aWerte/AWerteTestDataFactory.ts";
import { afterEach, describe, expect, it, vi } from "vitest";

import { useAWerteService } from "@/composables/aWerte/aWerteService.ts";

const mockDefinitions = vi.hoisted(() => ({
  apiGetAsyncProgress: vi.fn(),
  mappingAsyncProgressDtoToAWerteInitProgress: vi.fn(),
  asyncProgressControllerApiConstructor: class {
    getAsyncProgress = mockDefinitions.apiGetAsyncProgress;
  },
  configurationConstructor: vi.fn(),
}));

vi.mock("@/api/wls-clients/generated-ergebnismeldung-api", () => ({
  AsyncProgressControllerApi:
    mockDefinitions.asyncProgressControllerApiConstructor,
  Configuration: mockDefinitions.configurationConstructor,
}));

vi.mock(import("@/composables/aWerte/aWerteMapper.ts"), () => ({
  useAWerteMapper: () => ({
    asyncProgressDtoToAWerteInitProgress:
      mockDefinitions.mappingAsyncProgressDtoToAWerteInitProgress,
  }),
}));

const { createAsyncProgressDTOComplete, createAWerteInitProgressComplete } =
  useAWerteTestDataFactory();

const unitUnderTest = useAWerteService();

describe("aWerteService.ts", () => {
  afterEach(() => {
    vi.clearAllMocks();
    vi.resetAllMocks();
  });

  describe("useWerteService", () => {
    describe("getAWerteProgress", () => {
      it("should_returnAWerteInitProgress_when_apiReturnData", async () => {
        const mockedApiResponse = createAsyncProgressDTOComplete();
        mockDefinitions.apiGetAsyncProgress.mockReturnValue(
          Promise.resolve({ data: mockedApiResponse })
        );

        const mockedApiResponseMappedToModel =
          createAWerteInitProgressComplete();
        mockDefinitions.mappingAsyncProgressDtoToAWerteInitProgress.mockReturnValue(
          mockedApiResponseMappedToModel
        );

        const result = await unitUnderTest.getAWerteProgress();

        expect(result).toStrictEqual(mockedApiResponseMappedToModel);
      });

      it("should_rejectPromise_when_apiThrowsError", async () => {
        const mockedApiError = new Error("api call failed");
        mockDefinitions.apiGetAsyncProgress.mockRejectedValue(mockedApiError);

        const mockedApiResponseMappedToModel =
          createAWerteInitProgressComplete();
        mockDefinitions.mappingAsyncProgressDtoToAWerteInitProgress.mockReturnValue(
          mockedApiResponseMappedToModel
        );

        await expect(unitUnderTest.getAWerteProgress()).rejects.toStrictEqual(
          mockedApiError
        );
      });

      it("should_useErgebnismeldungsServiceForBasePath_when_creatingInstanceOfControllerAPI", () => {
        useAWerteService();

        const configurationConstructorParameter =
          mockDefinitions.configurationConstructor.mock.calls[0]?.[0];

        expect(configurationConstructorParameter["basePath"]).toStrictEqual(
          "/api/ergebnismeldung-service"
        );
      });
    });
  });
});
