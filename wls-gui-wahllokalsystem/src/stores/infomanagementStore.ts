import type { Konfigurationsparameter } from "@/types/infomanagement/Konfigurationsparameter.ts";

import { defineStore } from "pinia";
import { ref } from "vue";

import { useKonfigurationsparameterService } from "@/composables/infomanagement/konfigurationsparameterService.ts";

export const storeID = "infomanagement";
const { getKonfigurationsparameter } = useKonfigurationsparameterService();

export const useInfomanagementStore = defineStore(storeID, () => {
  const konfigurationsparameter = ref<Konfigurationsparameter[] | null>(null);

  async function initKonfigurationsparameter(sendNotification = true) {
    try {
      konfigurationsparameter.value =
        await getKonfigurationsparameter(sendNotification);
    } catch (error) {
      konfigurationsparameter.value = null;
      throw error;
    }
  }

  return {
    konfigurationsparameter,
    initKonfigurationsparameter,
  };
});
