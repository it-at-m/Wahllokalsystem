import type { PflegeWaehlerverzeichnis } from "@/types/wahlbezirk/PflegeWaehlerverzeichnis.ts";
import type { UngueltigerWahlschein } from "@/types/wahlbezirk/UngueltigerWahlschein.ts";
import type { Urnenwahlvorbereitung } from "@/types/wahlvorbereitung/Urnenwahlvorbereitung.ts";

import { defineStore, storeToRefs } from "pinia";
import { ref, watch } from "vue";

import { useUngueltigeWahlscheineService } from "@/composables/basisdaten/ungueltigeWahlscheineService.ts";
import { useDateTimeUtils } from "@/composables/common/dateTimeUtils.ts";
import { useHmrUpdate } from "@/composables/common/hmrUpdate.ts";
import { useWaehlerverzeichnisService } from "@/composables/wahlvorbereitung/waehlerverzeichnisService.ts";
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
    currentUserHauptWahlID,
  } = storeToRefs(useUserStore());
  const {
    createDefaultPflegeWaehlerverzeichnis,
    getWaehlerverzeichnis,
    postWaehlerverzeichnis,
  } = useWaehlerverzeichnisService();

  const { getWaehlerverzeichnisOrUndefinedById } = useWahlenStore();
  const { isValidDate } = useDateTimeUtils();
  const { wahlen } = storeToRefs(useWahlenStore());
  const eroeffnungsuhrzeit = ref<Date | undefined>(undefined);
  const eroeffnungsuhrzeitSent = ref<Date | undefined>(undefined);
  const eroeffnungsuhrzeitIsSaving = ref(false);

  const pflegeWaehlerverzeichnis = ref<PflegeWaehlerverzeichnis>(
    createDefaultPflegeWaehlerverzeichnis()
  );
  const pflegeWaehlerverzeichnisIsSaving = ref(false);

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
    ungueltigeWahlscheine.value = await getUngueltigeWahlscheine(
      currentUserWahltagID.value,
      currentUserWahlbezirksArt.value,
      sendNotification
    );
  }

  async function loadPflegeWaehlerverzeichnis(sendNotification = true) {
    const waehlerverzeichnisNummer = getWaehlerverzeichnisOrUndefinedById(
      currentUserHauptWahlID.value
    );
    if (waehlerverzeichnisNummer !== undefined) {
      pflegeWaehlerverzeichnis.value = await getWaehlerverzeichnis(
        currentUserWahlbezirkID.value,
        waehlerverzeichnisNummer
      );
    } else {
      console.warn(
        `sendPflegeWaehlerverzeichnis - es gibt keine waehlerverzeichnisNummer`
      );
    }
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

  async function sendPflegeWaehlerverzeichnis() {
    const waehlerverzeichnisNummer = getWaehlerverzeichnisOrUndefinedById(
      currentUserHauptWahlID.value
    );
    if (waehlerverzeichnisNummer !== undefined) {
      try {
        pflegeWaehlerverzeichnisIsSaving.value = true;
        await postWaehlerverzeichnis(
          currentUserWahlbezirkID.value,
          waehlerverzeichnisNummer,
          pflegeWaehlerverzeichnis.value
        );
      } finally {
        pflegeWaehlerverzeichnisIsSaving.value = false;
      }
    } else {
      console.warn(
        `sendPflegeWaehlerverzeichnis - es gibt keine waehlerverzeichnisNummer`
      );
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
    pflegeWaehlerverzeichnis,
    pflegeWaehlerverzeichnisIsSaving,
    schliessungsuhrzeit,
    schliessungsuhrzeitIsSaving,
    schliessungsuhrzeitSent,
    ungueltigeWahlscheine,
    initUngueltigeWahlscheine,
    loadPflegeWaehlerverzeichnis,
    sendEroeffnungsuhrzeit,
    sendPflegeWaehlerverzeichnis,
    sendSchliessungsuhrzeit,
    sendUrnenwahlvorbereitung,
    urnenwahlVorbereitung,
    urnenWahlVorbereitungIsSaving,
  };
});

registerStoreHMR(useWahlbezirkStore);
