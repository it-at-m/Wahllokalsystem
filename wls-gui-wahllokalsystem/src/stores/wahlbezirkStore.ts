import type { UngueltigerWahlschein } from "@/types/wahlbezirk/UngueltigerWahlschein.ts";

import { defineStore, storeToRefs } from "pinia";
import { ref } from "vue";

import { useUngueltigeWahlscheineService } from "@/composables/basisdaten/ungueltigeWahlscheineService.ts";
import { useDateTimeUtils } from "@/composables/common/dateTimeUtils.ts";
import { useHmrUpdate } from "@/composables/common/hmrUpdate.ts";
import { useWahlvorbereitungService } from "@/composables/wahlvorbereitung/wahlvorbereitungService.ts";
import { useUserStore } from "@/stores/userStore.ts";

export const storeID = "wahlbezirk";
const { registerStoreHMR } = useHmrUpdate();

export const useWahlbezirkStore = defineStore(storeID, () => {
  const { postUrnenwahlSchliessungsuhrzeit, postEroeffnungsuhrzeit } =
    useWahlvorbereitungService();
  const { getUngueltigeWahlscheine } = useUngueltigeWahlscheineService();
  const {
    currentUserWahlbezirkID,
    currentUserWahltagID,
    currentUserWahlbezirksArt,
  } = storeToRefs(useUserStore());
  const { isValidDate } = useDateTimeUtils();

  const eroeffnungsuhrzeit = ref<Date | undefined>(undefined);
  const eroeffnungsuhrzeitSent = ref<Date | undefined>(undefined);
  const eroeffnungsuhrzeitIsSaving = ref(false);

  const schliessungsuhrzeit = ref<Date | undefined>(undefined);
  const schliessungsuhrzeitSent = ref<Date | undefined>(undefined);
  const schliessungsuhrzeitIsSaving = ref(false);

  const ungueltigeWahlscheine = ref<UngueltigerWahlschein[]>([]);

  async function initUngueltigeWahlscheine(sendNotification = true) {
    ungueltigeWahlscheine.value = await getUngueltigeWahlscheine(
      currentUserWahltagID.value,
      currentUserWahlbezirksArt.value,
      sendNotification
    );
  }

  async function sendEroeffnungsuhrzeit() {
    if (eroeffnungsuhrzeit.value) {
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
    if (schliessungsuhrzeit.value) {
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

  return {
    eroeffnungsuhrzeit,
    eroeffnungsuhrzeitIsSaving,
    eroeffnungsuhrzeitSent,
    schliessungsuhrzeit,
    schliessungsuhrzeitIsSaving,
    schliessungsuhrzeitSent,
    ungueltigeWahlscheine,
    initUngueltigeWahlscheine,
    sendEroeffnungsuhrzeit,
    sendSchliessungsuhrzeit,
  };
});

registerStoreHMR(useWahlbezirkStore);
