import type { Konfigurationsparameter } from "@/types/infomanagement/Konfigurationsparameter.ts";

import { defineStore } from "pinia";
import { ref } from "vue";

import { useKonfigurationsparameterService } from "@/composables/infomanagement/konfigurationsparameterService.ts";

export const storeID = "infomanagement";
const { getKonfigurationsparameter } = useKonfigurationsparameterService();

export const useInfomanagementStore = defineStore(storeID, () => {
  const konfigurationsparameter = ref<Konfigurationsparameter[] | null>(null);
  const konfigurationsparameterReady = ref(false);

  async function initKonfigurationsparameter(sendNotification = true) {
    try {
      konfigurationsparameter.value =
        await getKonfigurationsparameter(sendNotification);
      konfigurationsparameterReady.value =
        !!konfigurationsparameter.value &&
        konfigurationsparameter.value.length > 0;
    } catch (error) {
      konfigurationsparameter.value = null;
      konfigurationsparameterReady.value = false;
      throw error;
    }
  }

  return {
    konfigurationsparameter,
    konfigurationsparameterReady,
    initKonfigurationsparameter,
  };
});
