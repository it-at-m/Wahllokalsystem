<template>
  <v-card>
    <v-card-title>
      Gültige Stimmen für die einzelnen Kandidat*innen
    </v-card-title>
    <v-card-text>
      <v-row>
        <v-col
          v-for="vorschlag in kandidatenstimmen"
          :key="vorschlag.identifikator"
          class="pa-0"
          cols="12"
          sm="6"
        >
          <base-card-kandidatenstimmen-anzeigen
            :wahlvorschlag-nr="vorschlag.ordnungszahl"
            :wahlvorschlag-name="vorschlag.kurzname"
            :kandidatenergebnisse="vorschlag.kandidatenErgebnisse"
          />
        </v-col>
      </v-row>
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import type { WahlvorschlagWithKandidatenErgebnissen } from "@/types/ergebnismeldung/common/WahlvorschlagWithKandidatenErgebnissen.ts";

import BaseCardKandidatenstimmenAnzeigen from "@/components/ergebnismeldung/common/BaseCardKandidatenstimmenAnzeigen.vue";

defineProps<{
  kandidatenstimmen: WahlvorschlagWithKandidatenErgebnissen[];
}>();
</script>
