<template>
  <base-text-button
    prepend-icon="$save"
    :disabled="areAllElectionsFinished || disabled"
    data-test="buttonSave"
    :active="active"
  >
    {{ saveText }}
  </base-text-button>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";

import BaseTextButton from "@/components/common/buttons/BaseTextButton.vue";
import { useWorkflowStore } from "@/stores/workflowStore.ts";

defineProps({
  /**
   * Is the Button interactable
   */
  disabled: {
    type: Boolean,
    default: false,
  },
  /**
   * Custom Save Text
   */
  saveText: {
    type: String,
    default: "Speichern",
  },
  /**
   * Is the Button active
   */
  active: {
    type: Boolean,
    default: true,
  },
});

const { areAllElectionsFinished } = storeToRefs(useWorkflowStore());
</script>
