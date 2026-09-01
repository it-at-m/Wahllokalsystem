<template>
  <v-card>
    <v-card-title>Eingabehistorie</v-card-title>
    <v-card-text>
      <div v-if="firstHistoryItem">
        <div class="font-weight-bold d-flex align-center ga-1">
          <base-eingabehistorie-icon :input-type="firstHistoryItem.type" />
          <div>
            <div
              v-for="text in firstHistoryItem.text"
              :key="text"
            >
              {{ text }}
            </div>
          </div>
        </div>
      </div>
      <div v-else>Noch keine Stimmen erfasst</div>
      <div
        v-if="nextToFiveItems.length > 0"
        class="my-4"
      >
        <v-divider thickness="4" />
      </div>
      <template
        v-for="(item, index) in nextToFiveItems"
        :key="index"
      >
        <div class="d-flex align-center ga-1">
          <base-eingabehistorie-icon :input-type="item.type" />
          <div>
            <div
              v-for="text in item.text"
              :key="text"
            >
              {{ text }}
            </div>
          </div>
        </div>
        <div class="my-2">
          <v-divider thickness="1" />
        </div>
      </template>
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import type { InputHistoryItem } from "@/types/dse/stimmzettelerfassung/InputHistoryItem.ts";

import { computed } from "vue";

import BaseEingabehistorieIcon from "@/components/dse/stimmzettelerfassung/baseComponents/BaseEingabehistorieIcon.vue";

const props = defineProps<{
  changeHistory: InputHistoryItem[];
}>();

const firstHistoryItem = computed(() =>
  props.changeHistory.length > 0 ? props.changeHistory[0] : null
);
const nextToFiveItems = computed(() =>
  props.changeHistory.filter((_, index) => index < 5 && index > 0)
);
</script>
