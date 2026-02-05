<template>
  <v-card>
    <v-card-title> Wähler </v-card-title>
    <v-card-text>
      <v-table>
        <thead>
          <tr>
            <th class="index-column" />
            <th />
            <th class="font-weight-bold text-right">Insgesamt</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="(row, idx) in rows"
            :key="idx"
          >
            <td
              v-for="(value, index1) in row"
              :key="index1"
              :class="[
                { 'index-column': index1 === 0 },
                { 'text-right': index1 === 2 },
              ]"
            >
              {{ value }}
            </td>
          </tr>
        </tbody>
        <tfoot>
          <tr>
            <td
              v-for="(value, index2) in resultRow"
              :key="index2"
              :class="[
                { 'index-column': index2 === 0 },
                { 'font-weight-bold': true },
                { 'text-right': index2 === 2 },
              ]"
            >
              {{ value }}
            </td>
          </tr>
        </tfoot>
      </v-table>
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";
import { computed, onActivated, ref } from "vue";

import { useMbwUtils } from "@/composables/ergebnismeldung/MBW/mbwUtils.ts";
import { useUserStore } from "@/stores/userStore.ts";

const { isUWB } = storeToRefs(useUserStore());

const props = defineProps<{
  wahlbezirkId: string;
  wahlId: string;
}>();

const b1 = ref(0);
const b2 = ref(0);
const b = ref(0);

const rows = computed(() =>
  isUWB.value
    ? [
        [
          "B1",
          "Wähler mit Stimmabgabevermerken im Wählerverzeichnis",
          b1.value,
        ],
        ["B2", "Wähler mit Wahlschein", b2.value],
      ]
    : []
);
const resultRow = computed(() =>
  isUWB.value ? ["B1 + B2", "Wähler", b.value] : ["B", "Wähler", b.value]
);

onActivated(async () => {
  const { getBWerteForWahlbezirkAndWahl } = useMbwUtils(
    props.wahlId,
    props.wahlbezirkId
  );

  const bWerte = await getBWerteForWahlbezirkAndWahl();
  b.value = bWerte.b;
  b1.value = bWerte.b1;
  b2.value = bWerte.b2;
});
</script>
