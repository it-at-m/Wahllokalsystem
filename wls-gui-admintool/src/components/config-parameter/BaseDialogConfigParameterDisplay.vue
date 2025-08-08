<template>
  <v-dialog
    v-model="visible"
    data-test="dialog-configparameter-override-wert"
  >
    <v-confirm-edit
      :model-value="model"
      hide-actions
    >
      <template v-slot:default="{ model: proxyModel, isPristine }">
        <v-card
          :title="`Konfigurationsparameter '${configParameter.name}' Bearbeiten`"
        >
          <template v-slot:text>
            <v-row class="my-2 align-center">
              <v-col cols="6">Standardwert:</v-col>
              <v-col cols="6">{{ configParameter?.defaultValue }}</v-col>
            </v-row>
            <v-text-field
              v-model="proxyModel.value"
              data-test="config-value-input"
              variant="underlined"
              label="Konfigurationsparameterwert"
              hint="Bitte Wert eingeben und Änderung übernehmen oder verwerfen"
            />
          </template>
          <template v-slot:actions>
            <base-button-cancel
              @click="onConfigParameterEditCanceled(proxyModel)"
              data-test="cancel-edit-button"
            />
            <base-button-confirm
              :disabled="isPristine"
              @click="onConfigParameterEditCommited(proxyModel)"
              data-test="commit-edit-button"
            />
            <v-btn
              @click="resetToDefaultValue(proxyModel)"
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

import { ref } from "vue";
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

const emit = defineEmits<{
  cancelEdit: [];
  commitEdit: [configParameter: InfomanagementConfigParameter];
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

function onConfigParameterEditCanceled(proxyModel: { value: string }) {
  emit("cancelEdit");
  resetModel(proxyModel);
  hideDialog();
}

function onConfigParameterEditCommited(proxyModel: { value: string }) {
  emit("commitEdit", { ...props.configParameter, wert: proxyModel.value });
  hideDialog();
}

function resetToDefaultValue(proxyModel: { value: string }) {
  proxyModel.value = props.configParameter.defaultValue ?? "";
}

function hideDialog() {
  visible.value = false;
}

function resetModel(proxyModel: { value: string }) {
  proxyModel.value = props.configParameter.wert ?? "";
}
</script>
