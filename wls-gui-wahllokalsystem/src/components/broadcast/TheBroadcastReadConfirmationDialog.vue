<template>
  <v-dialog
    :model-value="hasBroadcastnachricht"
    max-width="400px"
    persistent
    data-test="dialog-show-broadcast-nachricht"
  >
    <v-card>
      <v-card-title>
        <v-icon
          icon="$information"
          size="x-small"
        ></v-icon>
        <span class="ml-2">Nachricht vom Wahlamt</span>
      </v-card-title>
      <v-card-text class="pb-0">
        <div class="ml-3">{{ currentBroadcastNachricht?.nachricht }}</div>
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
            data-test="button-ok"
            @click="onOkClicked"
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
  VCardActions,
  VCardText,
  VCardTitle,
  VCheckbox,
  VDialog,
  VIcon,
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
