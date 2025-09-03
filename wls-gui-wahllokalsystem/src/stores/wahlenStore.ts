import type { Wahl } from "@/types/wahl/Wahl.ts";
import type { Ref } from "vue";

import { defineStore, storeToRefs } from "pinia";
import { computed, ref } from "vue";

import { useBriefwahlService } from "@/composables/briefwahl/briefwahlService.ts";
import { useHmrUpdate } from "@/composables/common/hmrUpdate.ts";
import { useErgebnisermittlungService } from "@/composables/ergebnisermittlung/ergebnisermittlungService.ts";
import { useWahlService } from "@/composables/wahl/wahlService.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { ZurueckweisungsgrundEnum } from "@/types/briefwahl/ZurueckweisungsgrundEnum.ts";

export const storeID = "wahlen";
const wahlenService = useWahlService();
const briefwahlService = useBriefwahlService();
const ergebnisermittlungService = useErgebnisermittlungService();
const { registerStoreHMR } = useHmrUpdate();

export const useWahlenStore = defineStore(storeID, () => {
  const { currentUserWahltagID, currentUserWahlbezirkID, user } =
    storeToRefs(useUserStore());

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
  };

  /* --- beanstandeteWahlbriefe --- */
  const beanstandeteWahlbriefeState: Ref<{
    isBeanstandeteWahlbriefeTableValid: null | boolean;
    isBeanstandeteWahlbriefeSaving: boolean;
  }> = ref({
    isBeanstandeteWahlbriefeTableValid: true,
    isBeanstandeteWahlbriefeSaving: false,
  });

  const beanstandeteWahlbriefeGetter = computed(() => ({
    summeGueltigerWahlbriefe: () => {
      if (!wahlenState.value.wahlen) return [];
      return wahlenState.value.wahlen.map(
        (wahl) =>
          wahl.beanstandeteWahlbriefe.filter(
            (brief) => brief === ZurueckweisungsgrundEnum.Zugelassen
          ).length
      );
    },
    summeUngueltigerWahlbriefe: () => {
      if (!wahlenState.value.wahlen) return [];
      return wahlenState.value.wahlen.map(
        (wahl) =>
          wahl.beanstandeteWahlbriefe.filter(
            (brief) =>
              brief !== ZurueckweisungsgrundEnum.Zugelassen && brief !== null
          ).length
      );
    },
    summenZurueckweisungsgruende: () => {
      if (!wahlenState.value.wahlen) return [];
      const anzahlWahlen = wahlenState.value.wahlen.length;
      const summenZurueckweisungsgruende = Object.values(
        ZurueckweisungsgrundEnum
      )
        .filter((grund) => grund !== ZurueckweisungsgrundEnum.Zugelassen)
        .map((grund) => ({
          summen: new Array(anzahlWahlen).fill(0),
          grund: grund,
        }));

      wahlenState.value.wahlen.forEach((wahl, wahlIndex) => {
        if (
          wahl.beanstandeteWahlbriefe &&
          wahl.beanstandeteWahlbriefe.every((grund) => grund !== null)
        ) {
          wahl.beanstandeteWahlbriefe.forEach((beanstandeterWahlbrief) => {
            if (
              beanstandeterWahlbrief !== ZurueckweisungsgrundEnum.Zugelassen
            ) {
              const index = summenZurueckweisungsgruende.findIndex(
                (item) => item.grund === beanstandeterWahlbrief
              );
              summenZurueckweisungsgruende[index].summen[wahlIndex] += 1;
            }
          });
        }
      });
      return summenZurueckweisungsgruende;
    },
  }));

  const beanstandeteWahlbriefeActions = {
    initBeanstandeteWahlbriefe: async function initBeanstandeteWahlbriefe() {
      for (const wvzNr of waehlerverzeichnisNummern.value) {
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
    saveStimmzettelumschlaege: async function saveStimmzettelumschlaege(
      wahlID: string
    ) {
      const wahl = wahlenActions.getWahlOrUndefinedById(wahlID);
      if (wahl) {
        stimmzettelumschlaegeState.value.isStimmzettelumschlaegeSaving = true;
        try {
          await ergebnisermittlungService.saveStimmzettelumschlaege(
            wahl.wahlID,
            currentUserWahlbezirkID.value,
            wahl.stimmzettelumschlaege
          );
        } finally {
          stimmzettelumschlaegeState.value.isStimmzettelumschlaegeSaving = false;
        }
      }
    },
  };

  const waehlerverzeichnisNummern = computed<number[]>(() => {
    if (!wahlenState.value.wahlen) return [];

    const nummern = new Set<number>();

    for (const wahl of wahlenState.value.wahlen) {
      nummern.add(wahl.waehlerverzeichnisNummer);
    }
    return Array.from(nummern);
  });

  function getWaehlerverzeichnisNummerOrUndefinedById(wahlID: string) {
    const wahl = wahlenActions.getWahlOrUndefinedById(wahlID);
    return wahl ? wahl.waehlerverzeichnisNummer : undefined;
  }

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
    getWaehlerverzeichnisNummerOrUndefinedById,
    waehlerverzeichnisNummern,
  };
});

registerStoreHMR(useWahlenStore);
