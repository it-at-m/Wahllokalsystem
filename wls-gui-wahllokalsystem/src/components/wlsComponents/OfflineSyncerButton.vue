<template>
  <v-tooltip text="Offline-Data synchronisieren">
    <template #activator="{ props: tooltipActivator }">
      <v-btn
        v-bind="tooltipActivator"
        icon="$reload"
        class="px-0"
        size="x-small"
        color="primary"
        data-test="button-sync-offline-data"
        @click="$emit('click')"
      />
    </template>
  </v-tooltip>
</template>

<script setup lang="ts">
defineEmits<{
  click: [];
}>();
</script>
