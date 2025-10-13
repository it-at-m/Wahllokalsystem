<template>
  <div>
    <v-card>
      <v-card-title>
        Stapel c - Stimmzettel, die Anlass zu Bedenken geben
      </v-card-title>
      <v-card-text>
        <base-table-row-manager
          :current-row-count="stapelCErgebnisseOrdereByNumIndex.length"
          :model-value="countRows"
          :rules="[minNumber(0), maxNumber(9999)]"
          @change-row-count-clicked="onApplyRowCountClicked"
        />
        <v-form v-model="isRowsFormValid">
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
          :loading="isSaving"
          @click="onSaveClicked"
        />
      </v-card-actions>
    </v-card>
    <the-o-b-w-stapel-c-deletion-denied-dialog
      :ref="REF_DELETION_DENIED_DIALOG"
    />
  </div>
</template>

<script setup lang="ts">
import type { ErgebnisAndStapelArt } from "@/types/ergebnisermittlung/ErgebnisAndStapelArt.ts";
import type { ErgebnisWithNumIndexAndStapel } from "@/types/ergebnisermittlung/ErgebnisWithNumIndexAndStapel.ts";
import type { Ergebnis } from "@/types/ergebnismeldung/Ergebnis.ts";

import { computed, onMounted, ref, useTemplateRef } from "vue";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import BaseTableRowManager from "@/components/common/tables/BaseTableRowManager.vue";
import BaseRowStapelC from "@/components/ergebnisermittlung/OBW/stapelC/BaseRowStapelC.vue";
import TheOBWStapelCDeletionDeniedDialog from "@/components/ergebnisermittlung/OBW/stapelC/TheOBWStapelCDeletionDeniedDialog.vue";
import { useRules } from "@/composables/common/rules.ts";
import { useOBWStapelCUtils } from "@/composables/ergebnisermittlung/obwStapelCUtils.ts";
import { useErgebnisUtils } from "@/composables/ergebnismeldung/ergebnisUtils.ts";

const { orderedByNumIndexWithNullAtEnd } = useErgebnisUtils();
const { minNumber, maxNumber } = useRules();

const REF_DELETION_DENIED_DIALOG = "refDeletionDeniedDialog";

const {
  addGueltigErgebnisse,
  isSaving,
  removeErgebnisseWithNumIndexAbove,
  getMaxNumIndex,
  getMaxNumIndexWithValueSet,
  getStimmzettelNumIndexThatPreventDeletion,
  saveErgebnisse,
  stapelCUngueltigErgebnisse,
  stapelCGueltigErgebnisse,
  switchStapelCOfErgebnis,
  wahlvorschlaege,
} = useOBWStapelCUtils(
  computed(() => props.wahlId),
  computed(() => props.wahlbezirkId)
);

const props = defineProps({
  wahlId: {
    type: String,
    required: true,
  },
  wahlbezirkId: {
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

const isRowsFormValid = ref<boolean | null>(null);
const countRows = ref<number | null>(null);
const templateRefDeletionDeniedDialog = useTemplateRef<
  typeof TheOBWStapelCDeletionDeniedDialog
>(REF_DELETION_DENIED_DIALOG);

const areStapelCGueltigeErgebnisseValid = computed(() =>
  stapelCGueltigErgebnisse.value.every(
    (value) => value.ergebnis.wahlvorschlagID !== null
  )
);

const isNewRowCountSameAsActualRowCount = computed(
  () => stapelCErgebnisseOrdereByNumIndex.value.length === countRows.value
);

onMounted(() => {
  countRows.value = stapelCErgebnisseOrdereByNumIndex.value.length;
});

function isTryingToRemoveNonEmptyValues() {
  return (
    countRows.value !== null &&
    countRows.value < (getMaxNumIndexWithValueSet() || 0)
  );
}

function onApplyRowCountClicked(newRowCount: number | null) {
  countRows.value = newRowCount;

  if (countRows.value === null || countRows.value === undefined) {
    return;
  }

  if (isTryingToRemoveNonEmptyValues()) {
    const stimmzettel = getStimmzettelNumIndexThatPreventDeletion(
      countRows.value
    );
    templateRefDeletionDeniedDialog.value?.showDialog(stimmzettel);
    return;
  }

  if (!isNewRowCountSameAsActualRowCount.value) {
    const maxUsedNumIndex = getMaxNumIndex();
    if (maxUsedNumIndex === null || countRows.value > maxUsedNumIndex) {
      addGueltigErgebnisse(countRows.value);
    } else {
      removeErgebnisseWithNumIndexAbove(countRows.value);
    }
  }
}

function onSaveClicked() {
  saveErgebnisse();
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
