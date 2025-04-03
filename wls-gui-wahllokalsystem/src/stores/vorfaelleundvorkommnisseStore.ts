import type { WahlbezirkEreignisse } from "@/types/vorfaelleundvorkommnisse/WahlbezirkEreignisse.ts";

import { defineStore } from "pinia";
import { ref } from "vue";

import { useEreignisService } from "@/composables/vorfaelleundvorkommnisse/ereignisService.ts";
import { useUserStore } from "@/stores/user";
import { WahlbezirkEreignisseBuilder } from "@/types/vorfaelleundvorkommnisse/WahlbezirkEreignisse.ts";

const { getEreignisse, saveEreignisse } = useEreignisService();

export const storeID = "vorfaelleundvorkommnisse";

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
    const currentUserWahlbezirkID = getUsersWahlbezirkID();
    if (currentUserWahlbezirkID) {
      wahlbezirkEreignisse.value = await getEreignisse(currentUserWahlbezirkID);
      sortEreignisse();
    }
  }

  async function sendEreignisse() {
    const currentUserWahlbezirkID = getUsersWahlbezirkID();
    if (currentUserWahlbezirkID) {
      sortEreignisse();
      await saveEreignisse(currentUserWahlbezirkID, wahlbezirkEreignisse.value);
    }
  }

  function getUsersWahlbezirkID(): string | undefined {
    return userStore.getUser?.wahlbezirkID;
  }

  // Funktion zum Sortieren der Ereignisse
  function sortEreignisse() {
    return wahlbezirkEreignisse.value.ereigniseintraege?.sort((a, b) => {
      const timeA = a.uhrzeit ? new Date(a.uhrzeit).getTime() : Infinity;
      const timeB = b.uhrzeit ? new Date(b.uhrzeit).getTime() : Infinity;

      return timeA - timeB;
    });
  }

  return {
    wahlbezirkEreignisse,
    loadEreignisse,
    sendEreignisse,
    addEreignis,
  };
});
