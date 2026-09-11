<template>
  <v-card>
    <v-card-text class="overflow-y-auto">
      <base-stimmzettel-gueltigkeit-icon :gueltigkeit="gueltigkeit" />
      <span class="ml-2 font-weight-bold">{{ toText(gueltigkeit) }}</span>
      <v-divider
        class="my-2"
        thickness="2"
      />
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
      <div
        v-for="item in einzelstimmen"
        :key="item.label"
        class="d-flex justify-space-between align-baseline ga-1"
      >
        <div>{{ item.label }}</div>
        <div class="dots flex-grow-1" />
        <div>{{ item.value }}</div>
      </div>
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import type { Wahlvorschlag } from "@/types/dse/stimmzettelerfassung/Wahlvorschlag.ts";

import { computed } from "vue";

import BaseStimmzettelGueltigkeitIcon from "@/components/dse/BaseStimmzettelGueltigkeitIcon.vue";
import { useStimmzettelGueltigkeitEnumTools } from "@/composables/dse/stimmzettelerfassung/StimmzettelGueltigkeitEnumTools.ts";
import { StimmzettelGueltigkeitEnum } from "@/types/dse/persistedStimmzettel/StimmzettelGueltigkeitEnum.ts";

const props = defineProps<{
  listenstimmen: Wahlvorschlag[];
  ungueltigestimmen: number;
  direktstimmen: number;
  reststimmen: number;
  streichungen: number;
  gueltigkeit: StimmzettelGueltigkeitEnum;
}>();

const { toText } = useStimmzettelGueltigkeitEnumTools();

const gesamtstimmen = computed(
  () => props.ungueltigestimmen + props.direktstimmen + props.reststimmen
);

const einzelstimmen = computed(() => [
  { label: "Stimmen gesamt", value: gesamtstimmen.value },
  { label: "ungültige Stimmen", value: props.ungueltigestimmen },
  { label: "direkt vergebene Stimmen", value: props.direktstimmen },
  { label: "Reststimmen", value: props.reststimmen },
  { label: "Streichungen", value: props.streichungen },
]);
</script>

<style scoped>
.dots {
  text-align: center;
  border-top: 1px black dotted;
  height: 1px;
}
</style>
