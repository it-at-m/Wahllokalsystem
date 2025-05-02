<template>
  <v-card data-test="initWahltageView">
    <v-card-title>Benutzer bearbeiten</v-card-title>
    <v-card-text>
      <div class="d-flex justify-between flex-wrap">
        <base-autocomplete-wahltag
          :model-value="wahltagSelected"
          :items="wahltage"
          :loading="wahltageAreLoading"
          @update:model-value="onSelectedWahltagChanged"
        />
        <base-icon-button-refresh
          class="ml-2"
          @click="onRefreshWahltageClicked"
        />
      </div>
      <base-wahltag-event-stepper
        :wahltag-events="wahltagEventsOfSelectedWahltag"
        :konfigurierte-wahltage="konfigurierteWahltage"
      />
      <the-wahltag-init-progress-div class="mt-2" />
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import type { Wahltag } from "@/types/wahltag/Wahltag.ts";
import type { Ref } from "vue";

import { computed, onMounted, ref } from "vue";
import { VCard, VCardText, VCardTitle } from "vuetify/components";

import BaseAutocompleteWahltag from "@/components/common/BaseAutocompleteWahltag.vue";
import BaseIconButtonRefresh from "@/components/common/BaseIconButtonRefresh.vue";
import BaseWahltagEventStepper from "@/components/wahltag/BaseWahltagEventStepper.vue";
import TheWahltagInitProgressDiv from "@/components/wahltag/TheWahltagInitProgressDiv.vue";
import { useWahltagService } from "@/composables/wahltag/wahltagService.ts";

const { getWahltage, isKonfigurierterWahltag } = useWahltagService();

const wahltage: Ref<Wahltag[]> = ref([]);
const wahltagSelected: Ref<Wahltag | undefined> = ref(undefined);
const wahltageAreLoading = ref(false);

const konfigurierteWahltage: Ref<Map<string, boolean | undefined>> = ref(
  new Map<string, boolean | undefined>()
);

const wahltagEventsOfSelectedWahltag = computed(
  () => wahltagSelected.value?.events ?? []
);

onMounted(() => {
  loadWahltage();
});

function onRefreshWahltageClicked() {
  loadWahltage();
}

function onSelectedWahltagChanged(newSelectedWahltag?: Wahltag) {
  wahltagSelected.value = newSelectedWahltag;

  if (newSelectedWahltag) {
    reloadKonfigurierteWahltageOfDate(newSelectedWahltag);
  }
}

async function loadWahltage(): Promise<void> {
  wahltage.value = await getWahltage(wahltageAreLoading);
}

function reloadKonfigurierteWahltageOfDate(wahltag: Wahltag) {
  konfigurierteWahltage.value = new Map<string, boolean>();

  wahltag.events.forEach((event) => {
    isKonfigurierterWahltag(event.wahltagID).then((result) =>
      konfigurierteWahltage.value.set(event.wahltagID, result)
    );
  });
}
</script>
