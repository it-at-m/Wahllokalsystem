<template>
  <v-card>
    <v-card-title>Zusammenfassung</v-card-title>
    <v-card-text>
      <div class="font-weight-bold mb-2">Listenstimmen</div>
      <div v-if="listenstimmen.length == 0">Keine Listenkreuze gesetzt</div>
      <div v-else>
        <div
          v-for="stimme in listenstimmen"
          :key="stimme.ordnungszahl"
        >
          {{ stimme.ordnungszahl }} - {{ stimme.kurzname }}
        </div>
      </div>
      <v-divider
        class="mb-2 mt-2"
        :thickness="2"
      />
      <div class="font-weight-bold mb-2">Einzelstimmen</div>
      <v-row
        v-for="item in einzelstimmen"
        :key="item.label"
        dense
      >
        <v-col>{{ item.label }}</v-col>
        <v-col>{{ item.value }}</v-col>
      </v-row>
      <v-divider
        class="mb-2 mt-2"
        :thickness="2"
      />
      <v-icon
        :icon="gueltigkeitIconMap[gueltigkeit]"
        :color="gueltigkeitColorMap[gueltigkeit]"
      />
      <span class="ml-2 font-weight-bold">{{
        gueltigkeitTextMap[gueltigkeit]
      }}</span>
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";

import { computed } from "vue";

import { StimmzettelGueltigkeitEnum } from "@/types/dse/persistedStimmzettel/StimmzettelGueltigkeitEnum.ts";

const props = defineProps<{
  listenstimmen: Wahlvorschlag[];
  gesamtstimmen: number;
  ungueltigestimmen: number;
  direktstimmen: number;
  reststimmen: number;
  streichungen: number;
  gueltigkeit: StimmzettelGueltigkeitEnum;
}>();

const einzelstimmen = computed(() => [
  { label: "Stimmen gesamt", value: props.gesamtstimmen },
  { label: "ungültige Stimmen", value: props.ungueltigestimmen },
  { label: "direkt vergebene Stimmen", value: props.direktstimmen },
  { label: "Reststimmen", value: props.reststimmen },
  { label: "Streichungen", value: props.streichungen },
]);

const gueltigkeitIconMap = {
  [StimmzettelGueltigkeitEnum.Valid]: "$stimmzettelGueltig",
  [StimmzettelGueltigkeitEnum.Invalid]: "$stimmzettelUngueltig",
  [StimmzettelGueltigkeitEnum.BeschlussAusstehend]: "$stimmzettelBeschluss",
  [StimmzettelGueltigkeitEnum.BwbPseudoStimmzettelLeererUmschlag]:
    "$stimmzettelUngueltig",
  [StimmzettelGueltigkeitEnum.Leer]: "$stimmzettelUngueltig",
};

const gueltigkeitColorMap = {
  [StimmzettelGueltigkeitEnum.Valid]: "success",
  [StimmzettelGueltigkeitEnum.Invalid]: "error",
  [StimmzettelGueltigkeitEnum.BeschlussAusstehend]: "info",
  [StimmzettelGueltigkeitEnum.BwbPseudoStimmzettelLeererUmschlag]: "error",
  [StimmzettelGueltigkeitEnum.Leer]: "error",
};

const gueltigkeitTextMap = {
  [StimmzettelGueltigkeitEnum.Valid]: "Stimmzettel ist gültig",
  [StimmzettelGueltigkeitEnum.Invalid]: "Stimmzettel ist ungültig",
  [StimmzettelGueltigkeitEnum.BeschlussAusstehend]:
    "Stimmzettel ist für Beschluss vorgemerkt",
  [StimmzettelGueltigkeitEnum.BwbPseudoStimmzettelLeererUmschlag]:
    "Stimmzettel ist ungültig",
  [StimmzettelGueltigkeitEnum.Leer]: "Stimmzettel ist ungültig",
};
</script>
