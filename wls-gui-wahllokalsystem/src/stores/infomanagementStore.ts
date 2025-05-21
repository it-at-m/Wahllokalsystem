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
    konfigurationsparameter.value =
      await getKonfigurationsparameter(sendNotification);
    konfigurationsparameterReady.value = true;
  }

  return {
    konfigurationsparameter,
    konfigurationsparameterReady,
    initKonfigurationsparameter,
  };
});
