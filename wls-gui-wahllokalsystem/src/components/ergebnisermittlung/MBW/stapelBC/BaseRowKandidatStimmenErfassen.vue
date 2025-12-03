<template>
  <tr>
    <td>{{ laufendeNummer }}</td>
    <td>{{ kandidat.name }}</td>
    <td>
      <base-number-input
        v-model="ergebnis.ergebnis"
        :rules="[required, minNumber(0), maxNumber(9999)]"
        class="styleErgebnisCell"
      />
    </td>
  </tr>
</template>

<script setup lang="ts">
import type { Ergebnis } from "@/types/ergebnismeldung/Ergebnis.ts";
import type { Kandidat } from "@/types/wahlvorschlaege/Kandidat.ts";
import type { PropType } from "vue";

import { computed } from "vue";

import BaseNumberInput from "@/components/common/inputs/BaseNumberInput.vue";
import { useRules } from "@/composables/common/rules.ts";
import { useWahlvorschlagUtils } from "@/composables/wahlvorschlaege/wahlvorschlagUtils.ts";

const { getKandidatLaufendeNummer } = useWahlvorschlagUtils();
const { required, minNumber, maxNumber } = useRules();

const ergebnis = defineModel<Ergebnis>({ required: true });

const props = defineProps({
  kandidat: {
    type: Object as PropType<Kandidat>,
    required: true,
  },
  wahlvorschlagNummer: {
    type: Number,
    required: true,
  },
});

const laufendeNummer = computed(() =>
  getKandidatLaufendeNummer(
    props.wahlvorschlagNummer,
    props.kandidat.listenposition
  )
);
</script>

<style scoped>
.styleErgebnisCell {
  min-width: 21em;
}
</style>
