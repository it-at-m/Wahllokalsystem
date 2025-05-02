<template>
  <v-card>
    <v-card-title>Wahlscheine und Uhrzeit Wahlende</v-card-title>
    <v-card-text class="pb-0">
      Bitte geben Sie hier die Uhrzeit ein, zu der die Wahl für geschlossen
      erklärt wurde.
      <v-form v-model="schliessungsuhrzeitValidForm">
        <base-time-input v-model="schliessungsuhrzeit" />
      </v-form>
    </v-card-text>
    <v-card-actions>
      <base-button-save
        active
        :disabled="isSaveButtonDisabled"
        @click="onSaveSchliessungsuhrzeitClicked"
      />
    </v-card-actions>
  </v-card>
</template>

<script setup lang="ts">
import type { Ref } from "vue";

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

const schliessungsuhrzeitValidForm: Ref<null | boolean> = ref(null);
const schliessungsuhrzeit: Ref<Date | undefined> = ref(undefined);

const wahlbezirkStore = useWahlbezirkStore();
const isSaveButtonDisabled = computed(
  () => schliessungsuhrzeitValidForm.value !== true
);

function onSaveSchliessungsuhrzeitClicked() {
  if (schliessungsuhrzeit.value) {
    wahlbezirkStore.sendSchliessungsuhrzeit(
      schliessungsuhrzeit.value.toISOString()
    );
  }
}
</script>
