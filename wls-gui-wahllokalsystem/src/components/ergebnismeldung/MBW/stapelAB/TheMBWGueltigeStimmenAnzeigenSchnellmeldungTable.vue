<template>
  <base-table-gueltige-stimmen-schnellmeldung-anzeigen
    :ergebnisse-and-wahlvorschlaege="sortedModelValue"
  />
</template>
<script setup lang="ts">
import type { MbwErgebnisseAndWahlvorschlag } from "@/types/ergebnismeldung/MBW/MbwErgebnisseAndWahlvorschlag.ts";
import type { PropType } from "vue";

import { computed } from "vue";

import BaseTableGueltigeStimmenSchnellmeldungAnzeigen from "@/components/ergebnismeldung/common/BaseTableGueltigeStimmenSchnellmeldungAnzeigen.vue";
import { useWahlvorschlagUtils } from "@/composables/wahlvorschlaege/wahlvorschlagUtils.ts";

const { sortMbwErgebnisseAndWahlvorschlagByOrdnungszahl } =
  useWahlvorschlagUtils();

const props = defineProps({
  ergebnisseAndWahlvorschlaege: {
    type: Array as PropType<MbwErgebnisseAndWahlvorschlag[]>,
    required: true,
  },
});

const sortedModelValue = computed(() => {
  return sortMbwErgebnisseAndWahlvorschlagByOrdnungszahl(
    props.ergebnisseAndWahlvorschlaege
  );
});
</script>
