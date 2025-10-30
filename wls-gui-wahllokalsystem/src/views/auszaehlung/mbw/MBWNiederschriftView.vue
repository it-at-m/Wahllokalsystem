<template>
  <v-card>
    <v-card-title>Niederschrift</v-card-title>
    <v-card-subtitle
      >Kontrolle, Übermittlung und Druck der Niederschrift</v-card-subtitle
    >
    <v-card-text>
      <v-form v-model="isFormValid">
        <!-- TBD: Platzhalter für Niederschrift-Komponenten -->
      </v-form>
    </v-card-text>
    <v-card-actions>
      <base-button-save
        save-text="Niederschrift senden"
        prepend-icon="$cloudUpload"
        :disabled="!isFormValid"
        @click="onSendenClicked"
      />
      <base-button-save
        save-text="Niederschrift korrigieren"
        prepend-icon="$edit"
        :disabled="!isFormValid"
        @click="onKorrigierenClicked"
      />
      <base-button-save
        save-text="Niederschrift drucken"
        prepend-icon="$printer"
        :disabled="!isFormValid"
        @click="onDruckenClicked"
      />
    </v-card-actions>
  </v-card>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useRoute, useRouter } from "vue-router";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import { EXAMPLE_ROUTES_NOTFOUND } from "@/constants.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

const route = useRoute();
const router = useRouter();
const { wahlenActions } = useWahlenStore();

const isFormValid = ref<null | boolean>(null);

const wahlID = route.params.wahlId as string;
const wahl = wahlenActions.getWahlOrUndefinedById(wahlID);

if (!wahl) {
  router.push({
    name: EXAMPLE_ROUTES_NOTFOUND,
  });
}

function onSendenClicked() {
  // to be implemented
}
function onKorrigierenClicked() {
  // to be implemented
}
function onDruckenClicked() {
  // to be implemented
}
</script>
