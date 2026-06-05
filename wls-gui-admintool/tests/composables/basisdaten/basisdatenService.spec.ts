import { useBasisdatenTestDataFactory } from "@tests/types/basisdaten/BasisdatenTestDataFactory.ts";
import { afterEach, describe, expect, it, vi } from "vitest";

import { useBasisdatenService } from "@/composables/basisdaten/basisdatenService.ts";

const mockDefinitions = vi.hoisted(() => ({
  apiGetAsyncProgress: vi.fn(),
  mapAsyncProgressDtoToBasisdatenInitProgress: vi.fn(),
  asyncProgressControllerApiConstructor: class MockedAsyncProgressControllerApi {
    getAsyncProgress = mockDefinitions.apiGetAsyncProgress;
  },
  configurationConstructor: vi.fn(),
}));

vi.mock("@/api/wls-clients/generated-basisdaten-api", () => ({
  AsyncProgressControllerApi:
    mockDefinitions.asyncProgressControllerApiConstructor,
  Configuration: mockDefinitions.configurationConstructor,
}));

vi.mock("@/composables/basisdaten/basisdatenMapper.ts", () => ({
  useBasisdatenMapper: () => ({
    mapAsyncProgressDtoToBasisdatenInitProgress:
      mockDefinitions.mapAsyncProgressDtoToBasisdatenInitProgress,
  }),
}));

const { createAsyncProgressDTOComplete, createBasisdatenInitProgressComplete } =
  useBasisdatenTestDataFactory();

const unitUnderTest = useBasisdatenService();

describe("basisdatenService.ts", () => {
  afterEach(() => {
    vi.clearAllMocks();
    vi.resetAllMocks();
  });

  describe("useBasisdatenService", () => {
    describe("getAsyncProgress", () => {
      it("should_returnBasisdatenInitProgress_when_apiReturnData", async () => {
        const mockedApiResponse = createAsyncProgressDTOComplete();
        mockDefinitions.apiGetAsyncProgress.mockReturnValue(
          Promise.resolve({ data: mockedApiResponse })
        );

        const mockedApiResponseMappedToModel =
          createBasisdatenInitProgressComplete();
        mockDefinitions.mapAsyncProgressDtoToBasisdatenInitProgress.mockReturnValue(
          mockedApiResponseMappedToModel
        );

        const result = await unitUnderTest.getAsyncProgress();

        expect(result).toStrictEqual(mockedApiResponseMappedToModel);
      });

      it("should_rejectPromise_when_apiThrowsError", async () => {
        const mockedApiError = new Error("api call failed");
        mockDefinitions.apiGetAsyncProgress.mockRejectedValue(mockedApiError);

        await expect(unitUnderTest.getAsyncProgress()).rejects.toStrictEqual(
          mockedApiError
        );
      });

      it("should_useErgebnismeldungsServiceForBasePath_when_creatingInstanceOfControllerAPI", () => {
        useBasisdatenService();

        const configurationConstructorParameter =
          mockDefinitions.configurationConstructor.mock.calls[0]?.[0];

        expect(configurationConstructorParameter["basePath"]).toStrictEqual(
          "/api/basisdaten-service"
        );
      });
    });
  });
});
