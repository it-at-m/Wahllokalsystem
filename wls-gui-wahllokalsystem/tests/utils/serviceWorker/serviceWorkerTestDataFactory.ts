import type { ServiceWorkerMessage } from "@/types/serviceWorker/ServiceWorkerMessage.ts";

import { proxyBuilder } from "@tests/utils/Builder.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

import { ServiceWorkerMessageTypeEnum } from "@/types/serviceWorker/ServiceWorkerMessageTypeEnum.ts";

const { getRandomItem, generateRandomString } = useCommonTestDataFactory();

export function useServiceWorkerTestDataFactory() {
  function createServiceWorkerMessage(): ServiceWorkerMessage {
    return {
      type: getRandomItem(Object.values(ServiceWorkerMessageTypeEnum)),
      payload: generateRandomString(100),
    };
  }

  function prepareServiceWorkerMessage() {
    return proxyBuilder<ServiceWorkerMessage>(createServiceWorkerMessage());
  }

  return {
    createServiceWorkerMessage,
    prepareServiceWorkerMessage,
  };
}
