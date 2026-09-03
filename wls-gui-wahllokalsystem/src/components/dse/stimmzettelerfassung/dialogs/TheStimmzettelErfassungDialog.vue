<template>
  <v-dialog
    v-model="isDialogVisibleModel"
    persistent
    fullscreen
  >
    <v-card
      class="h-screen"
      style="min-height: 0; max-width: 100%"
    >
      <v-card-title>
        Erfassung Stimmzettel Nummer {{ currentUserTeamName }}
        {{ stimmzettel.stimmzettelkennung }}
      </v-card-title>
      <v-card-text
        style="min-height: 0"
        class="ga-3 d-flex"
      >
        <div
          class="d-flex flex-column"
          style="flex: 0 0 200px"
        >
          <the-eingabehistorie-card
            :change-history="changeHistory.changeHistoryInReverseOrder.value"
            class="d-flex flex-column"
          />
          <base-stimmzettel-zusammenfassung-card
            class="mt-2 d-flex flex-column"
            :listenstimmen="
              stimmzettelManager.managedStimmzettel
                .wahlvorschlaegeWithListenkreuz.value
            "
            :ungueltigestimmen="
              stimmzettelManager.managedStimmzettel.stimmenSummary.value
                .ungueltigeStimmen
            "
            :direktstimmen="
              stimmzettelManager.managedStimmzettel.stimmenSummary.value
                .einzelstimmen
            "
            :reststimmen="
              stimmzettelManager.managedStimmzettel.stimmenSummary.value
                .reststimmen
            "
            :streichungen="
              stimmzettelManager.managedStimmzettel.stimmenSummary.value
                .streichungen
            "
            :gueltigkeit="'VALID'"
          />
        </div>
        <div
          class="flex-1-1 d-flex flex-column"
          style="min-height: 0; min-width: 0"
        >
          <the-stimmzettel-command-processing-text-field
            class="flex-0-0"
            :stimmzettel-manager="stimmzettelManager"
          />
          <div
            class="flex-1-1-0 d-flex"
            style="min-height: 0; min-width: 0"
          >
            <the-stimmzettel-content
              v-if="true"
              :active-wahlvorschlag-id="
                wahlvorschlagIdOfLatestChangeWahlvorschlag
              "
              :active-kandidat="latestChangedKandidat"
              :wahlvorschlaege="
                stimmzettelManager.managedStimmzettel.stimmzettel.value
                  .wahlvorschlaege
              "
              style="min-height: 0; overflow-y: auto; min-width: 0"
            />
          </div>
        </div>
        <div
          class="d-flex flex-column"
          style="flex: 0 0 300px"
        >
          <the-eingabehistorie-card
            :change-history="changeHistory.changeHistoryInReverseOrder.value"
            class="d-flex flex-column"
          />
          <base-stimmzettel-zusammenfassung-card
            class="mt-2 d-flex flex-column"
            :listenstimmen="
              stimmzettelManager.managedStimmzettel
                .wahlvorschlaegeWithListenkreuz.value
            "
            :ungueltigestimmen="
              stimmzettelManager.managedStimmzettel.stimmenSummary.value
                .ungueltigeStimmen
            "
            :direktstimmen="
              stimmzettelManager.managedStimmzettel.stimmenSummary.value
                .einzelstimmen
            "
            :reststimmen="
              stimmzettelManager.managedStimmzettel.stimmenSummary.value
                .reststimmen
            "
            :streichungen="
              stimmzettelManager.managedStimmzettel.stimmenSummary.value
                .streichungen
            "
            :gueltigkeit="'VALID'"
          />
        </div>
      </v-card-text>
      <v-card-actions>
        <base-text-button @click="onResetClicked"
          >Zurücksetzen</base-text-button
        >
        <v-spacer />
        <base-text-button @click="onCancelClicked">Abbrechen</base-text-button>
        <base-wls-button-save @click="onSavedClicked" />
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import type { Stimmzettel } from "@/types/dse/persistedStimmzettel/Stimmzettel.ts";
import type { Kandidat } from "@/types/dse/stimmzettelerfassung/Kandidat.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";
import type { PropType } from "vue";

import { storeToRefs } from "pinia";
import { computed } from "vue";
import { useRoute } from "vue-router";

import BaseTextButton from "@/components/common/buttons/BaseTextButton.vue";
import BaseWlsButtonSave from "@/components/common/buttons/BaseWlsButtonSave.vue";
import BaseStimmzettelZusammenfassungCard from "@/components/dse/stimmzettelerfassung/baseComponents/BaseStimmzettelZusammenfassungCard.vue";
import TheEingabehistorieCard from "@/components/dse/stimmzettelerfassung/TheEingabehistorieCard.vue";
import TheStimmzettelCommandProcessingTextField from "@/components/dse/stimmzettelerfassung/TheStimmzettelCommandProcessingTextField.vue";
import TheStimmzettelContent from "@/components/dse/stimmzettelerfassung/TheStimmzettelContent.vue";
import { useStimmzettelerfassungDialogUtils } from "@/composables/dse/stimmzettelerfassung/stimmzettelerfassungDialogUtils.ts";
import { useUserStore } from "@/stores/userStore.ts";

const isDialogVisibleModel = defineModel("modelValue", {
  type: Boolean,
  required: false,
});

const props = defineProps({
  stimmzettel: {
    type: Object as PropType<Stimmzettel>,
    required: true,
  },
  wahlvorschlaege: {
    type: Array as PropType<Wahlvorschlag[]>,
    required: true,
  },
});

const emit = defineEmits<{
  cancel: [];
  confirm: [stimmzettel: Stimmzettel];
}>();

const route = useRoute();
const wahlID = route.params.wahlId as string;

const { stimmzettelManager } = useStimmzettelerfassungDialogUtils(
  props.wahlvorschlaege,
  wahlID
);

const { currentUserTeamName } = storeToRefs(useUserStore());

const changeHistory = computed(
  () => stimmzettelManager.managedStimmzettel.changeHistory
);
const wahlvorschlagIdOfLatestChangeWahlvorschlag = computed<string | null>(
  () =>
    changeHistory.value.lastUsedWahlvorschlag?.value?.wahlvorschlagID ?? null
);
const latestChangedKandidat = computed<Kandidat | null>(
  () => changeHistory.value.lastUsedKandidat.value ?? null
);

function onCancelClicked() {
  emit("cancel");
}

function onSavedClicked() {
  emit("confirm", props.stimmzettel);
}

function onResetClicked() {
  stimmzettelManager.managedStimmzettel.resetStimmzettel();
}
</script>
