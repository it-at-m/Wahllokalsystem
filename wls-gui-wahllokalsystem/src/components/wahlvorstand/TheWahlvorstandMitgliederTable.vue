<template>
  <v-table data-test="tableWahlvorstandsMitglieder">
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
        <td data-test="textFamilienname">{{ mitglied.familienname }}</td>
        <td data-test="textVorname">{{ mitglied.vorname }}</td>
        <td data-test="textFunktion">{{ mitglied.funktionsname }}</td>
        <td>
          <v-checkbox
            :model-value="mitglied.anwesend"
            :hide-details="true"
            data-test="checkboxAnwesend"
            @update:model-value="onAnwesenheitChanged($event, mitglied)"
          />
        </td>
      </tr>
    </tbody>
  </v-table>
</template>

<script setup lang="ts">
import type { Wahlvorstandsmitglied } from "@/types/wahlvorstand/Wahlvorstandsmitglied";

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
