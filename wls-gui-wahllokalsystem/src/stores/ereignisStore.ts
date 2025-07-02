import type { Ereignis } from "@/types/vorfaelleundvorkommnisse/Ereignis.ts";
import type { WahlbezirkEreignisse } from "@/types/vorfaelleundvorkommnisse/WahlbezirkEreignisse.ts";

import { defineStore, storeToRefs } from "pinia";
import { computed, ref, watch, watchEffect } from "vue";

import { useHmrUpdate } from "@/composables/common/hmrUpdate.ts";
import { useEreignisService } from "@/composables/vorfaelleundvorkommnisse/ereignisService.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";
import {
  EreignisartEnum,
  getEreignisArtForDateRelatedToSchliessungsuhrzeit,
} from "@/types/vorfaelleundvorkommnisse/Ereignisart.ts";
import { WahlbezirkEreignisseBuilder } from "@/types/vorfaelleundvorkommnisse/WahlbezirkEreignisse.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const { getEreignisse, saveEreignisse } = useEreignisService();
const { registerStoreHMR } = useHmrUpdate();

export const storeID = "vorfaelleundvorkommnisse";

export const useEreignisStore = defineStore(storeID, () => {
  const error = ref<string | null>(null);

  const { currentUserWahlbezirkID, currentUserWahlbezirksArt } =
    storeToRefs(useUserStore());
  const { schliessungsuhrzeitSent } = storeToRefs(useWahlbezirkStore());

  const isSaving = ref(false);

  const wahlbezirkEreignisse = ref<WahlbezirkEreignisse>(
    WahlbezirkEreignisseBuilder.createEmptyWahlbezirkEreignisse()
  );

  const hasEintraege = computed(
    () => (wahlbezirkEreignisse.value.ereigniseintraege ?? []).length > 0
  );
  const hasVorfaelle = computed(
    () =>
      wahlbezirkEreignisse.value.ereigniseintraege?.some(
        (eintrag) => eintrag.ereignisart === EreignisartEnum.Vorfall
      ) === true
  );
  const hasVorkommnisse = computed(
    () =>
      wahlbezirkEreignisse.value.ereigniseintraege?.some(
        (eintrag) => eintrag.ereignisart === EreignisartEnum.Vorkommnis
      ) === true
  );

  const hasMissingEreignisFlagsForUWB = computed(
    () =>
      hasVorfaelle.value !== wahlbezirkEreignisse.value.keineVorfaelle &&
      (hasVorkommnisse.value !== wahlbezirkEreignisse.value.keineVorkommnisse ||
        !schliessungsuhrzeitSent.value)
  );

  const hasMissingEreignisFlagsForBWB = computed(
    () =>
      hasVorfaelle.value !== wahlbezirkEreignisse.value.keineVorfaelle &&
      hasVorkommnisse.value !== wahlbezirkEreignisse.value.keineVorkommnisse
  );

  const hasMissingEreignisFlags = computed(() => {
    const isUWB = currentUserWahlbezirksArt.value === WahlbezirksArtEnum.UWB;
    return isUWB
      ? hasMissingEreignisFlagsForUWB.value
      : hasMissingEreignisFlagsForBWB.value;
  });

  watch(schliessungsuhrzeitSent, _onSchliessunguhrzeitSentChanged);
  watchEffect(() => _updateKeineFlagsOfEreignisseBasedOnCurrentState());

  function addEreignis() {
    const currentDate = new Date();
    wahlbezirkEreignisse.value.ereigniseintraege?.push({
      uhrzeit: currentDate,
      beschreibung: "",
      ereignisart: getEreignisArtForDateRelatedToSchliessungsuhrzeit(
        currentDate,
        schliessungsuhrzeitSent.value
      ),
    });
  }

  function deleteEreignisByIndex(index: number) {
    wahlbezirkEreignisse.value.ereigniseintraege?.splice(index, 1);
  }

  function updateUhrzeitByIndex(uhrzeit: Date | undefined, index: number) {
    if (wahlbezirkEreignisse.value.ereigniseintraege) {
      const ereignisToChange =
        wahlbezirkEreignisse.value.ereigniseintraege[index];
      if (ereignisToChange == undefined) {
        return;
      }

      if (uhrzeit) {
        ereignisToChange.uhrzeit = uhrzeit;

        ereignisToChange.ereignisart =
          getEreignisArtForDateRelatedToSchliessungsuhrzeit(
            uhrzeit,
            schliessungsuhrzeitSent.value
          );
      } else {
        ereignisToChange.uhrzeit = undefined;
        ereignisToChange.ereignisart = EreignisartEnum.Vorfall;
      }
    }
  }

  async function loadEreignisse() {
    error.value = null;
    try {
      wahlbezirkEreignisse.value = await getEreignisse(
        currentUserWahlbezirkID.value
      );
      sortEreignisse(wahlbezirkEreignisse.value.ereigniseintraege);
    } catch (e) {
      error.value = "Fehler beim Laden der Ereignisse";
      console.debug(e);
    }
  }

  async function sendEreignisse() {
    error.value = null;
    isSaving.value = true;
    try {
      sortEreignisse(wahlbezirkEreignisse.value.ereigniseintraege);
      await saveEreignisse(
        currentUserWahlbezirkID.value,
        wahlbezirkEreignisse.value
      );
    } catch (e) {
      error.value = "Fehler beim Speichern der Ereignisse";
      console.debug(e);
    } finally {
      isSaving.value = false;
    }
  }

  function _hasEintragOfEreignisart(ereginisart: EreignisartEnum): boolean {
    return (
      wahlbezirkEreignisse.value.ereigniseintraege?.some(
        (eintrag) => eintrag.ereignisart === ereginisart
      ) === true
    );
  }

  function _onSchliessunguhrzeitSentChanged(
    newSchliessungsuhrzeit: Date | undefined
  ) {
    wahlbezirkEreignisse.value.ereigniseintraege?.forEach((eintrag) => {
      if (eintrag.uhrzeit) {
        eintrag.ereignisart = getEreignisArtForDateRelatedToSchliessungsuhrzeit(
          eintrag.uhrzeit,
          newSchliessungsuhrzeit
        );
      }
    });
    _updateKeineFlagsOfEreignisseBasedOnCurrentState();

    saveEreignisse(
      currentUserWahlbezirkID.value,
      wahlbezirkEreignisse.value,
      false
    );
  }

  function _updateKeineFlagsOfEreignisseBasedOnCurrentState() {
    wahlbezirkEreignisse.value.keineVorkommnisse = !_hasEintragOfEreignisart(
      EreignisartEnum.Vorkommnis
    );
    wahlbezirkEreignisse.value.keineVorfaelle = !_hasEintragOfEreignisart(
      EreignisartEnum.Vorfall
    );
  }

  return {
    hasMissingEreignisFlags,
    wahlbezirkEreignisse,
    hasEintraege,
    hasVorfaelle,
    hasVorkommnisse,
    isSaving,
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

registerStoreHMR(useEreignisStore);
