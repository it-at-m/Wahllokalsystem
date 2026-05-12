<template>
  <v-card>
    <v-card-title>
      <div class="d-flex justify-space-between">
        <div>Beschluss fassen für Stimmzettel 42</div>
        <div>
          <v-progress-linear
            style="min-width: 250px"
            color="primary"
            :min="0"
            :max="beschlussFortschrittMax"
            :model-value="beschlussFortschrittCurrent - 1"
            :buffer-value="beschlussFortschrittCurrent"
            >Beschluss {{ beschlussFortschrittCurrent }} von
            {{ beschlussFortschrittMax }}</v-progress-linear
          >
        </div>
      </div></v-card-title
    >
    <v-card-text>
      <v-card>
        <v-card-title>
          <v-tabs
            v-model="selectedTab"
            slider-color="primary"
            color="primary"
            density="compact"
          >
            <v-tab value="overview">Verfassen</v-tab>
            <v-tab value="input">Stimmzettel bearbeiten</v-tab>
          </v-tabs>
        </v-card-title>
        <v-card-text>
          <v-tabs-window v-model="selectedTab">
            <v-tabs-window-item value="overview">
              <v-row>
                <v-col>
                  <v-card class="ms-1 mb-1">
                    <v-card-title>Beschluss dokumentieren</v-card-title>
                    <!-- In Übersicht -->
                    <v-card-text>
                      <ul class="ms-3">
                        <li v-if="beschlussStimmzettelFailureZuVieleStimmen">
                          Mehr als 3 Stimmen bei einem Kandidaten
                        </li>
                        <li v-if="beschlussStimmzettelFailureListenkreuzen">
                          Zu viele Listenkreuze
                        </li>
                      </ul>
                      <v-textarea
                        class="mt-2"
                        label="Beschluss verfassen"
                        auto-grow
                        rows="1"
                      />
                      <v-radio-group label="Gültigkeit">
                        <v-radio
                          label="gültig"
                          :disabled="!beschlussGueltigkeit1IsSelectable"
                          value="1"
                        />
                        <v-radio
                          label="teilweise gültig"
                          :disabled="!beschlussGueltigkeit2IsSelectable"
                          value="2"
                        />
                        <v-radio
                          label="ungültig"
                          :disabled="!beschlussGueltigkeit3IsSelectable"
                          value="3"
                        />
                      </v-radio-group>
                    </v-card-text>
                  </v-card>
                </v-col>
                <v-col cols="4">
                  <v-card class="me-1 mb-1">
                    <v-card-title>Zusammenfassung Stimmzettel</v-card-title>
                    <v-card-text>
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
                    </v-card-text>
                  </v-card>
                </v-col>
              </v-row>
            </v-tabs-window-item>
            <v-tabs-window-item value="input">
              <the-stimmzettel-scores-card
                v-if="wahlvorschlaege"
                :wahlvorschlaege="wahlvorschlaege"
                :stimmzettel-snapshots="stimmzettelSnapshots"
                :is-saving-stimmzettel="isSavingStimmzettel"
                :show-title="false"
                @snapshot-created="onStimmzettelSnapshotCreated"
              />
            </v-tabs-window-item>
          </v-tabs-window>
        </v-card-text>
      </v-card>
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

import { storeToRefs } from "pinia";
import { onMounted, ref } from "vue";
import { useRoute } from "vue-router";

import BaseTextButton from "@/components/common/buttons/BaseTextButton.vue";
import BaseWlsButtonSave from "@/components/common/buttons/BaseWlsButtonSave.vue";
import TheStimmzettelScoresCard from "@/components/experimental/TheStimmzettelScoresCard.vue";
import { useMBWStimmzettelViewUtils } from "@/composables/experimental/MBWStimmzettelViewUtils.ts";
import { useExperimentalFeaturesStore } from "@/stores/experimentalFeaturesStore.ts";

const route = useRoute();
const wahlID = route.params.wahlId as string;
const wahlbezirkID = route.params.wahlbezirkId as string;

const {
  isSavingStimmzettel,
  loadStimmzettel,
  loadWahlvorschlaege,
  saveStimmzettel,
} = useMBWStimmzettelViewUtils(wahlID, wahlbezirkID);

const {
  beschlussFortschrittCurrent,
  beschlussFortschrittMax,
  beschlussGueltigkeit1IsSelectable,
  beschlussGueltigkeit2IsSelectable,
  beschlussGueltigkeit3IsSelectable,
  beschlussStimmzettelFailureZuVieleStimmen,
  beschlussStimmzettelFailureListenkreuzen,
} = storeToRefs(useExperimentalFeaturesStore());

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
