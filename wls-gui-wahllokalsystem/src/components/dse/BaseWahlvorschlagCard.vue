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
            :model-value="wahlvorschlag.selected"
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
            v-for="(kandidat, index) in kandidatenListe"
            :id="`kandidat-${index}`"
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
            <base-kandidat-list-item-content
              :kandidat="kandidat"
              :wahlvorschlag-nummer="wahlvorschlag.ordnungszahl"
            />
          </v-list-item>
        </v-list>
      </div>
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import type { Wahlvorschlag } from "@/types/dse/Wahlvorschlag.ts";
import type { ComponentPublicInstance } from "vue";

import { mdiCloseBoxOutline } from "@mdi/js";
import { computed, nextTick, onActivated, ref, watch } from "vue";

import BaseChipAnzahlStimmen from "@/components/dse/BaseChipAnzahlStimmen.vue";
import BaseKandidatListItemContent from "@/components/dse/BaseKandidatListItemContent.vue";
import { useViewportUtils } from "@/composables/common/viewportUtils.ts";

const props = defineProps<{
  wahlvorschlag: Wahlvorschlag;
  activeKandidatId?: string | null;
}>();

const kandidatenListe = computed(() => props.wahlvorschlag.kandidaten);

const listItems = ref<(ComponentPublicInstance | null)[]>([]);
const { scrollIntoView } = useViewportUtils();

const isDividerZwischenGleichemKandidat = (index: number) => {
  if (index <= 0) return false;
  const prev = kandidatenListe.value[index - 1];
  const curr = kandidatenListe.value[index];
  if (!prev || !curr) return false;
  return prev.kandidatId === curr.kandidatId;
};

const focusActive = async () => {
  const id = props.activeKandidatId;
  if (!id) return;

  const kandidaten = kandidatenListe.value;
  if (!kandidaten || kandidaten.length === 0) return;

  let lastIndex = -1;
  for (let i = 0; i < kandidaten.length; i++) {
    if (kandidaten[i].kandidatId === id) lastIndex = i;
  }
  if (lastIndex === -1) return;

  await nextTick();

  const item = listItems.value[lastIndex];

  if (item?.$el) {
    const selector = `#kandidat-${lastIndex}`;
    scrollIntoView(selector);

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
