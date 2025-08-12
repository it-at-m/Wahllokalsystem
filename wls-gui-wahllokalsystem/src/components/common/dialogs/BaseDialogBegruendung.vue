<template>
  <v-dialog
    :model-value="visible"
    persistent
    width="auto"
  >
    <v-card>
      <v-card-title>
        <v-icon
          class="mb-1"
          icon="$warning"
          color="warning"
          size="x-small"
        />
        <span class="ml-2">{{ dialogtitle }}</span>
      </v-card-title>
      <v-card-text>
        <div class="mb-3">
          <slot />
        </div>
        <v-textarea
          v-model="begruendung"
          :rules="[
            minLength(minLengthForBegruendung),
            maxLength(maxLengthForBegruendung),
          ]"
          rows="1"
          :label="label"
          auto-grow
          clearable
          autofocus
          persistent-counter
          :counter="maxLengthForBegruendung"
          data-test="basedialogbegruendung-textarea"
          @update:model-value="updateValidationState"
        />
      </v-card-text>
      <v-card-actions>
        <v-spacer />
        <v-btn
          data-test="basedialogbegruendung-btn-cancel"
          @click="onCancelClicked"
        >
          Eingaben ändern
        </v-btn>
        <base-button-save
          data-test="basedialogbegruendung-btn-confirm"
          active
          :disabled="!isBegruendungValid"
          @click="onConfirmClicked"
        />
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { ref } from "vue";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import { useRules } from "@/composables/common/rules.ts";

const { maxLength, minLength } = useRules();

defineProps<{
  visible: boolean;
  dialogtitle: string;
  label: string;
}>();

const begruendung = ref("");
const minLengthForBegruendung = 3;
const maxLengthForBegruendung = 500;
const isBegruendungValid = ref(false);

const emit = defineEmits<{
  cancel: [];
  confirm: [value: string];
}>();

function updateValidationState(): void {
  const value = begruendung.value;
  isBegruendungValid.value =
    value.length >= minLengthForBegruendung &&
    value.length <= maxLengthForBegruendung;
}

function onCancelClicked(): void {
  emit("cancel");
}
function onConfirmClicked(): void {
  if (isBegruendungValid.value) {
    emit("confirm", begruendung.value);
  }
}
</script>
