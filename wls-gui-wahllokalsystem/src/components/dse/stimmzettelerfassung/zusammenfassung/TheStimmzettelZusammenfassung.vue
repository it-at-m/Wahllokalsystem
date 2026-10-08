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
      <base-card-ungueltige-stimmen-anzeigen
        :ungueltige-stimmen="ungueltigeStimmen"
        :ungueltige-stimmzettel-nach-beschluss="0"
      />
      <v-card>
        <v-card-title> Gültige Stimmen </v-card-title>
        <v-card-text>
          <the-m-b-w-gueltige-stimmen-anzeigen-niederschrift-table
            :wahlvorschlaege-kandidaten-ergebnisse="
              wahlvorschlaegeWithKandidatenErgebnissen
            "
            :ergebnisse-and-wahlvorschlaege="
              wahlvorschlaegeErgebnisseStapelAAndB
            "
          />
        </v-card-text>
      </v-card>
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

import BaseCardUngueltigeStimmenAnzeigen from "@/components/ergebnismeldung/common/BaseCardUngueltigeStimmenAnzeigen.vue";
import BaseCardWahlvorschlaegeKandidatenstimmenAnzeigen from "@/components/ergebnismeldung/common/BaseCardWahlvorschlaegeKandidatenstimmenAnzeigen.vue";
import TheMBWGueltigeStimmenAnzeigenNiederschriftTable from "@/components/ergebnismeldung/MBW/stapelAB/TheMBWGueltigeStimmenAnzeigenNiederschriftTable.vue";
import TheMBWWaehlerAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelAB/TheMBWWaehlerAnzeigenCard.vue";
import TheMBWWahlberechtigteAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelAB/TheMBWWahlberechtigteAnzeigenCard.vue";
import { useMbwStimmzettelFilterService } from "@/composables/dse/mbwStimmzettelFilterService.ts";
import { useStimmzettelZusammenfassungUtils } from "@/composables/dse/stimmzettelerfassung/stimmzettelZusammenfassungUtils.ts";
import { useMbwErgebnisseAndWahlvorschlagStapelSumReactiveMapper } from "@/composables/ergebnismeldung/MBW/mbwErgebnisseAndWahlvorschlagStapelSumReactiveMapper.ts";
import { useUserStore } from "@/stores/userStore.ts";
import {
  isStimmzettelUngueltig,
  StimmzettelGueltigkeitEnum,
} from "@/types/dse/stimmzettelerfassung/StimmzettelGueltigkeitEnum.ts";

const { hasRoleSchriftfuehrung } = storeToRefs(useUserStore());
const route = useRoute();

const wahlbezirkID = route.params.wahlbezirkId as string;
const wahlID = route.params.wahlId as string;

const props = defineProps<{
  stimmzettelListe: PersistedStimmzettel[];
  wahlvorschlaege: Wahlvorschlag[];
}>();

const { stapelASumGroupedByWahlvorschlag, stapelBSumGroupedByWahlvorschlag } =
  useMbwStimmzettelFilterService(computed(() => props.stimmzettelListe));
const { wahlvorschlaegeErgebnisseStapelAAndB } =
  useMbwErgebnisseAndWahlvorschlagStapelSumReactiveMapper(
    computed(() => props.wahlvorschlaege),
    stapelASumGroupedByWahlvorschlag,
    stapelBSumGroupedByWahlvorschlag
  );

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

const ungueltigeStimmen = computed(
  () =>
    props.stimmzettelListe.filter((stimmzettel) =>
      isStimmzettelUngueltig(stimmzettel.gueltigkeit)
    ).length
);
</script>
