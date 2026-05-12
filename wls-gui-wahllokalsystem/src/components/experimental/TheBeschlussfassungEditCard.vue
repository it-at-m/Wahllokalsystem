<template>
  <v-card>
    <v-card-title>Beschluss fassen für Stimmzettel 42</v-card-title>
    <v-card-text>
      <v-row>
        <v-col cols="4">
          <div>
            <div>Vorschläge für Beschlussfassung</div>
            <v-checkbox
              label="Zu viele Listenkreuze"
              hide-details
            />
            <v-checkbox
              label="Mehr als 3 Stimmen bei einem Kandidaten"
              hide-details
            />
            <v-textarea
              label="Beschluss verfassen"
              auto-grow
              rows="1"
            />
          </div>
        </v-col>
        <v-col>
          <v-tabs v-model="selectedTab">
            <v-tab value="overview">Übersicht</v-tab>
            <v-tab value="input">Erfassung</v-tab>
          </v-tabs>
          <v-tabs-window v-model="selectedTab">
            <v-tabs-window-item value="overview">
              <v-row>
                <v-col>Kandidatenstimmen insgesamt</v-col>
                <v-col>45</v-col>
              </v-row>
              <v-row>
                <v-col>Kandidatenstimmen ungültig</v-col>
                <v-col>12</v-col>
              </v-row>
              <v-row>
                <v-col>Listenkreuze insgesamt</v-col>
                <v-col>3</v-col>
              </v-row>
            </v-tabs-window-item>
            <v-tabs-window-item value="input">
              <the-stimmzettel-scores-card
                v-if="wahlvorschlaege"
                :wahlvorschlaege="wahlvorschlaege"
                :stimmzettel-snapshots="stimmzettelSnapshots"
                :is-saving-stimmzettel="isSavingStimmzettel"
                @snapshot-created="onStimmzettelSnapshotCreated"
              />
            </v-tabs-window-item>
          </v-tabs-window>
        </v-col>
      </v-row>
    </v-card-text>
    <v-card-actions>
      <base-wls-button-save save-text="Speichern und fortfahren" />
      <base-text-button>Erfassung abbrechen</base-text-button>
    </v-card-actions>
  </v-card>
</template>

<script setup lang="ts">
import type { StimmzettelSnapshot } from "@/types/experimental/StimmzettelSnapshot.ts";
import type { Wahlvorschlaege } from "@/types/wahlvorschlaege/Wahlvorschlaege.ts";
import type { Ref } from "vue";

import { onMounted, ref } from "vue";
import { useRoute } from "vue-router";

import BaseTextButton from "@/components/common/buttons/BaseTextButton.vue";
import BaseWlsButtonSave from "@/components/common/buttons/BaseWlsButtonSave.vue";
import TheStimmzettelScoresCard from "@/components/experimental/TheStimmzettelScoresCard.vue";
import { useMBWStimmzettelViewUtils } from "@/composables/experimental/MBWStimmzettelViewUtils.ts";

const route = useRoute();
const wahlID = route.params.wahlId as string;
const wahlbezirkID = route.params.wahlbezirkId as string;

const {
  isLoadingStimmzettel,
  isLoadingWahlvorschlaege,
  isSavingStimmzettel,
  loadStimmzettel,
  loadWahlvorschlaege,
  saveStimmzettel,
} = useMBWStimmzettelViewUtils(wahlID, wahlbezirkID);

const selectedTab = ref("overview");

const wahlvorschlaege: Ref<Wahlvorschlaege | null> = ref(null);
const stimmzettelSnapshots: Ref<StimmzettelSnapshot[]> = ref([]);

onMounted(async () => {
  wahlvorschlaege.value = await loadWahlvorschlaege();
  stimmzettelSnapshots.value = await loadStimmzettel();
});

function onStimmzettelSnapshotCreated(
  stimmzettelSnapshot: StimmzettelSnapshot
) {
  stimmzettelSnapshots.value.push(stimmzettelSnapshot);
  saveStimmzettel(stimmzettelSnapshots.value);
}
</script>
