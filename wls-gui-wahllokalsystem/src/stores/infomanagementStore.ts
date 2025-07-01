import type { Konfigurationsparameter } from "@/types/infomanagement/Konfigurationsparameter.ts";

import { defineStore } from "pinia";
import { computed, ref } from "vue";

import { useHmrUpdate } from "@/composables/common/hmrUpdate.ts";
import { useKonfigurationsparameterService } from "@/composables/infomanagement/konfigurationsparameterService.ts";

export const storeID = "infomanagement";
const { getKonfigurationsparameter } = useKonfigurationsparameterService();
const { registerStoreHMR } = useHmrUpdate();

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

  /** FRUEHESTE_EROEFFNUNGSZEIT bezeichnet den frühesten Wert, zu dem die Wahlhandlung eröffnet werden kann. */
  const fruehesteEroeffnungsuhrzeitUWB = computed(() => {
    const param = konfigurationsparameter.value?.find(
      (param) => param.schluessel === "FRUEHESTE_EROEFFNUNGSZEIT_UW"
    );
    return param?.wert || "00:00:00";
  });

  /** FRUEHESTE_SCHLIESSUNGSZEIT bezeichnet den spätesten Wert, zu dem die Wahlhandlung eröffnet werden kann. */
  const fruehesteSchliessungsuhrzeitUWB = computed(() => {
    const param = konfigurationsparameter.value?.find(
      (param) => param.schluessel === "FRUEHESTE_SCHLIESSUNGSZEIT_UW"
    );
    return param?.wert || "00:00:00";
  });

  return {
    konfigurationsparameter,
    initKonfigurationsparameter,
    fruehesteEroeffnungsuhrzeitUWB,
    fruehesteSchliessungsuhrzeitUWB,
  };
});

registerStoreHMR(useInfomanagementStore);
