import type { RouteLocationRaw } from "vue-router";

import { storeToRefs } from "pinia";

import {
  AuthServerControllerApi,
  Configuration,
} from "@/api/wls-clients/generated-auth-api";
import { WahllokalZustandControllerApi } from "@/api/wls-clients/generated-monitoring-api";
import { useCommonApiUtils } from "@/composables/api/commonApiUtils.ts";
import { useLogging } from "@/composables/common/logging.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import {
  AUTH_SERVICE_API_URL,
  MONITORING_SERVICE_API_URL,
} from "@/constants.ts";
import router from "@/plugins/router.ts";
import { useSchedulerStore } from "@/stores/schedulerStore.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const { axiosConfigWrapper } = useCommonApiUtils();

const { logDebug, logError } = useLogging("logoutService");
const { addNotification } = useUserNotificationService();

export function useLogoutService() {
  const { isUserLoggedIn } = storeToRefs(useUserStore());
  const { stopAll } = useSchedulerStore();

  const authServerControllerApi = new AuthServerControllerApi(
    new Configuration({
      basePath: AUTH_SERVICE_API_URL,
    })
  );

  const wahllokalZustandControllerApi = new WahllokalZustandControllerApi(
    new Configuration({
      basePath: MONITORING_SERVICE_API_URL,
    })
  );

  async function logout(
    wahlbezirkID: string,
    teamID: string,
    routingTargetAfterSuccessfulLogout: RouteLocationRaw
  ) {
    try {
      const logoutUrl = (
        await authServerControllerApi.getLogoutUrl(
          axiosConfigWrapper().requestAsOnlineOnly()
        )
      ).data.url;

      await wahllokalZustandControllerApi.postLetzteAbmeldung(
        wahlbezirkID,
        teamID,
        axiosConfigWrapper().requestAsOnlineOnly()
      );

      const request = new Request(logoutUrl, {
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

      stopAll();
      isUserLoggedIn.value = false;

      await router.push(routingTargetAfterSuccessfulLogout);
    } catch (error) {
      logError(`fehler bei logout`, error);
      addNotification(
        "Logout fehlgeschlagen. Bitte versuchen Sie es später erneut.",
        UserNotificationCategoryEnum.ERROR
      );
    }
  }

  function forwardToLoginPage() {
    // Full page reload required because login is handled by the auth-service,
    // not by client-side routing
    window.location.reload();
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
    forwardToLoginPage,
  };
}
