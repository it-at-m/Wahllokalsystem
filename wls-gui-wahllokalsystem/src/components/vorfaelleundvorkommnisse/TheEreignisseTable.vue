<template>
  <v-table data-test="tableWahlvorstandsMitglieder">
    <thead>
      <tr>
        <th>Uhrzeit</th>
        <th>Beschreibung</th>
      </tr>
    </thead>

    <tbody>
      <tr
        v-for="(ereignis, index) in wahlbezirkEreignisse.ereigniseintraege"
        :key="index"
      >
        <td data-test="textUhrzeit">{{ ereignis.uhrzeit }}</td>
        <td data-test="textBeschreibung">{{ ereignis.beschreibung }}</td>
      </tr>
    </tbody>
  </v-table>
</template>

<script setup lang="ts">
import { computed } from "vue";
import { VTable } from "vuetify/components";

import { useEreignisStore } from "@/stores/vorfaelleundvorkommnisseStore.ts";

const ereignisStore = useEreignisStore();
const wahlbezirkEreignisse = computed(() => ereignisStore.wahlbezirkEreignisse);
</script>
