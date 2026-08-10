<template>
  <v-card class="pa-3 ma-1">
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
              <v-chip
                v-if="wahlvorschlag.ungueltigeStimmen"
                size="small"
                color="error"
                variant="tonal"
                class="mr-1"
              >
                {{ wahlvorschlag.ungueltigeStimmen }}
              </v-chip>
              <v-chip
                v-if="wahlvorschlag.gueltigeStimmen"
                size="small"
                color="success"
                variant="tonal"
              >
                {{ wahlvorschlag.gueltigeStimmen }}
              </v-chip>
              <v-chip
                v-else
                size="small"
                color="success"
                variant="tonal"
                style="visibility: hidden"
                aria-hidden="true"
              >
                &nbsp;
              </v-chip>
            </div>
          </div>
        </div>
      </div>
    </v-card-title>

    <div class="d-flex flex-column gap-2">
      <v-list>
        <v-list-item
          v-for="(slot, index) in slots"
          :key="index"
          ref="listItems"
          tabindex="-1"
          :data-kandidat-id="slot.slotIndex"
        >
          <v-divider
            v-if="index !== 0"
            :variant="
              isDividerZwischenGleichemKandidat(index) ? 'dashed' : 'solid'
            "
          />
          <base-kandidat-list-item-content
            :id="slot.anzeigeKandidat.identifikator"
            :name="slot.anzeigeKandidat.name"
            :anzahl-stimmen="slot.anzeigeKandidat.gesamtStimmen"
            :ungueltige-stimmen="slot.anzeigeKandidat.ungueltigeStimmen"
            :gueltige-stimmen="slot.anzeigeKandidat.gueltigeStimmen"
            :rest-stimmen-wahlvorschlag="slot.anzeigeKandidat.restStimmen"
            :durchgestrichen="slot.anzeigeKandidat.durchgestrichen"
          />
        </v-list-item>
      </v-list>
    </div>
  </v-card>
</template>

<script setup lang="ts">
import type { KandidatAnzeige } from "@/types/dse/KandidatAnzeige.ts";
import type { WahlvorschlagAnzeige } from "@/types/dse/WahlvorschlagAnzeige.ts";
import type { ComponentPublicInstance } from "vue";

import { mdiCloseBoxOutline } from "@mdi/js";
import { computed, nextTick, onActivated, ref, watch } from "vue";

import BaseKandidatListItemContent from "@/components/dse/BaseKandidatListItemContent.vue";

const props = defineProps<{
  wahlvorschlag: WahlvorschlagAnzeige;
  maximalErlaubteStimmenProWaehler: number;
  activeKandidatId?: string | null;
}>();

interface Slot {
  anzeigeKandidat: KandidatAnzeige;
  slotIndex: number;
}

const slots = computed<Slot[]>(() => {
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
  const prev = slots.value[index - 1];
  const curr = slots.value[index];
  if (!prev || !curr) return false;
  return (
    prev.anzeigeKandidat.identifikator === curr.anzeigeKandidat.identifikator
  );
};

const focusActive = async () => {
  const id = props.activeKandidatId;
  if (!id) return;

  const s = slots.value;
  if (!s || s.length === 0) return;

  let lastIndex = -1;
  for (let i = 0; i < s.length; i++) {
    if (s[i].anzeigeKandidat.identifikator === id) lastIndex = i;
  }
  if (lastIndex === -1) return;

  await nextTick();

  const item = listItems.value[lastIndex];

  if (item?.$el && typeof item.$el.focus === "function") {
    item.$el.focus();
  }
};

watch(
  [() => props.activeKandidatId, slots],
  () => {
    focusActive();
  },
  { immediate: true }
);

onActivated(() => {
  focusActive();
});
</script>
