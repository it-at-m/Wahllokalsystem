import type { Ref } from "vue";

import { useAxiosTestDataFactory } from "@tests/utils/common/AxiosTestDataFactory.ts";
import { useResolvedUrlTestDataFactory } from "@tests/utils/user/ResolvedUrlTestDataFactory.ts";
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

import { useCommonApiUtils } from "@/composables/api/commonApiUtils.ts";
import { useLogoutService } from "@/composables/user/logoutService.ts";
import { ROUTE_LOGOUT } from "@/constants.ts";
import router from "@/plugins/router.ts";

const mockDefinitions = vi.hoisted(() => ({
  getLogoutUrl: vi.fn(),
  postLetzteAbmeldung: vi.fn(),
  fetch: vi.fn(),
  isUserLoggedIn: undefined as Ref<boolean> | undefined,
  routerPush: vi.fn(),
}));

vi.mock("@/api/wls-clients/generated-auth-api", async (importOriginal) => {
  const mod = await importOriginal();
  return {
    ...(mod as object),
    AuthServerControllerApi: vi.fn().mockImplementation(() => ({
      getLogoutUrl: mockDefinitions.getLogoutUrl,
    })),
    Configuration: vi.fn(),
  };
});

vi.mock(
  "@/api/wls-clients/generated-monitoring-api",
  async (importOriginal) => {
    const mod = await importOriginal();
    return {
      ...(mod as object),
      WahllokalZustandControllerApi: vi.fn().mockImplementation(() => ({
        postLetzteAbmeldung: mockDefinitions.postLetzteAbmeldung,
      })),
      Configuration: vi.fn(),
    };
  }
);

vi.mock("@/stores/userStore.ts", () => ({
  useUserStore: () => ({
    isUserLoggedIn: mockDefinitions.isUserLoggedIn,
  }),
}));

router.push = mockDefinitions.routerPush;
global.fetch = mockDefinitions.fetch;

const { createAxiosResponse } = useAxiosTestDataFactory();
const { createResolvedUrlDTO } = useResolvedUrlTestDataFactory();
const { axiosConfigWrapper } = useCommonApiUtils();

describe("logoutService.ts", () => {
  let unitUnderTest: ReturnType<typeof useLogoutService>;

  const WAHLBEZIRK_ID = "wahlbezirkID";

  beforeEach(() => {
    setActivePinia(createPinia());
    mockDefinitions.isUserLoggedIn = ref(true);
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

      mockDefinitions.postLetzteAbmeldung.mockResolvedValue(Promise.resolve());

      mockDefinitions.fetch.mockImplementation(() => {
        return Promise.resolve({ ok: true });
      });

      await unitUnderTest.logout(WAHLBEZIRK_ID);

      expect(mockDefinitions.postLetzteAbmeldung).toHaveBeenCalledWith(
        WAHLBEZIRK_ID,
        axiosConfigWrapper().requestAsOnlineOnly()
      );
      expect(mockDefinitions.fetch).toHaveBeenCalledTimes(2);
      expect(
        (mockDefinitions.fetch.mock.calls[0]?.[0] as Request)?.url
      ).toStrictEqual(`${mockedLogoutUrlResponse.url.toLowerCase()}/`);
      expect(mockDefinitions.fetch.mock.calls[1]?.[0])?.toStrictEqual(
        `/logout`
      );
      // eslint-disable-next-line  @typescript-eslint/no-non-null-assertion
      expect(mockDefinitions.isUserLoggedIn!.value).toBe(false);
      expect(router.push).toHaveBeenCalledWith(ROUTE_LOGOUT);
    });

    it("should_throwError_when_gettingLogoutUrlFailed", async () => {
      const mockedGetLogoutUrlError = new Error("mocked get logout url failed");
      mockDefinitions.getLogoutUrl.mockRejectedValue(mockedGetLogoutUrlError);

      await expect(unitUnderTest.logout(WAHLBEZIRK_ID)).rejects.toThrow(
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

      await expect(unitUnderTest.logout(WAHLBEZIRK_ID)).rejects.toThrow();
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

      await expect(unitUnderTest.logout(WAHLBEZIRK_ID)).rejects.toThrow();
    });

    it("should_notPostLetzteAbmeldung_when_getLogoutUrlFailed", async () => {
      mockDefinitions.getLogoutUrl.mockResolvedValue(
        Promise.reject(new Error("no logout url"))
      );

      await expect(unitUnderTest.logout(WAHLBEZIRK_ID)).rejects.toThrow();
      expect(mockDefinitions.postLetzteAbmeldung).not.toHaveBeenCalled();
    });
  });
});
