<template>
  <v-list-item
    :to="to"
    :disabled="!listGroupActivator && disabled"
    :lines="subtitle ? false : 'one'"
  >
    <template #title>
      <div class="text-wrap">
        {{ splitTitle.part1 }}

        <br v-if="splitTitle.part2" >
        {{ splitTitle.part2 }}
      </div>
    </template>
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

import { computed } from "vue";

const props = defineProps<{
  title: string;
  to?: RouteLocationAsRelativeGeneric;
  disabled: boolean;
  subtitle?: string;
  isWorkflowStepFinished: boolean;
  listGroupActivator?: boolean;
}>();

const splitTitle = computed(() => {
  if (props.title === "Zählen der Stimmzettelumschläge") {
    return {
      part1: "Zählen der Stimmzettel-",
      part2: "umschläge",
    };
  }
  return {
    part1: props.title,
    part2: "",
  };
});
</script>
