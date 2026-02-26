<template>
  <v-list-group value="MBW_Scores">
    <template #activator="{ props }">
      <v-list-item
        v-bind="props"
        title="Wahl des Migrationsbeirats"
        :lines="groupActivatorListItemLines"
      >
        <template
          v-if="disabled"
          #subtitle
        >
          {{ disabledMessage }}
        </template>
      </v-list-item>
    </template>
    <v-list-item
      v-for="(route, index) in navigation"
      :key="index"
      :title="route.title"
      :to="route.targetRoute"
      :disabled="disabled || route.disabled"
    />
  </v-list-group>
</template>

<script setup lang="ts">
import { computed } from "vue";

import { useMbwNavigationService } from "@/composables/navigation/mbwNavigationService.ts";

const { wahlbezirkId, wahlId, disabled } = defineProps<{
  wahlId: string;
  wahlbezirkId: string;
  disabled: boolean;
  disabledMessage: string;
}>();

const { navigation } = useMbwNavigationService(wahlId, wahlbezirkId);
const groupActivatorListItemLines = computed(() => (disabled ? false : "one"));
</script>
