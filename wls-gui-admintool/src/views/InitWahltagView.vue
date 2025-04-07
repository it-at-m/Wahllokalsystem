<template>
  <v-card data-test="initWahltageView">
    <v-card-title>Benutzer bearbeiten</v-card-title>
    <v-card-text>
      <div class="d-flex justify-between flex-wrap">
        <base-autocomplete-wahltag
          v-model="wahltagSelected"
          :items="wahltage"
          :loading="wahltageAreLoading"
        />
        <base-icon-button-refresh
          class="ml-2"
          @click="onRefreshWahltageClicked"
        />
      </div>
      <div v-if="wahltagSelected">
        <div
          v-for="event in wahltagSelected.events"
          :key="event.wahltagID"
        >
          {{ event.wahltagID }} - {{ event.beschreibung }} - {{ event.nummer }}
        </div>
      </div>
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
import useWahltagService from "@/composables/wahltag/wahltagService.ts";

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

async function loadWahltage(): Promise<void> {
  wahltage.value = await getWahltage(wahltageAreLoading);
}
</script>
