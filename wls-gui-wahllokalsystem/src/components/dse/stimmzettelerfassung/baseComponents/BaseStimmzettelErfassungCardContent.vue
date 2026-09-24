<template>
  <v-card-text
    style="min-height: 0"
    class="ga-3 d-flex"
  >
    <div
      class="d-flex flex-column"
      style="flex: 0 0 250px; min-width: 0"
    >
      <the-eingabehistorie-card
        :change-history="changeHistory.changeHistoryInReverseOrder.value"
        class="d-flex flex-column"
      />
      <base-stimmzettel-zusammenfassung-card
        class="mt-2 d-flex flex-column"
        :listenstimmen="
          stimmzettelManager.bearbeitenDialogStimmzettelUtils
            .wahlvorschlaegeWithListenkreuz.value
        "
        :ungueltigestimmen="
          stimmzettelManager.bearbeitenDialogStimmzettelUtils.stimmenSummary
            .value.ungueltigeStimmen
        "
        :direktstimmen="
          stimmzettelManager.bearbeitenDialogStimmzettelUtils.stimmenSummary
            .value.einzelstimmen
        "
        :reststimmen="
          stimmzettelManager.bearbeitenDialogStimmzettelUtils.stimmenSummary
            .value.reststimmen
        "
        :streichungen="
          stimmzettelManager.bearbeitenDialogStimmzettelUtils.stimmenSummary
            .value.streichungen
        "
        :gueltigkeit="
          stimmzettelManager.bearbeitenDialogStimmzettelUtils.stimmzettel.value
            .gueltigkeit
        "
      />
    </div>
    <div
      class="flex-1-1 d-flex flex-column"
      style="min-height: 0; min-width: 0"
    >
      <the-stimmzettel-command-processing-text-field
        :ref="COMMAND_PROCESSING_TEXT_FIELD_TEMPLATE_REF_NAME"
        class="flex-0-0"
        :stimmzettel-manager="stimmzettelManager"
        :disabled="isCommandInputFieldDisabled"
      />
      <div
        class="flex-1-1-0 d-flex"
        style="min-height: 0; min-width: 0"
      >
        <the-stimmzettel-content
          :active-wahlvorschlag-id="latestChangedWahlvorschlagId"
          :active-kandidat="latestChangedKandidat"
          :wahlvorschlaege="
            stimmzettelManager.bearbeitenDialogStimmzettelUtils.stimmzettel
              .value.wahlvorschlaege
          "
          style="min-height: 0; overflow-y: auto; min-width: 0"
        />
      </div>
    </div>
    <div
      class="d-flex flex-column"
      style="flex: 0 0 250px; min-width: 0"
    >
      <base-stimmzettel-sonderfaelle-card
        v-model:invalid-votes="
          stimmzettelManager.bearbeitenDialogStimmzettelUtils.stimmzettel.value
            .invalideVotes
        "
        v-model:gueltigkeit="
          stimmzettelManager.bearbeitenDialogStimmzettelUtils.stimmzettel.value
            .gueltigkeit
        "
        v-model:wahlvorstand-beschlussvorschlag="
          stimmzettelManager.bearbeitenDialogStimmzettelUtils.stimmzettel.value
            .wahlvorstandBeschlussvorschlag
        "
        :deny-selection-of-stimmzettel-fehlt="
          stimmzettelManager.bearbeitenDialogStimmzettelUtils.hasAnyValuesSet
            .value
        "
        :deny-selection-of-stimmzettel-leer="
          stimmzettelManager.bearbeitenDialogStimmzettelUtils.hasAnyValuesSet
            .value
        "
        :team-id="currentUserTeamName"
        :system-beschlussgruende="
          stimmzettelManager.bearbeitenDialogStimmzettelUtils.stimmzettel.value
            .systemBeschlussvorschlag
        "
        :stimmzettelkennung="stimmzettel.stimmzettelkennung"
        :is-b-w-b="isBWB"
      />
    </div>
  </v-card-text>
</template>

<script setup lang="ts">
import type { StimmzettelManager } from "@/composables/dse/stimmzettelerfassung/stimmzettelManager.ts";
import type { DseKandidat } from "@/types/dse/stimmzettelerfassung/DseKandidat.ts";
import type { PersistedStimmzettel } from "@/types/dse/stimmzettelerfassung/PersistedStimmzettel.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";
import type { PropType } from "vue";

import { storeToRefs } from "pinia";
import { computed } from "vue";

import BaseStimmzettelSonderfaelleCard from "@/components/dse/stimmzettelerfassung/baseComponents/BaseStimmzettelSonderfaelleCard.vue";
import BaseStimmzettelZusammenfassungCard from "@/components/dse/stimmzettelerfassung/baseComponents/BaseStimmzettelZusammenfassungCard.vue";
import TheEingabehistorieCard from "@/components/dse/stimmzettelerfassung/TheEingabehistorieCard.vue";
import TheStimmzettelCommandProcessingTextField from "@/components/dse/stimmzettelerfassung/TheStimmzettelCommandProcessingTextField.vue";
import TheStimmzettelContent from "@/components/dse/stimmzettelerfassung/TheStimmzettelContent.vue";
import { useUserStore } from "@/stores/userStore.ts";
import { StimmzettelGueltigkeitEnum } from "@/types/dse/stimmzettelerfassung/StimmzettelGueltigkeitEnum.ts";

const { currentUserTeamName, isBWB } = storeToRefs(useUserStore());

const stimmzettelManager = defineModel("modelValue", {
  type: Object as PropType<StimmzettelManager>,
  required: true,
});

const properties = defineProps({
  stimmzettel: {
    type: Object as PropType<PersistedStimmzettel>,
    required: true,
  },
  wahlvorschlaege: {
    type: Array as PropType<Wahlvorschlag[]>,
    required: true,
  },
  stimmzettelGueltigkeit: {
    type: Object as PropType<StimmzettelGueltigkeitEnum>,
    required: true,
  },
});

const changeHistory = computed(
  () => stimmzettelManager.value.bearbeitenDialogStimmzettelUtils.changeHistory
);
const isCommandInputFieldDisabled = computed(
  () =>
    properties.stimmzettelGueltigkeit ===
      StimmzettelGueltigkeitEnum.BwbPseudoStimmzettelLeererUmschlag ||
    properties.stimmzettelGueltigkeit === StimmzettelGueltigkeitEnum.Leer
);
const latestChangedWahlvorschlagId = computed<string | null>(
  () =>
    changeHistory.value.lastUsedWahlvorschlag?.value?.wahlvorschlagID ?? null
);
const latestChangedKandidat = computed<DseKandidat | null>(
  () => changeHistory.value.lastUsedKandidat.value ?? null
);
</script>
