<template>
  <v-list-item
    :title="title"
    :to="to"
    :disabled="!listGroupActivator && disabled"
    :lines="subtitle ? false : 'one'"
  >
    <template
      v-if="subtitle"
      #subtitle
    >
      {{ subtitle }}
    </template>
    <template #prepend>
      <v-icon
        :icon="
          disabled ? '$disabled' : isWorkflowStepFinished ? '$valid' : '$edit'
        "
        size="small"
        :color="disabled ? '' : isWorkflowStepFinished ? 'success' : 'warning'"
      />
    </template>
  </v-list-item>
</template>
<script setup lang="ts">
import type { RouteLocationAsRelativeGeneric } from "vue-router";

defineProps<{
  title: string;
  to?: RouteLocationAsRelativeGeneric;
  disabled: boolean;
  subtitle?: string;
  isWorkflowStepFinished: boolean;
  listGroupActivator?: boolean;
}>();
</script>
