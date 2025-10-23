<template>
  <base-card-snipped-ergebnis
    v-model="ergebnis"
    snipped-title="Ungültige Stimmzettel"
    :is-ergebnis-saving="isErgebnisSaving"
    @save="onSave"
  />
</template>

<script setup lang="ts">
import type { Ergebnis } from "@/types/ergebnismeldung/Ergebnis.ts";
import type { Ergebnisse } from "@/types/ergebnismeldung/Ergebnisse.ts";

import { computed, onMounted, ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";

import BaseCardSnippedErgebnis from "@/components/ergebnisermittlung/BaseCardSnippedErgebnis.vue";
import { useErgebnisService } from "@/composables/ergebnismeldung/ergebnisService.ts";
import { EXAMPLE_ROUTES_NOTFOUND } from "@/constants.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

const route = useRoute();
const router = useRouter();
const { wahlenActions } = useWahlenStore();
const { getWahlbezirkIdFromWahlMetaDataByWahlId } = useUserStore();
const { getErgebnisse, postErgebnisse } = useErgebnisService();

const wahlID = computed(() => route.params.wahlId as string);
const wahlbezirkID = computed(() =>
  getWahlbezirkIdFromWahlMetaDataByWahlId(wahlID.value)
);
const stapelArt = StapelArtEnum.MbwD;
const isErgebnisSaving = ref(false);
const ergebnis = ref<Ergebnis>({
  wahlvorschlagID: null,
  kandidatID: null,
  wahlvorschlagsOrdnungszahl: null,
  ergebnis: null,
  numIndex: null,
});

onMounted(async () => {
  await _loadErgebnis();
});

watch(
  () => wahlbezirkID.value,
  async () => {
    await _loadErgebnis();
  }
);

watch(
  () => wahlID.value,
  (wahlID) => {
    const wahl = wahlenActions.getWahlOrUndefinedById(wahlID);

    if (!wahl) {
      router.push({
        name: EXAMPLE_ROUTES_NOTFOUND,
      });
    }
  }
);

async function onSave() {
  try {
    isErgebnisSaving.value = true;
    const ergebnisseToSend = {
      bezirkUndWahlIDStapelart: {
        stapelArt,
        wahlID: wahlID.value,
        wahlbezirkID: wahlbezirkID.value,
      },
      ergebnisse: [ergebnis.value],
    } as Ergebnisse;

    if (wahlbezirkID.value && ergebnisseToSend) {
      await postErgebnisse(
        wahlbezirkID.value,
        wahlID.value,
        stapelArt,
        ergebnisseToSend,
        true
      );
    }
  } catch {
    throw new Error("Fehler beim Speichern der Ergebnisse");
  } finally {
    isErgebnisSaving.value = false;
  }
}

async function _loadErgebnis() {
  if (wahlbezirkID.value) {
    const loadedErgebnisse = await getErgebnisse(
      wahlbezirkID.value,
      wahlID.value,
      stapelArt,
      false
    );
    if (loadedErgebnisse?.ergebnisse[0]) {
      ergebnis.value = loadedErgebnisse?.ergebnisse[0];
    }
  }
}
</script>
