import type { Wahlbriefdaten } from "@/types/briefwahl/Wahlbriefdaten";
import type { PflegeWaehlerverzeichnis } from "@/types/wahlbezirk/PflegeWaehlerverzeichnis.ts";
import type { UngueltigerWahlschein } from "@/types/wahlbezirk/UngueltigerWahlschein.ts";
import type { Urnenwahlvorbereitung } from "@/types/wahlhandlung/Urnenwahlvorbereitung.ts";
import type { Wahlvorbereitung } from "@/types/wahlhandlung/Wahlvorbereitung.ts";
import type { Ref } from "vue";

import { defineStore, storeToRefs } from "pinia";
import { computed, ref, watch } from "vue";

import { useUngueltigeWahlscheineService } from "@/composables/basisdaten/ungueltigeWahlscheineService.ts";
import { useBriefwahlService } from "@/composables/briefwahl/briefwahlService";
import { useDateTimeUtils } from "@/composables/common/dateTimeUtils.ts";
import { useHmrUpdate } from "@/composables/common/hmrUpdate.ts";
import { useWaehlerverzeichnisService } from "@/composables/wahlhandlung/waehlerverzeichnisService.ts";
import { useWahlvorbereitungService } from "@/composables/wahlhandlung/wahlvorbereitungService.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

export const storeID = "wahlbezirk";
const { registerStoreHMR } = useHmrUpdate();

