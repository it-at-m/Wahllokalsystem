<template>
  <v-dialog
      :model-value="visible"
      persistent
      max-width="648px"
      data-test="dialog-configparameter-override-wert"
      @update:model-value="updateVisible"
  >
    <v-card
    :title="`${configParameter.name} - Wertänderung`"
    :subtitle="configParameter.beschreibung">
      <v-card-text>
        <div data-test="div-confirm-information">
           Bitte geben Sie ein Text in das Eingabefeld ein und bestätigen Sie, ob Sie den neuen Wert übernehmen wollen.
        </div>
        <v-text-field
            v-model="configParameter.wert"
            :label="`Bitte geben Sie einen Wert für Konfigurationsparameter '${configParameter.name}' an`"
            class="mt-2"
            autofocus
            data-test="textfield-confirm-text"
        />
      </v-card-text>
      <v-card-actions>
        <base-button-cancel
            @click="onConfigParameterEditCanceled"
            data-test="cancel-edit-button"/>
        <base-button-confirm
            @click="onConfigParameterEditCommited"
            data-test="comit-edit-button"
        />
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import type { InfomanagementConfigParameter } from "@/types/config/InfomanagementConfigParameter.ts";
import {type PropType} from "vue";
import { defineEmits, defineProps } from "vue";
import {
  VCard,
  VCardActions,
  VCardText,
  VDialog,
  VTextField,
} from "vuetify/components";
import BaseButtonCancel from "@/components/common/BaseButtonCancel.vue";
import BaseButtonConfirm from "@/components/common/BaseButtonConfirm.vue";

const props = defineProps({
  configParameter: {
    type: Object as PropType<InfomanagementConfigParameter>,
    required: true,
  },
  visible: {
    type: Boolean,
    required: true,
  },
});


const emit = defineEmits<{
  cancelEdit: [wert: string];
  commitEdit: [wert: string];
  update: [boolean]
}>();


function onConfigParameterEditCanceled() {
  emit("cancelEdit", "Kein Payload, Keine Änderung");
  updateVisible(false);
}

function onConfigParameterEditCommited() {
  emit("commitEdit", `Neue Daten für den Konfigurationsparameter ${props.configParameter.name} => '${props.configParameter.wert}'`);
  updateVisible(false);
}

function updateVisible(value: boolean) {
  emit('update', value);
}

</script>
