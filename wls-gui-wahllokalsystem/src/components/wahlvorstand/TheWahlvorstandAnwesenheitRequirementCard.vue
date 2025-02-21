<template>
  <div>
    <v-card class="border-lg border-error">
      <v-card-title class="error-text"
        >Ungültige Zusammensetzung des Wahlvorstands</v-card-title
      >
      <v-card-text>
        <div class="error-text">
          TODO: Check Anzahl anwesende basieren auf Zustand (vor/nach 18
          Uhr/Schließung)
        </div>
        <div
          v-if="!wahlvorstandStore.isSchriftfuehrerAnwesend"
          class="error-text"
        >
          Die Rolle Schriftführer*in muss besetzt sein.
        </div>
        <div
          v-if="!wahlvorstandStore.isWahlvorsteherAnwesend"
          class="error-text"
        >
          Die Rolle Wahlvorsteher*in muss besetzt sein.
        </div>
      </v-card-text>
    </v-card>
  </div>
</template>

<script setup lang="ts">
import { VCard, VCardText, VCardTitle } from "vuetify/components";

import { useWahlvorstandStore } from "@/stores/wahlvorstandStore";

const wahlvorstandStore = useWahlvorstandStore();
</script>

<style scoped>
.error-text {
  color: rgb(var(--v-theme-error));
}
</style>
