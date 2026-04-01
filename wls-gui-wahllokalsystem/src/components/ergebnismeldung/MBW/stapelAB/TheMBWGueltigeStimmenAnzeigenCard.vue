<template>
  <v-card>
    <v-card-title> Gültige Stimmen </v-card-title>
    <v-card-text>
      <the-m-b-w-gueltige-stimmen-anzeigen-table
        :ergebnisse-and-wahlvorschlaege="ergebnisseAndWahlvorschlaege"
        :wahlvorschlaege-kandidaten-ergebnisse="
          wahlvorschlaegeWithKandidatenErgebnissen
        "
      />
    </v-card-text>
  </v-card>
</template>
<script setup lang="ts">
import type { MbwErgebnisseAndWahlvorschlag } from "@/types/ergebnismeldung/MBW/MbwErgebnisseAndWahlvorschlag.ts";

import { onActivated, ref } from "vue";

import TheMBWGueltigeStimmenAnzeigenTable from "@/components/ergebnismeldung/MBW/stapelAB/TheMBWGueltigeStimmenAnzeigenTable.vue";
import { useMbwUtils } from "@/composables/ergebnismeldung/MBW/mbwUtils.ts";
import { useMwbStapelBCUtils } from "@/composables/ergebnismeldung/MBW/mwbStapelBCUtils.ts";

const props = defineProps<{
  wahlbezirkId: string;
  wahlId: string;
}>();

const ergebnisseAndWahlvorschlaege = ref<MbwErgebnisseAndWahlvorschlag[]>([]);

const { loadAndCombineErgebnisseAndWahlvorschlaege } = useMbwUtils(
  props.wahlId,
  props.wahlbezirkId
);

const {
  wahlvorschlaegeWithKandidatenErgebnissen,
  loadWahlvorschlaegeAndErgebnisse,
} = useMwbStapelBCUtils(props.wahlbezirkId, props.wahlId);

onActivated(async () => {
  ergebnisseAndWahlvorschlaege.value =
    await loadAndCombineErgebnisseAndWahlvorschlaege();
  await loadWahlvorschlaegeAndErgebnisse();
});
</script>
