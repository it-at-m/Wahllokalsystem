<template>
  <the-m-b-w-gueltige-stimmzettel-erfassen-card
    v-if="wahlbezirkID && wahlID"
    :wahlbezirk-i-d="wahlbezirkID"
    :wahl-i-d="wahlID"
    :model-value="ergebnisse"
  />
</template>
<script setup lang="ts">
import type { MbwErgebnisseAndWahlvorschlag } from "@/types/ergebnisermittlung/MbwErgebnisseAndWahlvorschlag.ts";

import { computed, onMounted, ref } from "vue";
import { useRoute } from "vue-router";

import TheMBWGueltigeStimmzettelErfassenCard from "@/components/ergebnisermittlung/MBW/stapelAB/TheMBWGueltigeStimmzettelErfassenCard.vue";
import { useMbwUtils } from "@/composables/ergebnisermittlung/mbwUtils.ts";
import { useErgebnisService } from "@/composables/ergebnismeldung/ergebnisService.ts";
import { useWahlvorschlaegeService } from "@/composables/wahlvorschlaege/wahlvorschlaegeService.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

const route = useRoute();
const { getWahlbezirkIdFromWahlMetaDataByWahlId } = useUserStore();
const { getErgebnisse } = useErgebnisService();
const { getWahlvorschlaege } = useWahlvorschlaegeService();
const wahlID = computed(() => route.params.wahlId as string);

const wahlbezirkID = computed(() =>
  getWahlbezirkIdFromWahlMetaDataByWahlId(wahlID.value)
);

const ergebnisse = ref<MbwErgebnisseAndWahlvorschlag[]>([]);

onMounted(async () => {
  if (wahlID.value && wahlbezirkID.value) {
    await loadAndCombineErgebnisseAndWahlvorschlaege(
      wahlID.value,
      wahlbezirkID.value
    );
  }
});

async function loadAndCombineErgebnisseAndWahlvorschlaege(
  wahlID: string,
  wahlbezirkID: string
) {
  const { createEmptyErgebnisForWahlvorschlag } = useMbwUtils(
    wahlID,
    wahlbezirkID
  );

  const wahlvorschlaege = await loadWahlvorschlaege(wahlID, wahlbezirkID);
  const ergebnisseStapelA = await loadGueltigeErgebnisseByStapelArt(
    wahlID,
    wahlbezirkID,
    StapelArtEnum.MbwA
  );
  const ergebnisseStapelB = await loadGueltigeErgebnisseByStapelArt(
    wahlID,
    wahlbezirkID,
    StapelArtEnum.MbwB
  );

  for (const wahlvorschlag of wahlvorschlaege) {
    const ergebnisStapelAForWahlvorschlag = ergebnisseStapelA?.ergebnisse.find(
      (ergebnis) => ergebnis.wahlvorschlagID === wahlvorschlag.identifikator
    );
    const ergebnisStapelBForWahlvorschlag = ergebnisseStapelB?.ergebnisse.find(
      (ergebnis) => ergebnis.wahlvorschlagID === wahlvorschlag.identifikator
    );

    ergebnisse.value.push({
      wahlvorschlag: wahlvorschlag,
      ergebnisStapelA:
        ergebnisStapelAForWahlvorschlag ??
        createEmptyErgebnisForWahlvorschlag(wahlvorschlag),
      ergebnisStapelB:
        ergebnisStapelBForWahlvorschlag ??
        createEmptyErgebnisForWahlvorschlag(wahlvorschlag),
    });
  }
}

async function loadGueltigeErgebnisseByStapelArt(
  wahlID: string,
  wahlbezirkID: string,
  stapelArt: StapelArtEnum
) {
  try {
    return await getErgebnisse(wahlbezirkID, wahlID, stapelArt, false);
  } catch {
    throw new Error("Fehler beim Laden der Ergebnisse");
  }
}

async function loadWahlvorschlaege(wahlID: string, wahlbezirkID: string) {
  const { sortWahlvorschlaegeByOrdnungszahl } = useMbwUtils(
    wahlID,
    wahlbezirkID
  );
  try {
    const loadedWahlvorschlaege = await getWahlvorschlaege(
      wahlID,
      wahlbezirkID
    );
    return sortWahlvorschlaegeByOrdnungszahl(loadedWahlvorschlaege);
  } catch {
    throw new Error("Fehler beim Laden der Wahlvorschläge");
  }
}
</script>
