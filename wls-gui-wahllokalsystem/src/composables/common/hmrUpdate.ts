import type { StoreDefinition } from "pinia";

import { acceptHMRUpdate } from "pinia";

export function useHmrUpdate() {
  function registerStoreHMR(store: StoreDefinition) {
    if (import.meta.hot) {
      import.meta.hot.accept(acceptHMRUpdate(store, import.meta.hot));
    }
  }

  return { registerStoreHMR };
}
