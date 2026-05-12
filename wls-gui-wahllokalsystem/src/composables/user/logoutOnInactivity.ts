import { storeToRefs } from "pinia";
import { ref } from "vue";

import { useLogging } from "@/composables/common/logging.ts";
import { useDateOfActionTimeout } from "@/composables/scheduler/dateOfActionTimeout.ts";
import { useLogoutService } from "@/composables/user/logoutService.ts";
import { createLogoutRoute } from "@/plugins/router/commonRoutes.ts";
import { useInfomanagementStore } from "@/stores/infomanagementStore.ts";
import { useUserStore } from "@/stores/userStore.ts";

const TIMEOUT_TITLE = "Inaktivität";

export function useLogoutOnInactivity() {
  const { logDebug } = useLogging("LogoutOnInactivity");

  const { currentUserWahlbezirkID } = storeToRefs(useUserStore());

  window.addEventListener("load", _registerUserActivity);
  window.addEventListener("mousemove", _registerUserActivity);
  window.addEventListener("mousedown", _registerUserActivity);
  window.addEventListener("touchstart", _registerUserActivity);
  window.addEventListener("click", _registerUserActivity);
  window.addEventListener("keypress", _registerUserActivity);
  window.addEventListener("scroll", _registerUserActivity);

  const { logout } = useLogoutService();
  const { delayBeforeInactiveLogoutInMilliseconds } = storeToRefs(
    useInfomanagementStore()
  );

  let dateOfLastActivityByUser = new Date();
  const dateNextCheckIfUserIsInactive = ref(_getDateForNextCheck());

  useDateOfActionTimeout(
    TIMEOUT_TITLE,
    dateNextCheckIfUserIsInactive,
    async () => await _checkIfUserIsActiveAndAct()
  ).setupTimer();

  async function _checkIfUserIsActiveAndAct() {
    logDebug("Check ifUserIsActiveAndAct");
    if (_isUserInactive()) {
      logDebug("user was inactive");
      await logout(currentUserWahlbezirkID.value, createLogoutRoute(true));
    } else {
      logDebug("user was active");
      _resetInactivityCheck();
    }
  }

  function _getDateForNextCheck() {
    return new Date(
      dateOfLastActivityByUser.getTime() +
        delayBeforeInactiveLogoutInMilliseconds.value
    );
  }

  function _isUserInactive(): boolean {
    return (
      new Date().getTime() - delayBeforeInactiveLogoutInMilliseconds.value >=
      dateOfLastActivityByUser.getTime()
    );
  }

  function _registerUserActivity() {
    dateOfLastActivityByUser = new Date();
  }

  function _resetInactivityCheck() {
    dateNextCheckIfUserIsInactive.value = _getDateForNextCheck();
  }
}
