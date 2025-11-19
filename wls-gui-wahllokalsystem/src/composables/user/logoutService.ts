import {
  AuthServerControllerApi,
  Configuration,
} from "@/api/wls-clients/generated-auth-api";
import { useCommonApiUtils } from "@/composables/api/commonApiUtils.ts";
import { useLogging } from "@/composables/common/logging.ts";
import { AUTH_SERVICE_API_URL } from "@/constants.ts";

const { axiosConfigWrapper } = useCommonApiUtils();

const { logDebug, logError } = useLogging("logoutService");

export function useLogoutService() {
  const authServerControllerApi = new AuthServerControllerApi(
    new Configuration({
      basePath: AUTH_SERVICE_API_URL,
    })
  );

  async function logout() {
    try {
      const authServerLogoutUrlResponse =
        await authServerControllerApi.getLogoutUrl(
          axiosConfigWrapper().requestAsOnlineOnly()
        );
      const request = new Request(authServerLogoutUrlResponse.data.url, {
        method: "GET",
        credentials: "include",
      });
      await fetch(request).then((response) => {
        if (!response.ok) {
          return Promise.reject(
            new Error("logout bei auth-Service war nicht erfolgreich")
          );
        }
      });

      await fetch("/logout", _getPOSTConfig()).then((response) => {
        if (!response.ok) {
          return Promise.reject(
            new Error("logout bei api gateway war nicht erfolgreich")
          );
        }
      });

      logDebug(`logout erfolgreich durchgeführt`);
    } catch (error) {
      logError(`fehler bei logout`, error);
      throw error;
    }
  }

  function _getHeaders(): Headers {
    const headers = new Headers({
      "Content-Type": "application/json",
    });
    const csrfCookie = _getXSRFToken();
    if (csrfCookie !== "") {
      headers.append("X-XSRF-TOKEN", csrfCookie);
    }
    return headers;
  }

  function _getPOSTConfig(): RequestInit {
    return {
      method: "POST",
      body: undefined,
      headers: _getHeaders(),
      mode: "cors",
      credentials: "include",
      redirect: "manual",
    };
  }

  function _getXSRFToken(): string {
    const xsrfMatches = document.cookie.match(
      "(^|;)\\s*" + "XSRF-TOKEN" + "\\s*=\\s*([^;]+)"
    );
    return (xsrfMatches ? xsrfMatches.pop() : "") as string;
  }

  return {
    logout,
  };
}
