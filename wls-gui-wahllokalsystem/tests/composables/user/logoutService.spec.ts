import { useAxiosTestDataFactory } from "@tests/utils/common/AxiosTestDataFactory.ts";
import { useResolvedUrlTestDataFactory } from "@tests/utils/user/ResolvedUrlTestDataFactory.ts";
import {
  afterAll,
  afterEach,
  beforeEach,
  describe,
  expect,
  it,
  vi,
} from "vitest";

import { useLogoutService } from "@/composables/user/logoutService.ts";

const mockDefinitions = vi.hoisted(() => ({
  getLogoutUrl: vi.fn(),
  fetch: vi.fn(),
}));

vi.mock("@/api/wls-clients/generated-auth-api", () => ({
  AuthServerControllerApi: vi.fn().mockImplementation(() => ({
    getLogoutUrl: mockDefinitions.getLogoutUrl,
  })),
  Configuration: vi.fn(),
}));

global.fetch = mockDefinitions.fetch;

const { createAxiosResponse } = useAxiosTestDataFactory();
const { createResolvedUrlDTO } = useResolvedUrlTestDataFactory();

describe("logoutService.ts", () => {
  let unitUnderTest: ReturnType<typeof useLogoutService>;

  beforeEach(() => {
    unitUnderTest = useLogoutService();
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  afterAll(() => {
    vi.resetAllMocks();
    vi.unstubAllGlobals();
  });

  describe("logout", () => {
    it("should_doLogout_when_logoutUrlIsRead", async () => {
      const mockedLogoutUrlResponse = createResolvedUrlDTO();
      mockDefinitions.getLogoutUrl.mockResolvedValue(
        createAxiosResponse({
          status: 200,
          data: mockedLogoutUrlResponse,
        })
      );

      mockDefinitions.fetch.mockImplementation(() => {
        return Promise.resolve({ ok: true });
      });

      await unitUnderTest.logout();

      expect(mockDefinitions.fetch).toHaveBeenCalledTimes(2);
      expect(
        (mockDefinitions.fetch.mock.calls[0]?.[0] as Request)?.url
      ).toStrictEqual(`${mockedLogoutUrlResponse.url.toLowerCase()}/`);
      expect(mockDefinitions.fetch.mock.calls[1]?.[0])?.toStrictEqual(`logout`);
    });

    it("should_throwError_when_gettingLogoutFailed", async () => {
      const mockedGetLogoutUrlError = new Error("mocked get logout url failed");
      mockDefinitions.getLogoutUrl.mockRejectedValue(mockedGetLogoutUrlError);

      await expect(unitUnderTest.logout()).rejects.toThrow(
        mockedGetLogoutUrlError
      );
    });

    it("should_throwError_when_logoutOnAuthServiceFailed", async () => {
      const mockedLogoutUrlResponse = createResolvedUrlDTO();
      mockDefinitions.getLogoutUrl.mockResolvedValue(
        createAxiosResponse({
          status: 200,
          data: mockedLogoutUrlResponse,
        })
      );

      const mockedAuthServerLogoutError = new Error(
        "mocked logout on auth service failed"
      );
      mockDefinitions.fetch.mockImplementation((request: Request | string) => {
        if (typeof request === "string") {
          return Promise.resolve();
        } else if (request.url.startsWith(mockedLogoutUrlResponse.url)) {
          return Promise.reject(mockedAuthServerLogoutError);
        }
      });

      await expect(unitUnderTest.logout()).rejects.toThrow();
    });

    it("should_throwError_when_logoutOnGatewayFailed", async () => {
      const mockedLogoutUrlResponse = createResolvedUrlDTO();
      mockDefinitions.getLogoutUrl.mockResolvedValue(
        createAxiosResponse({
          status: 200,
          data: mockedLogoutUrlResponse,
        })
      );

      const mockedAuthServerLogoutError = new Error(
        "mocked logout on api gateway failed"
      );
      mockDefinitions.fetch.mockImplementation((request: Request | string) => {
        if (typeof request === "string") {
          return Promise.reject(mockedAuthServerLogoutError);
        } else {
          return Promise.resolve();
        }
      });

      await expect(unitUnderTest.logout()).rejects.toThrow();
    });
  });
});
