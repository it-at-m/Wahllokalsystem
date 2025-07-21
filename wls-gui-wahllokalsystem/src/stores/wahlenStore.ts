import type { BeanstandeteWahlbriefeCreateDTO } from "@/api/wls-clients/generated-briefwahl-api";
import type { Wahl } from "@/types/wahl/Wahl.ts";

import { defineStore, storeToRefs } from "pinia";
import { computed, ref } from "vue";

import { useBriefwahlService } from "@/composables/briefwahl/briefwahlService.ts";
import { useHmrUpdate } from "@/composables/common/hmrUpdate.ts";
import { useWahlService } from "@/composables/wahl/wahlService.ts";
import { useUserStore } from "@/stores/userStore.ts";

export const storeID = "wahlen";
const wahlenService = useWahlService();
const briefwahlService = useBriefwahlService();
const { registerStoreHMR } = useHmrUpdate();

export const useWahlenStore = defineStore(storeID, () => {
  const { currentUserWahltagID, currentUserWahlbezirkID } =
    storeToRefs(useUserStore());
  const wahlen = ref<Wahl[] | null>();
  const isBeanstandeteWahlbriefeSaving = ref<boolean>(false);

  const waehlerverzeichnisNummern = computed<number[]>(() => {
    if (!wahlen.value) return [];

    const nummern = new Set<number>();

    for (const wahl of wahlen.value) {
      nummern.add(wahl.waehlerverzeichnisNummer);
    }
    return Array.from(nummern);
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

  async function initBeanstandeteWahlbriefe(waehlerverzeichnisNummer: number) {
    const beanstandeteWahlbriefe =
      await briefwahlService.getBeanstandeteWahlbriefe(
        waehlerverzeichnisNummer,
        currentUserWahlbezirkID.value
      );
    if (wahlen.value && beanstandeteWahlbriefe) {
      for (const wahl of wahlen.value) {
        wahl.beanstandeteWahlbriefe =
          beanstandeteWahlbriefe.beanstandeteWahlbriefe.get(wahl.wahlID) ?? [];
      }
    }
  }

  async function saveBeanstandeteWahlbriefe() {
    isBeanstandeteWahlbriefeSaving.value = true;
    const beanstandeteWahlbriefeDTO: BeanstandeteWahlbriefeCreateDTO = {
      beanstandeteWahlbriefe: {},
    };

    for (const wvzNr of waehlerverzeichnisNummern.value) {
      if (wahlen.value) {
        const wahlenWithWvzNr = wahlen.value.filter(
          (wahl) => wahl.waehlerverzeichnisNummer === wvzNr
        );

        for (const wahl of wahlenWithWvzNr) {
          if (
            wahl.beanstandeteWahlbriefe &&
            wahl.beanstandeteWahlbriefe.every((grund) => grund !== null)
          ) {
            beanstandeteWahlbriefeDTO.beanstandeteWahlbriefe[wahl.wahlID] =
              wahl.beanstandeteWahlbriefe.map(
                (grund) => grund?.toString() ?? ""
              );
          }
        }
        await briefwahlService.postBeanstandeteWahlbriefe(
          beanstandeteWahlbriefeDTO,
          currentUserWahlbezirkID.value,
          wvzNr
        );
      }
    }
    isBeanstandeteWahlbriefeSaving.value = false;
  }

  function getWahlNameOrBlankStringById(wahlID: string) {
    const wahl = getWahlOrUndefinedById(wahlID);
    return wahl ? wahl.name : "";
  }

  function getWahlTagOrBlankStringById(wahlID: string) {
    const wahl = getWahlOrUndefinedById(wahlID);
    return wahl ? wahl.wahltag : "";
  }

  function getWaehlerverzeichnisOrUndefinedById(wahlID: string) {
    const wahl = getWahlOrUndefinedById(wahlID);
    return wahl ? wahl.waehlerverzeichnisNummer : undefined;
  }

  function getWahlOrUndefinedById(wahlID: string) {
    return wahlen.value
      ? wahlen.value.find((wahl) => wahl.wahlID === wahlID)
      : undefined;
  }

  return {
    wahlen,
    waehlerverzeichnisNummern,
    getWahlOrUndefinedById,
    getWahlNameOrBlankStringById,
    getWahlTagOrBlankStringById,
    getWaehlerverzeichnisOrUndefinedById,
    initWahlen,
    initBeanstandeteWahlbriefe,
    saveBeanstandeteWahlbriefe,
    isBeanstandeteWahlbriefeSaving,
  };
});

registerStoreHMR(useWahlenStore);
