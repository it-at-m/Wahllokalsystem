<template>
  <div
    class="flex-1-1-0 d-flex flex-column"
    style="min-width: 0"
  >
    <v-slide-group
      show-arrows
      center-active
      :model-value="activeWahlvorschlagId"
      class="flex-1-1-0"
    >
      <template #prev="{ prev }">
        <v-icon
          color="primary"
          icon="$slideLeft"
          size="x-large"
          variant="outlined"
          @click="prev()"
        />
      </template>
      <template #next="{ next }">
        <v-icon
          color="primary"
          icon="$slideRight"
          size="x-large"
          variant="outlined"
          @click="next()"
        />
      </template>
      <v-slide-group-item
        v-for="wv in renderedWahlvorschlaege"
        :key="wv.wahlvorschlagID"
        :value="wv.wahlvorschlagID"
      >
        <base-wahlvorschlag-card
          :wahlvorschlag="wv"
          :active-kandidat="activeKandidat"
          style="min-width: 0"
        />
      </v-slide-group-item>
    </v-slide-group>
    <div
      v-if="isPreloadingWahlvorschlaege"
      class="px-4 pb-4"
    >
      <v-progress-linear
        :model-value="preloadProgress"
        color="primary"
      />
      <span class="text-caption">
        Stimmzettel wird vorbereitet (Wahlvorschlag
        {{ renderedWahlvorschlaege.length }} von {{ wahlvorschlaege.length }})
      </span>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { DseKandidat } from "@/types/dse/stimmzettelerfassung/DseKandidat.ts";
import type { DseWahlvorschlag } from "@/types/dse/stimmzettelerfassung/DseWahlvorschlag.ts";

import { computed, nextTick, onBeforeUnmount, onMounted, ref } from "vue";

import BaseWahlvorschlagCard from "./baseComponents/BaseWahlvorschlagCard.vue";

const props = defineProps<{
  activeWahlvorschlagId: string | null;
  activeKandidat: DseKandidat | null;
  wahlvorschlaege: DseWahlvorschlag[];
}>();

const renderedWahlvorschlaegeCount = ref(0);
const renderedWahlvorschlaege = computed(() =>
  props.wahlvorschlaege.slice(0, renderedWahlvorschlaegeCount.value)
);
const isPreloadingWahlvorschlaege = computed(
  () => renderedWahlvorschlaegeCount.value < props.wahlvorschlaege.length
);
const preloadProgress = computed(() =>
  props.wahlvorschlaege.length === 0
    ? 100
    : (renderedWahlvorschlaegeCount.value / props.wahlvorschlaege.length) * 100
);

let idleCallbackId: number | undefined;
let preloadTimeoutId: number | undefined;

onMounted(() => scheduleNextWahlvorschlag());

onBeforeUnmount(() => {
  if (idleCallbackId !== undefined) {
    window.cancelIdleCallback(idleCallbackId);
  }
  if (preloadTimeoutId !== undefined) {
    window.clearTimeout(preloadTimeoutId);
  }
});

async function scheduleNextWahlvorschlag() {
  if (!isPreloadingWahlvorschlaege.value) {
    return;
  }

  if (window.requestIdleCallback) {
    idleCallbackId = window.requestIdleCallback(preloadNextWahlvorschlag, {
      timeout: 1_000,
    });
  } else {
    preloadTimeoutId = window.setTimeout(preloadNextWahlvorschlag);
  }
}

async function preloadNextWahlvorschlag() {
  renderedWahlvorschlaegeCount.value += 1;
  await nextTick();
  scheduleNextWahlvorschlag();
}
</script>
