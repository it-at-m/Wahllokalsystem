<template>
  <div>
  <v-card-subtitle  class="font-weight-bold mt-2 mb-2">
    aktueller Stand der erfassten Stimmzettel
  </v-card-subtitle>
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
    <base-erfasste-stimmzettel-card />
    <the-m-b-w-gueltige-kandidatenstimmen-anzeigen-card
        :wahlbezirk-id="wahlbezirkID"
        :wahl-id="wahlID"
    />
  </div>
</template>

<script setup lang="ts">
import TheMBWWahlberechtigteAnzeigenCard
  from "@/components/ergebnismeldung/MBW/stapelAB/TheMBWWahlberechtigteAnzeigenCard.vue";
import {useRoute} from "vue-router";
import TheMBWWaehlerAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelAB/TheMBWWaehlerAnzeigenCard.vue";
import {storeToRefs} from "pinia";
import {useUserStore} from "@/stores/userStore.ts";
import TheMBWGueltigeKandidatenstimmenAnzeigenCard
  from "@/components/ergebnismeldung/MBW/stapelBC/TheMBWGueltigeKandidatenstimmenAnzeigenCard.vue";
import BaseErfassteStimmzettelCard
  from "@/components/dse/stimmzettelerfassung/baseComponents/BaseErfassteStimmzettelCard.vue";

const { hasRoleSchriftfuehrung } = storeToRefs(useUserStore());
const route = useRoute();

const wahlbezirkID = route.params.wahlbezirkId as string;
const wahlID = route.params.wahlId as string;
</script>
