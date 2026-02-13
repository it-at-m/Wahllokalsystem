import { useLogging } from "@/composables/common/logging.ts";
import { type ServiceWorkerMessage } from "@/types/serviceWorker/ServiceWorkerMessage.ts";

export function useServiceWorkerUtils() {
  const { logDebug } = useLogging("useServiceWorkerUtils");

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
    let numberOfChecker = 1;
    while (!isServiceWorkerActive() && numberOfChecker < countTries) {
      await _sleep(retryDelayInMilliseconds);
      numberOfChecker++;
    }
    const result = isServiceWorkerActive();
    logDebug(`result after ${numberOfChecker} is ${result}`);
    return result;
  }

  async function _sleep(milliseconds: number) {
    await new Promise((resolve) => setTimeout(resolve, milliseconds));
  }

  return {
    awaitServiceWorkerActive,
    isServiceWorkerActive,
    sendMessage,
  };
}
