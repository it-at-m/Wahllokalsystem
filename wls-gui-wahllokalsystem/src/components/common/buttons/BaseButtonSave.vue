<template>
  <v-btn
    prepend-icon="$save"
    :disabled="props.disabled"
    data-test="buttonSave"
    >Speichern</v-btn
  >
</template>

<script setup lang="ts">
const props = defineProps({
  /**
   * Is the Button interactable
   */
  disabled: {
    type: Boolean,
    default: false,
    required: false,
  },
});
</script>
