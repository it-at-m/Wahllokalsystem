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
          max-width="300"
          :rules="[
            REQUIRED,
            TIME_NOT_IN_FUTURE,
            TIME_GREATER_OR_EQUAL(FRUEHESTE_EROEFFNUNGSZEIT_UW_PARAM),
            TIME_LESS_OR_EQUAL(FRUEHESTE_SCHLIESSUNGSZEIT_UW_PARAM),
          ]"
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
import { useInfomanagementStore } from "@/stores/infomanagementStore.ts";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";
import {
  REQUIRED,
  TIME_GREATER_OR_EQUAL,
  TIME_LESS_OR_EQUAL,
  TIME_NOT_IN_FUTURE,
} from "@/util/rules.ts";

const wahlbezirkStore = useWahlbezirkStore();
const { eroeffnungsuhrzeit, eroeffnungsuhrzeitIsSaving } =
  storeToRefs(wahlbezirkStore);
const {
  FRUEHESTE_EROEFFNUNGSZEIT_UW_PARAM,
  FRUEHESTE_SCHLIESSUNGSZEIT_UW_PARAM,
} = storeToRefs(useInfomanagementStore());

const isEroeffnungsuhrzeitFormValid = ref<boolean | null>(null);

const isSaveButtonDisabled = computed(
  () => isEroeffnungsuhrzeitFormValid.value !== true
);

function onSaveEroeffnungsuhrzeitClicked() {
  wahlbezirkStore.sendEroeffnungsuhrzeit();
}
</script>
