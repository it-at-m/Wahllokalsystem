<template>
  <v-card>
    <v-card-title>Stimmzettelerfassung</v-card-title>
    <v-card-text>
      <v-tabs v-model="tab">
        <v-tab value="1">Erfassung</v-tab>
        <v-tab value="2">Zusammenfassung</v-tab>
      </v-tabs>

      <v-tabs-window v-model="tab">
        <v-tabs-window-item value="1">
          <v-row>
            <v-col
              v-for="(wahlvorschlag, index) in wahlvorschlaege.wahlvorschlaege"
              :key="index"
            >
              <base-wahlvorschlag-scores-card :wahlvorschlag="wahlvorschlag" />
            </v-col>
          </v-row>
        </v-tabs-window-item>
        <v-tabs-window-item value="2">
          <the-stimmzettel-summary-card />
        </v-tabs-window-item>
      </v-tabs-window>
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import type { Ergebnis } from "@/types/ergebnismeldung/Ergebnis.ts";
import type { Wahlvorschlaege } from "@/types/wahlvorschlaege/Wahlvorschlaege.ts";
import type { PropType } from "vue";

import { ref } from "vue";

import BaseWahlvorschlagScoresCard from "@/components/experimental/BaseWahlvorschlagScoresCard.vue";
import TheStimmzettelSummaryCard from "@/components/experimental/TheStimmzettelSummaryCard.vue";
import { getStimmzettelManger } from "@/composables/experimental/stimmzettelManager.ts";

const props = defineProps({
  wahlvorschlaege: {
    type: Object as PropType<Wahlvorschlaege>,
    required: true,
  },
});

const ergebnisse = ref<Ergebnis[]>([]);
const tab = ref("1");

const stimmzettelManager = getStimmzettelManger({
  wahlId: "wahlId",
  wahlbezirkId: "wahlbezirkId",
});
stimmzettelManager.setWahlvorschlaege(props.wahlvorschlaege.wahlvorschlaege);
</script>

<style scoped></style>
