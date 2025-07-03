<template>
  <v-card>
    <v-card-title>Wahlbriefe zulassen oder zurückweisen</v-card-title>
    <v-card-text>
      <!-- bedenklicher wahlbrief row -->
    </v-card-text>
    <v-card-actions>
      <v-btn
        prepend-icon="$add"
        @click="onAddBedenklicherWahlbriefClicked()"
      >
        Beschluss erfassen
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
import {
  VBtn,
  VCard,
  VCardActions,
  VCardText,
  VCardTitle,
} from "vuetify/components";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";

const isSaveButtonDisabled = false; // todo
const isSaving = false; // todo

function onAddBedenklicherWahlbriefClicked() {
  // addBeschluss();
}

function onSaveClicked() {
  // sendBeschluesse();
}
</script>
