<template>
  <v-card>
    <v-card-title> Gültige Stimmen </v-card-title>
    <v-card-text>
      <the-m-b-w-gueltige-stimmen-anzeigen-table
        :ergebnisse-and-wahlvorschlaege="ergebnisseAndWahlvorschlaege"
      />
    </v-card-text>
  </v-card>
</template>
<script setup lang="ts">
import type { MbwErgebnisseAndWahlvorschlag } from "@/types/ergebnismeldung/MBW/MbwErgebnisseAndWahlvorschlag.ts";

import { onActivated, ref } from "vue";

import TheMBWGueltigeStimmenAnzeigenTable from "@/components/ergebnismeldung/MBW/stapelAB/TheMBWGueltigeStimmenAnzeigenTable.vue";
import { useMbwUtils } from "@/composables/ergebnismeldung/MBW/mbwUtils.ts";

const props = defineProps<{
  wahlbezirkId: string;
  wahlId: string;
}>();

const ergebnisseAndWahlvorschlaege = ref<MbwErgebnisseAndWahlvorschlag[]>([]);

const { loadAndCombineErgebnisseAndWahlvorschlaege } = useMbwUtils(
  props.wahlId,
  props.wahlbezirkId
);

onActivated(async () => {
  ergebnisseAndWahlvorschlaege.value =
    await loadAndCombineErgebnisseAndWahlvorschlaege();
});
</script>
