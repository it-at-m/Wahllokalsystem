import type { ServiceWorkerMessage } from "@/types/serviceWorker/ServiceWorkerMessage.ts";

import { storeToRefs } from "pinia";
import { watch } from "vue";

import { useLogging } from "@/composables/common/logging.ts";
import { useCryptoUtils } from "@/composables/crypto/cryptoUtils.ts";
import { useServiceWorkerUtils } from "@/composables/serviceWorker/serviceWorkerUtils.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { ServiceWorkerMessageTypeEnum } from "@/types/serviceWorker/ServiceWorkerMessageTypeEnum.ts";

export function useServiceWorkerPinSyncer() {
  const { user } = storeToRefs(useUserStore());
  const { importKey } = useCryptoUtils();
  const { sendMessage } = useServiceWorkerUtils();
  const { logWarn } = useLogging("serviceWorkerPinSyncer");

  watch(
    () => user.value.pin,
    async () => {
      await syncPin();
    }
  );

  if ("serviceWorker" in navigator) {
    navigator.serviceWorker.addEventListener("message", (event) =>
      _handleServiceWorkerInstalledMessage(event.data)
    );

    navigator.serviceWorker.addEventListener("controllerchange", () =>
      syncPin()
    );
  } else {
    logWarn("ServiceWorkerPinSyncer konnte EventListener nicht registrieren");
  }

  async function syncPin() {
    const cryptoKey = await importKey(user.value.pin);
    sendMessage({ type: ServiceWorkerMessageTypeEnum.PIN, payload: cryptoKey });
  }

  function _handleServiceWorkerInstalledMessage(
    serviceWorkerMessage: ServiceWorkerMessage
  ) {
    if (
      serviceWorkerMessage.type ===
      ServiceWorkerMessageTypeEnum.SERVICE_WORKER_INSTALLED
    ) {
      syncPin();
    }
  }

  return {
    syncPin,
  };
}
