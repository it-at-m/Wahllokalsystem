<template>
  <v-card>
    <v-card-title>Wahlbriefe zulassen oder zurückweisen</v-card-title>
    <v-card-text>
      <v-form v-model="bedenklicheWahlbriefeFormValid">
        <the-bedenklicher-wahlbrief-row />
      </v-form>
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
import { computed, ref } from "vue";
import {
  VBtn,
  VCard,
  VCardActions,
  VCardText,
  VCardTitle,
  VForm,
} from "vuetify/components";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import TheBedenklicherWahlbriefRow from "@/components/wahlvorbereitung/TheBedenklicherWahlbriefRow.vue";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

const { wahlen, isBeanstandeteWahlbriefeSaving } =
  storeToRefs(useWahlenStore());
const { saveBeanstandeteWahlbriefe } = useWahlenStore();

const bedenklicheWahlbriefeFormValid = ref<null | boolean>(null);

const isSaveButtonDisabled = computed(
  () => !bedenklicheWahlbriefeFormValid.value
);
const isSaving = computed(() => isBeanstandeteWahlbriefeSaving.value);

function onAddBedenklicherWahlbriefClicked() {
  if (wahlen.value) {
    wahlen.value.map((wahl) => wahl.beanstandeteWahlbriefe.push(null));
  }
}

function onSaveClicked() {
  saveBeanstandeteWahlbriefe();
}
</script>
