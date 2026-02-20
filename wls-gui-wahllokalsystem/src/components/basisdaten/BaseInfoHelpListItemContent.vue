<template>
  <v-row align="center">
    <v-icon
      :icon="icon"
      color="primary"
      size="40"
      class="mr-2"
    />
    <v-col class="pa-2 text-subtitle-2">
      {{ title }}
      <p
        v-if="text"
        class="text-grey-darken-1 text-caption"
      >
        {{ text }}
      </p>
    </v-col>
  </v-row>
</template>
<script setup lang="ts">
defineProps({
  title: { type: String, required: true },
  text: { type: String, required: false, default: null },
  icon: { type: String, required: true },
});
</script>
