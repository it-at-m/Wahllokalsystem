<template>
  <v-dialog
    :model-value="hasBroadcastnachricht"
    max-width="400px"
    persistent
    data-test="dialog-show-broadcast-nachricht"
  >
    <v-card>
      <v-card-title>Nachricht vom Wahlamt</v-card-title>
      <v-card-text>
        <div>{{ currentBroadcastNachricht?.nachricht }}</div>
        <v-checkbox
          v-model="isMessageMarkedAsRead"
          label="Nachricht gelesen"
          hide-details
          data-test="checkbox-mark-as-read"
        ></v-checkbox>
      </v-card-text>
      <v-card-actions>
        <div class="ml-2 mb-2">
          <v-btn
            :disabled="!isMessageMarkedAsRead"
            active
            @click="onOkClicked"
            data-test="button-ok"
            >Ok</v-btn
          >
        </div>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";
import { computed, ref } from "vue";
import {
  VBtn,
  VCard,
  VCardText,
  VCardTitle,
  VCheckbox,
  VDialog,
} from "vuetify/components";

import { useBroadcastStore } from "@/stores/broadcastStore.ts";

const broadcastStore = useBroadcastStore();

const { currentBroadcastNachricht } = storeToRefs(useBroadcastStore());
const { markMessageAsReadAndLoadNextMessage } = broadcastStore;

const isMessageMarkedAsRead = ref(false);

const hasBroadcastnachricht = computed(
  () => currentBroadcastNachricht.value !== null
);

async function onOkClicked() {
  await markMessageAsReadAndLoadNextMessage();
  isMessageMarkedAsRead.value = false;
}
</script>

<style scoped></style>
