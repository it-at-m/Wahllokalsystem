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
        <v-btn
          v-if="canceltext"
          data-test="basedialog-btn-cancel"
          variant="text"
          @click="onCancelClicked"
        >
          {{ canceltext }}
        </v-btn>
        <v-btn
          data-test="basedialog-btn-confirm"
          active
          @click="onConfirmClicked"
        >
          {{ confirmtext }}
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import {
  VBtn,
  VCard,
  VCardActions,
  VCardText,
  VCardTitle,
  VDialog,
  VIcon,
  VSpacer,
} from "vuetify/components";

defineProps<{
  visible: boolean;
  dialogtitle: string;
  canceltext?: string;
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
