<template>
  <v-list-item>
    <v-divider :thickness="2">
      <span class="text-medium-emphasis">{{ title }}</span>
    </v-divider>
  </v-list-item>
</template>

<script setup lang="ts">
defineProps<{
  title: string;
}>();
</script>