export const useWahlbezirkStore = defineStore(storeID, () => {
  const {
    postUrnenwahlSchliessungsuhrzeit,
    postEroeffnungsuhrzeit,
    postUrnenwahlvorbereitung,
    postBriefwahlvorbereitung,
  } = useWahlvorbereitungService();
  const { getUngueltigeWahlscheine } = useUngueltigeWahlscheineService();
  const { getWahlbriefdaten, postWahlbriefdaten } = useBriefwahlService();
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

  const { getWaehlerverzeichnisNummerOrUndefinedById } = useWahlenStore();
  const { isValidDate } = useDateTimeUtils();
  const { wahlen } = storeToRefs(useWahlenStore());

  /* --- eroeffnungsuhrzeit --- */
  const eroeffnungsuhrzeitState: Ref<{
    eroeffnungsuhrzeit: Date | undefined;
    eroeffnungsuhrzeitSent: Date | undefined;
    eroeffnungsuhrzeitIsSaving: boolean;
  }> = ref({
    eroeffnungsuhrzeit: undefined,
    eroeffnungsuhrzeitSent: undefined,
    eroeffnungsuhrzeitIsSaving: false,
  });

  const eroeffnungsuhrzeitActions = {
    sendEroeffnungsuhrzeit: async function sendEroeffnungsuhrzeit() {
      if (eroeffnungsuhrzeitState.value.eroeffnungsuhrzeit) {
        const eroeffnungsuhrzeitToSave = new Date(
          eroeffnungsuhrzeitState.value.eroeffnungsuhrzeit
        );
        eroeffnungsuhrzeitState.value.eroeffnungsuhrzeitIsSaving = true;
        try {
          await postEroeffnungsuhrzeit(
            currentUserWahlbezirkID.value,
            eroeffnungsuhrzeitToSave
          );
          eroeffnungsuhrzeitState.value.eroeffnungsuhrzeitSent =
            eroeffnungsuhrzeitToSave;
        } finally {
          eroeffnungsuhrzeitState.value.eroeffnungsuhrzeitIsSaving = false;
        }
      }
    },
  };

  /* --- schliessungsuhrzeit --- */
  const schliessungsuhrzeitState: Ref<{
    schliessungsuhrzeit: Date | undefined;
    schliessungsuhrzeitSent: Date | undefined;
    schliessungsuhrzeitIsSaving: boolean;
  }> = ref({
    schliessungsuhrzeit: undefined,
    schliessungsuhrzeitSent: undefined,
    schliessungsuhrzeitIsSaving: false,
  });

  const schliessungsuhrzeitActions = {
    sendSchliessungsuhrzeit: async function sendSchliessungsuhrzeit() {
      if (schliessungsuhrzeitState.value.schliessungsuhrzeit) {
        const schliessungszeitToSave = new Date(
          schliessungsuhrzeitState.value.schliessungsuhrzeit
        );
        if (isValidDate(schliessungszeitToSave)) {
          schliessungsuhrzeitState.value.schliessungsuhrzeitIsSaving = true;
          try {
            await postUrnenwahlSchliessungsuhrzeit(
              currentUserWahlbezirkID.value,
              schliessungszeitToSave
            );
            schliessungsuhrzeitState.value.schliessungsuhrzeitSent =
              schliessungszeitToSave;
          } finally {
            schliessungsuhrzeitState.value.schliessungsuhrzeitIsSaving = false;
          }
        }
      }
    },
  };

  const pflegeWaehlerverzeichnis = ref<PflegeWaehlerverzeichnis>(
    createDefaultPflegeWaehlerverzeichnis()
  );
  const pflegeWaehlerverzeichnisIsSaving = ref(false);

  const urnenWahlVorbereitungIsSaving = ref(false);
  const briefWahlVorbereitungIsSaving = ref(false);
  const ungueltigeWahlscheine = ref<UngueltigerWahlschein[]>([]);
  const ungueltigeWahlscheineIsLoading = ref(false);
  const ungueltigeWahlscheineLoadingFailed = ref(false);
  const ungueltigeWahlscheineIsEmpty = computed(
    () => ungueltigeWahlscheine.value.length === 0
  );

  const wahlbriefDatenIsSaving = ref(false);
  const wahlbriefDaten = ref<Wahlbriefdaten>({
    wahlbriefe: undefined,
    verzeichnisseUngueltige: undefined,
    nachtraege: undefined,
    nachtraeglichUeberbrachte: undefined,
    zeitNachtraeglichUeberbrachte: undefined,
  });

  const urnenwahlVorbereitung = ref<Urnenwahlvorbereitung>({
    anzahlNebenraeume: null,
    anzahlWahlkabinen: null,
    anzahlWahltische: null,
    urneVersiegelt: false,
    wahlbezirkID: currentUserWahlbezirkID.value,
    urnenAnzahl: [],
  });

  const briefwahlVorbereitung = ref<Wahlvorbereitung>({
    urneVersiegelt: false,
    wahlbezirkID: currentUserWahlbezirkID.value,
    urnenAnzahl: [],
  });

  watch(wahlen, () => {
    urnenwahlVorbereitung.value.urnenAnzahl =
      wahlen.value?.map((wahl) => ({
        wahlID: wahl.wahlID,
        anzahl: null,
      })) || [];
    briefwahlVorbereitung.value.urnenAnzahl =
      wahlen.value?.map((wahl) => ({
        wahlID: wahl.wahlID,
        anzahl: null,
      })) || [];
  });

  function getUngueltigerWahlscheinByWahlscheinnummer(
    wahlscheinNummer: string
  ) {
    return (
      ungueltigeWahlscheine.value.find(
        (ungueltigerWahlschein) =>
          ungueltigerWahlschein.wahlscheinnummer === wahlscheinNummer
      ) ?? null
    );
  }

  async function initUngueltigeWahlscheine(sendNotification = true) {
    ungueltigeWahlscheine.value = await getUngueltigeWahlscheine(
      currentUserWahltagID.value,
      currentUserWahlbezirksArt.value,
      sendNotification
    );
  }

  async function initWahlbriefdaten() {
    wahlbriefDaten.value = await getWahlbriefdaten(
      currentUserWahlbezirkID.value
    );
  }

  async function loadPflegeWaehlerverzeichnis(sendNotification = true) {
    const waehlerverzeichnisNummer = getWaehlerverzeichnisNummerOrUndefinedById(
      currentUserHauptWahlID.value
    );
    if (waehlerverzeichnisNummer !== undefined) {
      pflegeWaehlerverzeichnis.value = await getWaehlerverzeichnis(
        currentUserWahlbezirkID.value,
        waehlerverzeichnisNummer,
        sendNotification
      );
    }
  }

  async function loadUngueltigeWahlscheine() {
    ungueltigeWahlscheineIsLoading.value = true;
    ungueltigeWahlscheineLoadingFailed.value = false;
    ungueltigeWahlscheine.value = [];
    try {
      ungueltigeWahlscheine.value = await getUngueltigeWahlscheine(
        currentUserWahltagID.value,
        currentUserWahlbezirksArt.value,
        true
      );
    } catch {
      ungueltigeWahlscheineLoadingFailed.value = true;
    } finally {
      ungueltigeWahlscheineIsLoading.value = false;
    }
  }

  async function sendPflegeWaehlerverzeichnis() {
    const waehlerverzeichnisNummer = getWaehlerverzeichnisNummerOrUndefinedById(
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
    }
  }

  async function sendUrnenwahlvorbereitung() {
    const wahlbezirkID = currentUserWahlbezirkID.value;
    urnenWahlVorbereitungIsSaving.value = true;
    try {
      if (wahlbezirkID) {
        await postUrnenwahlvorbereitung(
          wahlbezirkID,
          urnenwahlVorbereitung.value
        );
      }
    } finally {
      urnenWahlVorbereitungIsSaving.value = false;
    }
  }

  async function sendWahlbriefdaten() {
    const wahlbezirkID = currentUserWahlbezirkID.value;
    wahlbriefDatenIsSaving.value = true;
    try {
      await postWahlbriefdaten(wahlbezirkID, wahlbriefDaten.value);
    } finally {
      wahlbriefDatenIsSaving.value = false;
    }
  }

  async function sendBriefwahlvorbereitung() {
    const wahlbezirkID = currentUserWahlbezirkID.value;
    briefWahlVorbereitungIsSaving.value = true;
    try {
      if (wahlbezirkID) {
        await postBriefwahlvorbereitung(
          wahlbezirkID,
          briefwahlVorbereitung.value
        );
      }
    } finally {
      briefWahlVorbereitungIsSaving.value = false;
    }
  }

  return {
    eroeffnungsuhrzeitState,
    eroeffnungsuhrzeitActions,
    schliessungsuhrzeitState,
    schliessungsuhrzeitActions,
    pflegeWaehlerverzeichnis,
    pflegeWaehlerverzeichnisIsSaving,
    ungueltigeWahlscheine,
    ungueltigeWahlscheineIsLoading,
    ungueltigeWahlscheineIsEmpty,
    ungueltigeWahlscheineLoadingFailed,
    getUngueltigerWahlscheinByWahlscheinnummer,
    initUngueltigeWahlscheine,
    loadPflegeWaehlerverzeichnis,
    loadUngueltigeWahlscheine,
    sendPflegeWaehlerverzeichnis,
    sendUrnenwahlvorbereitung,
    urnenwahlVorbereitung,
    urnenWahlVorbereitungIsSaving,
    initWahlbriefdaten,
    sendWahlbriefdaten,
    wahlbriefDaten,
    wahlbriefDatenIsSaving,
    sendBriefwahlvorbereitung,
    briefwahlVorbereitung,
    briefWahlVorbereitungIsSaving,
  };
});

registerStoreHMR(useWahlbezirkStore);
