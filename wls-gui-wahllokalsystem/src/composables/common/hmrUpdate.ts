import type { StoreDefinition } from "pinia";

import { acceptHMRUpdate } from "pinia";

export function useHmrUpdate() {
  function registerStoreHMR(
    store: StoreDefinition,
    hot: ImportMeta["hot"] | undefined
  ) {
    if (hot) {
      hot.accept(acceptHMRUpdate(store, hot));
    }
  }

  return { registerStoreHMR };
}
