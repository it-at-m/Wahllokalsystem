import { storeToRefs } from "pinia";
import { ref } from "vue";

import { useLogging } from "@/composables/common/logging.ts";
import { useDateOfActionTimeout } from "@/composables/scheduler/dateOfActionTimeout.ts";
import { useLogoutService } from "@/composables/user/logoutService.ts";
import { useInfomanagementStore } from "@/stores/infomanagementStore.ts";

export function useLogoutOnInactivity() {
  const { logDebug } = useLogging("LogoutOnInactivity");

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

  const dateToCheckIfUserIsInactive = ref(_getDateForNextCheck());

  const { setupTimer } = useDateOfActionTimeout(
    "Inaktivität",
    dateToCheckIfUserIsInactive,
    async () => await _checkIfUserIsActiveAndAct()
  );
  setupTimer();

  let dateOfLastActivityByUser = new Date();

  async function _checkIfUserIsActiveAndAct() {
    logDebug("Check ifUserIsActiveAndAct");
    if (
      new Date().getTime() - delayBeforeInactiveLogoutInMilliseconds.value >
      dateOfLastActivityByUser.getTime()
    ) {
      logDebug("user was inactive");
      await _logout();
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

  async function _logout() {
    await logout();
  }

  function _registerUserActivity() {
    dateOfLastActivityByUser = new Date();
  }

  function _resetInactivityCheck() {
    dateToCheckIfUserIsInactive.value = _getDateForNextCheck();
  }
}
