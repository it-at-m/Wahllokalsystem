<template>
  <v-btn
    prepend-icon="$printer"
    @click="onNachbesetzungDruckenClicked"
    :disabled="props.disabled"
  >
    Nachbesetzung drucken
  </v-btn>
</template>

<script setup lang="ts">
import { VBtn } from "vuetify/components";

const props = defineProps({
  disabled: {
    type: Boolean,
    default: false,
    required: false,
  },
});

function onNachbesetzungDruckenClicked() {
  console.log("NachbesetzungDruckenClicked");
}
</script>
