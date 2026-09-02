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
            <base-div-item-with-scores
              :ordnungszahl="
                wahlvorschlag.ordnungszahl *
                WAHLVORSCHLAG_NUMBER_MULTIPLIER_FOR_ORDNUNGSZAHL
              "
              :name="wahlvorschlag.kurzname"
              :is-gestrichen="false"
              :ungueltige-stimmen="
                wahlvorschlag.kandidaten.reduce(
                  (sum, kandidat) => sum + (kandidat.ungueltigeStimmen ?? 0),
                  0
                )
              "
              :einzelstimmen="
                wahlvorschlag.kandidaten.reduce(
                  (sum, kandidat) => sum + (kandidat.einzelstimmen ?? 0),
                  0
                )
              "
              :reststimmen="
                wahlvorschlag.kandidaten.reduce(
                  (sum, kandidat) => sum + (kandidat.reststimmen ?? 0),
                  0
                )
              "
              ><v-checkbox
                density="compact"
                hide-details
                :model-value="wahlvorschlag.selected"
                :true-icon="mdiCloseBoxOutline"
                :ripple="false"
                readonly
              />
            </base-div-item-with-scores>
          </div>
        </div>
      </div>
    </v-card-title>

    <v-card-text class="pa-0">
      <div style="max-height: 600px; overflow-y: auto">
        <v-list>
          <v-list-item
            v-for="(kandidat, index) in kandidatenListe"
            :id="`kandidat-${index}`"
            :key="index"
            ref="listItems"
            v-ripple="
              kandidat.kandidatId === activeKandidat?.kandidatId &&
              kandidat.nennung === activeKandidat?.nennung
            "
            tabindex="-1"
          >
            <v-divider
              v-if="index !== 0"
              :variant="
                isDividerZwischenGleichemKandidat(index) ? 'dashed' : 'solid'
              "
            />
            <base-kandidat-list-item-content :kandidat="kandidat" />
          </v-list-item>
        </v-list>
      </div>
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import type { Kandidat } from "@/types/dse/stimmzettelerfassung/Kandidat.ts";
import type { Wahlvorschlag } from "@/types/dse/stimmzettelerfassung/Wahlvorschlag.ts";
import type { ComponentPublicInstance } from "vue";

import { mdiCloseBoxOutline } from "@mdi/js";
import { computed, nextTick, onActivated, ref, watch } from "vue";

import BaseDivItemWithScores from "@/components/dse/stimmzettelerfassung/baseComponents/BaseDivItemWithScores.vue";
import BaseKandidatListItemContent from "@/components/dse/stimmzettelerfassung/baseComponents/BaseKandidatListItemContent.vue";
import { useViewportUtils } from "@/composables/common/viewportUtils.ts";
import { WAHLVORSCHLAG_NUMBER_MULTIPLIER_FOR_ORDNUNGSZAHL } from "@/constants.ts";

const props = defineProps<{
  wahlvorschlag: Wahlvorschlag;
  activeKandidat?: Kandidat | null;
}>();

const kandidatenListe = computed(() => props.wahlvorschlag.kandidaten);

const listItems = ref<(ComponentPublicInstance | null)[]>([]);
const { scrollIntoView, triggerRippleEffect } = useViewportUtils();

//TODO refactor to function instead of constant
const isDividerZwischenGleichemKandidat = (index: number) => {
  if (index <= 0) return false;
  const prev = kandidatenListe.value[index - 1];
  const curr = kandidatenListe.value[index];
  if (!prev || !curr) return false;
  return prev.kandidatId === curr.kandidatId;
};

//TODO refactor to function instead of constant
const focusActive = async () => {
  const id = props.activeKandidat?.kandidatId;
  if (!id) return;

  const nennung = props.activeKandidat?.nennung;
  if (!nennung) return;

  const kandidaten = kandidatenListe.value;
  if (!kandidaten || kandidaten.length === 0) return;

  let lastIndex = -1;
  for (let i = 0; i < kandidaten.length; i++) {
    if (kandidaten[i].kandidatId === id && kandidaten[i].nennung === nennung)
      lastIndex = i;
  }
  if (lastIndex === -1) return;

  await nextTick();

  const item = listItems.value[lastIndex];

  if (item?.$el) {
    const selector = `#kandidat-${lastIndex}`;
    scrollIntoView(selector);
    triggerRippleEffect(item.$el);
  }
};

watch(
  [() => props.activeKandidat, kandidatenListe],
  () => {
    focusActive();
  },
  { immediate: true }
);

onActivated(() => {
  focusActive();
});
</script>
