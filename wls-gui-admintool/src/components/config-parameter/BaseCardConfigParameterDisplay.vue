<template>
  <v-card
    class="card"
    :v-bind="configParameter"
    :title="configParameter.name"
    :subtitle="configParameter?.beschreibung"
  >
    <hr >
    <v-card-text class="card-content">
      <v-card-actions class="card-action">
        <div class="card-wert">
          <div v-if="configParameter?.wert">{{ configParameter?.wert }}</div>
          <div v-else>{{ configParameter?.defaultValue }}</div>
        </div>
      </v-card-actions>
      <v-btn
        class="v-button"
        icon="$edit"
        @click="onConfirmEdit"
        data-test="confirm-edit-button"
      />
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import type { InfomanagementConfigParameter } from "@/types/config/InfomanagementConfigParameter.ts";
import type { PropType } from "vue";

import { defineEmits, defineProps } from "vue";
import { VBtn, VCard, VCardActions, VCardText } from "vuetify/components";

const props = defineProps({
  configParameter: {
    type: Object as PropType<InfomanagementConfigParameter>,
    required: true,
  },
});

const emit = defineEmits<{
  confirmEdit: InfomanagementConfigParameter; // confirmEdit erwartet ein configParameter Objekt
}>();

//Payload
function onConfirmEdit() {
  emit("confirmEdit", props.configParameter.name);
}
</script>

<style scoped>

.card:hover {
  background-color: #f1f1f1;
}

.card-content {
  font-size: 1.5rem;
  padding-left: 16px;
  margin: auto;
  display: block ruby;
}

hr {
  border-top: 1px #333333;
}

.v-button {
  color: white;
  background-color: #333;
  width: 30px;
  height: 30px;
  margin-bottom: 10px;
  font-size: 0.6em;
}

.v-button:hover {
  background-color: dimgrey;
}
</style>
