<template>
  <div>
    <v-card>
      <v-card-title>Beginn der Stimmabgabe</v-card-title>
      <v-card-text class="pb-0">
        <slot name="userHint" />
        <v-form v-model="isEroeffnungsuhrzeitFormValid">
          <base-time-input
            v-model="eroeffnungsuhrzeit"
            class="mt-5"
            max-width="300"
            :rules="[
              REQUIRED,
              TIME_NOT_IN_FUTURE,
              TIME_GREATER_OR_EQUAL(fruehesteEroeffnungsuhrzeit),
              TIME_LESS_OR_EQUAL(fruehesteSchliessungsuhrzeit),
            ]"
          />
        </v-form>
      </v-card-text>
      <v-card-actions>
        <base-button-save
          active
          :loading="eroeffnungsuhrzeitIsSaving"
          :disabled="isSaveButtonDisabled"
          @click="onSaveEroeffnungsuhrzeitClicked"
        />
      </v-card-actions>
    </v-card>
    <base-dialog-begruendung
      :visible="isZuSpaet"
      dialogtitle=""
      label="Begründung"
      data-test="zuSpaetDialog"
      @cancel="onCancelBegruendung"
      @confirm="onConfirmBegruendung"
    >
      <span>
        Die eingetragene Uhrzeit ist nach
        {{ toHhMm(getDateFromTimeString(spaetesteEroeffnungsuhrzeit)) }} Uhr,
        bitte begründen Sie die verspätete Eröffnung der Wahlhandlung in Form
        eines besonderen Vorfalls.
      </span>
    </base-dialog-begruendung>
  </div>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";
import { computed, ref } from "vue";
import {
  VCard,
  VCardActions,
  VCardText,
  VCardTitle,
  VForm,
} from "vuetify/components";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import BaseDialogBegruendung from "@/components/common/dialogs/BaseDialogBegruendung.vue";
import BaseTimeInput from "@/components/common/inputs/BaseTimeInput.vue";
import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";
import { useEreignisStore } from "@/stores/ereignisStore.ts";
import { useInfomanagementStore } from "@/stores/infomanagementStore.ts";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";
import {
  REQUIRED,
  TIME_GREATER_OR_EQUAL,
  TIME_LESS_OR_EQUAL,
  TIME_NOT_IN_FUTURE,
} from "@/util/rules.ts";

const { getDateFromTimeString, toHhMm } = useDateTimeFormatter();

const wahlbezirkStore = useWahlbezirkStore();
const { eroeffnungsuhrzeit, eroeffnungsuhrzeitIsSaving } =
  storeToRefs(wahlbezirkStore);
const {
  fruehesteEroeffnungsuhrzeit,
  fruehesteSchliessungsuhrzeit,
  spaetesteEroeffnungsuhrzeit,
} = storeToRefs(useInfomanagementStore());
const ereignisStore = useEreignisStore();
const {
  addEreignis,
  updateUhrzeitByIndex,
  updateBeschreibungByIndex,
  sendEreignisse,
  wahlbezirkEreignisse,
} = ereignisStore;

const isEroeffnungsuhrzeitFormValid = ref<boolean | null>(null);
const isZuSpaet = ref(false);

const isSaveButtonDisabled = computed(
  () => isEroeffnungsuhrzeitFormValid.value !== true
);

function onSaveEroeffnungsuhrzeitClicked() {
  if (
    eroeffnungsuhrzeit.value !== undefined &&
    eroeffnungsuhrzeit.value <=
      getDateFromTimeString(spaetesteEroeffnungsuhrzeit.value)
  ) {
    wahlbezirkStore.sendEroeffnungsuhrzeit();
  } else {
    isZuSpaet.value = true;
  }
}

function onCancelBegruendung() {
  isZuSpaet.value = false;
  eroeffnungsuhrzeit.value = undefined;
}

function onConfirmBegruendung(begruendung: string): void {
  isZuSpaet.value = false;

  addEreignis();
  const index = wahlbezirkEreignisse.ereigniseintraege?.length
    ? wahlbezirkEreignisse.ereigniseintraege?.length - 1
    : 0;
  updateUhrzeitByIndex(eroeffnungsuhrzeit.value, index);
  updateBeschreibungByIndex(begruendung, index);
  sendEreignisse();

  wahlbezirkStore.sendEroeffnungsuhrzeit();
}
</script>
