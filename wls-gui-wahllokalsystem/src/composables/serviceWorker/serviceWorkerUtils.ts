import { ref } from "vue";

import { useLogging } from "@/composables/common/logging.ts";
import { type ServiceWorkerMessage } from "@/types/serviceWorker/ServiceWorkerMessage.ts";

export function useServiceWorkerUtils() {
  const { logDebug, logWarn } = useLogging("useServiceWorkerUtils");

  const isCheckingIfServiceWorkerIsActive = ref(false);

  function isServiceWorkerActive() {
    return !!navigator.serviceWorker.controller;
  }

  function sendMessage(message: ServiceWorkerMessage) {
    if (navigator.serviceWorker.controller) {
      logDebug(`sending message of type ${message.type}`);
      navigator.serviceWorker.controller.postMessage(message);
    }
  }

  async function awaitServiceWorkerActive(
    countTries = 3,
    retryDelayInMilliseconds = 100
  ) {
    if (isCheckingIfServiceWorkerIsActive.value) {
      logWarn(`a check, if the service worker is running, is already active`);
      return;
    }

    isCheckingIfServiceWorkerIsActive.value = true;
    let numberOfChecker = 1;
    while (
      !isCheckingIfServiceWorkerIsActive &&
      numberOfChecker <= countTries
    ) {
      await _sleep(retryDelayInMilliseconds);
      numberOfChecker++;
    }
    logDebug(`result after ${numberOfChecker} is ${isServiceWorkerActive()}`);
    isCheckingIfServiceWorkerIsActive.value = false;
  }

  async function _sleep(milliseconds: number) {
    await new Promise((resolve) => setTimeout(resolve, milliseconds));
  }

  return {
    isCheckingIfServiceWorkerIsActive,
    awaitServiceWorkerActive,
    isServiceWorkerActive,
    sendMessage,
  };
}
