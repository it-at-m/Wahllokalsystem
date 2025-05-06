import { acceptHMRUpdate, defineStore, storeToRefs } from "pinia";
import { ref } from "vue";

import { useWahlvorbereitungService } from "@/composables/wahlvorbereitung/wahlvorbereitungService.ts";
import { useUserStore } from "@/stores/userStore.ts";

export const storeID = "wahlbezirk";

export const useWahlbezirkStore = defineStore(storeID, () => {
  const { postUrnenwahlSchliessungsuhrzeit } = useWahlvorbereitungService();
  const { currentUserWahlbezirkID } = storeToRefs(useUserStore());

  const schliessungsUhrzeitSent = ref<string | undefined>(undefined);

  async function sendSchliessungsuhrzeit(time: string) {
    const wahlbezirkID = currentUserWahlbezirkID.value;
    if (wahlbezirkID) {
      await postUrnenwahlSchliessungsuhrzeit(wahlbezirkID, time);
      schliessungsUhrzeitSent.value = time;
    }
  }

  return { schliessungsUhrzeitSent, sendSchliessungsuhrzeit };
});

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useWahlbezirkStore, import.meta.hot));
}
