<template>
  <div>
    <v-card-title> Gültige Stimmen </v-card-title>
    <v-card-text>
      <the-m-b-w-gueltige-stimmen-anzeigen-table
        :ergebnisse-and-wahlvorschlaege="ergebnisseAndWahlvorschlaege"
      />
    </v-card-text>
  </div>
</template>
<script setup lang="ts">
import type { MbwErgebnisseAndWahlvorschlag } from "@/types/ergebnisermittlung/MbwErgebnisseAndWahlvorschlag.ts";

import { onMounted, ref } from "vue";

import TheMBWGueltigeStimmenAnzeigenTable from "@/components/ergebnisermittlung/MBW/stapelAB/TheMBWGueltigeStimmenAnzeigenTable.vue";
import { useMbwUtils } from "@/composables/ergebnisermittlung/mbwUtils.ts";

const props = defineProps<{
  wahlbezirkId: string;
  wahlId: string;
}>();

const ergebnisseAndWahlvorschlaege = ref<MbwErgebnisseAndWahlvorschlag[]>([]);

const { loadAndCombineErgebnisseAndWahlvorschlaege } = useMbwUtils(
  props.wahlId,
  props.wahlbezirkId
);

onMounted(async () => {
  ergebnisseAndWahlvorschlaege.value =
    await loadAndCombineErgebnisseAndWahlvorschlaege();
});
</script>
