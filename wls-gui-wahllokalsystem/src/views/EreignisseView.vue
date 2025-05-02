<template>
  <v-card>
    <v-card-title>Dokumentation eingetretener Ereignisse</v-card-title>
    <v-card-text>
      <v-form v-model="ereignisseValidForm">
        <the-ereignisse-row />
      </v-form>
    </v-card-text>
    <v-card-actions>
      <v-btn
        prepend-icon="$add"
        @click="onAddEreignisClicked()"
        >Ereignis hinzufügen</v-btn
      >
      <base-button-save
        :disabled="isSaveButtonDisabled"
        @click="ereignisStore.sendEreignisse()"
      />
    </v-card-actions>
  </v-card>
</template>

<script setup lang="ts">
import type { Ref } from "vue";

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
import TheEreignisseRow from "@/components/vorfaelleundvorkommnisse/TheEreignisseRow.vue";
import { useEreignisStore } from "@/stores/ereignisStore.ts";

const ereignisStore = useEreignisStore();

const ereignisseValidForm: Ref<null | boolean> = ref(null);

const isSaveButtonDisabled = computed(() => ereignisseValidForm.value !== true);

function onAddEreignisClicked() {
  ereignisStore.addEreignis();
}
</script>
