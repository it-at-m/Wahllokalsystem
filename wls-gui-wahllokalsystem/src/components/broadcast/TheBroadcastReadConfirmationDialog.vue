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
        />
        <span class="ml-2">Nachricht vom Wahlamt</span>
      </v-card-title>
      <v-card-text class="pb-0">
        <div class="ml-3">{{ currentBroadcastNachricht?.nachricht }}</div>
        <v-checkbox
          v-model="isMessageMarkedAsRead"
          label="Nachricht gelesen"
          hide-details
          data-test="checkbox-mark-as-read"
        />
      </v-card-text>
      <v-card-actions>
        <div class="ml-2 mb-2">
          <base-text-button
            :disabled="!isMessageMarkedAsRead"
            active
            data-test="button-ok"
            @click="onOkClicked"
            >Ok</base-text-button
          >
        </div>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";
import { computed, ref } from "vue";

import BaseTextButton from "@/components/common/buttons/BaseTextButton.vue";
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
