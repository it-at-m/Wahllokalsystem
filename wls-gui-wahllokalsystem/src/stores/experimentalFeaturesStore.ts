import { defineStore } from "pinia";

import { useHmrUpdate } from "@/composables/common/hmrUpdate.ts";

const { registerStoreHMR } = useHmrUpdate();

const storeID = "experimentalFeaturesStore";

export const useExperimentalFeaturesStore = defineStore(storeID, () => {});

registerStoreHMR(useExperimentalFeaturesStore);
