<template>
  <v-card>
    <v-card-title>{{ TITEL_SONDERFAELLE }}</v-card-title>
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
      <div class="d-flex justify-space-between align-center ga-1">
        <div>ungültige Stimmen die nicht zugeordnet werden können</div>
        <div style="flex: 0 1 130px">
          <v-number-input
            v-model="modelValueInvalidVotes"
            :disabled="isInputOfInvalidVotesDisabled"
            control-variant="stacked"
            density="compact"
            hide-details
            :clearable="false"
            :min="0"
          />
        </div>
      </div>

      <base-dialog
        :visible="isStimmzettelFehltInstructionDialogVisible"
        dialogtitle="Bitte Kennung anbringen"
        confirmtext="Bestätigen"
        icon="$information"
        @confirm="onStimmzettelFehlInstructionDialogConfirm"
      >
        <div>
          Bitte notieren Sie die Stimmzettelkennung des fehlenden Stimmzettels
          auf dem Umschlag oder auf dem Hilfsblatt.
        </div>

        <base-stimmzettelkennung-strong-text
          :stimmzettelkennung="stimmzettelkennung"
          :team-name="teamId"
        />
      </base-dialog>
    </v-card-text>
    <v-card-title v-if="showBeschlussfassung">Beschlussfassung</v-card-title>
    <v-card-text v-if="showBeschlussfassung">
      <v-checkbox
        :model-value="isCheckboxMarkForBeschlussfassungSelected"
        label="für Beschlussfassung vormerken"
        :disabled="isCheckboxMarkeForBeschlussfassungDisabled"
        class="mb-4"
        density="compact"
        :hint="systemBeschlussgruendeAsText"
        :persistent-hint="!!systemBeschlussgruendeAsText"
        @update:model-value="onMarkForBeschlussfassungModelUpdated"
      />
      Begründung auswählen oder eingeben (abweichende Gründe mit Enter
      bestätigen)
      <v-form v-model="modelValueIsBeschlussfassungValid">
        <v-combobox
          :ref="REF_COMBOBOX_WAHLVORSTAND_BESCHLUSSVORSCHLAEGE"
          v-model="stimmzettelWahlvorstandBeschlussgruende"
          :items="wahlvorstandBeschlussvorschlaegeItems"
          class="combobox-as-textarea mt-1"
          multiple
          chips
          closable-chips
          :rules="wahlvorstandBeschlussvorschlaegeRules"
        />
      </v-form>
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import type { SystemBeschlussgrund } from "@/types/dse/beschlussfassung/SystemBeschlussgrund.ts";
import type { WahlvorstandBeschlussgrund } from "@/types/dse/beschlussfassung/WahlvorstandBeschlussgrund.ts";
import type { PropType } from "vue";

import { computed, nextTick, ref, useTemplateRef } from "vue";
import { VCombobox } from "vuetify/components";

import BaseDialog from "@/components/common/dialogs/BaseDialog.vue";
import BaseStimmzettelkennungStrongText from "@/components/dse/stimmzettelerfassung/baseComponents/BaseStimmzettelkennungStrongText.vue";
import { useRules } from "@/composables/common/rules.ts";
import { useBeschlussgrundTools } from "@/composables/dse/beschlussfassung/beschlussgrundTools.ts";
import { useSystemBeschlussgrundReasonEnumTools } from "@/composables/dse/stimmzettelerfassung/systemBeschlussgrundReasonEnumTools.ts";
import { TITEL_SONDERFAELLE } from "@/constants.ts";
import { StimmzettelGueltigkeitEnum } from "@/types/dse/stimmzettelerfassung/StimmzettelGueltigkeitEnum.ts";

const REF_COMBOBOX_WAHLVORSTAND_BESCHLUSSVORSCHLAEGE =
  "comboBoxWahlvorstandBeschlussgruende";

const { createBeschlussgrundWithText, getWahlvorstandBeschlussvorschlaege } =
  useBeschlussgrundTools();
