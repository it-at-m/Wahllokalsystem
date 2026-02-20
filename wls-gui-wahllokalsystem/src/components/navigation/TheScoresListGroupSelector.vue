<template>
  <div v-if="wahlbezirkIdForWahl">
    <the-o-b-w-scores-list-group
      v-if="wahl.wahlart === WahlWahlartEnum.Obw"
      :title-stimmen-zaehlen="titleStimmenZaehlen"
      :wahl-id="wahl.wahlID"
      :wahlbezirk-id="wahlbezirkIdForWahl"
    />
    <the-s-r-w-scores-list-group
      v-if="wahl.wahlart === WahlWahlartEnum.Srw"
      :title-stimmen-zaehlen="titleStimmenZaehlen"
      :wahl-id="wahl.wahlID"
      :wahlbezirk-id="wahlbezirkIdForWahl"
    />
    <the-b-a-w-scores-list-group
      v-if="wahl.wahlart === WahlWahlartEnum.Baw"
      :title-stimmen-zaehlen="titleStimmenZaehlen"
      :wahl-id="wahl.wahlID"
      :wahlbezirk-id="wahlbezirkIdForWahl"
    />
    <the-m-b-w-scores-list-group
      v-if="wahl.wahlart === WahlWahlartEnum.Mbw"
      :wahl-id="wahl.wahlID"
      :wahlbezirk-id="wahlbezirkIdForWahl"
    />
  </div>
</template>

<script setup lang="ts">
import type { Wahl } from "@/types/wahl/Wahl.ts";
import type { PropType } from "vue";

import { computed } from "vue";

import TheBAWScoresListGroup from "@/components/navigation/auszaehlung_wahlarten/TheBAWScoresListGroup.vue";
import TheMBWScoresListGroup from "@/components/navigation/auszaehlung_wahlarten/TheMBWScoresListGroup.vue";
import TheOBWScoresListGroup from "@/components/navigation/auszaehlung_wahlarten/TheOBWScoresListGroup.vue";
import TheSRWScoresListGroup from "@/components/navigation/auszaehlung_wahlarten/TheSRWScoresListGroup.vue";
import { useTextFormatter } from "@/composables/common/textFormatter.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { WahlWahlartEnum } from "@/types/wahl/WahlWahlartEnum.ts";

const props = defineProps({
  wahl: {
    type: Object as PropType<Wahl>,
    required: true,
  },
});

const { getStimmzettelTermForWahl } = useTextFormatter();

const titleStimmenZaehlen = computed(
  () => `Zählen der ${getStimmzettelTermForWahl(props.wahl)}`
);

const wahlbezirkIdForWahl = computed(() =>
  useUserStore().getWahlbezirkIdFromWahlMetaDataByWahlId(props.wahl.wahlID)
);
</script>
