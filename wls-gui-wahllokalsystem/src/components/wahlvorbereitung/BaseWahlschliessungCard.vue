<template>
  <v-card>
    <v-card-title>Wahlscheine und Uhrzeit Wahlende</v-card-title>
    <v-card-text class="pb-0">
      Bitte geben Sie hier die Uhrzeit ein, zu der die Wahl für geschlossen
      erklärt wurde.
      <v-form v-model="schliessungsuhrzeitValidForm">
        <base-time-input
          v-model="schliessungsUhrzeit"
          class="mt-5"
          max-width="150"
        />
      </v-form>
    </v-card-text>
    <v-card-actions>
      <base-button-save
        active
        :loading="schliessungsuhrzeitIsSaving"
        :disabled="isSaveButtonDisabled"
        @click="onSaveSchliessungsuhrzeitClicked"
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

const { sendSchliessungsuhrzeit } = useWahlbezirkStore();
const { schliessungsUhrzeit, schliessungsuhrzeitIsSaving } =
  storeToRefs(useWahlbezirkStore());

const schliessungsuhrzeitValidForm = ref<null | boolean>(null);

const isSaveButtonDisabled = computed(
  () => schliessungsuhrzeitValidForm.value !== true
);

function onSaveSchliessungsuhrzeitClicked() {
  sendSchliessungsuhrzeit();
}
</script>
