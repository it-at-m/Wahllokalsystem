<template>
  <v-card>
    <v-tabs
      v-model="tab"
      bg-color="grey-lighten-3"
      slider-color="primary"
      color="primary"
      class="rounded-t border-b"
    >
      <v-tab
        value="one"
        :prepend-icon="isMBWBedenklicheStimmzettelDone ? `$valid` : `$edit`"
        data-test="wahlbriefe-zulassen-tab"
      >
        Bedenkliche Stimmzettel beschließen
      </v-tab>
      <v-tab
        value="two"
        prepend-icon="$summary"
      >
        Beschlussergebnis
      </v-tab>
    </v-tabs>
    <v-tabs-window v-model="tab">
      <v-tabs-window-item value="one">
        <the-stimmzettel-beschlussfassung-table
          v-model:bedenkliche-stimmzettel="bedenklicheStimmzettel"
        />
      </v-tabs-window-item>
      <v-tabs-window-item value="two">
        <the-stimmzettel-beschlussfassung-summary-card
          :bedenkliche-stimmzettel="bedenklicheStimmzettel"
          class="mb-3"
        />
      </v-tabs-window-item>
    </v-tabs-window>
  </v-card>
</template>

<script setup lang="ts">
import type { BedenklicherStimmzettel } from "@/types/ergebnismeldung/MBW/bedenklicheStimmzettel/BedenklicherStimmzettel.ts";

import { computed, onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";

import TheStimmzettelBeschlussfassungSummaryCard from "@/components/ergebnismeldung/MBW/stapelE/TheStimmzettelBeschlussfassungSummaryCard.vue";
import TheStimmzettelBeschlussfassungTable from "@/components/ergebnismeldung/MBW/stapelE/TheStimmzettelBeschlussfassungTable.vue";
import { useLogging } from "@/composables/common/logging.ts";
import { useBedenklicheStimmzettelService } from "@/composables/ergebnismeldung/MBW/bedenklicheStimmzettelService.ts";
import { ROUTE_NOTFOUND } from "@/constants.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { useWorkflowStore } from "@/stores/workflowStore.ts";
import { MbwRoutesEnum } from "@/types/navigation/MbwRoutesEnum.ts";

const bedenklicheStimmzettel = ref<BedenklicherStimmzettel[]>([]);

const route = useRoute();
const router = useRouter();
const { wahlenActions } = useWahlenStore();
const { getWahlbezirkIdFromWahlMetaDataByWahlId } = useUserStore();
const { isStepDone } = useWorkflowStore();
const { getBedenklicheStimmzettel } = useBedenklicheStimmzettelService();
const { logError } = useLogging("mbwStapelEView");

const wahlID = route.params.wahlId as string;
const wahl = wahlenActions.getWahlOrUndefinedById(wahlID);
const wahlbezirkID = getWahlbezirkIdFromWahlMetaDataByWahlId(wahlID);

const tab = ref(null);

const isMBWBedenklicheStimmzettelDone = computed(() =>
  isStepDone(wahlID, wahlbezirkID ?? "", MbwRoutesEnum.MBW_STAPEL_E)
);

if (!wahl) {
  router.push({
    name: ROUTE_NOTFOUND,
  });
}

onMounted(async () => {
  if (wahlbezirkID) {
    try {
      const loadedBedenklicheStimmzettel = await getBedenklicheStimmzettel(
        wahlID,
        wahlbezirkID
      );
      if (
        loadedBedenklicheStimmzettel &&
        loadedBedenklicheStimmzettel.length > 0
      ) {
        bedenklicheStimmzettel.value = loadedBedenklicheStimmzettel;
      }
    } catch (error) {
      logError("Fehler beim Laden der bedenklichen Stimmzettel: ", error);
      throw error;
    }
  }
});
</script>
