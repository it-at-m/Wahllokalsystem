<template>
  <v-card>
    <v-card-title>Wahlbriefe zulassen oder zurückweisen</v-card-title>
    <v-card-text>
      <the-bedenklicher-wahlbrief-row />
    </v-card-text>
    <v-card-actions>
      <v-btn
        prepend-icon="$add"
        @click="onAddBedenklicherWahlbriefClicked()"
      >
        Neuen Beschluss erfassen
      </v-btn>
      <base-button-save
        :loading="isSaving"
        :disabled="isSaveButtonDisabled"
        @click="onSaveClicked"
      />
    </v-card-actions>
  </v-card>
</template>
<script setup lang="ts">
import { storeToRefs } from "pinia";
import {
  VBtn,
  VCard,
  VCardActions,
  VCardText,
  VCardTitle,
} from "vuetify/components";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import TheBedenklicherWahlbriefRow from "@/components/wahlvorbereitung/TheBedenklicherWahlbriefRow.vue";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

const { wahlen } = storeToRefs(useWahlenStore());

const isSaveButtonDisabled = false; // todo
const isSaving = false; // todo

// add row + add empty item to beanstandeteWahlbriefeList
function onAddBedenklicherWahlbriefClicked() {
  wahlen.value!.map(
    (wahl) => wahl.beanstandeteWahlbriefe!.push("") // todo: muss leer sein beim befüllen
  );
}

function onSaveClicked() {
  // sendBeschluesse();
}
</script>
