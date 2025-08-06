import type { Wahl } from "@/types/wahl/Wahl.ts";

import { defineStore, storeToRefs } from "pinia";
import { computed, ref } from "vue";

import { useBriefwahlService } from "@/composables/briefwahl/briefwahlService.ts";
import { useHmrUpdate } from "@/composables/common/hmrUpdate.ts";
import { useWahlService } from "@/composables/wahl/wahlService.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { ZurueckweisungsgrundEnum } from "@/types/briefwahl/ZurueckweisungsgrundEnum.ts";

export const storeID = "wahlen";
const wahlenService = useWahlService();
const briefwahlService = useBriefwahlService();
const { registerStoreHMR } = useHmrUpdate();

export const useWahlenStore = defineStore(storeID, () => {
  const { currentUserWahltagID, currentUserWahlbezirkID } =
    storeToRefs(useUserStore());
  const wahlen = ref<Wahl[] | null>();
  const isBeanstandeteWahlbriefeTableValid = ref<null | boolean>(true);
  const isBeanstandeteWahlbriefeSaving = ref<boolean>(false);

  const waehlerverzeichnisNummern = computed<number[]>(() => {
    if (!wahlen.value) return [];

    const nummern = new Set<number>();

    for (const wahl of wahlen.value) {
      nummern.add(wahl.waehlerverzeichnisNummer);
    }
    return Array.from(nummern);
  });

  const summeGueltigerWahlbriefe = computed(() => {
    if (!wahlen.value) return [];
    return wahlen.value.map(
      (wahl) =>
        wahl.beanstandeteWahlbriefe.filter(
          (brief) => brief === ZurueckweisungsgrundEnum.Zugelassen
        ).length
    );
  });

  const summeUngueltigerWahlbriefe = computed(() => {
    if (!wahlen.value) return [];
    return wahlen.value.map(
      (wahl) =>
        wahl.beanstandeteWahlbriefe.filter(
          (brief) =>
            brief !== ZurueckweisungsgrundEnum.Zugelassen && brief !== null
        ).length
    );
  });

  const summenZurueckweisungsgruende = computed(() => {
    if (!wahlen.value) return [];
    const anzahlWahlen = wahlen.value.length;
    const summenZurueckweisungsgruende = Object.values(ZurueckweisungsgrundEnum)
      .filter((grund) => grund !== ZurueckweisungsgrundEnum.Zugelassen)
      .map((grund) => ({
        summen: new Array(anzahlWahlen).fill(0),
        grund: grund,
      }));

    wahlen.value.forEach((wahl, wahlIndex) => {
      if (
        wahl.beanstandeteWahlbriefe &&
        wahl.beanstandeteWahlbriefe.every((grund) => grund !== null)
      ) {
        wahl.beanstandeteWahlbriefe.forEach((beanstandeterWahlbrief) => {
          if (beanstandeterWahlbrief !== ZurueckweisungsgrundEnum.Zugelassen) {
            const index = summenZurueckweisungsgruende.findIndex(
              (item) => item.grund === beanstandeterWahlbrief
            );
            summenZurueckweisungsgruende[index].summen[wahlIndex] += 1;
          }
        });
      }
    });
    return summenZurueckweisungsgruende;
  });

  async function initWahlen(sendNotification = true) {
    wahlen.value = await wahlenService.getWahlen(
      currentUserWahltagID.value,
      sendNotification
    );

    if (wahlen.value) {
      wahlen.value.sort((a: Wahl, b: Wahl) => {
        if (a.nummer && b.nummer) {
          return a.nummer.localeCompare(b.nummer);
        } else {
          return 0;
        }
      });
    }
  }

  function getWahlOrUndefinedById(wahlID: string) {
    return wahlen.value?.find((wahl) => wahl.wahlID === wahlID);
  }

  function getWaehlerverzeichnisNummerOrUndefinedById(wahlID: string) {
    const wahl = getWahlOrUndefinedById(wahlID);
    return wahl ? wahl.waehlerverzeichnisNummer : undefined;
  }

  async function initBeanstandeteWahlbriefe() {
    for (const wvzNr of waehlerverzeichnisNummern.value) {
      const beanstandeteWahlbriefe =
        await briefwahlService.getBeanstandeteWahlbriefe(
          wvzNr,
          currentUserWahlbezirkID.value
        );
      if (wahlen.value && beanstandeteWahlbriefe) {
        wahlen.value.forEach((wahl) => {
          if (wahl.waehlerverzeichnisNummer == wvzNr) {
            wahl.beanstandeteWahlbriefe =
              beanstandeteWahlbriefe.beanstandeteWahlbriefe.get(wahl.wahlID) ??
              [];
          }
        });
      }
    }
  }

  function addBeanstandeterWahlbriefEntry() {
    if (wahlen.value) {
      wahlen.value.map((wahl) => wahl.beanstandeteWahlbriefe.push(null));
    }
  }

  function deleteBeanstandeterWahlbriefEntry(index: number) {
    if (wahlen.value) {
      wahlen.value.forEach((wahl) =>
        wahl.beanstandeteWahlbriefe.splice(index, 1)
      );
    }
  }

  async function saveBeanstandeteWahlbriefe() {
    isBeanstandeteWahlbriefeSaving.value = true;

    try {
      const wahlenGroupedByWvzNr = new Map<number, Wahl[]>();
      if (wahlen.value) {
        for (const wahl of wahlen.value) {
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
      isBeanstandeteWahlbriefeSaving.value = false;
    }
  }

  function getWahlNameOrBlankStringById(wahlID: string) {
    const wahl = getWahlOrUndefinedById(wahlID);
    return wahl ? wahl.name : "";
  }

  function getWahlTagOrBlankStringById(wahlID: string) {
    const wahl = getWahlOrUndefinedById(wahlID);
    return wahl ? wahl.wahltag : "";
  }

  return {
    wahlen,
    getWaehlerverzeichnisNummerOrUndefinedById,
    waehlerverzeichnisNummern,
    getWahlNameOrBlankStringById,
    getWahlTagOrBlankStringById,
    getWahlOrUndefinedById,
    initWahlen,
    initBeanstandeteWahlbriefe,
    addBeanstandeterWahlbriefEntry,
    deleteBeanstandeterWahlbriefEntry,
    saveBeanstandeteWahlbriefe,
    isBeanstandeteWahlbriefeSaving,
    isBeanstandeteWahlbriefeTableValid,
    summeGueltigerWahlbriefe,
    summeUngueltigerWahlbriefe,
    summenZurueckweisungsgruende,
  };
});

registerStoreHMR(useWahlenStore);
