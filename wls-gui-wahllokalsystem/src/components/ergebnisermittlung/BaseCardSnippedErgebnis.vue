<template>
  <v-card>
    <v-form v-model="inputForm">
      <v-card-title>{{ snippedTitle }}</v-card-title>
      <v-card-text>
        <base-number-input
          :model-value="ergebnis"
          :rules="[required, minNumber(minValue), maxNumber(maxValue)]"
          min-width="20rem"
          @update:model-value="onModelValueChanged(props.stapelArt, $event)"
        />
      </v-card-text>
      <v-card-actions>
        <base-button-save
          :loading="isErgebnisseSaving"
          :disabled="!inputForm"
          @click="onSaveClicked"
        />
      </v-card-actions>
    </v-form>
  </v-card>
</template>

<script setup lang="ts">
import type { Ergebnis } from "@/types/ergebnismeldung/Ergebnis.ts";

import { storeToRefs } from "pinia";
import { computed, ref } from "vue";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import BaseNumberInput from "@/components/common/inputs/BaseNumberInput.vue";
import { useRules } from "@/composables/common/rules.ts";
import { useErgebnismeldungStore } from "@/stores/ergebnismeldungStore.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

const { maxNumber, minNumber, required } = useRules();
const {
  getErgebnisseByWahlIdAndStapelartOrUndefined,
  findAndUpdateErgebnisseByWahlIdAndStapelArt,
  sendErgebnisseByStapelArt,
} = useErgebnismeldungStore();
const { isErgebnisseSaving } = storeToRefs(useErgebnismeldungStore());

const props = defineProps({
  snippedTitle: {
    type: String,
    required: true,
  },
  wahlId: {
    type: String,
    required: true,
  },
  stapelArt: {
    type: String as () => StapelArtEnum,
    required: true,
  },
  minValue: {
    type: Number,
    required: false,
    default: 0,
  },
  maxValue: {
    type: Number,
    required: false,
    default: 9999,
  },
});

const ergebnis = computed(
  () =>
    getErgebnisseByWahlIdAndStapelartOrUndefined(
      props.wahlId,
      StapelArtEnum.MbwD
    )?.ergebnisse[0]?.ergebnis
);

const inputForm = ref<null | boolean>(null);

function onModelValueChanged(
  stapelArt: StapelArtEnum,
  newValue?: number | null | undefined
) {
  const ergebnis: Ergebnis = {
    wahlvorschlagID: null,
    kandidatID: null,
    wahlvorschlagsOrdnungszahl: null,
    ergebnis: newValue ?? null,
    numIndex: null,
  };
  findAndUpdateErgebnisseByWahlIdAndStapelArt(props.wahlId, stapelArt, [
    ergebnis,
  ]);
}

function onSaveClicked() {
  sendErgebnisseByStapelArt(props.wahlId, props.stapelArt);
}
</script>
