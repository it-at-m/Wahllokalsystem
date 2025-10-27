<template>
  <base-text-button
    prepend-icon="$save"
    :disabled="disabled"
    data-test="buttonSave"
    :active="active"
  >
    {{ saveText }}
  </base-text-button>
</template>

<script setup lang="ts">
import BaseTextButton from "@/components/common/buttons/BaseTextButton.vue";

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
</script>
