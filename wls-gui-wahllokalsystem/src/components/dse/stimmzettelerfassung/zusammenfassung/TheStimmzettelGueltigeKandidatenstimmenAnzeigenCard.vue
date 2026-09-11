<template>
  <v-card>
    <v-card-title>
      Gültige Stimmen für die einzelnen Kandidat*innen
    </v-card-title>
    <v-card-text>
      <v-row>
        <v-col
          v-for="vorschlag in wahlvorschlaegeWithKandidatenErgebnissen"
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
import type { Stimmzettel } from "@/types/dse/persistedStimmzettel/Stimmzettel.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";

import { computed } from "vue";

import BaseCardKandidatenstimmenAnzeigen from "@/components/ergebnismeldung/MBW/stapelBC/BaseCardKandidatenstimmenAnzeigen.vue";
import { useStimmzettelGueltigeKandidatenstimmenAnzeigenCardUtils } from "@/composables/dse/stimmzettelerfassung/stimmzettelGueltigeKandidatenstimmenAnzeigenCardUtils.ts";
import { StimmzettelGueltigkeitEnum } from "@/types/dse/persistedStimmzettel/StimmzettelGueltigkeitEnum.ts";

const props = defineProps<{
  stimmzettelListe: Stimmzettel[];
  wahlvorschlaege: Wahlvorschlag[];
}>();

const { wahlvorschlaegeWithKandidatenErgebnissen } =
  useStimmzettelGueltigeKandidatenstimmenAnzeigenCardUtils(
    computed(() =>
      props.stimmzettelListe.filter(
        (stimmzettel) =>
          stimmzettel.gueltigkeit === StimmzettelGueltigkeitEnum.Valid
      )
    ),
    computed(() => props.wahlvorschlaege)
  );
</script>
