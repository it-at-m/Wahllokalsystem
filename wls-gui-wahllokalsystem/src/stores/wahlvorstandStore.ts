import type { Wahlvorstand } from "@/types/wahlvorstand/Wahlvorstand";

import { acceptHMRUpdate, defineStore, storeToRefs } from "pinia";
import { computed, ref } from "vue";

import { useWahlvorstandService } from "@/composables/wahlvorstand/wahlvorstandService";
import {
  MIN_WAHLVORSTAND_ANWESEND_NACH_SCHLIESSUNG,
  MIN_WAHLVORSTAND_ANWESEND_VOR_SCHLIESSUNG,
} from "@/constants.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";
import { WahlvorstandBuilder } from "@/types/wahlvorstand/Wahlvorstand";
import {
  isSchriftfuehrer,
  isWahlvorsteher,
} from "@/types/wahlvorstand/WahlvorstandsmitgliedFunktion";

const { getWahlvorstand, saveWahlvorstand } = useWahlvorstandService();

export const storeID = "wahlvorstand";

export const useWahlvorstandStore = defineStore(storeID, () => {
  const error = ref<string | null>(null);

  const { currentUserWahlbezirkID } = storeToRefs(useUserStore());
  const { schliessungsUhrzeitSent } = storeToRefs(useWahlbezirkStore());

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
  const isMindestanwesenheitErreicht = computed<boolean>(() => {
    const anwesend = wahlvorstand.value.wahlvorstandsmitglieder.filter(
      (mitglied) => mitglied.anwesend
    ).length;
    if (!schliessungsUhrzeitSent.value) {
      return anwesend >= MIN_WAHLVORSTAND_ANWESEND_VOR_SCHLIESSUNG;
    } else {
      return anwesend >= MIN_WAHLVORSTAND_ANWESEND_NACH_SCHLIESSUNG;
    }
  });
  const isWahlvorstandAusreichendAnwesend = computed<boolean>(
    () =>
      isWahlvorsteherAnwesend.value &&
      isSchriftfuehrerAnwesend.value &&
      isMindestanwesenheitErreicht.value
  );

  async function loadWahlvorstand() {
    const wahlbezirkID = currentUserWahlbezirkID.value;
    if (wahlbezirkID) {
      error.value = null;
      try {
        wahlvorstand.value = await getWahlvorstand(wahlbezirkID);
        lastLoading.value = new Date();
      } catch {
        error.value = "Fehler beim Laden des Wahlvorstandes";
      }
    }
  }

  async function sendWahlvorstand() {
    const wahlbezirkID = currentUserWahlbezirkID.value;
    if (wahlbezirkID) {
      error.value = null;
      try {
        const { updateDatetime } = await saveWahlvorstand(
          wahlbezirkID,
          wahlvorstand.value
        );
        lastSending.value = updateDatetime;
      } catch {
        error.value = "Fehler beim Speichern des Wahlvorstandes";
      }
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
    isMindestanwesenheitErreicht,
    isSchriftfuehrerAnwesend,
    isWahlvorstandAusreichendAnwesend,
    isWahlvorsteherAnwesend,
    lastLoading,
    lastSending,
    wahlvorstand,
    changeAnwesendOfMitglied,
    loadWahlvorstand,
    sendWahlvorstand,
    error,
  };
});

if (import.meta.hot) {
  import.meta.hot.accept(
    acceptHMRUpdate(useWahlvorstandStore, import.meta.hot)
  );
}
