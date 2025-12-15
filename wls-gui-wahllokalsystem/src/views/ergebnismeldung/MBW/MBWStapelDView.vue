<template>
  <base-card-snipped-ergebnis
    v-model="ergebnis"
    snipped-title="Ungültige Stimmzettel"
    :is-ergebnis-saving="isErgebnisSaving"
    @save="onSave"
  />
</template>

<script setup lang="ts">
import type { Ergebnis } from "@/types/ergebnismeldung/common/Ergebnis.ts";
import type { Ergebnisse } from "@/types/ergebnismeldung/common/Ergebnisse.ts";

import { onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";

import BaseCardSnippedErgebnis from "@/components/ergebnismeldung/common/BaseCardSnippedErgebnis.vue";
import { useLogging } from "@/composables/common/logging.ts";
import { useErgebnisService } from "@/composables/ergebnismeldung/common/ergebnisService.ts";
import { EXAMPLE_ROUTES_NOTFOUND } from "@/constants.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/common/StapelArtEnum.ts";

const route = useRoute();
const router = useRouter();
const { wahlenActions } = useWahlenStore();
const { getWahlbezirkIdFromWahlMetaDataByWahlId } = useUserStore();
const { getErgebnisse, postErgebnisse } = useErgebnisService();
const { logError } = useLogging("requestStrategies");

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

if (!wahl) {
  router.push({
    name: EXAMPLE_ROUTES_NOTFOUND,
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

    if (wahlbezirkID && ergebnisseToSend) {
      await postErgebnisse(
        wahlbezirkID,
        wahlID,
        stapelArt,
        ergebnisseToSend,
        true
      );
    }
  } catch (error) {
    logError("Fehler beim Speichern der Ergebnisse: ", error);
    throw error;
  } finally {
    isErgebnisSaving.value = false;
  }
}
</script>
