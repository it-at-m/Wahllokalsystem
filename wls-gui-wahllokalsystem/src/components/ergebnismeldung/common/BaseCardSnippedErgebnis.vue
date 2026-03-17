<template>
  <v-card>
    <v-form v-model="isFormValid">
      <v-card-title>{{ snippedTitle }}</v-card-title>
      <v-card-text>
        <base-number-input
          v-model="modelValue.ergebnis"
          :min="minValue"
          :max="maxValue"
          :rules="[required]"
          min-width="20rem"
        />
      </v-card-text>
      <v-card-actions>
        <base-button-save
          :loading="isErgebnisSaving"
          :disabled="!isFormValid"
          save-text="Speichern und Weiter"
          @click="onSaveClicked"
        />
      </v-card-actions>
    </v-form>
  </v-card>
</template>

<script setup lang="ts">
import type { Ergebnis } from "@/types/ergebnismeldung/common/Ergebnis.ts";

import { ref } from "vue";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import BaseNumberInput from "@/components/common/inputs/BaseNumberInput.vue";
import { useRules } from "@/composables/common/rules.ts";

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
    default: 0,
  },
  maxValue: {
    type: Number,
    required: false,
    default: 9999,
  },
  isErgebnisSaving: {
    type: Boolean,
    required: false,
    default: false,
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
