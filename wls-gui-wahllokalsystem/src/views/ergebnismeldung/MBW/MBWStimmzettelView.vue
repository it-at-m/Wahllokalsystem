<template>
  <div>
    <v-skeleton-loader
      v-if="!isViewReady"
      type="card"
    />
    <template v-else>
      <the-stimmzettel-scores-card
        v-if="wahlvorschlaege"
        :wahlvorschlaege="wahlvorschlaege"
        :stimmzettel-snapshots="stimmzettelSnapshots"
        :is-saving-stimmzettel="isSavingStimmzettel"
        @snapshot-created="onStimmzettelSnapshotCreated"
      />
      <div v-else>Es gibt liegen keine Wahlvorschläge vor.</div>
    </template>
  </div>
</template>

<script setup lang="ts">
import type { StimmzettelSnapshot } from "@/types/experimental/StimmzettelSnapshot.ts";
import type { Wahlvorschlaege } from "@/types/wahlvorschlaege/Wahlvorschlaege.ts";
import type { Ref } from "vue";

import { computed, onMounted, ref } from "vue";
import { useRoute } from "vue-router";

import TheStimmzettelScoresCard from "@/components/experimental/TheStimmzettelScoresCard.vue";
import { useMBWStimmzettelViewUtils } from "@/composables/experimental/MBWStimmzettelViewUtils.ts";

const route = useRoute();
const wahlID = route.params.wahlId as string;
const wahlbezirkID = route.params.wahlbezirkId as string;

const {
  isLoadingStimmzettel,
  isLoadingWahlvorschlaege,
  isSavingStimmzettel,
  loadStimmzettel,
  loadWahlvorschlaege,
  saveStimmzettel,
} = useMBWStimmzettelViewUtils(wahlID, wahlbezirkID);

const wahlvorschlaege: Ref<Wahlvorschlaege | null> = ref(null);
const stimmzettelSnapshots: Ref<StimmzettelSnapshot[]> = ref([]);

const isViewReady = computed(
  () => !isLoadingWahlvorschlaege.value && !isLoadingStimmzettel.value
);

onMounted(async () => {
  wahlvorschlaege.value = await loadWahlvorschlaege();
  stimmzettelSnapshots.value = await loadStimmzettel();
});

function onStimmzettelSnapshotCreated(
  stimmzettelSnapshot: StimmzettelSnapshot
) {
  stimmzettelSnapshots.value.push(stimmzettelSnapshot);
  saveStimmzettel(stimmzettelSnapshots.value);
}
</script>
