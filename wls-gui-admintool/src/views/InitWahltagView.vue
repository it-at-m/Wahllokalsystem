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
      <v-stepper
        v-if="steps.length > 0"
        v-model="activeStep"
        :items="steps"
        item-title="nummer"
        editable
        :hide-actions="!wahltagSelectedHasMultipleEvents"
      >
        <template #title="{ step }"
          ><div class="mb-1">
            Wahlnummer: {{ steps[step - 1].nummer }}
          </div></template
        >
        <template #subtitle="{ step }">
          <div>{{ steps[step - 1].beschreibung }}</div>
        </template>
        <v-stepper-window>
          <v-stepper-window-item
            v-for="(step, index) in steps"
            :key="step.wahltagID"
            :value="index + 1"
          >
            <base-step-wahltag-init
              :wahltag-event="step"
              :wahltermin-daten-exists="
                konfigurierteWahltage.get(step.wahltagID)
              "
            />
          </v-stepper-window-item>
        </v-stepper-window>
      </v-stepper>
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import type { Wahltag } from "@/types/wahltag/Wahltag.ts";
import type { Ref } from "vue";

import { computed, onMounted, ref } from "vue";
import {
  VCard,
  VCardText,
  VCardTitle,
  VStepper,
  VStepperWindow,
  VStepperWindowItem,
} from "vuetify/components";

import BaseAutocompleteWahltag from "@/components/common/BaseAutocompleteWahltag.vue";
import BaseIconButtonRefresh from "@/components/common/BaseIconButtonRefresh.vue";
import BaseStepWahltagInit from "@/components/wahltag/BaseStepWahltagInit.vue";
import useWahltagService from "@/composables/wahltag/wahltagService.ts";

const { getWahltage, isKonfigurierterWahltag } = useWahltagService();

const wahltage: Ref<Wahltag[]> = ref([]);
const wahltagSelected: Ref<Wahltag | undefined> = ref(undefined);
const wahltagSelectedHasMultipleEvents = computed(
  () => (wahltagSelected.value?.events.length ?? 0) > 1
);
const wahltageAreLoading = ref(false);

const konfigurierteWahltage: Ref<Map<string, boolean | undefined>> = ref(
  new Map<string, boolean | undefined>()
);

const steps = computed(() => wahltagSelected.value?.events ?? []);
const activeStep = ref(0);

onMounted(() => {
  loadWahltage();
});

function onRefreshWahltageClicked() {
  loadWahltage();
}

function onSelectedWahltagChanged(newSelectedWahltag?: Wahltag) {
  wahltagSelected.value = newSelectedWahltag;
  activeStep.value = 0;

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
