<template>
  <div>
    <v-card-subtitle class="font-weight-bold mt-2 mb-10">
      aktueller Stand der erfassten Stimmzettel
    </v-card-subtitle>
    <div class="d-flex flex-column ga-5 mx-4">
      <the-m-b-w-wahlberechtigte-anzeigen-card
        v-if="hasRoleSchriftfuehrung"
        :wahlbezirk-id="wahlbezirkID"
        :wahl-id="wahlID"
      />
      <the-m-b-w-waehler-anzeigen-card
        v-if="hasRoleSchriftfuehrung"
        :wahlbezirk-id="wahlbezirkID"
        :wahl-id="wahlID"
      />
      <base-erfasste-stimmzettel-card :stimmzettel-liste="stimmzettelListe" />
      <base-stimmzettel-gueltige-kandidatenstimmen-anzeigen-card
        :stimmzettel-liste="stimmzettelListe"
        :wahlvorschlaege="wahlvorschlaege"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Stimmzettel } from "@/types/dse/persistedStimmzettel/Stimmzettel.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";

import { storeToRefs } from "pinia";
import { useRoute } from "vue-router";

import BaseErfassteStimmzettelCard from "@/components/dse/stimmzettelerfassung/baseComponents/BaseErfassteStimmzettelCard.vue";
import BaseStimmzettelGueltigeKandidatenstimmenAnzeigenCard from "@/components/dse/stimmzettelerfassung/baseComponents/BaseStimmzettelGueltigeKandidatenstimmenAnzeigenCard.vue";
import TheMBWWaehlerAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelAB/TheMBWWaehlerAnzeigenCard.vue";
import TheMBWWahlberechtigteAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelAB/TheMBWWahlberechtigteAnzeigenCard.vue";
import { useUserStore } from "@/stores/userStore.ts";

const { hasRoleSchriftfuehrung } = storeToRefs(useUserStore());
const route = useRoute();

const wahlbezirkID = route.params.wahlbezirkId as string;
const wahlID = route.params.wahlId as string;

defineProps<{
  stimmzettelListe: Stimmzettel[];
  wahlvorschlaege: Wahlvorschlag[];
}>();
</script>
