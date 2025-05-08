<template>
  <div>
    <v-card class="border-lg border-error">
      <v-card-title class="error-text"
        >Ungültige Zusammensetzung des Wahlvorstands</v-card-title
      >
      <v-card-text>
        <div class="d-flex align-center mb-2">
          <v-icon
            class="mr-2 error-text"
            icon="$invalid"
          />
          <div class="error-text">
            <div v-if="!isMindestanwesenheitErreicht">
              Vor der Wahlschliessung müssen mindestens 3 und nach der
              Schliessung mindestens 5 Wahlvorstandsmitglieder anwesend sein.
            </div>
            <div v-if="!isSchriftfuehrerAnwesend">
              Die Rolle Schriftführer*in muss besetzt sein.
            </div>
            <div v-if="!isWahlvorsteherAnwesend">
              Die Rolle Wahlvorsteher*in muss besetzt sein.
            </div>
          </div>
        </div>
        <div class="mt-6">
          Bitte wenden Sie sich bei fehlenden Mitgliedern oder getauschten
          Rollen an die Bezirksinspektion. Dort werden die Rollen im System
          richtig hinterlegt. Bis dahin bleiben Sie bitte auf dieser Seite.
        </div>
      </v-card-text>
    </v-card>
  </div>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";
import { VCard, VCardText, VCardTitle, VIcon } from "vuetify/components";

import { useWahlvorstandStore } from "@/stores/wahlvorstandStore";

const {
  isSchriftfuehrerAnwesend,
  isWahlvorsteherAnwesend,
  isMindestanwesenheitErreicht,
} = storeToRefs(useWahlvorstandStore());
</script>

<style scoped>
.error-text {
  color: rgb(var(--v-theme-error));
}
</style>
