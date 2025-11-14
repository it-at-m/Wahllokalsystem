<template>
  <div>
    <v-card>
      <v-card-title>Beginn der Stimmabgabe</v-card-title>
      <v-card-text class="pb-0">
        <slot name="userHint" />
        <v-form v-model="isEroeffnungsuhrzeitFormValid">
          <base-time-input
            v-model="eroeffnungsuhrzeitState.eroeffnungsuhrzeit"
            class="mt-5"
            max-width="300"
            :rules="[
              required,
              timeNotInFuture,
              timeGreaterOrEqual(fruehesteEroeffnungsuhrzeit),
              timeLessOrEqual(fruehesteSchliessungsuhrzeit),
            ]"
          />
        </v-form>
      </v-card-text>
      <v-card-actions>
        <base-button-save
          :loading="eroeffnungsuhrzeitState.eroeffnungsuhrzeitIsSaving"
          :disabled="isSaveButtonDisabled"
          @click="onSaveEroeffnungsuhrzeitClicked"
        />
      </v-card-actions>
    </v-card>
    <base-dialog-begruendung
      :visible="isZuSpaet"
      dialogtitle="Verspäteter Beginn der Wahlhandlung"
      label="Begründung"
      :max-length-for-begruendung="
        MAX_LENGTH_FOR_TEXT_INPUT - BEGRUENDUNG_PREFIX.length
      "
      data-test="zuSpaetDialog"
      @cancel="onCancelBegruendung"
      @confirm="onConfirmBegruendung"
    >
      <span>
        Die eingetragene Uhrzeit ist nach
        {{ toHhMm(createTodayWithTime(spaetesteEroeffnungsuhrzeit)) }} Uhr,
        bitte begründen Sie die verspätete Eröffnung der Wahlhandlung in Form
        eines besonderen Vorfalls.
      </span>
    </base-dialog-begruendung>
  </div>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";
import { computed, ref } from "vue";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import BaseDialogBegruendung from "@/components/common/dialogs/BaseDialogBegruendung.vue";
import BaseTimeInput from "@/components/common/inputs/BaseTimeInput.vue";
import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";
import { useDateTimeUtils } from "@/composables/common/dateTimeUtils.ts";
import { useRules } from "@/composables/common/rules.ts";
import { MAX_LENGTH_FOR_TEXT_INPUT } from "@/constants.ts";
import { useEreignisStore } from "@/stores/ereignisStore.ts";
import { useInfomanagementStore } from "@/stores/infomanagementStore.ts";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";

const { required, timeGreaterOrEqual, timeLessOrEqual, timeNotInFuture } =
  useRules();

const { toHhMm } = useDateTimeFormatter();
const { createTodayWithTime } = useDateTimeUtils();

const { eroeffnungsuhrzeitActions } = useWahlbezirkStore();
const { eroeffnungsuhrzeitState } = storeToRefs(useWahlbezirkStore());
const {
  fruehesteEroeffnungsuhrzeit,
  fruehesteSchliessungsuhrzeit,
  spaetesteEroeffnungsuhrzeit,
} = storeToRefs(useInfomanagementStore());
const ereignisStore = useEreignisStore();
const { addEreignis, sendEreignisse } = ereignisStore;

const isEroeffnungsuhrzeitFormValid = ref<boolean | null>(null);
const isZuSpaet = ref(false);

const BEGRUENDUNG_PREFIX = "Verspätete Eröffnung: ";

const isSaveButtonDisabled = computed(
  () => isEroeffnungsuhrzeitFormValid.value !== true
);

function onSaveEroeffnungsuhrzeitClicked() {
  if (
    eroeffnungsuhrzeitState.value.eroeffnungsuhrzeit !== undefined &&
    eroeffnungsuhrzeitState.value.eroeffnungsuhrzeit <=
      createTodayWithTime(spaetesteEroeffnungsuhrzeit.value)
  ) {
    eroeffnungsuhrzeitActions.sendEroeffnungsuhrzeit();
  } else {
    isZuSpaet.value = true;
  }
}

function onCancelBegruendung() {
  isZuSpaet.value = false;
  eroeffnungsuhrzeitState.value.eroeffnungsuhrzeit = undefined;
}

function onConfirmBegruendung(begruendung: string): void {
  isZuSpaet.value = false;

  addEreignis({
    uhrzeit: eroeffnungsuhrzeitState.value.eroeffnungsuhrzeit,
    beschreibung: BEGRUENDUNG_PREFIX + begruendung,
  });
  sendEreignisse();

  eroeffnungsuhrzeitActions.sendEroeffnungsuhrzeit();
}
</script>
