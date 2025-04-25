<template>
  <v-card>
    <v-card-title>Wahlscheine und Uhrzeit Wahlende</v-card-title>
    <v-card-text class="text-center mt-2 pb-0">
      Bitte geben Sie hier die Uhrzeit ein, zu der die Wahl für geschlossen
      erklärt wurde.
      <v-form v-model="schliessungsuhrzeitValidForm">
        <div class="d-flex justify-center mt-2">
          <base-time-input :model-value="schliessungsuhrzeit" />
        </div>
      </v-form>
    </v-card-text>
    <v-card-actions class="justify-center mb-0">
      <!-- todo: wird noch nicht richtig aktiviert -->
      <base-button-save
        color="primary"
        :disabled="isSaveButtonDisabled"
        @click="
          wahlbezirkStore.sendSchliessungsuhrzeit(
            schliessungsuhrzeit.toISOString()
          )
        "
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
const schliessungsuhrzeit: Ref<Date> = ref(new Date());

const wahlbezirkStore = useWahlbezirkStore();
const isSaveButtonDisabled = computed(
  () => schliessungsuhrzeitValidForm.value !== true
);
</script>
