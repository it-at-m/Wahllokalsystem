import type { BeanstandeteWahlbriefe } from "@/types/briefwahl/BeanstandeteWahlbriefe.ts";

import { defineStore, storeToRefs } from "pinia";
import { ref } from "vue";

import { useBriefwahlService } from "@/composables/briefwahl/briefwahlService.ts";
import { useHmrUpdate } from "@/composables/common/hmrUpdate.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

export const storeID = "briefwahl";
const { registerStoreHMR } = useHmrUpdate();

export const useBriefwahlStore = defineStore(storeID, () => {
  const { getBeanstandeteWahlbriefe } = useBriefwahlService();
  const { wahlen } = storeToRefs(useWahlenStore());

  const beanstandeteWahlbriefe = ref<BeanstandeteWahlbriefe | null>();

  async function initBeanstandeteWahlbriefe(waehlerverzeichnisNummer: number) {
    beanstandeteWahlbriefe.value = await getBeanstandeteWahlbriefe(
      waehlerverzeichnisNummer
    );
    await _assignBeanstandeteWahlbriefeToCorrespondingWahl();
  }

  async function _assignBeanstandeteWahlbriefeToCorrespondingWahl() {
    if (wahlen.value) {
      for (const wahl of wahlen.value) {
        wahl.beanstandeteWahlbriefe =
          beanstandeteWahlbriefe.value?.beanstandeteWahlbriefe.get(
            wahl.wahlID
          ) ?? undefined;
      }
    }
  }

  return {
    beanstandeteWahlbriefe,
    initBeanstandeteWahlbriefe,
  };
});

registerStoreHMR(useUserStore);
