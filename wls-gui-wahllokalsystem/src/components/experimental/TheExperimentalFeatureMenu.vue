<template>
  <v-menu>
    <template #activator="{ props: menuActivator }">
      <v-btn
        v-bind="menuActivator"
        icon="$experimentalFeature"
        variant="text"
        density="comfortable"
        size="x-large"
        color="white"
      />
    </template>
    <v-card>
      <v-card-title>Experimental Features</v-card-title>
      <v-card-text>
        <v-list>
          <v-list-item>
            <template #prepend>
              <v-switch
                v-model="hasStimmzettelSummaryItems"
                hide-details
              />
            </template>
            Daten bei Zusammenfassung vorhanden
          </v-list-item>
        </v-list>
      </v-card-text>
    </v-card>
  </v-menu>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";

import { useExperimentalFeaturesStore } from "@/stores/experimentalFeaturesStore.ts";

const { hasStimmzettelSummaryItems } = storeToRefs(
  useExperimentalFeaturesStore()
);
</script>
