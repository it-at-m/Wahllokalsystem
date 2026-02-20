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
        <slot />
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
          :disabled="isSaveDisabled"
          :save-text="saveText"
          @click="onConfirmClicked"
        />
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import BaseTextButton from "@/components/common/buttons/BaseTextButton.vue";

defineProps({
  visible: {
    type: Boolean,
    required: true,
  },
  dialogtitle: {
    type: String,
    required: true,
  },
  isSaveDisabled: {
    type: Boolean,
    required: false,
    default: false,
  },
  saveText: {
    type: String,
    default: "Speichern",
  },
});

const emit = defineEmits<{
  cancel: [];
  confirm: [];
}>();

function onCancelClicked(): void {
  emit("cancel");
}
function onConfirmClicked(): void {
  emit("confirm");
}
</script>
