import type { Ereignis } from "@/types/vorfaelleundvorkommnisse/Ereignis.ts";
import type { WahlbezirkEreignisse } from "@/types/vorfaelleundvorkommnisse/WahlbezirkEreignisse.ts";

import { defineStore, storeToRefs } from "pinia";
import { computed, ref, watch } from "vue";

import { useHmrUpdate } from "@/composables/common/hmrUpdate.ts";
import { useLogging } from "@/composables/common/logging.ts";
import { useEreignisService } from "@/composables/vorfaelleundvorkommnisse/ereignisService.ts";
import { useEreignisComparator } from "@/composables/vorfaelleundvorkommnisse/ereignisUtils.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";
import {
  EreignisartEnum,
  getEreignisArtForDateRelatedToSchliessungsuhrzeit,
} from "@/types/vorfaelleundvorkommnisse/Ereignisart.ts";
import { WahlbezirkEreignisseBuilder } from "@/types/vorfaelleundvorkommnisse/WahlbezirkEreignisse.ts";

const { compareEreignisseByUhrzeit } = useEreignisComparator();
const { getEreignisse, saveEreignisse } = useEreignisService();
const { registerStoreHMR } = useHmrUpdate();

export const storeID = "vorfaelleundvorkommnisse";
const { logDebug } = useLogging(`store-${storeID}`);

interface EreignisCreateTemplate {
  beschreibung?: string;
  uhrzeit?: Date;
}

