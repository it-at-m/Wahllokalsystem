<template>
  <v-card>
    <v-form v-model="isFormValid">
      <v-card-title>Ungültige Stimmzettel</v-card-title>
      <v-card-text>
        <base-number-input
          v-model="modelValue.ergebnis"
          :rules="[required]"
          min-width="20rem"
        />
        <v-divider class="my-2" />
        <v-row>
          <v-col cols="4">
            <div class="d-flex align-center ga-1">
              <v-icon icon="$add" />Anzahl ungültiger Stimmzettel nach Beschluss
            </div>
          </v-col>
          <v-col
            cols="1"
            class="d-flex align-center"
            >{{ ungueltigeStimmzettelNachBeschluss }}</v-col
          >
        </v-row>
        <v-row class="font-weight-bold">
          <v-col cols="4">
            <div class="d-flex align-center ga-1">
              <v-icon icon="$equal" />
              Summe ungültiger Stimmzettel
            </div></v-col
          >
          <v-col
            cols="1"
            class="d-flex align-center"
            >{{ summeUngueltigerStimmzettel }}</v-col
          >
        </v-row>
      </v-card-text>
      <v-card-actions>
        <base-wls-button-save
          :loading="isSaving"
          :disabled="isWahlFinished || !isFormValid"
          :save-text="SAVE_CONTINUE"
          @click="onSaveClicked"
        />
      </v-card-actions>
    </v-form>
  </v-card>
</template>

<script setup lang="ts">
import type { Ergebnis } from "@/types/ergebnismeldung/common/Ergebnis.ts";

import { computed, ref } from "vue";

import BaseWlsButtonSave from "@/components/common/buttons/BaseWlsButtonSave.vue";
import BaseNumberInput from "@/components/common/inputs/BaseNumberInput.vue";
import { useRules } from "@/composables/common/rules.ts";
import { SAVE_CONTINUE } from "@/constants.ts";

const { required } = useRules();

const isFormValid = ref<null | boolean>(null);

const modelValue = defineModel<Ergebnis>({ required: true });

const { ungueltigeStimmzettelNachBeschluss } = defineProps({
  isSaving: {
    type: Boolean,
    required: false,
    default: false,
  },
  isWahlFinished: {
    type: Boolean,
    required: true,
  },
  ungueltigeStimmzettelNachBeschluss: {
    type: Number,
    required: true,
  },
});

const emit = defineEmits<{
  save: [];
}>();

const summeUngueltigerStimmzettel = computed(
  () => ungueltigeStimmzettelNachBeschluss + (modelValue.value.ergebnis || 0)
);

function onSaveClicked() {
  emit("save");
}
</script>
