<template>
  <v-card>
    <v-card-title> Ungültige Stimmzettel </v-card-title>
    <v-card-text>
      <v-table>
        <thead>
          <tr>
            <th class="index-column" />
            <th />
            <th class="font-weight-bold text-right">Zweifelsfrei ungültig</th>
            <th class="font-weight-bold text-right">Laut Beschluss ungültig</th>
            <th class="font-weight-bold text-right">Insgesamt</th>
          </tr>
        </thead>
        <tfoot>
          <tr>
            <td class="font-weight-bold index-column">C</td>
            <td class="font-weight-bold">Ungültige Stimmzettel</td>
            <td class="font-weight-bold text-right">{{ ungueltigeStimmen }}</td>
            <td class="font-weight-bold text-right">
              {{ ungueltigeStimmzettelNachBeschluss }}
            </td>
            <td class="font-weight-bold text-right">
              {{ sumUngueltigeStimmzettel }}
            </td>
          </tr>
        </tfoot>
      </v-table>
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import { computed } from "vue";

const props = defineProps<{
  ungueltigeStimmen: number;
  ungueltigeStimmzettelNachBeschluss: number;
}>();

const sumUngueltigeStimmzettel = computed(
  () => props.ungueltigeStimmen + props.ungueltigeStimmzettelNachBeschluss
);
</script>
