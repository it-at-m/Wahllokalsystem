import type { Wahlvorstand } from "@/types/wahlvorstand/Wahlvorstand";

import { defineStore, storeToRefs } from "pinia";
import { computed, ref } from "vue";

import { useHmrUpdate } from "@/composables/common/hmrUpdate.ts";
import { useWahlvorstandService } from "@/composables/wahlvorstand/wahlvorstandService";
import {
  MIN_WAHLVORSTAND_ANWESEND_NACH_SCHLIESSUNG,
  MIN_WAHLVORSTAND_ANWESEND_VOR_SCHLIESSUNG,
} from "@/constants.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";
import { useWorkflowStore } from "@/stores/workflowStore.ts";
import { createEmptyWahlvorstand } from "@/types/wahlvorstand/Wahlvorstand";
import {
  isSchriftfuehrer,
  isWahlvorsteher,
} from "@/types/wahlvorstand/WahlvorstandsmitgliedFunktion";

const { getWahlvorstand, saveWahlvorstand } = useWahlvorstandService();

export const storeID = "wahlvorstand";
const { registerStoreHMR } = useHmrUpdate();

export const useWahlvorstandStore = defineStore(storeID, () => {
  const { currentUserWahlbezirkID } = storeToRefs(useUserStore());
  const { schliessungsuhrzeitState } = storeToRefs(useWahlbezirkStore());
  const { isWahlvorstandErfasst } = storeToRefs(useWorkflowStore());

  const isLoading = ref(false);
  const isSaving = ref(false);
  const lastLoading = ref<Date | null>(null);
  const lastSending = ref<Date | null>(null);
  const wahlvorstand = ref<Wahlvorstand>(createEmptyWahlvorstand());

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
    if (!schliessungsuhrzeitState.value.schliessungsuhrzeitSent) {
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
    wahlvorstand.value = await getWahlvorstand(currentUserWahlbezirkID.value, {
      forceUpdate: true,
      sendNotification: sendNotification,
    });
    isWahlvorstandErfasst.value = false;
  }

  async function forceLoadWahlvorstand() {
    await _loadWahlvorstand(true, true);
  }

  async function loadWahlvorstand() {
    await _loadWahlvorstand(false, false);
  }

  function resetAllAnwesenheiten() {
    wahlvorstand.value.wahlvorstandsmitglieder.forEach(
      (wahlvorstandsMitglied) => (wahlvorstandsMitglied.anwesend = false)
    );
    isWahlvorstandErfasst.value = false;
  }

  async function sendWahlvorstand() {
    isSaving.value = true;
    try {
      const { updateDatetime } = await saveWahlvorstand(
        currentUserWahlbezirkID.value,
        wahlvorstand.value
      );
      isWahlvorstandErfasst.value = true;
      lastSending.value = updateDatetime;
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
      wahlvorstand.value = await getWahlvorstand(
        currentUserWahlbezirkID.value,
        {
          forceUpdate: forceUpdate,
          sendNotification: sendNotification,
        }
      );
      isWahlvorstandErfasst.value = false;
      lastLoading.value = new Date();
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
    initWahlvorstand,
    changeAnwesendOfMitglied,
    forceLoadWahlvorstand,
    loadWahlvorstand,
    resetAllAnwesenheiten,
    sendWahlvorstand,
  };
});

registerStoreHMR(useWahlvorstandStore);
