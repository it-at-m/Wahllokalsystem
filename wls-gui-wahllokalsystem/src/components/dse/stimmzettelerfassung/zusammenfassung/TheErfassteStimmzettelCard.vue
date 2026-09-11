<template>
  <v-card>
    <v-card-title>Erfasste Stimmzettel</v-card-title>
    <v-card-text>
      <v-table>
        <thead>
          <tr>
            <th />
            <th class="font-weight-bold text-right">Insgesamt</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>Stimmzettel für Beschluss vorgemerkt</td>
            <td class="text-right">{{ beschlussVorgemerktCount }}</td>
          </tr>
          <tr>
            <td>Ungültige Stimmzettel</td>
            <td class="text-right">{{ invalidStimmzettelCount }}</td>
          </tr>
          <tr>
            <td>Gültige Stimmzettel</td>
            <td class="text-right">{{ validStimmzettelCount }}</td>
          </tr>
        </tbody>
        <tfoot>
          <tr>
            <td class="font-weight-bold">Stimmzettel Gesamt</td>
            <td class="font-weight-bold text-right">
              {{ stimmzettelListe.length }}
            </td>
          </tr>
        </tfoot>
      </v-table>
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import type { Stimmzettel } from "@/types/dse/persistedStimmzettel/Stimmzettel.ts";

import { computed } from "vue";

import { StimmzettelGueltigkeitEnum } from "@/types/dse/persistedStimmzettel/StimmzettelGueltigkeitEnum.ts";

const props = defineProps<{
  stimmzettelListe: Stimmzettel[];
}>();

const validStimmzettelCount = computed(
  () =>
    props.stimmzettelListe.filter(
      (stimmzettel) =>
        stimmzettel.gueltigkeit === StimmzettelGueltigkeitEnum.Valid
    ).length
);

const beschlussVorgemerktCount = computed(
  () =>
    props.stimmzettelListe.filter(
      (stimmzettel) =>
        stimmzettel.gueltigkeit ===
        StimmzettelGueltigkeitEnum.BeschlussAusstehend
    ).length
);

const invalidStimmzettelCount = computed(
  () =>
    props.stimmzettelListe.length -
    validStimmzettelCount.value -
    beschlussVorgemerktCount.value
);
</script>
