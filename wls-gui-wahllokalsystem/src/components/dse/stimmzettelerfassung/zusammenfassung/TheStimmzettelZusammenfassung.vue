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
      <the-erfasste-stimmzettel-card :stimmzettel-liste="stimmzettelListe" />

      <base-card-wahlvorschlaege-kandidatenstimmen-anzeigen
        :kandidatenstimmen="wahlvorschlaegeWithKandidatenErgebnissen"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import type { PersistedStimmzettel } from "@/types/dse/stimmzettelerfassung/PersistedStimmzettel.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";

import { storeToRefs } from "pinia";
import { computed } from "vue";
import { useRoute } from "vue-router";

import TheErfassteStimmzettelCard from "@/components/dse/stimmzettelerfassung/zusammenfassung/TheErfassteStimmzettelCard.vue";
import BaseCardWahlvorschlaegeKandidatenstimmenAnzeigen from "@/components/ergebnismeldung/common/BaseCardWahlvorschlaegeKandidatenstimmenAnzeigen.vue";
import TheMBWWaehlerAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelAB/TheMBWWaehlerAnzeigenCard.vue";
import TheMBWWahlberechtigteAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelAB/TheMBWWahlberechtigteAnzeigenCard.vue";
import { useStimmzettelZusammenfassungUtils } from "@/composables/dse/stimmzettelerfassung/stimmzettelZusammenfassungUtils.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { StimmzettelGueltigkeitEnum } from "@/types/dse/stimmzettelerfassung/StimmzettelGueltigkeitEnum.ts";

const { hasRoleSchriftfuehrung } = storeToRefs(useUserStore());
const route = useRoute();

const wahlbezirkID = route.params.wahlbezirkId as string;
const wahlID = route.params.wahlId as string;

const props = defineProps<{
  stimmzettelListe: PersistedStimmzettel[];
  wahlvorschlaege: Wahlvorschlag[];
}>();

const { wahlvorschlaegeWithKandidatenErgebnissen } =
  useStimmzettelZusammenfassungUtils(
    computed(() =>
      props.stimmzettelListe.filter(
        (stimmzettel) =>
          stimmzettel.gueltigkeit === StimmzettelGueltigkeitEnum.Valid
      )
    ),
    computed(() => props.wahlvorschlaege)
  );
</script>
