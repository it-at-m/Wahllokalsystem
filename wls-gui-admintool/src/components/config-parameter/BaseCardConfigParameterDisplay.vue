<template>
  <v-card
    class="card"
    :title="configParameter.name"
    :subtitle="configParameter?.beschreibung"
  >
    <v-divider class="card-divider" />
    <v-card-text class="card-content">
      <v-row>
          <v-card-actions class="card-action">
            <v-col cols="6" class="mx-10" >Wert:</v-col>
            <v-col cols="6" class="mx-2">{{ configParameter?.wert }}</v-col>
            <v-btn
                class="v-button"
                icon="$edit"
                @click="onConfigParameterEditClicked"
                data-test="click-edit-button"
            />
          </v-card-actions>
      </v-row>
      <v-row>
        <v-col cols="6" class="mx-5">   Standardwert: </v-col>
        <v-col cols="6" class="mx-2">'{{ configParameter?.defaultValue }}'</v-col>
      </v-row>
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import type { InfomanagementConfigParameter } from "@/types/config/InfomanagementConfigParameter.ts";
import type { PropType } from "vue";

import { defineEmits, defineProps } from "vue";
import {
  VBtn,
  VCard,
  VCardActions,
  VCardText,
  VDivider,
  VRow,
  VCol
} from "vuetify/components";

const props = defineProps({
  configParameter: {
    type: Object as PropType<InfomanagementConfigParameter>,
    required: true,
  },
});

const emit = defineEmits<(event: "clickEdit", name: string) => void>();

function onConfigParameterEditClicked() {
  emit("clickEdit", props.configParameter.name);
}
</script>

<style scoped>

.card-divider {
  border: 1px solid#333333;
  display: block;
  opacity: 0.2;
}

</style>
