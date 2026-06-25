<template>
  <v-card data-test="wahlenView">
    <v-card-title>Wahlen bearbeiten</v-card-title>
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

      <template v-if="wahltagSelected">
        <div
          v-for="event in wahltagSelected.events"
          :key="event.wahltagID"
          class="mt-4"
          data-test="wahlen-event-section"
        >
          <div class="text-subtitle-1 mb-2">
            Wahlnummer: {{ event.nummer }}
            <span class="text-medium-emphasis">· {{ event.beschreibung }}</span>
          </div>
          <base-list-wahlen :wahltag-id="event.wahltagID" />
        </div>
      </template>
    </v-card-text>
  </v-card>
</template>
<script setup lang="ts">
import type { Wahltag } from "@/types/wahltag/Wahltag.ts";
import type { Ref } from "vue";

import { onMounted, ref } from "vue";
import { VCard, VCardText, VCardTitle } from "vuetify/components";

import BaseAutocompleteWahltag from "@/components/common/BaseAutocompleteWahltag.vue";
import BaseIconButtonRefresh from "@/components/common/BaseIconButtonRefresh.vue";
import BaseListWahlen from "@/components/wahltag/BaseListWahlen.vue";
import { useWahltagService } from "@/composables/wahltag/wahltagService.ts";

const { getWahltage } = useWahltagService();

const wahltage: Ref<Wahltag[]> = ref([]);
const wahltagSelected: Ref<Wahltag | undefined> = ref(undefined);
const wahltageAreLoading = ref(false);

onMounted(() => {
  loadWahltage();
});

function onRefreshWahltageClicked() {
  loadWahltage();
}

function onSelectedWahltagChanged(newSelectedWahltag?: Wahltag) {
  wahltagSelected.value = newSelectedWahltag;
}

async function loadWahltage(): Promise<void> {
  wahltage.value = await getWahltage(wahltageAreLoading);
}
</script>
