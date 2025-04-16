<template>
  <div>
    <v-card class="border-lg border-error">
      <v-card-title class="error-text"
        >Ungültige Zusammensetzung des Wahlvorstands</v-card-title
      >
      <v-card-text>
        <div class="d-flex align-center mb-2">
          <v-icon
            class="mr-2"
            icon="$invalid"
            color="red"
          />
          <div class="error-text">
            <div>
              TODO: Check Anzahl Anwesende, basierend auf Zustand (vor/nach 18
              Uhr/Schließung)
            </div>
            <div v-if="!wahlvorstandStore.isSchriftfuehrerAnwesend">
              Die Rolle Schriftführer*in muss besetzt sein.
            </div>
            <div v-if="!wahlvorstandStore.isWahlvorsteherAnwesend">
              Die Rolle Wahlvorsteher*in muss besetzt sein.
            </div>
          </div>
        </div>
        <div class="v-spacer">
          Bitte wenden Sie sich bei fehlenden Mitgliedern oder getauschten
          Rollen an die Bezirksinspektion. Dort werden die Rollen im System
          richtig hinterlegt. Bis dahin bleiben Sie bitte auf dieser Seite.
        </div>
      </v-card-text>
    </v-card>
  </div>
</template>

<script setup lang="ts">
import { VCard, VCardText, VCardTitle, VIcon } from "vuetify/components";

import { useWahlvorstandStore } from "@/stores/wahlvorstandStore";

const wahlvorstandStore = useWahlvorstandStore();
</script>

<style scoped>
.error-text {
  color: rgb(var(--v-theme-error));
  display: flex;
  flex-direction: column;
}
.v-spacer {
  margin-top: 32px;
}
</style>
