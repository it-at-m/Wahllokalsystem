import type { Wahl } from "@/types/wahl/Wahl.ts";
import type { WahlWahlartEnum } from "@/types/wahl/WahlWahlartEnum.ts";
import type { Ref } from "vue";

import { defineStore, storeToRefs } from "pinia";
import { computed, ref } from "vue";

import { useBeanstandeteWahlbriefeGetter } from "@/composables/briefwahl/beanstandeteWahlbriefeGetter.ts";
import { useBriefwahlService } from "@/composables/briefwahl/briefwahlService.ts";
import { useHmrUpdate } from "@/composables/common/hmrUpdate.ts";
import { useErgebnisermittlungService } from "@/composables/ergebnisermittlung/ergebnisermittlungService.ts";
import { useWaehlerverzeichnisGetter } from "@/composables/wahl/waehlerverzeichnisGetter.ts";
import { useWahlService } from "@/composables/wahl/wahlService.ts";
import { useUserStore } from "@/stores/userStore.ts";

export const storeID = "wahlen";
const wahlenService = useWahlService();
const briefwahlService = useBriefwahlService();
const { getStimmzettelumschlaege, postStimmzettelumschlaege } =
  useErgebnisermittlungService();
const { registerStoreHMR } = useHmrUpdate();

export const useWahlenStore = defineStore(storeID, () => {
  const {
    currentUserWahltagID,
    currentUserWahlbezirkID,
    user,
    currentUserWahlbezirksArt,
  } = storeToRefs(useUserStore());

  /* --- wahlen --- */
  const wahlenState: Ref<{
    wahlen: Wahl[] | null;
  }> = ref({
    wahlen: null,
  });

  const wahlenActions = {
    initWahlen: async function initWahlen(sendNotification = true) {
      wahlenState.value.wahlen = await wahlenService.getWahlen(
        currentUserWahltagID.value,
        sendNotification
      );

      _mapWahlMetaDataToWahlNummer();
      _sortWahlenByWahlNummer();
    },
    getWahlOrUndefinedById: function getWahlOrUndefinedById(wahlID: string) {
      return wahlenState.value.wahlen?.find((wahl) => wahl.wahlID === wahlID);
    },
    getWahlNameOrBlankStringById: function getWahlNameOrBlankStringById(
      wahlID: string
    ) {
      const wahl = wahlenActions.getWahlOrUndefinedById(wahlID);
      return wahl ? wahl.name : "";
    },
    getWahlTagOrBlankStringById: function getWahlTagOrBlankStringById(
      wahlID: string
    ) {
      const wahl = wahlenActions.getWahlOrUndefinedById(wahlID);
      return wahl ? wahl.wahltag : "";
    },
    getWahlIdOrUndefinedByWahlart: function getWahlIdOrUndefinedByWahlart(
      wahlart: WahlWahlartEnum
    ) {
      if (wahlenState.value.wahlen) {
        const wahl = wahlenState.value.wahlen.find(
          (wahl) => wahl.wahlart === wahlart
        );
        return wahl ? wahl.wahlID : undefined;
      }
    },
  };

  /* --- beanstandeteWahlbriefe --- */
  const beanstandeteWahlbriefeState: Ref<{
    isBeanstandeteWahlbriefeTableValid: null | boolean;
    isBeanstandeteWahlbriefeSaving: boolean;
  }> = ref({
    isBeanstandeteWahlbriefeTableValid: true,
    isBeanstandeteWahlbriefeSaving: false,
  });

  const {
    summeGueltigerWahlbriefe,
    summeUngueltigerWahlbriefe,
    summenZurueckweisungsgruende,
  } = useBeanstandeteWahlbriefeGetter(wahlenState);
  const beanstandeteWahlbriefeGetter = computed(() => ({
    summeGueltigerWahlbriefe: summeGueltigerWahlbriefe.value,
    summeUngueltigerWahlbriefe: summeUngueltigerWahlbriefe.value,
    summenZurueckweisungsgruende: summenZurueckweisungsgruende.value,
  }));

  const beanstandeteWahlbriefeActions = {
    initBeanstandeteWahlbriefe: async function initBeanstandeteWahlbriefe() {
      for (const wvzNr of waehlerverzeichnisGetter.value
        .waehlerverzeichnisNummern) {
        const beanstandeteWahlbriefe =
          await briefwahlService.getBeanstandeteWahlbriefe(
            wvzNr,
            currentUserWahlbezirkID.value
          );
        if (wahlenState.value.wahlen && beanstandeteWahlbriefe) {
          wahlenState.value.wahlen.forEach((wahl) => {
            if (wahl.waehlerverzeichnisNummer == wvzNr) {
              wahl.beanstandeteWahlbriefe =
                beanstandeteWahlbriefe.beanstandeteWahlbriefe.get(
                  wahl.wahlID
                ) ?? [];
            }
          });
        }
      }
    },
    addBeanstandeterWahlbriefEntry: function addBeanstandeterWahlbriefEntry() {
      if (wahlenState.value.wahlen) {
        wahlenState.value.wahlen.map((wahl) =>
          wahl.beanstandeteWahlbriefe.push(null)
        );
      }
    },
    deleteBeanstandeterWahlbriefEntry:
      function deleteBeanstandeterWahlbriefEntry(index: number) {
        if (wahlenState.value.wahlen) {
          wahlenState.value.wahlen.forEach((wahl) =>
            wahl.beanstandeteWahlbriefe.splice(index, 1)
          );
        }
      },
    isBeanstandeterWahlbriefEntryEmpty:
      function isBeanstandeterWahlbriefEntryEmpty(index: number) {
        return !wahlenState.value.wahlen?.some(
          (wahl) => wahl.beanstandeteWahlbriefe[index] !== null
        );
      },
    getBeanstandeterWahlbriefEntryByWahl:
      function getBeanstandeterWahlbriefEntryByWahl(
        index: number,
        wahlId: string
      ) {
        return wahlenState.value.wahlen?.find((wahl) => wahl.wahlID === wahlId)
          ?.beanstandeteWahlbriefe[index];
      },
    saveBeanstandeteWahlbriefe: async function saveBeanstandeteWahlbriefe() {
      beanstandeteWahlbriefeState.value.isBeanstandeteWahlbriefeSaving = true;

      try {
        const wahlenGroupedByWvzNr = new Map<number, Wahl[]>();
        if (wahlenState.value.wahlen) {
          for (const wahl of wahlenState.value.wahlen) {
            const wahlenWithWVZNummer =
              wahlenGroupedByWvzNr.get(wahl.waehlerverzeichnisNummer) ?? [];
            wahlenWithWVZNummer.push(wahl);
            wahlenGroupedByWvzNr.set(
              wahl.waehlerverzeichnisNummer,
              wahlenWithWVZNummer
            );
          }
        }

        await briefwahlService.postBeanstandeteWahlbriefe(
          wahlenGroupedByWvzNr,
          currentUserWahlbezirkID.value
        );
      } finally {
        beanstandeteWahlbriefeState.value.isBeanstandeteWahlbriefeSaving = false;
      }
    },
  };

  /* --- stimmzettelumschlaege --- */
  const stimmzettelumschlaegeState: Ref<{
    isStimmzettelumschlaegeSaving: boolean;
  }> = ref({
    isStimmzettelumschlaegeSaving: false,
  });

  const stimmzettelumschlaegeActions = {
    loadStimmzettelumschlaege: async function loadStimmzettelumschlaege(
      wahlID: string,
      sendNotification = true
    ) {
      const wahl = wahlenActions.getWahlOrUndefinedById(wahlID);
      if (wahl) {
        const loadedStimmzettelumschlaege = await getStimmzettelumschlaege(
          wahl.wahlID,
          currentUserWahlbezirkID.value,
          currentUserWahlbezirksArt.value,
          wahl.name,
          sendNotification
        );

        if (loadedStimmzettelumschlaege) {
          wahl.stimmzettelumschlaege = loadedStimmzettelumschlaege;
        }
      }
    },
    saveStimmzettelumschlaege: async function saveStimmzettelumschlaege(
      wahlID: string
    ) {
      const wahl = wahlenActions.getWahlOrUndefinedById(wahlID);
      if (wahl) {
        stimmzettelumschlaegeState.value.isStimmzettelumschlaegeSaving = true;
        try {
          await postStimmzettelumschlaege(
            wahl.wahlID,
            currentUserWahlbezirkID.value,
            wahl.stimmzettelumschlaege,
            currentUserWahlbezirksArt.value,
            wahl.name
          );
        } finally {
          stimmzettelumschlaegeState.value.isStimmzettelumschlaegeSaving = false;
        }
      }
    },
  };

  /* --- waehlerverzeichnis --- */
  const { waehlerverzeichnisNummern } =
    useWaehlerverzeichnisGetter(wahlenState);
  const waehlerverzeichnisGetter = computed(() => ({
    waehlerverzeichnisNummern: waehlerverzeichnisNummern.value,
  }));

  const waehlerverzeichnisActions = {
    getWaehlerverzeichnisNummerOrUndefinedById:
      function getWaehlerverzeichnisNummerOrUndefinedById(wahlID: string) {
        const wahl = wahlenActions.getWahlOrUndefinedById(wahlID);
        return wahl ? wahl.waehlerverzeichnisNummer : undefined;
      },
  };

  function _mapWahlMetaDataToWahlNummer() {
    if (wahlenState.value.wahlen && user.value?.wahlMetaData) {
      const wahlnummerMap = new Map(
        user.value.wahlMetaData.map((meta) => [meta.wahlID, meta.wahlnummer])
      );
      wahlenState.value.wahlen.forEach((wahl) => {
        const wahlnummer = wahlnummerMap.get(wahl.wahlID);
        if (wahlnummer !== undefined) {
          wahl.nummer = wahlnummer;
        }
      });
    }
  }

  function _sortWahlenByWahlNummer() {
    if (wahlenState.value.wahlen) {
      wahlenState.value.wahlen.sort((a: Wahl, b: Wahl) => {
        if (a.nummer && b.nummer) {
          return a.nummer.localeCompare(b.nummer);
        } else {
          return 0;
        }
      });
    }
  }

  return {
    wahlenState,
    wahlenActions,
    beanstandeteWahlbriefeState,
    beanstandeteWahlbriefeGetter,
    beanstandeteWahlbriefeActions,
    stimmzettelumschlaegeState,
    stimmzettelumschlaegeActions,
    waehlerverzeichnisGetter,
    waehlerverzeichnisActions,
  };
});

registerStoreHMR(useWahlenStore);
