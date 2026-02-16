import type { ServiceWorkerMessageTypeEnum } from "@/types/serviceWorker/ServiceWorkerMessageTypeEnum.ts";

export interface ServiceWorkerMessage {
  type: ServiceWorkerMessageTypeEnum;
  payload: unknown;
}
