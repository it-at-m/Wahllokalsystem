<template>
  <v-card>
    <v-row>
      <v-col
        cols="3"
        class="pa-0"
        v-for="(item, index) in configParameterItems"
        :key="index"
      >
        <v-card
          :subtitle="item.subtitle"
          height="200px"
        >
          <v-divider class="card-divider" />
          <v-card-text>
            {{ item.key }}
            <v-card-actions v-if="item.key === props.configParameter.wert">
              <v-btn
                icon="$edit"
                @click="onConfigParameterEditClicked"
                data-test="click-edit-button"
                class="ml-auto"
              />
            </v-card-actions>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-card>
</template>

<script setup lang="ts">
import type { InfomanagementConfigParameter } from "@/types/config/InfomanagementConfigParameter.ts";
import type { PropType } from "vue";

import { defineEmits, defineProps, ref } from "vue";
import {
  VBtn,
  VCard,
  VCardActions,
  VCardText,
  VCol,
  VDivider,
  VRow,
} from "vuetify/components";

const props = defineProps({
  configParameter: {
    type: Object as PropType<InfomanagementConfigParameter>,
    required: true,
  },
});

const configParameterItems = ref([
  { subtitle: "Konfigurationsparameter", key: props.configParameter.name },
  { subtitle: "Beschreibung", key: props.configParameter.beschreibung },
  { subtitle: "Wert", key: props.configParameter.wert },
  { subtitle: "Standardwert", key: props.configParameter.defaultValue },
]);

const emit = defineEmits<(event: "clickEdit", name: string) => void>();

function onConfigParameterEditClicked() {
  emit("clickEdit", props.configParameter.name);
}
</script>

<style scoped>
.card-divider {
  border: 1px solid #333333;
  display: block;
  opacity: 0.2;
}
</style>
