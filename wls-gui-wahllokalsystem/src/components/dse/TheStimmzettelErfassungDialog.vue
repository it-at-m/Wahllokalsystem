<template>
  <v-dialog
    v-model="isDialogVisibleModel"
    persistent
    fullscreen
  >
    <v-card>
      <v-card-title>
        Erfassung Stimmzettel Nummer {{ currentUserTeamName }}
        {{ stimmzettelNummer }}
      </v-card-title>
      <v-card-text> In progress </v-card-text>
      <v-card-actions>
        <base-text-button @click="onCancelClicked">Abbrechen</base-text-button>
        <base-wls-button-save @click="onSavedClicked" />
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";

import BaseTextButton from "@/components/common/buttons/BaseTextButton.vue";
import BaseWlsButtonSave from "@/components/common/buttons/BaseWlsButtonSave.vue";
import { useUserStore } from "@/stores/userStore.ts";

const isDialogVisibleModel = defineModel("modelValue", {
  type: Boolean,
  required: false,
});

const props = defineProps({
  stimmzettelNummer: {
    type: Number,
    required: true,
  },
});

const emit = defineEmits<{
  cancel: [];
  confirm: [];
}>();

const { currentUserTeamName } = storeToRefs(useUserStore());

function onCancelClicked() {
  emit("cancel");
}

function onSavedClicked() {
  emit("confirm");
}
</script>

<style scoped></style>
