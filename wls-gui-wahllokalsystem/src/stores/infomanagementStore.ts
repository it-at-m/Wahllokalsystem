import type { Konfigurationsparameter } from "@/types/infomanagement/Konfigurationsparameter.ts";

import { defineStore, storeToRefs } from "pinia";
import { computed, ref } from "vue";

import { useHmrUpdate } from "@/composables/common/hmrUpdate.ts";
import { useKonfigurationsparameterService } from "@/composables/infomanagement/konfigurationsparameterService.ts";
import { useUserStore } from "@/stores/userStore.ts";

export const storeID = "infomanagement";
const { getKonfigurationsparameter } = useKonfigurationsparameterService();
const { registerStoreHMR } = useHmrUpdate();

const KONFIG_KEY_CHECK_ANWESENHEIT = "MELDUNGSZEIT_ANWESENHEIT_CHECK";

export const useInfomanagementStore = defineStore(storeID, () => {
  const { currentUserWahltag } = storeToRefs(useUserStore());

  const konfigurationsparameter = ref<Konfigurationsparameter[] | null>(null);

  const timeToCheckAnwesenheit = computed(() => {
    const timeToCheckAnwesenheit = konfigurationsparameter.value?.find(
      (parameter) => parameter.schluessel === KONFIG_KEY_CHECK_ANWESENHEIT
    )?.wert;

    return timeToCheckAnwesenheit && currentUserWahltag.value
      ? new Date(`${currentUserWahltag.value}T${timeToCheckAnwesenheit}`)
      : undefined;
  });

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
    timeToCheckAnwesenheit,
    initKonfigurationsparameter,
  };
});

registerStoreHMR(useInfomanagementStore);
