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
import { createEmptyWahlvorstand } from "@/types/wahlvorstand/Wahlvorstand";
import {
  isSchriftfuehrer,
  isWahlvorsteher,
} from "@/types/wahlvorstand/WahlvorstandsmitgliedFunktion";

const { getWahlvorstand, saveWahlvorstand } = useWahlvorstandService();

export const storeID = "wahlvorstand";

export const useWahlvorstandStore = defineStore(storeID, () => {
  const { currentUserWahlbezirkID } = storeToRefs(useUserStore());
  const { schliessungsUhrzeitSent } = storeToRefs(useWahlbezirkStore());

  const isLoading = ref(false);
  const isSaving = ref(false);
  const lastLoading = ref<Date | null>(null);
  const lastSending = ref<Date | null>(null);
  const wahlvorstand = ref<Wahlvorstand>(createEmptyWahlvorstand());
  const wahlvorstandReady = ref(false);

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

  async function initWahlvorstand(sendNotification = true) {
    const wahlbezirkID = currentUserWahlbezirkID.value;
    if (wahlbezirkID) {
      wahlvorstand.value = await getWahlvorstand(wahlbezirkID, {
        forceUpdate: true,
        sendNotification: sendNotification,
      });
      wahlvorstandReady.value = true;
    } else {
      await Promise.reject();
    }
  }

  async function forceLoadWahlvorstand() {
    await _loadWahlvorstand(true, true);
  }

  async function loadWahlvorstand() {
    await _loadWahlvorstand(false, false);
  }

  async function sendWahlvorstand() {
    isSaving.value = true;
    try {
      const wahlbezirkID = currentUserWahlbezirkID.value;
      if (wahlbezirkID) {
        const { updateDatetime } = await saveWahlvorstand(
          wahlbezirkID,
          wahlvorstand.value
        );
        lastSending.value = updateDatetime;
      }
    } finally {
      isSaving.value = false;
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

  async function _loadWahlvorstand(
    forceUpdate: boolean,
    sendNotification: boolean
  ) {
    isLoading.value = true;
    try {
      const wahlbezirkID = currentUserWahlbezirkID.value;
      if (wahlbezirkID) {
        wahlvorstand.value = await getWahlvorstand(wahlbezirkID, {
          forceUpdate: forceUpdate,
          sendNotification: sendNotification,
        });
        lastLoading.value = new Date();
      }
    } finally {
      isLoading.value = false;
    }
  }

  return {
    isMindestanwesenheitErreicht,
    isSchriftfuehrerAnwesend,
    isWahlvorstandAusreichendAnwesend,
    isWahlvorsteherAnwesend,
    lastLoading,
    lastSending,
    isLoading,
    isSaving,
    wahlvorstand,
    wahlvorstandReady,
    initWahlvorstand,
    changeAnwesendOfMitglied,
    forceLoadWahlvorstand,
    loadWahlvorstand,
    sendWahlvorstand,
  };
});

if (import.meta.hot) {
  import.meta.hot.accept(
    acceptHMRUpdate(useWahlvorstandStore, import.meta.hot)
  );
}
