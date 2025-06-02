import type { Urnenwahlvorbereitung } from "@/types/wahlvorbereitung/Urnenwahlvorbereitung.ts";
import type { Wahlvorstand } from "@/types/wahlvorstand/Wahlvorstand.ts";

import { acceptHMRUpdate, defineStore, storeToRefs } from "pinia";
import { ref } from "vue";

import { useDateTimeUtils } from "@/composables/common/dateTimeUtils.ts";
import { useWahlvorbereitungService } from "@/composables/wahlvorbereitung/wahlvorbereitungService.ts";
import { useUserStore } from "@/stores/userStore.ts";

export const storeID = "wahlbezirk";

export const useWahlbezirkStore = defineStore(storeID, () => {
  const {
    postUrnenwahlSchliessungsuhrzeit,
    postEroeffnungsuhrzeit,
    postUrnenwahlvorbereitung,
  } = useWahlvorbereitungService();
  const { currentUserWahlbezirkID } = storeToRefs(useUserStore());
  const { isValidDate } = useDateTimeUtils();

  const eroeffnungsuhrzeit = ref<Date | undefined>(undefined);
  const eroeffnungsuhrzeitSent = ref<Date | undefined>(undefined);
  const eroeffnungsuhrzeitIsSaving = ref(false);

  const schliessungsUhrzeitSent = ref<Date | undefined>(undefined);

  const urnenwahlVorbereitung = ref<Urnenwahlvorbereitung>();

  async function sendEroeffnungsuhrzeit() {
    if (currentUserWahlbezirkID.value && eroeffnungsuhrzeit.value) {
      const eroeffnungsuhrzeitToSave = new Date(eroeffnungsuhrzeit.value);
      eroeffnungsuhrzeitIsSaving.value = true;
      try {
        await postEroeffnungsuhrzeit(
          currentUserWahlbezirkID.value,
          eroeffnungsuhrzeitToSave
        );
        eroeffnungsuhrzeitSent.value = eroeffnungsuhrzeitToSave;
      } finally {
        eroeffnungsuhrzeitIsSaving.value = false;
      }
    }
  }

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

  async function sendUrnenwahlvorbereitung(
    urnenwahlvorbereitung: Urnenwahlvorbereitung
  ) {
    const wahlbezirkID = currentUserWahlbezirkID.value;

    if (wahlbezirkID) {
      await postUrnenwahlvorbereitung(wahlbezirkID, urnenwahlvorbereitung);
      urnenwahlVorbereitung.value = urnenwahlvorbereitung;
    }
  }

  return {
    eroeffnungsuhrzeit,
    eroeffnungsuhrzeitIsSaving,
    eroeffnungsuhrzeitSent,
    schliessungsUhrzeitSent,
    sendEroeffnungsuhrzeit,
    sendSchliessungsuhrzeit,
    sendUrnenwahlvorbereitung,
    urnenwahlVorbereitung,
  };
});

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useWahlbezirkStore, import.meta.hot));
}
