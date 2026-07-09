<template>
  <div>
    <base-divider-list-item title="DSE" />
    <base-workflow-list-item
      v-for="(route, index) in navigation"
      :key="index"
      :title="route.title"
      :to="route.targetRoute"
      :disabled="false"
      :is-workflow-step-finished="false"
    />
  </div>
</template>
<script setup lang="ts">
import BaseDividerListItem from "@/components/navigation/common/BaseDividerListItem.vue";
import BaseWorkflowListItem from "@/components/navigation/common/BaseWorkflowListItem.vue";
import { useDseNavigationService } from "@/composables/navigation/dseNavigationService.ts";

const { wahlbezirkId, wahlId } = defineProps<{
  wahlId: string;
  wahlbezirkId: string;
}>();

const { navigation } = useDseNavigationService(wahlId, wahlbezirkId);
</script>
