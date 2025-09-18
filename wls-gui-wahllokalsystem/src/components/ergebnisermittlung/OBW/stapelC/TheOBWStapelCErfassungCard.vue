<template>
  <div>
    <v-card>
      <v-card-title>
        Stapel c - Stimmzettel, die Anlass zu Bedenken geben
      </v-card-title>
      <v-card-text>
        <v-form v-model="isChangeRowCountFormValid">
          <div class="d-flex">
            <base-number-input
              v-model="countRows"
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
        </v-form>
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
    <base-dialog
      :visible="isConfirmDialogVisible"
      :dialogtitle="CONFIRM_DIALOG_TITLE"
      confirmtext="Hinweis schließen"
      icon="$information"
      @confirm="onConfirmDialogConfirmClicked"
    >
      Sie haben die Anzahl der bedenklichen Stimmzettel verändert. Zeilen können
      nur gelöscht werden, wenn für einen Stimmzettel noch kein Beschluss im
      System gespeichert worden ist. Bitte überprüfen Sie Ihre Eingaben.
    </base-dialog>
  </div>
</template>

<script setup lang="ts">
import type { ErgebnisAndStapelArt } from "@/types/ergebnisermittlung/ErgebnisAndStapelArt.ts";
import type { ErgebnisWithNumIndexAndStapel } from "@/types/ergebnisermittlung/ErgebnisWithNumIndexAndStapel.ts";
import type { Ergebnis } from "@/types/ergebnismeldung/Ergebnis.ts";

import { computed, onMounted, ref } from "vue";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import BaseDialog from "@/components/common/dialogs/BaseDialog.vue";
import BaseNumberInput from "@/components/common/inputs/BaseNumberInput.vue";
import BaseRowStapelC from "@/components/ergebnisermittlung/OBW/stapelC/BaseRowStapelC.vue";
import { useRules } from "@/composables/common/rules.ts";
import { useOBWStapelCUtils } from "@/composables/ergebnisermittlung/obwStapelCUtils.ts";
import { useErgebnisUtils } from "@/composables/ergebnismeldung/ergebnisUtils.ts";
import { useErgebnismeldungStore } from "@/stores/ergebnismeldungStore.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

const CONFIRM_DIALOG_TITLE = "Reduzierung der Anzahl bedenklicher Stimmzettel";

const ergebnismeldungsStore = useErgebnismeldungStore();
const { orderedByNumIndexWithNullAtEnd } = useErgebnisUtils();
const { minNumber, maxNumber, required } = useRules();

const {
  addGueltigErgebnisse,
  removeErgebnisseWithNumIndexAbove,
  getMaxNumIndex,
  getMaxNumIndexWithValueSet,
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
const isConfirmDialogVisible = ref(false);
const isChangeRowCountFormValid = ref<boolean | null>(null);
const countRows = ref<number | null>(null);

const areStapelCGueltigeErgebnisseValid = computed(() =>
  stapelCGueltigErgebnisse.value.every(
    (value) => value.ergebnis.wahlvorschlagID !== null
  )
);
const isApplyRowCountDisabled = computed(
  () =>
    stapelCErgebnisseOrdereByNumIndex.value.length === countRows.value ||
    countRows.value === null ||
    isChangeRowCountFormValid.value !== true
);

onMounted(() => {
  countRows.value = stapelCErgebnisseOrdereByNumIndex.value.length;
});

function onApplyRowCountClicked() {
  if (countRows.value === null || countRows.value === undefined) {
    return;
  }

  if (countRows.value < (getMaxNumIndexWithValueSet() || 0)) {
    showConfirmDialog();
    return;
  }

  if (countRows.value !== stapelCErgebnisseOrdereByNumIndex.value.length) {
    const maxUsedNumIndex = getMaxNumIndex();
    if (maxUsedNumIndex === null) {
      addGueltigErgebnisse(countRows.value);
    } else if (countRows.value > maxUsedNumIndex) {
      addGueltigErgebnisse(countRows.value);
    } else {
      removeErgebnisseWithNumIndexAbove(countRows.value);
    }
  }
}

function onConfirmDialogConfirmClicked() {
  hideConfirmDialog();
}

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

function hideConfirmDialog() {
  isConfirmDialogVisible.value = false;
}

function removeWahlvorschlagIDIfNewStapelIsCUngueltig(
  ergebnis: Ergebnis,
  isNewStapelCUngueltig: boolean
) {
  if (isNewStapelCUngueltig) {
    ergebnis.wahlvorschlagID = null;
  }
}

function showConfirmDialog() {
  isConfirmDialogVisible.value = true;
}
</script>
