<template>
  <v-card>
    <v-card-title
      >Wahlvorschlag Nr. {{ wahlvorschlag.ordnungszahl }}
      <v-checkbox v-model="wahlvorschlagSelected"
    /></v-card-title>
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
import type { StimmzettelWahlvorschlag } from "@/types/experimental/StimmzettelWahlvorschlag.ts";
import type { PropType } from "vue";

import { computed, ref } from "vue";

import BaseKandidatScore from "@/components/experimental/BaseKandidatScore.vue";
import { getStimmzettelManger } from "@/composables/experimental/stimmzettelManager.ts";

const props = defineProps({
  wahlvorschlag: {
    type: Object as PropType<StimmzettelWahlvorschlag>,
    required: true,
  },
});

const stimmzettelManager = getStimmzettelManger({
  wahlbezirkId: "wahlbezirkId",
  wahlId: "wahlId",
});

const kandidatenErgebnisse = ref<Ergebnis[]>([]);

const wahlvorschlagSelected = computed({
  set: (value: boolean) => {
    if (value) {
      stimmzettelManager.selectWahlvorschlag(props.wahlvorschlag.identifikator);
    } else {
      stimmzettelManager.deselectWahlvorschlag(
        props.wahlvorschlag.identifikator
      );
    }
  },
  get: () => {
    return stimmzettelManager.selectedWahlvorschlaege.value.some(
      (wahlvorschlag) =>
        wahlvorschlag.identifikator === props.wahlvorschlag.identifikator
    );
  },
});

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
