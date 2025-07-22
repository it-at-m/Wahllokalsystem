<template>
  <v-card>
    <v-card-title>Wahlscheine und Uhrzeit Wahlende</v-card-title>
    <v-card-text class="pb-0">
      Bitte geben Sie hier die Uhrzeit ein, zu der die Wahl für geschlossen
      erklärt wurde.
      <v-form v-model="schliessungsuhrzeitValidForm">
        <base-time-input
          v-model="schliessungsuhrzeit"
          class="mt-5"
          max-width="300"
          :rules="[
            REQUIRED,
            TIME_NOT_IN_FUTURE,
            TIME_GREATER_OR_EQUAL(fruehesteSchliessungsuhrzeit),
          ]"
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
import { useInfomanagementStore } from "@/stores/infomanagementStore.ts";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";
import {
  REQUIRED,
  TIME_GREATER_OR_EQUAL,
  TIME_NOT_IN_FUTURE,
} from "@/util/rules.ts";

const { sendSchliessungsuhrzeit } = useWahlbezirkStore();
const { schliessungsuhrzeit, schliessungsuhrzeitIsSaving } =
  storeToRefs(useWahlbezirkStore());
const { fruehesteSchliessungsuhrzeit } = storeToRefs(useInfomanagementStore());

const schliessungsuhrzeitValidForm = ref<null | boolean>(null);

const isSaveButtonDisabled = computed(
  () => schliessungsuhrzeitValidForm.value !== true
);

function onSaveSchliessungsuhrzeitClicked() {
  sendSchliessungsuhrzeit();
}
</script>
