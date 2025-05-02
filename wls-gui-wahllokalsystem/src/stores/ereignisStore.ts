import type { Ereignis } from "@/types/vorfaelleundvorkommnisse/Ereignis.ts";
import type { EreignisartEnum } from "@/types/vorfaelleundvorkommnisse/Ereignisart.ts";
import type { WahlbezirkEreignisse } from "@/types/vorfaelleundvorkommnisse/WahlbezirkEreignisse.ts";

import { acceptHMRUpdate, defineStore, storeToRefs } from "pinia";
import { computed, ref } from "vue";

import { useEreignisService } from "@/composables/vorfaelleundvorkommnisse/ereignisService.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";
import { WahlbezirkEreignisseBuilder } from "@/types/vorfaelleundvorkommnisse/WahlbezirkEreignisse.ts";

const ereignisService = useEreignisService();

export const storeID = "vorfaelleundvorkommnisse";

export const useEreignisStore = defineStore(storeID, () => {
  const error = ref<string | null>(null);

  const { currentUserWahlbezirkID } = storeToRefs(useUserStore());
  const { schliessungsUhrzeitSent } = storeToRefs(useWahlbezirkStore());

  const wahlbezirkEreignisse = ref<WahlbezirkEreignisse>(
    WahlbezirkEreignisseBuilder.createEmptyWahlbezirkEreignisse()
  );

  const areKeineEreignisseFlagsValid = computed(
    () =>
      hasVorfaelle.value !== wahlbezirkEreignisse.value.keineVorfaelle &&
      hasVorkommnisse.value !== wahlbezirkEreignisse.value.keineVorkommnisse
  );
  const hasVorfaelle = computed(
    () =>
      wahlbezirkEreignisse.value.ereigniseintraege?.some(
        (eintrag) => eintrag.ereignisart === "VORFALL"
      ) === true
  );
  const hasVorkommnisse = computed(
    () =>
      wahlbezirkEreignisse.value.ereigniseintraege?.some(
        (eintrag) => eintrag.ereignisart === "VORKOMMNIS"
      ) === true
  );

  function addEreignis() {
    const newEreignisEreignisart = _getArtOfNewEreignisse();
    wahlbezirkEreignisse.value.ereigniseintraege?.push({
      uhrzeit: new Date(),
      beschreibung: "",
      ereignisart: newEreignisEreignisart,
    });

    if (newEreignisEreignisart === "VORFALL") {
      wahlbezirkEreignisse.value.keineVorfaelle = false;
    } else {
      wahlbezirkEreignisse.value.keineVorkommnisse = false;
    }
  }

  function deleteEreignisByIndex(index: number) {
    wahlbezirkEreignisse.value.ereigniseintraege?.splice(index, 1);
    _updateKeineFlagsOfEreignisseBaseOnRemovedEreignisart();
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

  function _getArtOfNewEreignisse(): EreignisartEnum {
    return schliessungsUhrzeitSent.value === undefined
      ? "VORFALL"
      : "VORKOMMNIS";
  }

  function _hasEintragOfEreignisart(ereginisart: EreignisartEnum): boolean {
    return (
      wahlbezirkEreignisse.value.ereigniseintraege?.some(
        (eintrag) => eintrag.ereignisart === ereginisart
      ) === true
    );
  }

  function _updateKeineFlagsOfEreignisseBaseOnRemovedEreignisart() {
    wahlbezirkEreignisse.value.keineVorkommnisse =
      !_hasEintragOfEreignisart("VORKOMMNIS");
    wahlbezirkEreignisse.value.keineVorfaelle =
      !_hasEintragOfEreignisart("VORFALL");
  }

  return {
    areKeineEreignisseFlagsValid,
    wahlbezirkEreignisse,
    hasVorfaelle,
    hasVorkommnisse,
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
