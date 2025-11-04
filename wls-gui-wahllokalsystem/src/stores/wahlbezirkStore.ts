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
    getUrnenwahlSchliessungsUhrzeit,
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

  const { waehlerverzeichnisActions } = useWahlenStore();
  const { isValidDate } = useDateTimeUtils();
  const { wahlenState } = storeToRefs(useWahlenStore());

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

  const schliessungsuhrzeitGetter = computed(() => ({
    isAuszaehlungStarted:
      schliessungsuhrzeitState.value.schliessungsuhrzeitSent !== undefined,
  }));

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

    initSchliessungsuhrzeit: async function initSchliessungsuhrzeit() {
      const urnenwahlSchliessungsuhrzeit =
        await getUrnenwahlSchliessungsUhrzeit(currentUserWahlbezirkID.value);
      schliessungsuhrzeitState.value.schliessungsuhrzeit = new Date(
        urnenwahlSchliessungsuhrzeit.schliessungsuhrzeit
      );
      schliessungsuhrzeitState.value.schliessungsuhrzeitSent = new Date(
        urnenwahlSchliessungsuhrzeit.schliessungsuhrzeit
      );
    },
  };

  /* --- pflegeWaehlerverzeichnis --- */
  const pflegeWaehlerverzeichnisState: Ref<{
    pflegeWaehlerverzeichnis: PflegeWaehlerverzeichnis;
    pflegeWaehlerverzeichnisIsSaving: boolean;
  }> = ref({
    pflegeWaehlerverzeichnis: createDefaultPflegeWaehlerverzeichnis(),
    pflegeWaehlerverzeichnisIsSaving: false,
  });

  const pflegeWaehlerverzeichnisActions = {
    loadPflegeWaehlerverzeichnis: async function loadPflegeWaehlerverzeichnis(
      sendNotification = true
    ) {
      const waehlerverzeichnisNummer =
        waehlerverzeichnisActions.getWaehlerverzeichnisNummerOrUndefinedById(
          currentUserHauptWahlID.value
        );
      if (waehlerverzeichnisNummer !== undefined) {
        pflegeWaehlerverzeichnisState.value.pflegeWaehlerverzeichnis =
          await getWaehlerverzeichnis(
            currentUserWahlbezirkID.value,
            waehlerverzeichnisNummer,
            sendNotification
          );
      }
    },
    sendPflegeWaehlerverzeichnis:
      async function sendPflegeWaehlerverzeichnis() {
        const waehlerverzeichnisNummer =
          waehlerverzeichnisActions.getWaehlerverzeichnisNummerOrUndefinedById(
            currentUserHauptWahlID.value
          );
        if (waehlerverzeichnisNummer !== undefined) {
          try {
            pflegeWaehlerverzeichnisState.value.pflegeWaehlerverzeichnisIsSaving = true;
            await postWaehlerverzeichnis(
              currentUserWahlbezirkID.value,
              waehlerverzeichnisNummer,
              pflegeWaehlerverzeichnisState.value.pflegeWaehlerverzeichnis
            );
          } finally {
            pflegeWaehlerverzeichnisState.value.pflegeWaehlerverzeichnisIsSaving = false;
          }
        }
      },
  };

  /* --- urnenWahlVorbereitung --- */
  const urnenwahlVorbereitungState: Ref<{
    urnenwahlVorbereitung: Urnenwahlvorbereitung;
    urnenwahlVorbereitungIsSaving: boolean;
  }> = ref({
    urnenwahlVorbereitung: {
      anzahlNebenraeume: null,
      anzahlWahlkabinen: null,
      anzahlWahltische: null,
      urneVersiegelt: false,
      wahlbezirkID: currentUserWahlbezirkID.value,
      urnenAnzahl: [],
    },
    urnenwahlVorbereitungIsSaving: false,
  });

  const urnenwahlVorbereitungActions = {
    sendUrnenwahlvorbereitung: async function sendUrnenwahlvorbereitung() {
      const wahlbezirkID = currentUserWahlbezirkID.value;
      urnenwahlVorbereitungState.value.urnenwahlVorbereitungIsSaving = true;
      try {
        if (wahlbezirkID) {
          await postUrnenwahlvorbereitung(
            wahlbezirkID,
            urnenwahlVorbereitungState.value.urnenwahlVorbereitung
          );
        }
      } finally {
        urnenwahlVorbereitungState.value.urnenwahlVorbereitungIsSaving = false;
      }
    },
  };

  /* --- briefwahlVorbereitung --- */
  const briefwahlVorbereitungState: Ref<{
    briefwahlVorbereitung: Wahlvorbereitung;
    briefWahlVorbereitungIsSaving: boolean;
  }> = ref({
    briefwahlVorbereitung: {
      urneVersiegelt: false,
      wahlbezirkID: currentUserWahlbezirkID.value,
      urnenAnzahl: [],
    },
    briefWahlVorbereitungIsSaving: false,
  });

  const briefwahlVorbereitungActions = {
    sendBriefwahlvorbereitung: async function sendBriefwahlvorbereitung() {
      const wahlbezirkID = currentUserWahlbezirkID.value;
      briefwahlVorbereitungState.value.briefWahlVorbereitungIsSaving = true;
      try {
        if (wahlbezirkID) {
          await postBriefwahlvorbereitung(
            wahlbezirkID,
            briefwahlVorbereitungState.value.briefwahlVorbereitung
          );
        }
      } finally {
        briefwahlVorbereitungState.value.briefWahlVorbereitungIsSaving = false;
      }
    },
  };

  /* --- ungueltigeWahlscheine --- */
  const ungueltigeWahlscheineState: Ref<{
    ungueltigeWahlscheine: UngueltigerWahlschein[];
    ungueltigeWahlscheineIsLoading: boolean;
    ungueltigeWahlscheineLoadingFailed: boolean;
  }> = ref({
    ungueltigeWahlscheine: [],
    ungueltigeWahlscheineIsLoading: false,
    ungueltigeWahlscheineLoadingFailed: false,
  });

  const ungueltigeWahlscheineGetter = computed(() => ({
    ungueltigeWahlscheineIsEmpty:
      ungueltigeWahlscheineState.value.ungueltigeWahlscheine.length === 0,
  }));

  const ungueltigeWahlscheineActions = {
    getUngueltigerWahlscheinByWahlscheinnummer:
      function getUngueltigerWahlscheinByWahlscheinnummer(
        wahlscheinNummer: string
      ) {
        return (
          ungueltigeWahlscheineState.value.ungueltigeWahlscheine.find(
            (ungueltigerWahlschein) =>
              ungueltigerWahlschein.wahlscheinnummer === wahlscheinNummer
          ) ?? null
        );
      },
    initUngueltigeWahlscheine: async function initUngueltigeWahlscheine(
      sendNotification = true
    ) {
      ungueltigeWahlscheineState.value.ungueltigeWahlscheine =
        await getUngueltigeWahlscheine(
          currentUserWahltagID.value,
          currentUserWahlbezirksArt.value,
          sendNotification
        );
    },
    loadUngueltigeWahlscheine: async function loadUngueltigeWahlscheine() {
      ungueltigeWahlscheineState.value.ungueltigeWahlscheineIsLoading = true;
      ungueltigeWahlscheineState.value.ungueltigeWahlscheineLoadingFailed = false;
      ungueltigeWahlscheineState.value.ungueltigeWahlscheine = [];

      try {
        ungueltigeWahlscheineState.value.ungueltigeWahlscheine =
          await getUngueltigeWahlscheine(
            currentUserWahltagID.value,
            currentUserWahlbezirksArt.value,
            true
          );
      } catch {
        ungueltigeWahlscheineState.value.ungueltigeWahlscheineLoadingFailed = true;
      } finally {
        ungueltigeWahlscheineState.value.ungueltigeWahlscheineIsLoading = false;
      }
    },
  };

  /* --- wahlbriefDaten --- */
  const wahlbriefDatenState: Ref<{
    wahlbriefDaten: Wahlbriefdaten;
    wahlbriefDatenIsSaving: boolean;
  }> = ref({
    wahlbriefDaten: {
      wahlbriefe: undefined,
      verzeichnisseUngueltige: undefined,
      nachtraege: undefined,
      nachtraeglichUeberbrachte: undefined,
      zeitNachtraeglichUeberbrachte: undefined,
    },
    wahlbriefDatenIsSaving: false,
  });

  const wahlbriefDatenActions = {
    initWahlbriefdaten: async function initWahlbriefdaten() {
      wahlbriefDatenState.value.wahlbriefDaten = await getWahlbriefdaten(
        currentUserWahlbezirkID.value
      );
    },
    sendWahlbriefdaten: async function sendWahlbriefdaten() {
      const wahlbezirkID = currentUserWahlbezirkID.value;
      wahlbriefDatenState.value.wahlbriefDatenIsSaving = true;
      try {
        await postWahlbriefdaten(
          wahlbezirkID,
          wahlbriefDatenState.value.wahlbriefDaten
        );
      } finally {
        wahlbriefDatenState.value.wahlbriefDatenIsSaving = false;
      }
    },
  };

  /* --- watcher --- */
  watch(
    () => wahlenState.value.wahlen,
    () => {
      urnenwahlVorbereitungState.value.urnenwahlVorbereitung.urnenAnzahl =
        wahlenState.value.wahlen?.map((wahl) => ({
          wahlID: wahl.wahlID,
          anzahl: null,
        })) || [];
      briefwahlVorbereitungState.value.briefwahlVorbereitung.urnenAnzahl =
        wahlenState.value.wahlen?.map((wahl) => ({
          wahlID: wahl.wahlID,
          anzahl: null,
        })) || [];
    }
  );

  return {
    eroeffnungsuhrzeitState,
    eroeffnungsuhrzeitActions,
    schliessungsuhrzeitState,
    schliessungsuhrzeitGetter,
    schliessungsuhrzeitActions,
    pflegeWaehlerverzeichnisState,
    pflegeWaehlerverzeichnisActions,
    urnenwahlVorbereitungState,
    urnenwahlVorbereitungActions,
    briefwahlVorbereitungState,
    briefwahlVorbereitungActions,
    ungueltigeWahlscheineState,
    ungueltigeWahlscheineGetter,
    ungueltigeWahlscheineActions,
    wahlbriefDatenState,
    wahlbriefDatenActions,
  };
});

registerStoreHMR(useWahlbezirkStore);
