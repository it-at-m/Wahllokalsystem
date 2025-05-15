import { defineStore, storeToRefs } from "pinia";
import { ref } from "vue";

import { useMonitoringService } from "@/composables/monitoring/monitoringService.ts";
import { useUserStore } from "@/stores/userStore.ts";

const { getWahlbeteiligung, postWahlbeteiligung } = useMonitoringService();

export const storeID = "monitoring";

export const useMonitoringStore = defineStore(storeID, () => {
  const { currentUserWahlbezirkID, currentUserHauptWahlID } =
    storeToRefs(useUserStore());

  const waehler = ref<number>(0);

  function increaseWaehlerByOne() {
    waehler.value++;
  }

  async function loadWaehler() {
    const wahlbezirkID = currentUserWahlbezirkID.value;
    const wahlID = currentUserHauptWahlID.value;
    if (wahlbezirkID && wahlID) {
      const wahlbeteiligung = await getWahlbeteiligung(wahlID, wahlbezirkID);
      waehler.value = wahlbeteiligung ? wahlbeteiligung.anzahlWaehler : 0;
    }
  }

  async function sendWaehler() {
    const wahlbezirkID = currentUserWahlbezirkID.value;
    const wahlID = currentUserHauptWahlID.value;

    if (wahlbezirkID && wahlID) {
      await postWahlbeteiligung(wahlbezirkID, wahlID, waehler.value);
    }
  }

  return { waehler, increaseWaehlerByOne, loadWaehler, sendWaehler };
});
