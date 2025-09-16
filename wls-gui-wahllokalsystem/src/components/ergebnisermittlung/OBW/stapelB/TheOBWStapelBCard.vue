<template>
  <v-container>
    <v-card v-if="wahl">
      <v-form v-model="stapelBInputsForm">
        <div v-if="!isUWB">
          <v-card-title>Leere Stimmzettelumschläge</v-card-title>
          <v-card-text>
            <base-number-input
              :model-value="ergebnisseStapelBLeer"
              :rules="[required, minNumber(0), maxNumber(9999)]"
              min-width="20rem"
              label="Anzahl"
              @update:model-value="onmodelValueStapelBLeerChanged"
            />
          </v-card-text>
        </div>
        <v-card-title>Ungekennzeichnete Stimmzettel</v-card-title>
        <v-card-text>
          <base-number-input
            :model-value="ergebnisseStapelBUngekennzeichnet"
            :rules="[required, minNumber(0), maxNumber(9999)]"
            min-width="20rem"
            label="Anzahl"
            @update:model-value="onmodelValueStapelBUngekennzeichnetChanged"
          />
        </v-card-text>
        <v-card-title v-if="!isUWB">Summe: {{ sumStapelB }}</v-card-title>
      </v-form>
      <v-card-actions>
        <base-button-save
          :loading="stimmzettelumschlaegeState.isStimmzettelumschlaegeSaving"
          :disabled="isSaveButtonDisabled"
          @click="onSaveAnzahlStimmzettelClicked"
        />
      </v-card-actions>
    </v-card>
  </v-container>
</template>

<script setup lang="ts">
import type { Ergebnisse } from "@/types/ergebnismeldung/Ergebnisse.ts";

import { storeToRefs } from "pinia";
import { computed, ref } from "vue";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import BaseNumberInput from "@/components/common/inputs/BaseNumberInput.vue";
import { useRules } from "@/composables/common/rules.ts";
import { useErgebnismeldungStore } from "@/stores/ergebnismeldungStore.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

const { maxNumber, minNumber, required } = useRules();

const props = defineProps<{
  wahlId: string;
}>();

const { wahlenActions } = useWahlenStore();
const { stimmzettelumschlaegeState } = storeToRefs(useWahlenStore());
const { getWahlbezirkIdFromWahlMetaDataByWahlId } = useUserStore();
const { isUWB } = storeToRefs(useUserStore());
const {
  sendErgebnisseByStapelArt,
  getErgebnisseByWahlIdAndStapelartOrUndefined,
} = useErgebnismeldungStore();
const { ergebnisse } = storeToRefs(useErgebnismeldungStore());

const wahl = computed(() => wahlenActions.getWahlOrUndefinedById(props.wahlId));

const isSaveButtonDisabled = computed(() => {
  return !stapelBInputsForm.value;
});

const ergebnisseStapelBLeer = computed(() => {
  const ergebnisseFound = getErgebnisseByWahlIdAndStapelartOrUndefined(
    props.wahlId,
    StapelArtEnum.ObwBLeer
  );
  return ergebnisseFound?.ergebnisse[0].ergebnis;
});

const ergebnisseStapelBUngekennzeichnet = computed(() => {
  const ergebnisseFound = getErgebnisseByWahlIdAndStapelartOrUndefined(
    props.wahlId,
    StapelArtEnum.ObwBUngekennzeichnet
  );
  return ergebnisseFound?.ergebnisse[0].ergebnis;
});

const sumStapelB = computed(
  () =>
    (ergebnisseStapelBUngekennzeichnet.value || 0) +
    (ergebnisseStapelBLeer.value || 0)
);

const stapelBInputsForm = ref<null | boolean>(null);

function onmodelValueStapelBLeerChanged(newValue?: number | null | undefined) {
  if (newValue !== undefined) {
    findAndUpdateErgebnisse(StapelArtEnum.ObwBLeer, newValue);
  }
}

function onmodelValueStapelBUngekennzeichnetChanged(
  newValue?: number | null | undefined
) {
  if (newValue !== undefined) {
    findAndUpdateErgebnisse(StapelArtEnum.ObwBUngekennzeichnet, newValue);
  }
}

function onSaveAnzahlStimmzettelClicked() {
  sendErgebnisseByStapelArt(props.wahlId, StapelArtEnum.ObwBLeer);
  sendErgebnisseByStapelArt(props.wahlId, StapelArtEnum.ObwBUngekennzeichnet);
}

function findAndUpdateErgebnisse(
  stapelArt: StapelArtEnum,
  newValue: number | null
) {
  const ergebnisseFound = getErgebnisseByWahlIdAndStapelartOrUndefined(
    props.wahlId,
    stapelArt
  );
  if (ergebnisseFound) {
    ergebnisseFound.ergebnisse[0].ergebnis = newValue;
  } else {
    addNewErgebnisseForStapelart(stapelArt, newValue);
  }
}

function addNewErgebnisseForStapelart(
  stapelArt: StapelArtEnum,
  newValue: number | null
) {
  const wahlbezirkID = getWahlbezirkIdFromWahlMetaDataByWahlId(props.wahlId);

  if (wahlbezirkID) {
    const newErgebnisse: Ergebnisse = {
      bezirkUndWahlIDStapelart: {
        wahlID: props.wahlId,
        wahlbezirkID: wahlbezirkID,
        stapelArt: stapelArt,
      },
      ergebnisse: [
        {
          wahlvorschlagID: null,
          kandidatID: null,
          wahlvorschlagsOrdnungszahl: null,
          ergebnis: newValue,
          numIndex: null,
        },
      ],
    };
    ergebnisse.value.push(newErgebnisse);
  }
}
</script>
