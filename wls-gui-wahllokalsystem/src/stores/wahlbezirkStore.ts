import { defineStore, storeToRefs } from "pinia";
import { ref } from "vue";

import { useWahlvorbereitungService } from "@/composables/wahlvorbereitung/wahlvorbereitungService.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { UrnenwahlSchliessungsuhrzeitBuilder } from "@/types/wahlvorbereitung/UrnenwahlSchliessungsuhrzeit.ts";

export const storeID = "wahlbezirk";

export const useWahlbezirkStore = defineStore(storeID, () => {
  const { postUrnenwahlSchliessungsuhrzeit } = useWahlvorbereitungService();
  const { currentUserWahlbezirkID } = storeToRefs(useUserStore());

  const schliessungsUhrzeit = ref<string | undefined>(undefined);

  async function sendSchliessungsuhrzeit(time: string) {
    const dto =
      UrnenwahlSchliessungsuhrzeitBuilder.createWithSchliessungsuhrzeit(time);
    const wahlbezirkID = currentUserWahlbezirkID.value;
    if (wahlbezirkID) {
      await postUrnenwahlSchliessungsuhrzeit(wahlbezirkID, dto);
      schliessungsUhrzeit.value = time;
    }
  }

  return { schliessungsUhrzeit, sendSchliessungsuhrzeit };
});
