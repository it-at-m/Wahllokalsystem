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
      <base-wls-button-save
        :active="false"
        :loading="isSaving"
        :disabled="isSaveButtonDisabled"
        :save-text="shouldNavigate ? SAVE_CONTINUE : 'Speichern'"
        @click="onSaveClicked"
      />
    </v-card-actions>
  </v-card>
</template>

<script setup lang="ts">
import type { Ref } from "vue";

import { storeToRefs } from "pinia";
import { computed, ref } from "vue";
import { useRoute } from "vue-router";

import BaseTextButton from "@/components/common/buttons/BaseTextButton.vue";
import BaseWlsButtonSave from "@/components/common/buttons/BaseWlsButtonSave.vue";
import TheEreignisseNoEventsCheckboxes from "@/components/vorfaelleundvorkommnisse/TheEreignisseNoEventsCheckboxes.vue";
import TheEreignisseRows from "@/components/vorfaelleundvorkommnisse/TheEreignisseRows.vue";
import { useNavigationUtils } from "@/composables/navigation/navigationUtils.ts";
import { CONTINUE_QUERY_PARAM, SAVE_CONTINUE } from "@/constants.ts";
import router from "@/plugins/router.ts";
import { useEreignisStore } from "@/stores/ereignisStore.ts";

const ereignisStore = useEreignisStore();
const {
  hasEintraege,
  isEreignisFlagsAndEreigniseintraegeInconsistent,
  isSaving,
} = storeToRefs(ereignisStore);
const { addEreignis, sendEreignisse } = ereignisStore;
const route = useRoute();
const { getNextRoute } = useNavigationUtils();

const ereignisseValidForm: Ref<null | boolean> = ref(null);

const shouldNavigate = computed(
  () => route.query[CONTINUE_QUERY_PARAM] !== undefined
);

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

async function onSaveClicked() {
  await sendEreignisse();
  if (shouldNavigate.value) {
    await router.push(getNextRoute());
  }
}
</script>
