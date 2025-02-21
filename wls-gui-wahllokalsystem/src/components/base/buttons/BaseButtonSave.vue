<template>
  <v-btn
    prepend-icon="$save"
    :disabled="props.disabled"
    >Speichern</v-btn
  >
</template>

<script setup lang="ts">
import { VBtn } from "vuetify/components";

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
