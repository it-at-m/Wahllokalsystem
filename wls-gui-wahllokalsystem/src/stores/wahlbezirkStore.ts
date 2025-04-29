import { defineStore, storeToRefs } from "pinia";
import { ref } from "vue";

import { useWahlvorbereitungService } from "@/composables/wahlvorbereitung/wahlvorbereitungService.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { UrnenwahlSchliessungsuhrzeitBuilder } from "@/types/wahlvorbereitung/UrnenwahlSchliessungsuhrzeit.ts";

const { postUrnenwahlSchliessungsuhrzeit } = useWahlvorbereitungService();

export const storeID = "wahlbezirk";

export const useWahlbezirkStore = defineStore(storeID, () => {
  const { currentUserWahlbezirkID } = storeToRefs(useUserStore());

  const schliessungsuhrzeit = ref<string | null>(null);

  async function sendSchliessungsuhrzeit(time: string) {
    const dto =
      UrnenwahlSchliessungsuhrzeitBuilder.createWithSchliessungsuhrzeit(time);
    const wahlbezirkID = currentUserWahlbezirkID.value;
    if (wahlbezirkID) {
      await postUrnenwahlSchliessungsuhrzeit(wahlbezirkID, dto);
      schliessungsuhrzeit.value = time;
    }
  }

  return { schliessungsuhrzeit, sendSchliessungsuhrzeit };
});
