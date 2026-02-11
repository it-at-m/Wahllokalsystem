<template>
  <v-dialog
    :model-value="visible"
    persistent
    max-width="648px"
  >
    <v-card>
      <v-card-title>
        <v-icon
          :icon="icon"
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
          v-if="canceltext"
          data-test="basedialog-btn-cancel"
          :is-disabled="cancelDisabled"
          @click="onCancelClicked"
        >
          {{ canceltext }}
        </base-text-button>
        <base-text-button
          data-test="basedialog-btn-confirm"
          active
          @click="onConfirmClicked"
        >
          {{ confirmtext }}
        </base-text-button>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import BaseTextButton from "@/components/common/buttons/BaseTextButton.vue";

defineProps<{
  visible: boolean;
  dialogtitle: string;
  canceltext?: string;
  cancelDisabled?: boolean;
  confirmtext: string;
  icon: string;
}>();

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