const { required } = useRules();
const { mapSystemBeschlussgrundReasonEnumToText } =
  useSystemBeschlussgrundReasonEnumTools();

const modelValueInvalidVotes = defineModel("invalidVotes", {
  type: [Number, null] as PropType<number | null>,
  required: true,
});
const modelValueIsBeschlussfassungValid = defineModel("beschlussfassungValid", {
  type: Boolean,
  required: false,
  default: true,
});
const modelValueGueltigkeit = defineModel("gueltigkeit", {
  type: [String, null] as PropType<StimmzettelGueltigkeitEnum | null>,
  required: true,
});
const modelValueWahlvorstandBeschlussvorschlag = defineModel(
  "wahlvorstandBeschlussvorschlag",
  {
    type: Array as PropType<WahlvorstandBeschlussgrund[]>,
    required: true,
  }
);

const props = defineProps({
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
  isBWB: {
    type: Boolean,
    required: true,
  },
  showBeschlussfassung: {
    type: Boolean,
    required: false,
    default: true,
  },
  stimmzettelkennung: {
    type: Number,
    required: true,
  },
  systemBeschlussgruende: {
    type: Array as PropType<SystemBeschlussgrund[]>,
    required: true,
  },
  teamId: {
    type: String,
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

const hasInvalidVotes = computed(() =>
  modelValueInvalidVotes.value === null
    ? false
    : modelValueInvalidVotes.value > 0
);
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

const isCheckboxMarkForBeschlussfassungSelected = computed(
  () =>
    hasSystemBeschlussGrund.value ||
    modelValueGueltigkeit.value ===
      StimmzettelGueltigkeitEnum.BeschlussAusstehend
);

const isCheckboxStimmzettelFehltDisabled = computed(
  () =>
    props.denySelectionOfStimmzettelFehlt ||
    isStimmzettelLeerSelected.value ||
    isCheckboxMarkForBeschlussfassungSelected.value ||
    hasInvalidVotes.value
);

const isCheckboxStimmzettelLeerDisabled = computed(
  () =>
    props.denySelectionOfStimmzettelLeer ||
    isStimmzettelFehltSelected.value ||
    isCheckboxMarkForBeschlussfassungSelected.value ||
    hasInvalidVotes.value
);

const isInputOfInvalidVotesDisabled = computed(
  () =>
    props.denyInputForInvalidVotes ||
    isStimmzettelFehltSelected.value ||
    isStimmzettelLeerSelected.value
);

const systemBeschlussgruendeAsText = computed(() =>
  props.systemBeschlussgruende
    .map((grund) => mapSystemBeschlussgrundReasonEnumToText(grund.reason))
    .join(", ")
);

const wahlvorstandBeschlussvorschlaegeItems = computed(() =>
  getWahlvorstandBeschlussvorschlaege(props.isBWB)
);
const wahlvorstandBeschlussvorschlaegeRules = computed(() => {
  if (
    isCheckboxMarkForBeschlussfassungSelected.value &&
    !hasSystemBeschlussGrund.value
  ) {
    return [required];
  } else {
    return [];
  }
});

const isStimmzettelFehltInstructionDialogVisible = ref(false);
const templateRefComboxBoxWahlvorstandBeschlussgruende = useTemplateRef<
  typeof VCombobox
>(REF_COMBOBOX_WAHLVORSTAND_BESCHLUSSVORSCHLAEGE);

function onMarkForBeschlussfassungModelUpdated(newValue: boolean | null) {
  if (newValue) {
    modelValueGueltigkeit.value =
      StimmzettelGueltigkeitEnum.BeschlussAusstehend;
  } else {
    modelValueGueltigkeit.value = null;
  }

  nextTick(() => {
    templateRefComboxBoxWahlvorstandBeschlussgruende.value?.validate();
  });
}

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
    isStimmzettelFehltInstructionDialogVisible.value = true;
  } else {
    modelValueGueltigkeit.value = null;
  }
}

function onStimmzettelFehlInstructionDialogConfirm() {
  isStimmzettelFehltInstructionDialogVisible.value = false;
}
</script>
