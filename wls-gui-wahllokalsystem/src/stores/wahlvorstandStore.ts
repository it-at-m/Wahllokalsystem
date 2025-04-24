import type { Wahlvorstand } from "@/types/wahlvorstand/Wahlvorstand";

import { defineStore, storeToRefs } from "pinia";
import { computed, ref } from "vue";

import { useWahlvorstandService } from "@/composables/wahlvorstand/wahlvorstandService";
import { useUserStore } from "@/stores/userStore.ts";
import { WahlvorstandBuilder } from "@/types/wahlvorstand/Wahlvorstand";
import {
  isSchriftfuehrer,
  isWahlvorsteher,
} from "@/types/wahlvorstand/WahlvorstandsmitgliedFunktion";

const { getWahlvorstand, saveWahlvorstand } = useWahlvorstandService();

export const storeID = "wahlvorstand";

export const useWahlvorstandStore = defineStore(storeID, () => {
  const { currentUserWahlbezirkID } = storeToRefs(useUserStore());
  const wahlvorstand = ref<Wahlvorstand>(
    WahlvorstandBuilder.createEmptyWahlvorstand()
  );
  const lastLoading = ref<Date | null>(null);
  const lastSending = ref<Date | null>(null);

  const isSchriftfuehrerAnwesend = computed<boolean>(() =>
    wahlvorstand.value.wahlvorstandsmitglieder.some(
      (mitglied) => isSchriftfuehrer(mitglied.funktion) && mitglied.anwesend
    )
  );
  const isWahlvorsteherAnwesend = computed<boolean>(() =>
    wahlvorstand.value.wahlvorstandsmitglieder.some(
      (mitglied) => isWahlvorsteher(mitglied.funktion) && mitglied.anwesend
    )
  );
  const isWahlvorstandAusreichendAnwesend = computed<boolean>(
    () => isWahlvorsteherAnwesend.value && isSchriftfuehrerAnwesend.value
  );

  async function loadWahlvorstand() {
    const wahlbezirkID = currentUserWahlbezirkID.value;
    if (wahlbezirkID) {
      wahlvorstand.value = await getWahlvorstand(wahlbezirkID);
      lastLoading.value = new Date();
    }
  }

  async function sendWahlvorstand() {
    const wahlbezirkID = currentUserWahlbezirkID.value;
    if (wahlbezirkID) {
      const { updateDatetime } = await saveWahlvorstand(
        wahlbezirkID,
        wahlvorstand.value
      );
      lastSending.value = updateDatetime;
    }
  }

  function changeAnwesendOfMitglied(newValue: boolean, id: string) {
    const wahlvorstandsMitgliedToUpdate =
      wahlvorstand.value.wahlvorstandsmitglieder?.find((mitglied) => {
        return mitglied.identifikator === id;
      });

    if (wahlvorstandsMitgliedToUpdate) {
      wahlvorstandsMitgliedToUpdate.anwesend = newValue;
    }
  }

  return {
    isSchriftfuehrerAnwesend,
    isWahlvorstandAusreichendAnwesend,
    isWahlvorsteherAnwesend,
    lastLoading,
    lastSending,
    wahlvorstand,
    changeAnwesendOfMitglied,
    loadWahlvorstand,
    sendWahlvorstand,
  };
});
