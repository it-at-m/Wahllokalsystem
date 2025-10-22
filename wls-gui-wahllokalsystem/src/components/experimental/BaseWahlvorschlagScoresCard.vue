<template>
  <v-card>
    <v-card-title
      >Wahlvorschlag Nr. {{ wahlvorschlag.ordnungszahl }}</v-card-title
    >
    <v-card-text>
      <base-kandidat-score
        v-for="(kandidat, index) in wahlvorschlag.kandidaten"
        :key="index"
        :model-value="ergebnisOfKandidat(kandidat.identifikator)"
        :kandidat="kandidat"
        :listennummer="wahlvorschlag.ordnungszahl"
        @update:model-value="
          onUpdateOfErgebnisOfKandidat(kandidat.identifikator, $event)
        "
      />
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import type { Ergebnis } from "@/types/ergebnismeldung/Ergebnis.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";
import type { PropType } from "vue";

import { ref } from "vue";

import BaseKandidatScore from "@/components/experimental/BaseKandidatScore.vue";

const props = defineProps({
  wahlvorschlag: {
    type: Object as PropType<Wahlvorschlag>,
    required: true,
  },
});

const kandidatenErgebnisse = ref<Ergebnis[]>([]);

function ergebnisOfKandidat(kandidatId: string): Ergebnis {
  let result = kandidatenErgebnisse.value.find(
    (k) => k.kandidatID === kandidatId
  );
  if (result === undefined) {
    result = {
      kandidatID: kandidatId,
      ergebnis: null,
      wahlvorschlagID: props.wahlvorschlag?.identifikator,
      numIndex: null,
      wahlvorschlagsOrdnungszahl: props.wahlvorschlag?.ordnungszahl,
    };
    kandidatenErgebnisse.value.push(result);
  }
  return result;
}

function onUpdateOfErgebnisOfKandidat(kandidatId: string, ergebnis: Ergebnis) {
  ergebnisOfKandidat(kandidatId).ergebnis = ergebnis.ergebnis;
}
</script>
