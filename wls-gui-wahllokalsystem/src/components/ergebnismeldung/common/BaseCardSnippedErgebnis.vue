<template>
  <v-card>
    <v-form v-model="isFormValid">
      <v-card-title>{{ snippedTitle }}</v-card-title>
      <v-card-text>
        <base-number-input
          v-model="modelValue.ergebnis"
          :min-valid="minValue"
          :max-valid="maxValue"
          :rules="[required]"
          min-width="20rem"
        />
      </v-card-text>
      <v-card-actions>
        <base-wls-button-save
          :loading="isErgebnisSaving"
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

import { ref } from "vue";

import BaseWlsButtonSave from "@/components/common/buttons/BaseWlsButtonSave.vue";
import BaseNumberInput from "@/components/common/inputs/BaseNumberInput.vue";
import { useRules } from "@/composables/common/rules.ts";
import {
  NUMBER_INPUT_DEFAULT_MAX,
  NUMBER_INPUT_DEFAULT_MIN,
  SAVE_CONTINUE,
} from "@/constants.ts";

const { required } = useRules();

const modelValue = defineModel<Ergebnis>({ required: true });

defineProps({
  snippedTitle: {
    type: String,
    required: true,
  },
  minValue: {
    type: Number,
    required: false,
    default: NUMBER_INPUT_DEFAULT_MIN,
  },
  maxValue: {
    type: Number,
    required: false,
    default: NUMBER_INPUT_DEFAULT_MAX,
  },
  isErgebnisSaving: {
    type: Boolean,
    required: false,
    default: false,
  },
  isWahlFinished: {
    type: Boolean,
    required: true,
  },
});

const emit = defineEmits<{
  save: [];
}>();

const isFormValid = ref<null | boolean>(null);

function onSaveClicked() {
  emit("save");
}
</script>
