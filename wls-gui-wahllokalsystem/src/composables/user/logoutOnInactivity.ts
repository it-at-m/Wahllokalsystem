import { storeToRefs } from "pinia";
import { onUnmounted, ref } from "vue";

import { useLogging } from "@/composables/common/logging.ts";
import { useDateOfActionTimeout } from "@/composables/scheduler/dateOfActionTimeout.ts";
import { useLogoutService } from "@/composables/user/logoutService.ts";
import { useInfomanagementStore } from "@/stores/infomanagementStore.ts";
import { useUserStore } from "@/stores/userStore.ts";

const TIMEOUT_TITLE = "Inaktivität";
const INACTIVITY_BREAKING_EVENTS = [
  "load",
  "mousemove",
  "mousedown",
  "touchstart",
  "click",
  "keypress",
  "scroll",
];

export function useLogoutOnInactivity() {
  const { logDebug } = useLogging("LogoutOnInactivity");

  const { currentUserWahlbezirkID } = storeToRefs(useUserStore());

  const { logout } = useLogoutService();
  const { delayBeforeInactiveLogoutInMilliseconds } = storeToRefs(
    useInfomanagementStore()
  );

  let dateOfLastActivityByUser = new Date();
  const dateNextCheckIfUserIsInactive = ref(_getDateForNextCheck());
  INACTIVITY_BREAKING_EVENTS.forEach((event) => {
    window.addEventListener(event, _registerUserActivity);
  });

  useDateOfActionTimeout(
    TIMEOUT_TITLE,
    dateNextCheckIfUserIsInactive,
    async () => await _checkIfUserIsActiveAndAct()
  ).setupTimer();

  onUnmounted(() => {
    INACTIVITY_BREAKING_EVENTS.forEach((event) => {
      window.removeEventListener(event, _registerUserActivity);
    });
  });

  async function _checkIfUserIsActiveAndAct() {
    logDebug("Check ifUserIsActiveAndAct");
    if (_isUserInactive()) {
      logDebug("user was inactive");
      await logout(currentUserWahlbezirkID.value);
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
