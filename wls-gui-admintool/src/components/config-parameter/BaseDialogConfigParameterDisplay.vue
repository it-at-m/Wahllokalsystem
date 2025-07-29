<template>
  <base-card-config-parameter-display
      :config-parameter="configParameter"
      @click="showDialog"
  />
  <div ref="dialogRef" v-if="visible" >
    <v-confirm-edit
        :model-value="visible"
        persistent
        max-width="648px"
        data-test="dialog-configparameter-override-wert"
        update:model-Value="updateVisible"
        >
      <template v-slot:default="{actions}">
        <v-card
            max-width="600"
            style="background-color: #ffb74d"
            :title="`Dialog Konfigurationsparameter ${ configParameter.name }`"
        >
          <template v-slot:text>
            <v-row class="my-2 align-center">
              <v-col cols="6">Standardwert:</v-col>
              <v-col cols="6">{{ configParameter?.defaultValue }}</v-col>
            </v-row>
            <v-row class="my-2 align-center">
              <v-col cols="6">Wert:</v-col>
              <v-col cols="6">{{ configParameter?.wert }}
                <v-card-actions>
                  <v-btn
                      @click="resetValue"
                  >Zurücksetzen</v-btn>
                </v-card-actions>
              </v-col>
            </v-row>
            <v-spacer></v-spacer>
            <v-text-field
                v-model="model"
                @input="isChanged = true"
                messages="Bitte geben Sie einen Text in das Eingabefeld ein und bestätigen Sie, ob Sie den neuen Wert übernehmen wollen."
            />
          </template>
          <template v-slot:actions>
              <base-button-cancel
                  color="red"
                  :disabled="!isChanged"
                  @click="onConfigParameterEditCanceled"/>
              <base-button-confirm
                  :disabled="!isChanged"
                  @click="onConfigParameterEditCommited"/>

          </template>
        </v-card>
      </template>
    </v-confirm-edit>
  </div>
</template>

<script setup lang="ts">
import type { InfomanagementConfigParameter } from "@/types/config/InfomanagementConfigParameter.ts";
import {type PropType} from "vue";
import { shallowRef,ref } from 'vue'
import { defineEmits, defineProps } from "vue";
import {
  VCard, VCol,
  VConfirmEdit, VRow,
  VTextField,VBtn
} from "vuetify/components";
import BaseButtonCancel from "@/components/common/BaseButtonCancel.vue";
import BaseButtonConfirm from "@/components/common/BaseButtonConfirm.vue";
import BaseCardConfigParameterDisplay from "@/components/config-parameter/BaseCardConfigParameterDisplay.vue";

const props = defineProps({
  configParameter: {
    type: Object as PropType<InfomanagementConfigParameter>,
    required: true,
  },
});

const model = shallowRef(props.configParameter.wert)
const visible = ref(false)
const isChanged = ref(false);
const dialogRef = ref();

const emit = defineEmits<{
  cancelEdit: [wert: string];
  commitEdit: [wert: string];
}>();

defineExpose({
  showDialog,
  hideDialog,
  resetValue,
});

function onConfigParameterEditCanceled() {
  emit("cancelEdit", "Kein Payload, Keine Änderung");
  resetModel(); // Setze isChanged zurück
  hideDialog();
}

function onConfigParameterEditCommited() {
  emit("commitEdit", `Neue Daten für den Konfigurationsparameter ${props.configParameter.name} => '${model.value}'`);
  props.configParameter.wert = model.value;
  hideDialog(); // Schließe den Dialog
}

function resetValue() {
  model.value = props.configParameter.defaultValue; // Setze den Wert auf den Standardwert
  props.configParameter.wert = model.value; // Aktualisiere den Wert des Konfigurationsparameters
  isChanged.value = true; // Setze isChanged auf true, um den Zustand zu reflektieren
}

function showDialog() {
  visible.value = true;
}

function hideDialog() {
  visible.value = false;
}

function resetModel() {
  model.value = props.configParameter.wert; // Setze das Modell zurück
  isChanged.value = false; // Setze isChanged zurück
}

</script>
