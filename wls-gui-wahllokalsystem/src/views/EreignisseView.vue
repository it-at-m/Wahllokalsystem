<template>
  <v-card>
    <v-card-title>Dokumentation eingetretener Ereignisse</v-card-title>
    <v-card-text>
      <the-ereignisse-table />
    </v-card-text>
    <v-card-actions>
      <v-btn
        prepend-icon="$add"
        @click="onAddEreignisClicked()"
        >Ereignis hinzufügen</v-btn
      >
      <base-button-save @click="ereignisStore.sendEreignisse()" />
    </v-card-actions>
  </v-card>
</template>

<script setup lang="ts">
import {
  VBtn,
  VCard,
  VCardActions,
  VCardText,
  VCardTitle,
} from "vuetify/components";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import TheEreignisseTable from "@/components/vorfaelleundvorkommnisse/TheEreignisseTable.vue";
import { useEreignisStore } from "@/stores/vorfaelleundvorkommnisseStore.ts";

const ereignisStore = useEreignisStore();

function onAddEreignisClicked() {
  ereignisStore.addEreignis();
}
</script>
