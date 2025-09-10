<template>
  <v-btn
    prepend-icon="$save"
    :disabled="disabled"
    data-test="buttonSave"
    active
  >
    {{ saveText }}
  </v-btn>
</template>

<script setup lang="ts">
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
});
</script>
