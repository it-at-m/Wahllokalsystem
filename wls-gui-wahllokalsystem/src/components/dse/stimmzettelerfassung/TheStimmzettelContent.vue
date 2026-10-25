<template>
  <div
    class="flex-1-1-0 d-flex flex-column"
    style="min-width: 0; min-height: 0"
  >
    <div class="wahlvorschlaege-container flex-1-1-0 ga-2">
      <base-wahlvorschlag-card
        v-for="wv in renderedWahlvorschlaege"
        :key="wv.wahlvorschlagID"
        :id="`wahlvorschlag-${wv.wahlvorschlagID}`"
        :wahlvorschlag="wv"
        :active-kandidat="activeKandidat"
        class="wahlvorschlag-item h-100"
        style="min-width: 0; min-height: 0"
      />
    </div>
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

import {
  computed,
  nextTick,
  onBeforeUnmount,
  onMounted,
  ref,
  watch,
} from "vue";

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

watch(
  [() => props.activeWahlvorschlagId, renderedWahlvorschlaegeCount],
  () => scrollToActiveWahlvorschlag(),
  { immediate: true }
);

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
  const startedAt = performance.now();
  await nextTick();
  console.debug(
    `nextTicket ${renderedWahlvorschlaegeCount.value}: ${performance.now() - startedAt} ms`
  );
  scheduleNextWahlvorschlag();
}

async function scrollToActiveWahlvorschlag() {
  if (!props.activeWahlvorschlagId) {
    return;
  }

  const el = document.getElementById(
    `wahlvorschlag-${props.activeWahlvorschlagId}`
  );
  if (!el) {
    return;
  }

  await nextTick();
  el.scrollIntoView({
    behavior: "smooth",
    block: "nearest",
    inline: "nearest",
  });
}
</script>

<style scoped>
.wahlvorschlaege-container {
  display: flex;
  overflow-x: auto;
  scroll-snap-type: x proximity;
}

.wahlvorschlag-item {
  flex: 0 0 auto;
  min-width: 0;
  scroll-snap-align: start;
}
</style>
