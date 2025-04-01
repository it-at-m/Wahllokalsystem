<template>
  <v-card>
    <v-card-title>WahltagEvent initialisieren</v-card-title>
    <v-card-text>
      <div class="d-flex justify-between flex-wrap">
        <base-autocomplete-wahltag
          v-model="wahltagSelected"
          :items="wahltageAsDTO"
        />
        <base-refresh-icon-button
          class="ml-2"
          @click="onRefreshWahltageClicked"
        />
      </div>
      <div v-if="wahltagModelForSelected">
        <div
          v-for="event in wahltagModelForSelected.events"
          :key="event.wahltagID"
        >
          {{ event.wahltagID }} - {{ event.beschreibung }} - {{ event.nummer }}
        </div>
      </div>
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import type { WahltagDTO } from "@/api/wls-clients/generated-admin-api";
import type { Wahltag } from "@/types/wahltag/Wahltag.ts";
import type { Ref } from "vue";

import { computed, onMounted, ref } from "vue";
import { VCard, VCardText, VCardTitle } from "vuetify/components";

import BaseAutocompleteWahltag from "@/components/common/BaseAutocompleteWahltag.vue";
import BaseRefreshIconButton from "@/components/common/BaseRefreshIconButton.vue";
import { useWahltagMapper } from "@/composables/wahltag/wahltagMapper.ts";
import useWahltagService from "@/composables/wahltag/wahltagService.ts";

const { getWahltage } = useWahltagService();
const { wahltagModelToWahltagDto } = useWahltagMapper();

const wahltage: Ref<Wahltag[]> = ref([]);
const wahltageAsDTO = computed(() =>
  wahltage.value.map((wahltag) => wahltagModelToWahltagDto(wahltag))
);
const wahltagSelected: Ref<WahltagDTO | undefined> = ref(undefined);
const wahltagModelForSelected = computed(() => {
  if (wahltagSelected.value) {
    return wahltage.value.find(
      (wahltag) => wahltag.wahltag === wahltagSelected.value?.wahltag
    );
  } else {
    return undefined;
  }
});

onMounted(() => {
  loadWahltage();
});

function onRefreshWahltageClicked() {
  loadWahltage();
}

async function loadWahltage(): Promise<void> {
  wahltage.value = await getWahltage();
}
</script>
