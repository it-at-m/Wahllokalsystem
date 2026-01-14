<template>
  <v-card>
    <v-card-title> Gültige Stimmen für die einzelnen Bewerber </v-card-title>
    <v-card-text>
      <v-row>
        <v-col
          v-for="vorschlag in ergebnisseAndWahlvorschlaege"
          :key="vorschlag.identifikator"
          class="pa-0"
          cols="12"
          sm="6"
        >
          <base-card-kandidatenstimmen-anzeigen
            :wahlvorschlag-nr="vorschlag.ordnungszahl"
            :wahlvorschlag-name="vorschlag.kurzname"
            :kandidatenergebnisse="vorschlag.kandidatenErgebnisse"
            class="flex-grow-1"
          />
        </v-col>
      </v-row>
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import type { WahlvorschlagWithKandidatenErgebnissen } from "@/types/ergebnismeldung/common/WahlvorschlagWithKandidatenErgebnissen.ts";

import { onActivated, ref } from "vue";

import BaseCardKandidatenstimmenAnzeigen from "@/components/ergebnismeldung/MBW/stapelBC/BaseCardKandidatenstimmenAnzeigen.vue";
import { useMwbStapelBCUtils } from "@/composables/ergebnismeldung/MBW/mwbStapelBCUtils.ts";

const props = defineProps<{
  wahlbezirkId: string;
  wahlId: string;
}>();

const {
  wahlvorschlaegeWithKandidatenErgebnissen,
  loadWahlvorschlaegeAndErgebnisse,
} = useMwbStapelBCUtils(props.wahlbezirkId, props.wahlId);

const ergebnisseAndWahlvorschlaege = ref<
  WahlvorschlagWithKandidatenErgebnissen[]
>([]);

onActivated(async () => {
  await loadWahlvorschlaegeAndErgebnisse();
  ergebnisseAndWahlvorschlaege.value =
    wahlvorschlaegeWithKandidatenErgebnissen.value;
});
</script>
