import type { Urnenwahlvorbereitung } from "@/types/wahlvorbereitung/Urnenwahlvorbereitung.ts";

import { defineStore, storeToRefs } from "pinia";
import { ref } from "vue";

import { useDateTimeUtils } from "@/composables/common/dateTimeUtils.ts";
import { useHmrUpdate } from "@/composables/common/hmrUpdate.ts";
import { useWahlvorbereitungService } from "@/composables/wahlvorbereitung/wahlvorbereitungService.ts";
import { useUserStore } from "@/stores/userStore.ts";

export const storeID = "wahlbezirk";
const { registerStoreHMR } = useHmrUpdate();

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

  const schliessungsuhrzeit = ref<Date | undefined>(undefined);
  const schliessungsuhrzeitSent = ref<Date | undefined>(undefined);
  const schliessungsuhrzeitIsSaving = ref(false);

  const urnenWahlVorbereitungIsSaving = ref(false);

  const urnenwahlVorbereitung = ref<Urnenwahlvorbereitung | undefined>(
    undefined
  );

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
      if (isValidDate(schliessungszeitToSave)) {
        schliessungsuhrzeitIsSaving.value = true;
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

  async function sendUrnenwahlvorbereitung(
    urnenwahlvorbereitung: Urnenwahlvorbereitung
  ) {
    const wahlbezirkID = currentUserWahlbezirkID.value;
    urnenWahlVorbereitungIsSaving.value = true;
    try {
      if (wahlbezirkID) {
        await postUrnenwahlvorbereitung(wahlbezirkID, urnenwahlvorbereitung);
        urnenwahlVorbereitung.value = urnenwahlvorbereitung;
      }
    } finally {
      urnenWahlVorbereitungIsSaving.value = false;
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
    sendUrnenwahlvorbereitung,
    urnenwahlVorbereitung,
    urnenWahlVorbereitungIsSaving,
  };
});

registerStoreHMR(useWahlbezirkStore);
