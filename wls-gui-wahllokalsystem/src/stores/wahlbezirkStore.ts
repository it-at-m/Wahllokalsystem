import { acceptHMRUpdate, defineStore, storeToRefs } from "pinia";
import { ref } from "vue";

import { useDateTimeUtils } from "@/composables/common/dateTimeUtils.ts";
import { useWahlvorbereitungService } from "@/composables/wahlvorbereitung/wahlvorbereitungService.ts";
import { useUserStore } from "@/stores/userStore.ts";

export const storeID = "wahlbezirk";

export const useWahlbezirkStore = defineStore(storeID, () => {
  const { postUrnenwahlSchliessungsuhrzeit, postEroeffnungsuhrzeit } =
    useWahlvorbereitungService();
  const { currentUserWahlbezirkID } = storeToRefs(useUserStore());
  const { isValidDate } = useDateTimeUtils();

  const eroeffnungsuhrzeit = ref<Date | undefined>(undefined);
  const eroeffnungsuhrzeitSent = ref<Date | undefined>(undefined);
  const eroeffnungsuhrzeitIsSaving = ref(false);

  const schliessungsuhrzeit = ref<Date | undefined>(undefined);
  const schliessungsuhrzeitSent = ref<Date | undefined>(undefined);
  const schliessungsuhrzeitIsSaving = ref(false);

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

  async function sendSchliessungsuhrzeit() {
    if (currentUserWahlbezirkID.value && schliessungsuhrzeit.value) {
      const schliessungszeitToSave = new Date(schliessungsuhrzeit.value);
      schliessungsuhrzeitIsSaving.value = true;
      if (isValidDate(schliessungszeitToSave)) {
        try {
          await postUrnenwahlSchliessungsuhrzeit(
            currentUserWahlbezirkID.value,
            schliessungszeitToSave
          );
          schliessungsuhrzeitSent.value = schliessungszeitToSave;
        } finally {
          schliessungsuhrzeitIsSaving.value = false;
        }
      }
    }
  }

  return {
    eroeffnungsuhrzeit,
    eroeffnungsuhrzeitIsSaving,
    eroeffnungsuhrzeitSent,
    schliessungsuhrzeit,
    schliessungsuhrzeitIsSaving,
    schliessungsuhrzeitSent,
    sendEroeffnungsuhrzeit,
    sendSchliessungsuhrzeit,
  };
});

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useWahlbezirkStore, import.meta.hot));
}
