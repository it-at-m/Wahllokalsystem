import { acceptHMRUpdate, defineStore, storeToRefs } from "pinia";
import { ref } from "vue";

import { useDateTimeUtils } from "@/composables/common/dateTimeUtils.ts";
import { useWahlvorbereitungService } from "@/composables/wahlvorbereitung/wahlvorbereitungService.ts";
import { useUserStore } from "@/stores/userStore.ts";

export const storeID = "wahlbezirk";

export const useWahlbezirkStore = defineStore(storeID, () => {
  const { postUrnenwahlSchliessungsuhrzeit } = useWahlvorbereitungService();
  const { currentUserWahlbezirkID } = storeToRefs(useUserStore());
  const { isValidDate } = useDateTimeUtils();

  const schliessungsUhrzeitSent = ref<Date | undefined>(undefined);

  async function sendSchliessungsuhrzeit(time: string) {
    const wahlbezirkID = currentUserWahlbezirkID.value;
    const schliessungszeitAsDate = new Date(time);
    if (wahlbezirkID && isValidDate(schliessungszeitAsDate)) {
      await postUrnenwahlSchliessungsuhrzeit(
        wahlbezirkID,
        schliessungszeitAsDate
      );
      schliessungsUhrzeitSent.value = schliessungszeitAsDate;
    }
  }

  return { schliessungsUhrzeitSent, sendSchliessungsuhrzeit };
});

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useWahlbezirkStore, import.meta.hot));
}
