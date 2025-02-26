import type { Wahlvorstand } from "@/types/wahlvorstand/wahlvorstand";

import { defineStore } from "pinia";
import { computed, ref } from "vue";

import { useWahlvorstandService } from "@/composables/wahlvorstand/wahlvorstandService";
import { useUserStore } from "@/stores/user";
import { WahlvorstandBuilder } from "@/types/wahlvorstand/wahlvorstand";
import {
  isSchriftfuehrer,
  isWahlvorsteher,
} from "@/types/wahlvorstand/wahlvorstandsmitgliedFunktion";

const { getWahlvorstand, saveWahlvorstand } = useWahlvorstandService();

export const storeID = "wahlvorstand";

export const useWahlvorstandStore = defineStore(storeID, () => {
  const userStore = useUserStore();

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
    const userStore = useUserStore();
    const currentUserWahlbezirkID = userStore.getUser?.wahlbezirkID;
    if (currentUserWahlbezirkID) {
      wahlvorstand.value = await getWahlvorstand(currentUserWahlbezirkID);
      lastLoading.value = new Date();
    }
  }

  async function sendWahlvorstand() {
    const currentUserWahlbezirkID = userStore.getUser?.wahlbezirkID;
    if (currentUserWahlbezirkID) {
      const { updateDatetime } = await saveWahlvorstand(
        currentUserWahlbezirkID,
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
