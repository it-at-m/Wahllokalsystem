export const ServiceWorkerMessageTypeEnum = {
  PIN: "PIN",
  SERVICE_WORKER_INSTALLED: "SERVICE_WORKER_INSTALLED",
} as const;

export type ServiceWorkerMessageTypeEnum =
  (typeof ServiceWorkerMessageTypeEnum)[keyof typeof ServiceWorkerMessageTypeEnum];
