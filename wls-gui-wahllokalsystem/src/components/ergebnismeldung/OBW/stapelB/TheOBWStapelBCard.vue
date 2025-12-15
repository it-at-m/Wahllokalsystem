<template>
  <v-container>
    <v-card>
      <v-form v-model="stapelBInputsForm">
        <div v-if="!isUWB">
          <v-card-title>Leere Stimmzettelumschläge</v-card-title>
          <v-card-text>
            <base-number-input
              :model-value="ergebnisseStapelBLeer"
              :rules="[required, minNumber(0), maxNumber(9999)]"
              min-width="20rem"
              @update:model-value="
                onModelValueStapelBChanged(StapelArtEnum.ObwBLeer, $event)
              "
            />
          </v-card-text>
        </div>
        <v-card-title>Ungekennzeichnete Stimmzettel</v-card-title>
        <v-card-text>
          <base-number-input
            :model-value="ergebnisseStapelBUngekennzeichnet"
            :rules="[required, minNumber(0), maxNumber(9999)]"
            min-width="20rem"
            @update:model-value="
              onModelValueStapelBChanged(
                StapelArtEnum.ObwBUngekennzeichnet,
                $event
              )
            "
          />
        </v-card-text>
        <v-card-title v-if="!isUWB">Summe: {{ sumStapelB }}</v-card-title>
      </v-form>
      <v-card-actions>
        <base-button-save
          :loading="isErgebnisseSaving"
          :disabled="!stapelBInputsForm"
          @click="onSaveAnzahlStimmzettelClicked"
        />
      </v-card-actions>
    </v-card>
  </v-container>
</template>

<script setup lang="ts">
import type { Ergebnis } from "@/types/ergebnismeldung/Ergebnis.ts";

import { storeToRefs } from "pinia";
import { computed, ref } from "vue";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import BaseNumberInput from "@/components/common/inputs/BaseNumberInput.vue";
import { useRules } from "@/composables/common/rules.ts";
import { useOBWStapelBUtils } from "@/composables/ergebnismeldung/OBW/obwStapelBUtils.ts";
import { useErgebnismeldungStore } from "@/stores/ergebnismeldungStore.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

const { maxNumber, minNumber, required } = useRules();

const props = defineProps<{
  wahlId: string;
}>();

const { isUWB } = storeToRefs(useUserStore());
const {
  sendErgebnisseByStapelArt,
  findAndUpdateErgebnisseByWahlIdAndStapelArt,
} = useErgebnismeldungStore();
const { isErgebnisseSaving } = storeToRefs(useErgebnismeldungStore());
const { ergebnisseStapelBUngekennzeichnet, ergebnisseStapelBLeer, sumStapelB } =
  useOBWStapelBUtils(computed(() => props.wahlId));

const stapelBInputsForm = ref<null | boolean>(null);

function onModelValueStapelBChanged(
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

function onSaveAnzahlStimmzettelClicked() {
  if (!isUWB.value) {
    sendErgebnisseByStapelArt(props.wahlId, StapelArtEnum.ObwBLeer);
  }
  sendErgebnisseByStapelArt(props.wahlId, StapelArtEnum.ObwBUngekennzeichnet);
}
</script>
