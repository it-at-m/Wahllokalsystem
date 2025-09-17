<template>
  <v-card>
    <v-card-title>
      Stapel c - Stimmzettel, die Anlass zu Bedenken geben
    </v-card-title>
    <v-card-text>
      <div class="d-flex">
        <base-number-input
          :model-value="countRows"
          :rules="[minNumber(0), maxNumber(9999), required]"
          label="Anzahl"
          max-width="15rem"
        />
        <v-btn
          active
          :disabled="isApplyRowCountDisabled"
          class="ml-4 mt-3"
          @click="onApplyRowCountClicked"
          >Übernehmen</v-btn
        >
      </div>
      <v-form v-model="isFormValid">
        <v-table>
          <thead>
            <tr>
              <th />
              <th>Ungültig</th>
              <th>Gültig für Wahlvorschlag</th>
              <th />
            </tr>
          </thead>
          <tbody>
            <base-row-stapel-c
              v-for="(
                ergebnisAndStapel, index
              ) in stapelCErgebnisseOrdereByNumIndex"
              :key="
                ergebnisAndStapel.ergebnis.numIndex ??
                `${ergebnisAndStapel.stapelArt}-${index}`
              "
              :wahlvorschlaege="wahlvorschlaege"
              :stapel-art="ergebnisAndStapel.stapelArt"
              :model-value="ergebnisAndStapel.ergebnis"
              :index="index + 1"
              @selection-changed="
                onGueltigkeitOfRowChanged($event, ergebnisAndStapel)
              "
            />
          </tbody>
        </v-table>
      </v-form>
    </v-card-text>
    <v-card-actions>
      <base-button-save
        :disabled="!areStapelCGueltigeErgebnisseValid"
        @click="onSaveClicked"
      />
    </v-card-actions>
  </v-card>
</template>

<script setup lang="ts">
import type { ErgebnisAndStapelArt } from "@/types/ergebnisermittlung/ErgebnisAndStapelArt.ts";
import type { ErgebnisWithNumIndexAndStapel } from "@/types/ergebnisermittlung/ErgebnisWithNumIndexAndStapel.ts";
import type { Ergebnis } from "@/types/ergebnismeldung/Ergebnis.ts";

import { computed, ref } from "vue";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import BaseNumberInput from "@/components/common/inputs/BaseNumberInput.vue";
import BaseRowStapelC from "@/components/ergebnisermittlung/OBW/stapelC/BaseRowStapelC.vue";
import { useRules } from "@/composables/common/rules.ts";
import { useOBWStapelCUtils } from "@/composables/ergebnisermittlung/obwStapelCUtils.ts";
import { useErgebnisUtils } from "@/composables/ergebnismeldung/ergebnisUtils.ts";
import { useErgebnismeldungStore } from "@/stores/ergebnismeldungStore.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

const ergebnismeldungsStore = useErgebnismeldungStore();
const { orderedByNumIndexWithNullAtEnd } = useErgebnisUtils();
const { minNumber, maxNumber, required } = useRules();

const {
  stapelCUngueltigErgebnisse,
  stapelCGueltigErgebnisse,
  switchStapelCOfErgebnis,
  wahlvorschlaege,
} = useOBWStapelCUtils(
  computed(() => props.wahlID),
  computed(() => props.wahlbezirkID)
);

const props = defineProps({
  wahlID: {
    type: String,
    required: true,
  },
  wahlbezirkID: {
    type: String,
    required: true,
  },
});

const stapelCErgebnisseOrdereByNumIndex = computed(() => {
  return [
    ...stapelCUngueltigErgebnisse.value,
    ...stapelCGueltigErgebnisse.value,
  ].sort((a, b) => orderedByNumIndexWithNullAtEnd(a.ergebnis, b.ergebnis));
});

const isFormValid = ref<boolean | null>(null);
const countRows = ref<number | null>(null);

const areStapelCGueltigeErgebnisseValid = computed(() =>
  stapelCGueltigErgebnisse.value.every(
    (value) => value.ergebnis.wahlvorschlagID !== null
  )
);
const isApplyRowCountDisabled = computed(
  () =>
    stapelCErgebnisseOrdereByNumIndex.value.length === countRows.value ||
    countRows.value === null
);

function onApplyRowCountClicked() {}

function onSaveClicked() {
  ergebnismeldungsStore.sendErgebnisseByStapelArt(
    props.wahlID,
    StapelArtEnum.ObwCGueltig
  );
  ergebnismeldungsStore.sendErgebnisseByStapelArt(
    props.wahlID,
    StapelArtEnum.ObwCUngueltig
  );
}

function onGueltigkeitOfRowChanged(
  shouldSetStapelUngueltig: boolean,
  ergebnisAndStapelArt: ErgebnisAndStapelArt
) {
  if (!hasErgebnisNumIndex(ergebnisAndStapelArt)) {
    return;
  }

  removeWahlvorschlagIDIfNewStapelIsCUngueltig(
    ergebnisAndStapelArt.ergebnis,
    shouldSetStapelUngueltig
  );
  switchStapelCOfErgebnis(ergebnisAndStapelArt, shouldSetStapelUngueltig);
}

function hasErgebnisNumIndex(
  ergebnisAndStapelArt: ErgebnisAndStapelArt
): ergebnisAndStapelArt is ErgebnisWithNumIndexAndStapel {
  return ergebnisAndStapelArt.ergebnis.numIndex !== null;
}

function removeWahlvorschlagIDIfNewStapelIsCUngueltig(
  ergebnis: Ergebnis,
  isNewStapelCUngueltig: boolean
) {
  if (isNewStapelCUngueltig) {
    ergebnis.wahlvorschlagID = null;
  }
}
</script>
