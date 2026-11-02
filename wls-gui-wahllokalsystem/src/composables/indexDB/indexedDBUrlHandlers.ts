import type { IndexedDBUrlHandler } from "@/types/indexDB/IndexedDBUrlHandler.ts";

import { useBusinessActionsIndexedDBUrlHandler } from "@/composables/indexDB/businessActionsIndexedDBUrlHandler.ts";
import { useStimmzettelerfassungIndexedDBUrlHandler } from "@/composables/indexDB/stimmzettelerfassungIndexedDBUrlHandler.ts";

export const INDEXED_DB_URL_HANDLERS: IndexedDBUrlHandler[] = [
  useBusinessActionsIndexedDBUrlHandler(),
  useStimmzettelerfassungIndexedDBUrlHandler(),
];