export const useEreignisStore = defineStore(storeID, () => {
  const { currentUserWahlbezirkID, isUWB, isBWB } = storeToRefs(useUserStore());
  const { schliessungsuhrzeitState, schliessungsuhrzeitGetter } =
    storeToRefs(useWahlbezirkStore());

  const error = ref<string | null>(null);
  const isSaving = ref(false);
  const isVorkommnisseMaintained = ref(false);

  const wahlbezirkEreignisse = ref<WahlbezirkEreignisse>(
    WahlbezirkEreignisseBuilder.createEmptyWahlbezirkEreignisse()
  );

  const hasEintraege = computed(
    () => wahlbezirkEreignisse.value.ereigniseintraege.length > 0
  );
  const ereigniseintraegeContainsVorfaelle = computed(() =>
    _hasEintragOfEreignisart(EreignisartEnum.Vorfall)
  );
  const ereigniseintraegeContainsVorkommnisse = computed(() =>
    _hasEintragOfEreignisart(EreignisartEnum.Vorkommnis)
  );

  const isEreignisFlagsAndEreigniseintraegeInconsistent = computed(() => {
    return isUWB.value
      ? _isKeineVorfaelleAndEreigniseintraegeContainsVorfaelleInconsistent.value ||
          (_isKeineVorkommnisseAndEreigniseintraegeContainsVorkommnisseInconsistent.value &&
            schliessungsuhrzeitGetter.value.isAuszaehlungStarted)
      : _isKeineVorkommnisseAndEreigniseintraegeContainsVorkommnisseInconsistent.value;
  });

  watch(
    () => schliessungsuhrzeitState.value.schliessungsuhrzeitSent,
    _onSchliessunguhrzeitSentChanged
  );

  function addEreignis(ereignisToAddTemplate?: EreignisCreateTemplate) {
    const ereignisToAdd = _createEreignis(ereignisToAddTemplate);

    wahlbezirkEreignisse.value.ereigniseintraege.push(ereignisToAdd);
    switch (ereignisToAdd.ereignisart) {
      case EreignisartEnum.Vorfall:
        wahlbezirkEreignisse.value.keineVorfaelle = false;
        break;
      case EreignisartEnum.Vorkommnis:
        wahlbezirkEreignisse.value.keineVorkommnisse = false;
        break;
    }

    _updateKeineVorkommnisseAndKeineVorfaelleBasedOnCurrentState();
  }

  function deleteEreignisByIndex(index: number) {
    wahlbezirkEreignisse.value.ereigniseintraege.splice(index, 1);

    _updateKeineVorkommnisseAndKeineVorfaelleBasedOnCurrentState();
  }

  function updateUhrzeitByIndex(uhrzeit: Date | undefined, index: number) {
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
          schliessungsuhrzeitState.value.schliessungsuhrzeitSent
        );
      _updateKeineVorkommnisseAndKeineVorfaelleBasedOnCurrentState();
    } else {
      ereignisToChange.uhrzeit = undefined;
      ereignisToChange.ereignisart = EreignisartEnum.Vorfall;
    }
  }

  function updateBeschreibungByIndex(beschreibung: string, index: number) {
    const ereignisToChange =
      wahlbezirkEreignisse.value.ereigniseintraege[index];
    if (ereignisToChange == undefined) {
      return;
    }
    ereignisToChange.beschreibung = beschreibung;
  }

  async function loadEreignisse() {
    error.value = null;
    try {
      wahlbezirkEreignisse.value = await getEreignisse(
        currentUserWahlbezirkID.value
      );
      _sortEreignisse(wahlbezirkEreignisse.value.ereigniseintraege);
      _checkIfVorkommnisseAreMaintained();
    } catch (e) {
      error.value = "Fehler beim Laden der Ereignisse";
      logDebug("Fehler beim Laden der Ereignisse", e);
    }
  }

  async function sendEreignisse(sendNotification = true) {
    error.value = null;
    isSaving.value = true;
    try {
      _sortEreignisse(wahlbezirkEreignisse.value.ereigniseintraege);
      await saveEreignisse(
        currentUserWahlbezirkID.value,
        wahlbezirkEreignisse.value,
        sendNotification
      );
      _checkIfVorkommnisseAreMaintained();
    } catch (e) {
      error.value = "Fehler beim Speichern der Ereignisse";
      logDebug("Fehler beim Speichern der Ereignisse", e);
    } finally {
      isSaving.value = false;
    }
  }

  const _isKeineVorfaelleAndEreigniseintraegeContainsVorfaelleInconsistent =
    computed(
      () =>
        ereigniseintraegeContainsVorfaelle.value ===
        wahlbezirkEreignisse.value.keineVorfaelle
    );

  const _isKeineVorkommnisseAndEreigniseintraegeContainsVorkommnisseInconsistent =
    computed(
      () =>
        ereigniseintraegeContainsVorkommnisse.value ===
        wahlbezirkEreignisse.value.keineVorkommnisse
    );

  function _createEreignis(
    nonDefaultValues?: EreignisCreateTemplate
  ): Ereignis {
    const uhrzeit = nonDefaultValues?.uhrzeit ?? new Date();
    const ereignisart = getEreignisArtForDateRelatedToSchliessungsuhrzeit(
      uhrzeit,
      schliessungsuhrzeitState.value.schliessungsuhrzeitSent
    );
    const beschreibung = nonDefaultValues?.beschreibung;

    return {
      uhrzeit,
      ereignisart,
      beschreibung,
    };
  }

  function _hasEintragOfEreignisart(ereginisart: EreignisartEnum): boolean {
    return wahlbezirkEreignisse.value.ereigniseintraege.some(
      (eintrag) => eintrag.ereignisart === ereginisart
    );
  }

  async function _onSchliessunguhrzeitSentChanged(
    newSchliessungsuhrzeit: Date | undefined
  ) {
    wahlbezirkEreignisse.value.ereigniseintraege.forEach((eintrag) => {
      if (eintrag.uhrzeit) {
        eintrag.ereignisart = getEreignisArtForDateRelatedToSchliessungsuhrzeit(
          eintrag.uhrzeit,
          newSchliessungsuhrzeit
        );
      }
    });
    _updateKeineVorkommnisseAndKeineVorfaelleBasedOnCurrentState();

    await sendEreignisse(false);
  }

  function _updateKeineVorkommnisseAndKeineVorfaelleBasedOnCurrentState() {
    if (schliessungsuhrzeitGetter.value.isAuszaehlungStarted) {
      wahlbezirkEreignisse.value.keineVorkommnisse =
        !ereigniseintraegeContainsVorkommnisse.value;
    }
    wahlbezirkEreignisse.value.keineVorfaelle =
      isBWB.value || !ereigniseintraegeContainsVorfaelle.value;
  }

  function _sortEreignisse(ereigniseintraege: Ereignis[]) {
    return ereigniseintraege.sort(compareEreignisseByUhrzeit);
  }

  function _checkIfVorkommnisseAreMaintained() {
    if (
      (!wahlbezirkEreignisse.value.keineVorfaelle &&
        _hasEintragOfEreignisart(EreignisartEnum.Vorfall)) ||
      (wahlbezirkEreignisse.value.keineVorfaelle &&
        !_hasEintragOfEreignisart(EreignisartEnum.Vorfall))
    ) {
      isVorkommnisseMaintained.value = true;
    }
  }

  return {
    isEreignisFlagsAndEreigniseintraegeInconsistent,
    wahlbezirkEreignisse,
    hasEintraege,
    ereigniseintraegeContainsVorfaelle,
    ereigniseintraegeContainsVorkommnisse,
    isSaving,
    deleteEreignisByIndex,
    loadEreignisse,
    sendEreignisse,
    addEreignis,
    updateUhrzeitByIndex,
    updateBeschreibungByIndex,
    error,
    isVorkommnisseMaintained,
  };
});

registerStoreHMR(useEreignisStore);
