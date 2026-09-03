<template>
  <v-card>
    <v-card-title>Ungültige Stimmzettel und Stimmen</v-card-title>
    <v-card-text>
      <v-checkbox
        :model-value="isStimmzettelLeerSelected"
        label="Stimmzettel ist leer"
        :disabled="isCheckboxStimmzettelLeerDisabled"
        density="compact"
        hide-details
        @update:model-value="onStimmzettelLeerChanged"
      />
      <v-checkbox
        v-if="isBWB"
        :model-value="isStimmzettelFehltSelected"
        label="Stimmzettel fehlt"
        :disabled="isCheckboxStimmzettelFehltDisabled"
        density="compact"
        hide-details
        @update:model-value="onStimmzettelFehltChanged"
      />
      <div class="d-flex justify-space-between align-center">
        <div>ungültige Stimmen die nicht zugeordnet werden können</div>
        <div style="flex: 0 1 100px">
          <v-number-input
            v-model="modelValueInvalidVotes"
            :disabled="isInputOfInvalidVotesDisabled"
            control-variant="hidden"
            density="compact"
            hide-details
            :min="0"
          />
        </div>
      </div>
    </v-card-text>
    <v-card-title v-if="showBeschlussfassung">Beschlussfassung</v-card-title>
    <v-card-text v-if="showBeschlussfassung">
      <v-checkbox
        :model-value="false"
        label="für Beschlussfassung vormerken"
        :disabled="isCheckboxMarkeForBeschlussfassungDisabled"
        class="mb-4"
        density="compact"
      />
      Begründung auswählen oder eingeben
      <v-combobox
        v-model="stimmzettelWahlvorstandBeschlussgruende"
        :items="wahlvorstandBeschlussvorschlaegeItems"
        class="combobox-as-textarea mt-1"
        multiple
        chips
        closable-chips
        hide-details
      />
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import type { Stimmzettel } from "@/types/dse/stimmzettelerfassung/Stimmzettel.ts";
import type { PropType } from "vue";

import { storeToRefs } from "pinia";
import { computed } from "vue";

import { useBeschlussgrundTools } from "@/composables/dse/beschlussfassung/beschlussgrundTools.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { StimmzettelGueltigkeitEnum } from "@/types/dse/stimmzettelerfassung/StimmzettelGueltigkeitEnum.ts";

const { isBWB } = storeToRefs(useUserStore());

const { createBeschlussgrundWithText } = useBeschlussgrundTools();

const modelValueInvalidVotes = defineModel("modelValueInvalidVotes", {
  type: Number,
  required: true,
});
const modelValueGueltigkeit = defineModel("modelValueGueltigkeit", {
  type: Object as PropType<StimmzettelGueltigkeitEnum | null>,
  required: true,
});
const modelValueStimmzettel = defineModel("modelValue", {
  type: Object as PropType<Stimmzettel>,
  required: true,
});

const props = defineProps({
  showBeschlussfassung: {
    type: Boolean,
    required: false,
    default: true,
  },
  denySelectionOfStimmzettelLeer: {
    type: Boolean,
    required: false,
    default: false,
  },
  denySelectionOfStimmzettelFehlt: {
    type: Boolean,
    required: false,
    default: false,
  },
  denyInputForInvalidVotes: {
    type: Boolean,
    required: false,
    default: false,
  },
});

const stimmzettelWahlvorstandBeschlussgruende = computed({
  get: () =>
    modelValueStimmzettel.value.wahlvorstandBeschlussvorschlag.map(
      (grund) => grund.text
    ),
  set: (gruende) => {
    modelValueStimmzettel.value.wahlvorstandBeschlussvorschlag = gruende.map(
      createBeschlussgrundWithText
    );
  },
});

const hasInvalidVotes = computed(() => modelValueInvalidVotes.value > 0);

const isCheckboxMarkeForBeschlussfassungDisabled = computed(
  () => isStimmzettelLeerSelected.value || isStimmzettelFehltSelected.value
);

const isStimmzettelLeerSelected = computed(
  () => modelValueGueltigkeit.value === StimmzettelGueltigkeitEnum.Leer
);
const isStimmzettelFehltSelected = computed(
  () =>
    modelValueGueltigkeit.value ===
    StimmzettelGueltigkeitEnum.BwbPseudoStimmzettelLeererUmschlag
);

const isCheckboxStimmzettelFehltDisabled = computed(
  () =>
    props.denySelectionOfStimmzettelFehlt ||
    isStimmzettelLeerSelected.value ||
    hasInvalidVotes.value
);

const isCheckboxStimmzettelLeerDisabled = computed(
  () =>
    props.denySelectionOfStimmzettelLeer ||
    isStimmzettelFehltSelected.value ||
    hasInvalidVotes.value
);

const isInputOfInvalidVotesDisabled = computed(
  () =>
    props.denyInputForInvalidVotes ||
    isStimmzettelFehltSelected.value ||
    isStimmzettelLeerSelected.value
);

const wahlvorstandBeschlussvorschlaegeItems = [
  "unzulässiger Zusatz/Vorbehalt",
  "Stimmzettel vollständig durchgestrichen",
  "Wählerwille nicht zweifelsfrei erkennbar",
  "handschriftlich ergänzte Person",
  "Kennzeichnung nicht eindeutig zuzuordnen",
];

function onStimmzettelLeerChanged(newValue: boolean | null) {
  if (newValue) {
    modelValueGueltigkeit.value = StimmzettelGueltigkeitEnum.Leer;
  } else {
    modelValueGueltigkeit.value = null;
  }
}

function onStimmzettelFehltChanged(newValue: boolean | null) {
  if (newValue) {
    modelValueGueltigkeit.value =
      StimmzettelGueltigkeitEnum.BwbPseudoStimmzettelLeererUmschlag;
  } else {
    modelValueGueltigkeit.value = null;
  }
}
</script>
