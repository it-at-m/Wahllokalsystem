<template>
  <v-dialog
    v-model="visible"
    data-test="dialog-configparameter-override-wert"
  >
    <v-confirm-edit
      :model-value="visible"
      hide-actions
    >
      <template v-slot:default>
        <v-card
          :title="`Konfigurationsparameter '${configParameter.name}' Bearbeiten`"
        >
          <template v-slot:text>
            <v-row class="my-2 align-center">
              <v-col cols="6">Standardwert:</v-col>
              <v-col cols="6">{{ configParameter?.defaultValue }}</v-col>
            </v-row>
            <v-text-field
              v-model="model"
              data-test="config-value-input"
              variant="underlined"
              label="Konfigurationsparameterwert"
              hint="Bitte Wert eingeben und Änderung übernehmen oder verwerfen"
            />
          </template>
          <template v-slot:actions>
            <base-button-cancel
              @click="onConfigParameterEditCanceled"
              data-test="cancel-edit-button"
            />
            <base-button-confirm
              :disabled="!isChanged"
              @click="onConfigParameterEditCommited"
              data-test="commit-edit-button"
            />
            <v-btn
              @click="resetToDefaultValue"
              class="ml-auto"
              data-test="reset-button"
              >Auf Standardwert Zurücksetzen
            </v-btn>
          </template>
        </v-card>
      </template>
    </v-confirm-edit>
  </v-dialog>
</template>

<script setup lang="ts">
import type { InfomanagementConfigParameter } from "@/types/config/InfomanagementConfigParameter.ts";
import type { PropType } from "vue";

import { computed, ref } from "vue";
import {
  VBtn,
  VCard,
  VCol,
  VConfirmEdit,
  VDialog,
  VRow,
  VTextField,
} from "vuetify/components";

import BaseButtonCancel from "@/components/common/BaseButtonCancel.vue";
import BaseButtonConfirm from "@/components/common/BaseButtonConfirm.vue";

const props = defineProps({
  configParameter: {
    type: Object as PropType<InfomanagementConfigParameter>,
    required: true,
  },
});

const model = ref(props.configParameter.wert);
const visible = ref(false);
const isChanged = computed(() => model.value !== props.configParameter.wert);

const emit = defineEmits<{
  cancelEdit: [];
  commitEdit: [wert: string];
}>();

defineExpose({
  showDialog() {
    visible.value = true;
  },
  hideDialog() {
    visible.value = false;
  },
  resetToDefaultValue,
});

function onConfigParameterEditCanceled() {
  emit("cancelEdit");
  resetModel();
  hideDialog();
}

function onConfigParameterEditCommited() {
  emit("commitEdit", `${model.value}`);
  hideDialog();
}

function resetToDefaultValue() {
  model.value = props.configParameter.defaultValue;
}

function hideDialog() {
  visible.value = false;
}

function resetModel() {
  model.value = props.configParameter.wert;
}
</script>
