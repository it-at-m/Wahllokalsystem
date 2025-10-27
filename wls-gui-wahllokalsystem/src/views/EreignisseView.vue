<template>
  <v-card>
    <v-card-title>Eingetretene Ereignisse</v-card-title>
    <v-card-text>
      <the-ereignisse-no-events-checkboxes />
    </v-card-text>
    <v-card-title>Dokumentation eingetretener Ereignisse</v-card-title>
    <v-card-text>
      <v-form v-model="ereignisseValidForm">
        <the-ereignisse-rows />
      </v-form>
    </v-card-text>
    <v-card-actions>
      <base-text-button
        prepend-icon="$add"
        active
        @click="onAddEreignisClicked()"
        >Ereignis hinzufügen</base-text-button
      >
      <base-button-save
        :active="false"
        :loading="isSaving"
        :disabled="isSaveButtonDisabled"
        @click="onSaveClicked"
      />
    </v-card-actions>
  </v-card>
</template>

<script setup lang="ts">
import type { Ref } from "vue";

import { storeToRefs } from "pinia";
import { computed, ref } from "vue";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import BaseTextButton from "@/components/common/buttons/BaseTextButton.vue";
import TheEreignisseNoEventsCheckboxes from "@/components/vorfaelleundvorkommnisse/TheEreignisseNoEventsCheckboxes.vue";
import TheEreignisseRows from "@/components/vorfaelleundvorkommnisse/TheEreignisseRows.vue";
import { useEreignisStore } from "@/stores/ereignisStore.ts";

const ereignisStore = useEreignisStore();
const {
  hasEintraege,
  isEreignisFlagsAndEreigniseintraegeInconsistent,
  isSaving,
} = storeToRefs(ereignisStore);
const { addEreignis, sendEreignisse } = ereignisStore;

const ereignisseValidForm: Ref<null | boolean> = ref(null);

const isEreignisseFormInvalid = computed(
  () => ereignisseValidForm.value !== true
);
const isSaveButtonDisabled = computed(
  () =>
    (hasEintraege.value && isEreignisseFormInvalid.value) ||
    isEreignisFlagsAndEreigniseintraegeInconsistent.value
);

function onAddEreignisClicked() {
  addEreignis();
}

function onSaveClicked() {
  sendEreignisse();
}
</script>
