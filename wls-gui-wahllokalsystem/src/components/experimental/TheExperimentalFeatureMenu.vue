<template>
  <v-menu :close-on-content-click="false">
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
          <v-list-item>
            <v-radio-group
              v-model="subViewBeschlussfassung"
              label="SubView Beschlussfassung"
            >
              <v-radio
                label="Übersicht"
                value="overview"
              />
              <v-radio
                label="Erfassung"
                value="erfassung"
              />
            </v-radio-group>
          </v-list-item>
          <v-list-item title="Subview Stimmzettelerfassung">
            <v-autocomplete
              v-model="subViewStimmzettelerfassung"
              :items="itemsSubviewStimmzettelerfassung"
            ></v-autocomplete>
          </v-list-item>
        </v-list>
      </v-card-text>
    </v-card>
  </v-menu>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";

import { useExperimentalFeaturesStore } from "@/stores/experimentalFeaturesStore.ts";

const {
  hasStimmzettelSummaryItems,
  subViewBeschlussfassung,
  subViewStimmzettelerfassung,
} = storeToRefs(useExperimentalFeaturesStore());

const subViewsBeschlussfassung = ["overview", "erfassung"];
const itemsSubviewStimmzettelerfassung = [
  {
    title: "Erfassung",
    value: "1",
  },
  {
    title: "Reduzierte Erfassung",
    value: "4",
  },
  {
    title: "Zusammenfassung",
    value: "2",
  },
  {
    title: "Gespeicherte Stimmzettel",
    value: "3",
  },
  {
    title: "Mocked Stimmzettelübersicht",
    value: "5",
  },
];
</script>
