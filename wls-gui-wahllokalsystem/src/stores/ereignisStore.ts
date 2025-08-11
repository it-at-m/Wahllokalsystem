import type { Ereignis } from "@/types/vorfaelleundvorkommnisse/Ereignis.ts";
import type { WahlbezirkEreignisse } from "@/types/vorfaelleundvorkommnisse/WahlbezirkEreignisse.ts";

import { defineStore, storeToRefs } from "pinia";
import { computed, ref, watch } from "vue";

import { useHmrUpdate } from "@/composables/common/hmrUpdate.ts";
import { useLogging } from "@/composables/common/logging.ts";
import { useEreignisService } from "@/composables/vorfaelleundvorkommnisse/ereignisService.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";
import {
  EreignisartEnum,
  getEreignisArtForDateRelatedToSchliessungsuhrzeit,
} from "@/types/vorfaelleundvorkommnisse/Ereignisart.ts";
import { WahlbezirkEreignisseBuilder } from "@/types/vorfaelleundvorkommnisse/WahlbezirkEreignisse.ts";

const { getEreignisse, saveEreignisse } = useEreignisService();
const { registerStoreHMR } = useHmrUpdate();

export const storeID = "vorfaelleundvorkommnisse";
const { logDebug } = useLogging(`store-${storeID}`);

interface EreignisCreateTemplate {
  beschreibung?: string;
  uhrzeit?: Date;
}

export const useEreignisStore = defineStore(storeID, () => {
  const error = ref<string | null>(null);

  const { currentUserWahlbezirkID, isUWB, isBWB } = storeToRefs(useUserStore());
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
      hasVorfaelle.value !==
        (isBWB.value || wahlbezirkEreignisse.value.keineVorfaelle) &&
      hasVorkommnisse.value !== wahlbezirkEreignisse.value.keineVorkommnisse
  );

  const hasMissingEreignisFlags = computed(() => {
    return isUWB.value
      ? hasMissingEreignisFlagsForUWB.value
      : hasMissingEreignisFlagsForBWB.value;
  });

  watch(schliessungsuhrzeitSent, _onSchliessunguhrzeitSentChanged);

  function addEreignis(ereignisToAddTemplate?: EreignisCreateTemplate) {
    const ereignisToAdd = _createEreignis(ereignisToAddTemplate);

    wahlbezirkEreignisse.value.ereigniseintraege?.push(ereignisToAdd);
    // uncheck checkbox if set before
    switch (ereignisToAdd.ereignisart) {
      case EreignisartEnum.Vorfall:
        wahlbezirkEreignisse.value.keineVorfaelle = false;
        break;
      case EreignisartEnum.Vorkommnis:
        wahlbezirkEreignisse.value.keineVorkommnisse = false;
        break;
    }

    _updateKeineFlagsOfEreignisseBasedOnCurrentState();
  }

  function deleteEreignisByIndex(index: number) {
    wahlbezirkEreignisse.value.ereigniseintraege?.splice(index, 1);

    _updateKeineFlagsOfEreignisseBasedOnCurrentState();
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
        _updateKeineFlagsOfEreignisseBasedOnCurrentState();
      } else {
        ereignisToChange.uhrzeit = undefined;
        ereignisToChange.ereignisart = EreignisartEnum.Vorfall;
      }
    }
  }

  function updateBeschreibungByIndex(beschreibung: string, index: number) {
    if (wahlbezirkEreignisse.value.ereigniseintraege) {
      const ereignisToChange =
        wahlbezirkEreignisse.value.ereigniseintraege[index];
      if (ereignisToChange == undefined) {
        return;
      }
      ereignisToChange.beschreibung = beschreibung;
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
      logDebug("Fehler beim Laden der Ereignisse", e);
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
      logDebug("Fehler beim Speichern der Ereignisse", e);
    } finally {
      isSaving.value = false;
    }
  }

  function _createEreignis(
    nonDefaultValues?: EreignisCreateTemplate
  ): Ereignis {
    const uhrzeit = nonDefaultValues?.uhrzeit ?? new Date();
    const ereignisart = getEreignisArtForDateRelatedToSchliessungsuhrzeit(
      uhrzeit,
      schliessungsuhrzeitSent.value
    );
    const beschreibung = nonDefaultValues?.beschreibung;

    return {
      uhrzeit,
      ereignisart,
      beschreibung,
    };
  }

  function _hasEintragOfEreignisart(ereginisart: EreignisartEnum): boolean {
    return (
      wahlbezirkEreignisse.value.ereigniseintraege?.some(
        (eintrag) => eintrag.ereignisart === ereginisart
      ) === true
    );
  }

  function _hasToUpdateKeineVorkommnisse(): boolean {
    return schliessungsuhrzeitSent.value !== undefined;
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
    if (_hasToUpdateKeineVorkommnisse()) {
      wahlbezirkEreignisse.value.keineVorkommnisse = !_hasEintragOfEreignisart(
        EreignisartEnum.Vorkommnis
      );
    }
    wahlbezirkEreignisse.value.keineVorfaelle =
      isBWB.value || !_hasEintragOfEreignisart(EreignisartEnum.Vorfall);
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
    updateBeschreibungByIndex,
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
