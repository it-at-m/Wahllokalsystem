<template>
  <v-list-group value="MBW_Scores">
    <template #activator="{ props }">
      <v-list-item
        v-bind="props"
        title="🚧 Wahl des Migrationsbeirats"
      />
    </template>
    <v-list-item
      v-for="(route, index) in navigation"
      :key="index"
      :title="route.title"
      :to="
        routeWithNameAndParams(route.targetRouteName, {
          wahlId: wahlId,
          wahlbezirkId: wahlbezirkId,
        })
      "
      :disabled="route.disabled"
    />
  </v-list-group>
</template>

<script setup lang="ts">
import { useMbwNavigationService } from "@/composables/navigation/mbwNavigationService.ts";
import { useNavigationUtils } from "@/composables/navigation/navigationUtils.ts";

const { routeWithNameAndParams } = useNavigationUtils();

const { wahlbezirkId, wahlId } = defineProps<{
  wahlId: string;
  wahlbezirkId: string;
}>();

const { navigation } = useMbwNavigationService(wahlId, wahlbezirkId);
</script>
