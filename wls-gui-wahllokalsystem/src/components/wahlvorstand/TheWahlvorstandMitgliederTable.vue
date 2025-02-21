<template>
  <v-table data-cy="tableWahlvorstandsMitglieder">
    <thead>
      <tr>
        <th>Familienname</th>
        <th>Vorname</th>
        <th>Funktion</th>
        <th>Anwesend</th>
      </tr>
    </thead>

    <tbody>
      <tr
        v-for="mitglied in wahlvorstand.wahlvorstandsmitglieder"
        :key="mitglied.identifikator"
      >
        <td data-cy="textFamilienname">{{ mitglied.familienname }}</td>
        <td data-cy="textVorname">{{ mitglied.vorname }}</td>
        <td data-cy="textFunktion">{{ mitglied.funktionsname }}</td>
        <td>
          <v-checkbox
            :model-value="mitglied.anwesend"
            :hide-details="true"
            data-cy="checkboxAnwesend"
            @update:model-value="onAnwesenheitChanged($event, mitglied)"
          />
        </td>
      </tr>
    </tbody>
  </v-table>
</template>

<script setup lang="ts">
import type { Wahlvorstandsmitglied } from "@/types/wahlvorstand/wahlvorstandsmitglied";

import { computed } from "vue";
import { VCheckbox, VTable } from "vuetify/components";

import { useWahlvorstandStore } from "@/stores/wahlvorstandStore";

const wahlvorstandStore = useWahlvorstandStore();
const wahlvorstand = computed(() => wahlvorstandStore.wahlvorstand);

function onAnwesenheitChanged(
  newAnwesenheit: boolean | null,
  mitglied: Wahlvorstandsmitglied
) {
  wahlvorstandStore.changeAnwesendOfMitglied(
    newAnwesenheit ?? false,
    mitglied.identifikator
  );
}
</script>

<style scoped></style>
