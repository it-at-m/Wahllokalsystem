import { defineStore, storeToRefs } from "pinia";
import { ref } from "vue";

import { useHmrUpdate } from "@/composables/common/hmrUpdate.ts";
import { useMonitoringService } from "@/composables/monitoring/monitoringService.ts";
import { useUserStore } from "@/stores/userStore.ts";

const { getWahlbeteiligung, postWahlbeteiligung } = useMonitoringService();
const { registerStoreHMR } = useHmrUpdate();

export const storeID = "monitoring";

export const useMonitoringStore = defineStore(storeID, () => {
  const { currentUserWahlbezirkID, currentUserHauptWahlID } =
    storeToRefs(useUserStore());

  const waehler = ref(0);

  function increaseWaehlerByOne() {
    if (waehler.value < 9999) {
      waehler.value++;
    }
  }

  async function loadWaehler() {
    const wahlbeteiligung = await getWahlbeteiligung(
      currentUserHauptWahlID.value,
      currentUserWahlbezirkID.value
    );
    waehler.value = wahlbeteiligung ? wahlbeteiligung.anzahlWaehler : 0;
  }

  async function sendWaehler() {
    await postWahlbeteiligung(
      currentUserWahlbezirkID.value,
      currentUserHauptWahlID.value,
      waehler.value
    );
  }

  return { waehler, increaseWaehlerByOne, loadWaehler, sendWaehler };
});

registerStoreHMR(useMonitoringStore);
