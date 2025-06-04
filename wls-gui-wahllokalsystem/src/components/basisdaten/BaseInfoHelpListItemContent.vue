<template>
  <v-row
    class="mx-0 my-1"
    align="center"
  >
    <v-icon
      :icon="icon"
      color="primary"
      size="40"
      class="mr-2"
    />
    <v-col class="pa-2 text-subtitle-2">
      {{ title }}
      <p class="text-grey-darken-1 text-caption">{{ text }}</p>
    </v-col>
  </v-row>
</template>
<script setup lang="ts">
import { VCol, VIcon, VRow } from "vuetify/components";

defineProps({
  title: String,
  text: String,
  icon: String,
});
</script>
