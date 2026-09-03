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
        :model-value="isCheckboxMarkForBeschlussfassingSelected"
        label="für Beschlussfassung vormerken"
        :disabled="isCheckboxMarkeForBeschlussfassungDisabled"
        class="mb-4"
        density="compact"
        :hint="systemBeschlussgruendeAsText"
        :persistent-hint="!!systemBeschlussgruendeAsText"
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
import type { SystemBeschlussgrund } from "@/types/dse/beschlussfassung/SystemBeschlussgrund.ts";
import type { WahlvorstandBeschlussgrund } from "@/types/dse/beschlussfassung/WahlvorstandBeschlussgrund.ts";
import type { PropType } from "vue";

import { storeToRefs } from "pinia";
import { computed } from "vue";

import { useBeschlussgrundTools } from "@/composables/dse/beschlussfassung/beschlussgrundTools.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { StimmzettelGueltigkeitEnum } from "@/types/dse/stimmzettelerfassung/StimmzettelGueltigkeitEnum.ts";

const { isBWB } = storeToRefs(useUserStore());

const { createBeschlussgrundWithText } = useBeschlussgrundTools();

//TODO kann noch null werden wenn man es löscht
const modelValueInvalidVotes = defineModel("modelValueInvalidVotes", {
  type: Number,
  required: true,
});
const modelValueGueltigkeit = defineModel("modelValueGueltigkeit", {
  type: [Object, null] as PropType<StimmzettelGueltigkeitEnum | null>,
  required: true,
});
const modelValueWahlvorstandBeschlussvorschlag = defineModel(
  "modelValueWahlvorstandBeschlussvorschlag",
  {
    type: Array as PropType<WahlvorstandBeschlussgrund[]>,
    required: true,
  }
);

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
  systemBeschlussgruende: {
    type: Array as PropType<SystemBeschlussgrund[]>,
    required: true,
  },
});

const stimmzettelWahlvorstandBeschlussgruende = computed({
  get: () =>
    modelValueWahlvorstandBeschlussvorschlag.value.map((grund) => grund.text),
  set: (gruende) => {
    modelValueWahlvorstandBeschlussvorschlag.value = gruende.map(
      createBeschlussgrundWithText
    );
  },
});

const hasInvalidVotes = computed(() => modelValueInvalidVotes.value > 0);
const hasSystemBeschlussGrund = computed(
  () => props.systemBeschlussgruende.length > 0
);

const isCheckboxMarkeForBeschlussfassungDisabled = computed(
  () =>
    isStimmzettelLeerSelected.value ||
    isStimmzettelFehltSelected.value ||
    hasSystemBeschlussGrund.value
);

const isStimmzettelLeerSelected = computed(
  () => modelValueGueltigkeit.value === StimmzettelGueltigkeitEnum.Leer
);
const isStimmzettelFehltSelected = computed(
  () =>
    modelValueGueltigkeit.value ===
    StimmzettelGueltigkeitEnum.BwbPseudoStimmzettelLeererUmschlag
);

const isCheckboxMarkForBeschlussfassingSelected = computed(
  () => hasSystemBeschlussGrund.value
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

const systemBeschlussgruendeAsText = computed(() =>
  props.systemBeschlussgruende.map((grund) => grund.reason).join(", ")
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
