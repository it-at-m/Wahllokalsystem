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
import type { PersistedStimmzettel } from "@/types/dse/persistedStimmzettel/PersistedStimmzettel.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";

import { computed } from "vue";

import BaseCardKandidatenstimmenAnzeigen from "@/components/ergebnismeldung/MBW/stapelBC/BaseCardKandidatenstimmenAnzeigen.vue";
import { useStimmzettelGueltigeKandidatenstimmenAnzeigenCardUtils } from "@/composables/dse/stimmzettelerfassung/stimmzettelGueltigeKandidatenstimmenAnzeigenCardUtils.ts";
import { PersistedStimmzettelGueltigkeitEnum } from "@/types/dse/persistedStimmzettel/PersistedStimmzettelGueltigkeitEnum.ts";

const props = defineProps<{
  stimmzettelListe: PersistedStimmzettel[];
  wahlvorschlaege: Wahlvorschlag[];
}>();

const { wahlvorschlaegeWithKandidatenErgebnissen } =
  useStimmzettelGueltigeKandidatenstimmenAnzeigenCardUtils(
    computed(() =>
      props.stimmzettelListe.filter(
        (stimmzettel) =>
          stimmzettel.gueltigkeit === PersistedStimmzettelGueltigkeitEnum.Valid
      )
    ),
    computed(() => props.wahlvorschlaege)
  );
</script>
