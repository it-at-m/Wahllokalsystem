import type { Ereignis } from "@/types/vorfaelleundvorkommnisse/Ereignis.ts";
import type { WahlbezirkEreignisse } from "@/types/vorfaelleundvorkommnisse/WahlbezirkEreignisse.ts";

import { defineStore, storeToRefs } from "pinia";
import { ref } from "vue";

import { useEreignisService } from "@/composables/vorfaelleundvorkommnisse/ereignisService.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { WahlbezirkEreignisseBuilder } from "@/types/vorfaelleundvorkommnisse/WahlbezirkEreignisse.ts";

const ereignisService = useEreignisService();

export const storeID = "vorfaelleundvorkommnisse";

export const useEreignisStore = defineStore(storeID, () => {
  const error = ref<string | null>(null);

  const { currentUserWahlbezirkID } = storeToRefs(useUserStore());
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
    const wahlbezirkID = currentUserWahlbezirkID.value;
    if (wahlbezirkID) {
      error.value = null;
      try {
        wahlbezirkEreignisse.value =
          await ereignisService.getEreignisse(wahlbezirkID);
        sortEreignisse(wahlbezirkEreignisse.value.ereigniseintraege);
      } catch (e) {
        error.value = "Fehler beim Laden der Ereignisse";
        console.debug(e);
      }
    }
  }

  async function sendEreignisse() {
    const wahlbezirkID = currentUserWahlbezirkID.value;
    if (wahlbezirkID) {
      error.value = null;
      try {
        sortEreignisse(wahlbezirkEreignisse.value.ereigniseintraege);
        await ereignisService.saveEreignisse(
          wahlbezirkID,
          wahlbezirkEreignisse.value
        );
      } catch (e) {
        error.value = "Fehler beim Speichern der Ereignisse";
        console.debug(e);
      }
    }
  }

  return {
    wahlbezirkEreignisse,
    loadEreignisse,
    sendEreignisse,
    addEreignis,
    error,
  };
});

// Funktion zum Sortieren der Ereignisse
function sortEreignisse(ereigniseintraege: Ereignis[] | undefined) {
  return ereigniseintraege?.sort((a, b) => {
    const timeA = a.uhrzeit ? new Date(a.uhrzeit).getTime() : Infinity;
    const timeB = b.uhrzeit ? new Date(b.uhrzeit).getTime() : Infinity;

    return timeA - timeB;
  });
}
