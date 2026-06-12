<template>
  <base-card-ungueltige-stimmzettel-erfassen
    v-model="ergebnis"
    :ungueltige-stimmzettel-nach-beschluss="0"
    :is-saving="isErgebnisSaving"
    :is-wahl-finished="isMBWAuszaehlungDone"
    @save="onSave"
  />
</template>

<script setup lang="ts">
import type { Ergebnis } from "@/types/ergebnismeldung/common/Ergebnis.ts";
import type { Ergebnisse } from "@/types/ergebnismeldung/common/Ergebnisse.ts";

import { computed, onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";

import BaseCardUngueltigeStimmzettelErfassen from "@/components/ergebnismeldung/MBW/stapelD/BaseCardUngueltigeStimmzettelErfassen.vue";
import { useLogging } from "@/composables/common/logging.ts";
import { useErgebnisService } from "@/composables/ergebnismeldung/common/ergebnisService.ts";
import { useNavigationUtils } from "@/composables/navigation/navigationUtils.ts";
import { ROUTE_NOTFOUND } from "@/constants.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { useWorkflowStore } from "@/stores/workflowStore.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/common/StapelArtEnum.ts";
import { MbwRoutesEnum } from "@/types/navigation/MbwRoutesEnum.ts";

const route = useRoute();
const router = useRouter();
const { wahlenActions } = useWahlenStore();
const { getWahlbezirkIdFromWahlMetaDataByWahlId } = useUserStore();
const { setStepDone, isElectionFinished } = useWorkflowStore();
const { getErgebnisse, postErgebnisse } = useErgebnisService();
const { logError } = useLogging("mbwStapelDView");
const { getNextRoute } = useNavigationUtils();

const wahlID = route.params.wahlId as string;
const wahl = wahlenActions.getWahlOrUndefinedById(wahlID);
const wahlbezirkID = getWahlbezirkIdFromWahlMetaDataByWahlId(wahlID);

const stapelArt = StapelArtEnum.MbwDUngueltig;
const isErgebnisSaving = ref(false);
const ergebnis = ref<Ergebnis>({
  wahlvorschlagID: null,
  kandidatID: null,
  wahlvorschlagsOrdnungszahl: null,
  ergebnis: null,
  numIndex: null,
});

const isMBWAuszaehlungDone = computed(() =>
  isElectionFinished(wahlID, wahlbezirkID ?? "")
);

if (!wahl) {
  router.push({
    name: ROUTE_NOTFOUND,
  });
}

onMounted(async () => {
  if (wahlbezirkID) {
    try {
      const loadedErgebnisse = await getErgebnisse(
        wahlbezirkID,
        wahlID,
        stapelArt,
        false
      );
      if (loadedErgebnisse?.ergebnisse[0]) {
        ergebnis.value = loadedErgebnisse?.ergebnisse[0];
      }
    } catch (error) {
      logError("Fehler beim Laden der Ergebnisse: ", error);
      throw error;
    }
  }
});

async function onSave() {
  try {
    isErgebnisSaving.value = true;
    const ergebnisseToSend = {
      bezirkUndWahlIDStapelart: {
        stapelArt,
        wahlID: wahlID,
        wahlbezirkID: wahlbezirkID,
      },
      ergebnisse: [ergebnis.value],
    } as Ergebnisse;

    if (wahlbezirkID) {
      await postErgebnisse(
        wahlbezirkID,
        wahlID,
        stapelArt,
        ergebnisseToSend,
        true
      );
      setStepDone(wahlID, wahlbezirkID, MbwRoutesEnum.MBW_STAPEL_D_UNGUELTIG);
      await router.push(getNextRoute());
    }
  } catch (error) {
    logError("Fehler beim Speichern der Ergebnisse: ", error);
    throw error;
  } finally {
    isErgebnisSaving.value = false;
  }
}
</script>
