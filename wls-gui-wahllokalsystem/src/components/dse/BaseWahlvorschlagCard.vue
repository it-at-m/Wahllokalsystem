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
                v-if="anzahlgueltigeStimmenGesamt"
                size="small"
                color="error"
                variant="tonal"
              >
                {{ anzahlgueltigeStimmenGesamt }}
              </v-chip>
              <v-chip
                v-if="anzahlungueltigeStimmenGesamt"
                size="small"
                color="success"
                variant="tonal"
              >
                {{ anzahlungueltigeStimmenGesamt }}
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
          :key="slot.slotIndex"
          ref="listItems"
          tabindex="-1"
          :data-kandidat-id="slot.kandidat.identifikator"
        >
          <base-kandidat-list-item-content
            :id="slot.kandidat.identifikator"
            :name="slot.kandidat.name"
            :anzahl-stimmen="getRawStimmen()"
            :ungueltige-stimmen="getUngueltig()"
            :gueltige-stimmen="getGueltig()"
            :rest-stimmen-wahlvorschlag="getRest()"
            :durchgestrichen="false"
          />
          <v-divider v-if="index !== slots.length - 1" />
        </v-list-item>
      </v-list>
    </div>
  </v-card>
</template>

<script setup lang="ts">
import type { Kandidat } from "@/types/wahlvorschlaege/Kandidat.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";
import type { ComponentPublicInstance } from "vue";

import { mdiCloseBoxOutline } from "@mdi/js";
import { computed, nextTick, onActivated, ref, watch } from "vue";

import BaseKandidatListItemContent from "@/components/dse/BaseKandidatListItemContent.vue";

const props = defineProps<{
  wahlvorschlag: Wahlvorschlag;
  maximalErlaubteStimmenProWaehler: number;
  activeKandidatId?: string | null;
}>();

interface Slot {
  kandidat: Kandidat;
  slotIndex: number;
}

const slots = computed<Slot[]>(() => {
  const result: Slot[] = [];
  const sortedKandidaten = (props.wahlvorschlag.kandidaten ?? [])
    .slice()
    .sort((a, b) => a.listenposition - b.listenposition);

  for (const kandidat of sortedKandidaten) {
    const nennungen = Math.max(kandidat.anzahlNennungen, 1);
    for (let i = 0; i < nennungen; i++) {
      result.push({
        kandidat: kandidat,
        slotIndex: result.length + 1,
      });
    }
  }
  return result;
});

//Stimmen pro Wahlvorschlag
const anzahlgueltigeStimmenGesamt = ref(Math.random() * 5);
const anzahlungueltigeStimmenGesamt = ref(Math.random() * 2);

//Stimmen pro Kandidat TBD
const getRawStimmen = () => Math.floor(Math.random() * 3);
const getGueltig = () => Math.floor(Math.random() * 3);
const getUngueltig = () => Math.floor(Math.random() * 3);

const getRest = () => Math.floor(Math.random() * 3);

const listItems = ref<(ComponentPublicInstance | null)[]>([]);

const focusActive = async () => {
  const id = props.activeKandidatId;
  if (!id) return;

  const s = slots.value;
  if (!s || s.length === 0) return;

  // Focus auf die letzte Nennung des Kandidaten
  let lastIndex = -1;
  for (let i = 0; i < s.length; i++) {
    if (s[i].kandidat.identifikator === id) lastIndex = i;
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
