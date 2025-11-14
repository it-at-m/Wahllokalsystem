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
          autofocus
          persistent-counter
          :counter="maxLengthForBegruendung"
          data-test="basedialogbegruendung-textarea"
          @update:model-value="updateValidationState"
        />
      </v-card-text>
      <v-card-actions>
        <v-spacer />
        <base-text-button
          data-test="basedialogbegruendung-btn-cancel"
          @click="onCancelClicked"
        >
          Eingaben ändern
        </base-text-button>
        <base-button-save
          data-test="basedialogbegruendung-btn-confirm"
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
import BaseTextButton from "@/components/common/buttons/BaseTextButton.vue";
import { useRules } from "@/composables/common/rules.ts";
import { MAX_LENGTH_FOR_TEXT_INPUT } from "@/constants.ts";

const { maxLength, minLength } = useRules();

interface Props {
  visible: boolean;
  dialogtitle: string;
  label: string;
  maxLengthForBegruendung?: number;
}

const props = withDefaults(defineProps<Props>(), {
  maxLengthForBegruendung: MAX_LENGTH_FOR_TEXT_INPUT,
});

const begruendung = ref("");
const minLengthForBegruendung = 3;
const isBegruendungValid = ref(false);

const emit = defineEmits<{
  cancel: [];
  confirm: [value: string];
}>();

function updateValidationState(): void {
  const value = begruendung.value;
  isBegruendungValid.value =
    value.length >= minLengthForBegruendung &&
    value.length <= props.maxLengthForBegruendung;
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
