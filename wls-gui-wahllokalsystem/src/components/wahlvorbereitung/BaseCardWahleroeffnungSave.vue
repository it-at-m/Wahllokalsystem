<template>
  <v-card>
    <v-card-title>Beginn der Stimmabgabe</v-card-title>
    <v-card-text class="pb-0">
      Bitte geben Sie die Uhrzeit ein, zu der mit der Stimmabgabe begonnen
      wurde.
      <v-form v-model="isEroeffnungsuhrzeitFormValid">
        <base-time-input
          v-model="eroeffnungsuhrzeit"
          class="mt-5"
          max-width="150"
        />
      </v-form>
    </v-card-text>
    <v-card-actions>
      <base-button-save
        active
        :loading="eroeffnungsuhrzeitIsSaving"
        :disabled="isSaveButtonDisabled"
        @click="onSaveEroeffnungsuhrzeitClicked"
      />
    </v-card-actions>
  </v-card>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";
import { computed, ref } from "vue";
import {
  VCard,
  VCardActions,
  VCardText,
  VCardTitle,
  VForm,
} from "vuetify/components";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import BaseTimeInput from "@/components/common/inputs/BaseTimeInput.vue";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";

const wahlbezirkStore = useWahlbezirkStore();
const { eroeffnungsuhrzeit, eroeffnungsuhrzeitIsSaving } =
  storeToRefs(wahlbezirkStore);

const isEroeffnungsuhrzeitFormValid = ref<boolean | null>(null);

const isSaveButtonDisabled = computed(
  () => isEroeffnungsuhrzeitFormValid.value !== true
);

function onSaveEroeffnungsuhrzeitClicked() {
  wahlbezirkStore.sendEroeffnungsuhrzeit();
}
</script>
