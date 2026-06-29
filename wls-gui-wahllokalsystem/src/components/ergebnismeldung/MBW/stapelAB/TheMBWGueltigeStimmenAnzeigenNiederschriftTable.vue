<template>
  <v-table>
    <thead>
      <tr>
        <th><!-- Ordnungszahl --></th>
        <th class="font-weight-bold">Wahlvorschlag</th>
        <th
          colspan="3"
          style="text-align: center"
          class="font-weight-bold"
        >
          Gültige Stimmzettel
        </th>
        <th class="font-weight-bold text-right">Gültige Stimmen</th>
      </tr>
      <tr>
        <th><!-- Ordnungszahl --></th>
        <th><!-- Wahlvorschlag --></th>
        <th class="font-weight-bold text-right smallText no-line-break">
          Stapel a
        </th>
        <th class="font-weight-bold text-right smallText no-line-break">
          Stapel b
        </th>
        <th class="font-weight-bold text-right smallText no-line-break">
          Stapel a + b
        </th>
        <th class="font-weight-bold text-right smallText">
          Gültig kumulierte und panaschierte
        </th>
      </tr>
    </thead>
    <tbody>
      <tr
        v-for="vorschlag in sortedModelValue"
        :key="vorschlag.wahlvorschlag.identifikator"
      >
        <td class="index-column">
          D {{ vorschlag.wahlvorschlag.ordnungszahl }}
        </td>
        <td>{{ vorschlag.wahlvorschlag.kurzname }}</td>
        <td class="text-right">
          {{ vorschlag.ergebnisStapelA.ergebnis ?? 0 }}
        </td>
        <td class="text-right">
          {{ vorschlag.ergebnisStapelB.ergebnis ?? 0 }}
        </td>
        <td class="text-right">
          {{
            (vorschlag.ergebnisStapelA.ergebnis ?? 0) +
            (vorschlag.ergebnisStapelB.ergebnis ?? 0)
          }}
        </td>
        <td class="text-right">
          {{
            summeKandidatenStimmen(
              wahlvorschlaegeKandidatenErgebnisse.find(
                (kandidatenErgebnis) =>
                  kandidatenErgebnis.identifikator ===
                  vorschlag.wahlvorschlag.identifikator
              )?.kandidatenErgebnisse ?? []
            )
          }}
        </td>
      </tr>
    </tbody>
    <tfoot>
      <tr>
        <td class="font-weight-bold index-column">D</td>
        <td class="font-weight-bold">Gültige Stimmen</td>
        <td class="font-weight-bold text-right">{{ totalSumStapelA }}</td>
        <td class="font-weight-bold text-right">{{ totalSumStapelB }}</td>
        <td class="font-weight-bold text-right">
          {{ totalSumStapelA + totalSumStapelB }}
        </td>
        <td class="font-weight-bold text-right">
          {{
            wahlvorschlaegeKandidatenErgebnisse.reduce(
              (sum, ergebnis) =>
                sum + summeKandidatenStimmen(ergebnis.kandidatenErgebnisse),
              0
            )
          }}
        </td>
      </tr>
    </tfoot>
  </v-table>
</template>
<script setup lang="ts">
import type { WahlvorschlagWithKandidatenErgebnissen } from "@/types/ergebnismeldung/common/WahlvorschlagWithKandidatenErgebnissen.ts";
import type { MbwErgebnisseAndWahlvorschlag } from "@/types/ergebnismeldung/MBW/MbwErgebnisseAndWahlvorschlag.ts";
import type { PropType } from "vue";

import { computed } from "vue";

import { useErgebnisAndKandidatUtils } from "@/composables/ergebnismeldung/common/ergebnisAndKandidatUtils.ts";
import { useWahlvorschlagUtils } from "@/composables/wahlvorschlaege/wahlvorschlagUtils.ts";

const { sortMbwErgebnisseAndWahlvorschlagByOrdnungszahl } =
  useWahlvorschlagUtils();
const { summeKandidatenStimmen } = useErgebnisAndKandidatUtils();

const props = defineProps({
  ergebnisseAndWahlvorschlaege: {
    type: Array as PropType<MbwErgebnisseAndWahlvorschlag[]>,
    required: true,
  },
  wahlvorschlaegeKandidatenErgebnisse: {
    type: Array as PropType<WahlvorschlagWithKandidatenErgebnissen[]>,
    required: true,
  },
});

const sortedModelValue = computed(() => {
  return sortMbwErgebnisseAndWahlvorschlagByOrdnungszahl(
    props.ergebnisseAndWahlvorschlaege
  );
});

const totalSumStapelA = computed(() =>
  props.ergebnisseAndWahlvorschlaege.reduce((sum, vorschlag) => {
    return sum + (vorschlag.ergebnisStapelA.ergebnis ?? 0);
  }, 0)
);
const totalSumStapelB = computed(() =>
  props.ergebnisseAndWahlvorschlaege.reduce((sum, vorschlag) => {
    return sum + (vorschlag.ergebnisStapelB.ergebnis ?? 0);
  }, 0)
);
</script>
<style scoped>
.no-line-break {
  white-space: nowrap;
}
</style>
