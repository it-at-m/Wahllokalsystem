import type { Ereignis } from "@/types/vorfaelleundvorkommnisse/Ereignis.ts";
import type { WahlbezirkEreignisse } from "@/types/vorfaelleundvorkommnisse/WahlbezirkEreignisse.ts";

import { defineStore } from "pinia";
import { ref } from "vue";

import { useEreignisService } from "@/composables/vorfaelleundvorkommnisse/ereignisService.ts";
import { useUserStore } from "@/stores/user";
import { WahlbezirkEreignisseBuilder } from "@/types/vorfaelleundvorkommnisse/WahlbezirkEreignisse.ts";

const { getEreignisse, saveEreignisse } = useEreignisService();

export const storeID = "vorfaelleundvorkommnisse";

// Funktion zum Sortieren der Ereignisse
export function sortEreignisse(ereigniseintraege: Ereignis[] | undefined) {
  return ereigniseintraege?.sort((a, b) => {
    const timeA = a.uhrzeit ? new Date(a.uhrzeit).getTime() : Infinity;
    const timeB = b.uhrzeit ? new Date(b.uhrzeit).getTime() : Infinity;

    return timeA - timeB;
  });
}

// Funktion zum Abrufen der Wahlbezirk-ID des Benutzers
export function getUsersWahlbezirkID(userStore: any): string | undefined {
  return userStore.getUser?.wahlbezirkID;
}

export const useEreignisStore = defineStore(storeID, () => {
  const userStore = useUserStore();

  const wahlbezirkEreignisse = ref<WahlbezirkEreignisse>(
    WahlbezirkEreignisseBuilder.createEmptyWahlbezirkEreignisse()
  );

  function addEreignis() {
    wahlbezirkEreignisse.value.ereigniseintraege?.push({
      uhrzeit: new Date(),
      beschreibung: "",
      ereignisart: "VORFALL",
    });
  }

  async function loadEreignisse() {
    const currentUserWahlbezirkID = getUsersWahlbezirkID(userStore);
    if (currentUserWahlbezirkID) {
      wahlbezirkEreignisse.value = await getEreignisse(currentUserWahlbezirkID);
      sortEreignisse(wahlbezirkEreignisse.value.ereigniseintraege);
    }
  }

  async function sendEreignisse() {
    const currentUserWahlbezirkID = getUsersWahlbezirkID(userStore);
    if (currentUserWahlbezirkID) {
      sortEreignisse(wahlbezirkEreignisse.value.ereigniseintraege);
      await saveEreignisse(currentUserWahlbezirkID, wahlbezirkEreignisse.value);
    }
  }

  return {
    wahlbezirkEreignisse,
    loadEreignisse,
    sendEreignisse,
    addEreignis,
  };
});
