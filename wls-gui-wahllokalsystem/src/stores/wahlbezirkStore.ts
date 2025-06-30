import type { UngueltigerWahlschein } from "@/types/wahlbezirk/UngueltigerWahlschein.ts";
import type { Urnenwahlvorbereitung } from "@/types/wahlvorbereitung/Urnenwahlvorbereitung.ts";

import { defineStore, storeToRefs } from "pinia";
import { ref, watch } from "vue";

import { useUngueltigeWahlscheineService } from "@/composables/basisdaten/ungueltigeWahlscheineService.ts";
import { useDateTimeUtils } from "@/composables/common/dateTimeUtils.ts";
import { useHmrUpdate } from "@/composables/common/hmrUpdate.ts";
import { useWahlvorbereitungService } from "@/composables/wahlvorbereitung/wahlvorbereitungService.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

export const storeID = "wahlbezirk";
const { registerStoreHMR } = useHmrUpdate();

export const useWahlbezirkStore = defineStore(storeID, () => {
  const {
    postUrnenwahlSchliessungsuhrzeit,
    postEroeffnungsuhrzeit,
    postUrnenwahlvorbereitung,
  } = useWahlvorbereitungService();
  const { getUngueltigeWahlscheine } = useUngueltigeWahlscheineService();
  const {
    currentUserWahlbezirkID,
    currentUserWahltagID,
    currentUserWahlbezirksArt,
  } = storeToRefs(useUserStore());
  const { isValidDate } = useDateTimeUtils();
  const { wahlen } = storeToRefs(useWahlenStore());
  const eroeffnungsuhrzeit = ref<Date | undefined>(undefined);
  const eroeffnungsuhrzeitSent = ref<Date | undefined>(undefined);
  const eroeffnungsuhrzeitIsSaving = ref(false);

  const schliessungsuhrzeit = ref<Date | undefined>(undefined);
  const schliessungsuhrzeitSent = ref<Date | undefined>(undefined);
  const schliessungsuhrzeitIsSaving = ref(false);
  const urnenWahlVorbereitungIsSaving = ref(false);
  const ungueltigeWahlscheine = ref<UngueltigerWahlschein[]>([]);

  const urnenwahlVorbereitung = ref<Urnenwahlvorbereitung>({
    anzahlNebenraeume: null,
    anzahlWahlkabinen: null,
    anzahlWahltische: null,
    urneVersiegelt: false,
    wahlbezirkID: currentUserWahlbezirksArt.value,
    urnenAnzahl: [],
  });

  watch(wahlen, () => {
    urnenwahlVorbereitung.value.urnenAnzahl =
      wahlen.value?.map((wahl) => ({
        wahlID: wahl.wahlID,
        anzahl: null,
      })) || [];
  });

  async function initUngueltigeWahlscheine(sendNotification = true) {
    if (currentUserWahltagID.value) {
      ungueltigeWahlscheine.value = await getUngueltigeWahlscheine(
        currentUserWahltagID.value,
        currentUserWahlbezirksArt.value,
        sendNotification
      );
    }
  }

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
    ungueltigeWahlscheine,
    initUngueltigeWahlscheine,
    sendEroeffnungsuhrzeit,
    sendSchliessungsuhrzeit,
    sendUrnenwahlvorbereitung,
    urnenwahlVorbereitung,
    urnenWahlVorbereitungIsSaving,
  };
});

registerStoreHMR(useWahlbezirkStore);
