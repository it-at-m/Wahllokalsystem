import type { Ereignis } from "@/types/vorfaelleundvorkommnisse/Ereignis.ts";
import type { WahlbezirkEreignisse } from "@/types/vorfaelleundvorkommnisse/WahlbezirkEreignisse.ts";

import { acceptHMRUpdate, defineStore, storeToRefs } from "pinia";
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

  function deleteEreignisByIndex(index: number) {
    wahlbezirkEreignisse.value.ereigniseintraege?.splice(index, 1);
  }

  function updateUhrzeitByIndex(uhrzeit: string | undefined, index: number) {
    if (wahlbezirkEreignisse.value.ereigniseintraege) {
      const ereignisToChange =
        wahlbezirkEreignisse.value.ereigniseintraege[index];
      if (ereignisToChange == undefined) {
        return;
      }

      if (uhrzeit) {
        const [hours, minutes] = uhrzeit.split(":").map(Number);
        const currentUhrzeit = ereignisToChange.uhrzeit
          ? new Date(ereignisToChange.uhrzeit)
          : new Date();
        currentUhrzeit.setHours(hours, minutes);
        ereignisToChange.uhrzeit = currentUhrzeit;
      } else {
        ereignisToChange.uhrzeit = undefined;
      }
    }
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
    deleteEreignisByIndex,
    loadEreignisse,
    sendEreignisse,
    addEreignis,
    updateUhrzeitByIndex,
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

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useEreignisStore, import.meta.hot));
}
