<template>
  <v-card class="ma-1">
    <v-card-title>
      <div
        class="d-flex flex-column"
        style="width: 100%"
      >
        <div>
          <div>Wahlvorschlag Nr. {{ wahlvorschlag.ordnungszahl }}</div>
          <div class="text-subtitle-2">
            Partei {{ wahlvorschlag.kurzname }} -
            {{ wahlvorschlag.ordnungszahl }}
          </div>
        </div>

        <div class="d-flex align-center mt-3">
          <v-checkbox
            class="mb-1"
            density="compact"
            hide-details
            :model-value="wahlvorschlag.erhaeltStimmen"
            :true-icon="mdiCloseBoxOutline"
            :ripple="false"
            readonly
          />
          <div class="d-flex flex-column ml-auto align-end">
            <div class="d-flex">
              <base-chip-anzahl-stimmen
                :stimmen="wahlvorschlag.ungueltigeStimmen"
                color="error"
                :visible="!!wahlvorschlag.ungueltigeStimmen"
                class="mr-1"
              />
              <base-chip-anzahl-stimmen
                :stimmen="wahlvorschlag.gueltigeStimmen"
                color="success"
                :hidden="!wahlvorschlag.gueltigeStimmen"
              />
            </div>
          </div>
        </div>
      </div>
    </v-card-title>

    <v-card-text class="pa-0">
      <div style="max-height: 600px; overflow-y: auto">
        <v-list>
          <v-list-item
            v-for="(slot, index) in kandidatenListe"
            :key="index"
            ref="listItems"
            tabindex="-1"
          >
            <v-divider
              v-if="index !== 0"
              :variant="
                isDividerZwischenGleichemKandidat(index) ? 'dashed' : 'solid'
              "
            />
            <base-kandidat-list-item-content :kandidat="slot.anzeigeKandidat" />
          </v-list-item>
        </v-list>
      </div>
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import type { KandidatAnzeige } from "@/types/dse/KandidatAnzeige.ts";
import type { WahlvorschlagAnzeige } from "@/types/dse/WahlvorschlagAnzeige.ts";
import type { ComponentPublicInstance } from "vue";

import { mdiCloseBoxOutline } from "@mdi/js";
import { computed, nextTick, onActivated, ref, watch } from "vue";

import BaseChipAnzahlStimmen from "@/components/dse/BaseChipAnzahlStimmen.vue";
import BaseKandidatListItemContent from "@/components/dse/BaseKandidatListItemContent.vue";

const props = defineProps<{
  wahlvorschlag: WahlvorschlagAnzeige;
  activeKandidatId?: string | null;
}>();

interface Slot {
  anzeigeKandidat: KandidatAnzeige;
  slotIndex: number;
}

const kandidatenListe = computed<Slot[]>(() => {
  const kandidaten = props.wahlvorschlag?.kandidaten ?? [];
  if (kandidaten.length === 0) return [];

  const sortedKandidaten = [...kandidaten].sort((a, b) => {
    const diffListenposition = a.listenposition - b.listenposition;
    return diffListenposition === 0
      ? a.nennungsposition - b.nennungsposition
      : diffListenposition;
  });

  return sortedKandidaten.map((kandidat, idx) => ({
    anzeigeKandidat: kandidat,
    slotIndex: idx + 1,
    uniqueId: `${kandidat.identifikator}_${kandidat.nennungsposition}`,
  }));
});

const listItems = ref<(ComponentPublicInstance | null)[]>([]);

const isDividerZwischenGleichemKandidat = (index: number) => {
  if (index <= 0) return false;
  const prev = kandidatenListe.value[index - 1];
  const curr = kandidatenListe.value[index];
  if (!prev || !curr) return false;
  return (
    prev.anzeigeKandidat.identifikator === curr.anzeigeKandidat.identifikator
  );
};

const focusActive = async () => {
  const id = props.activeKandidatId;
  if (!id) return;

  const kandidaten = kandidatenListe.value;
  if (!kandidaten || kandidaten.length === 0) return;

  let lastIndex = -1;
  for (let i = 0; i < kandidaten.length; i++) {
    if (kandidaten[i].anzeigeKandidat.identifikator === id) lastIndex = i;
  }
  if (lastIndex === -1) return;

  await nextTick();

  const item = listItems.value[lastIndex];

  if (item?.$el) {
    // scroll into view inside the scrollable container if available
    if (typeof item.$el.scrollIntoView === "function") {
      try {
        item.$el.scrollIntoView({ block: "nearest", inline: "nearest" });
      } finally {
        //ignore
      }
    }
    if (typeof item.$el.focus === "function") {
      item.$el.focus();
    }
  }
};

watch(
  [() => props.activeKandidatId, kandidatenListe],
  () => {
    focusActive();
  },
  { immediate: true }
);

onActivated(() => {
  focusActive();
});
</script>
