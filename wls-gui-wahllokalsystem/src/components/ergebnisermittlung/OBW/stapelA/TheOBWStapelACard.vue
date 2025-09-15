<template>
  <v-card>
    <v-card-title>Gültige Stimmabgaben</v-card-title>
    <v-form v-model="isFormValid">
      <v-card-text>
        <v-table>
          <thead>
            <tr>
              <th />
              <th class="font-weight-bold">Wahlvorschlag</th>
              <th class="font-weight-bold">Stapel a: zweifelsfrei gültig</th>
            </tr>
          </thead>
          <tbody>
            <base-row-obw-stapel-a
              v-for="(
                ergebnisWithWahlvorschlag, index
              ) in ergebnisseWithWahlvorschlag"
              :key="index"
              :model-value="ergebnisWithWahlvorschlag.ergebnis"
              :wahlvorschlag="ergebnisWithWahlvorschlag.wahlvorschlag"
            />
          </tbody>
          <tfoot>
            <tr>
              <td />
              <td class="font-weight-bold">Gültige Stimmen insgesamt</td>
              <td class="font-weight-bold">{{ sumOfValidVotes }}</td>
            </tr>
          </tfoot>
        </v-table>
      </v-card-text>
      <v-card-actions>
        <base-button-save
          :disabled="!isFormValid"
          @click="onSaveClicked"
        />
      </v-card-actions>
    </v-form>
  </v-card>
</template>

<script setup lang="ts">
import type { ErgebnisAndWahlvorschlag } from "@/types/ergebnisermittlung/ErgebnisAndWahlvorschlag.ts";
import type { Ergebnis } from "@/types/ergebnismeldung/Ergebnis.ts";

import { computed, ref } from "vue";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import BaseRowObwStapelA from "@/components/ergebnisermittlung/OBW/stapelA/BaseRowOBWStapelA.vue";
import { useLogging } from "@/composables/common/logging.ts";
import { useErgebnisUtils } from "@/composables/ergebnismeldung/ergebnisUtils.ts";
import { useErgebnismeldungStore } from "@/stores/ergebnismeldungStore.ts";
import { useWahlvorschlaegeStore } from "@/stores/wahlvorschlaegeStore.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

const {
  sendErgebnisseByStapelArt,
  getErgebnisseByWahlIdAndStapelartOrUndefined,
} = useErgebnismeldungStore();
const { getWahlvorschlagOrUndefinedByWahlIDWahlbezirkIDAndWahlvorschlagID } =
  useWahlvorschlaegeStore();

const { orderedByNumIndexWithNullAtEnd } = useErgebnisUtils();
const { logWarn } = useLogging("TheOBWStapelACard");

const STAPEL = StapelArtEnum.ObwA;

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

const isFormValid = ref<boolean | null>(null);

const ergebnisseWithWahlvorschlag = computed<ErgebnisAndWahlvorschlag[]>(() => {
  return createErgebnisseAndWahlvorschlaege();
});

const sumOfValidVotes = computed(() =>
  ergebnisseWithWahlvorschlag.value
    .map((item) => item.ergebnis)
    .reduce((sum, ergebnis) => {
      return sum + (ergebnis.ergebnis ?? 0);
    }, 0)
);

function onSaveClicked() {
  sendErgebnisseByStapelArt(props.wahlID, STAPEL);
}

function addWahlvorschlagForErgebnisIfExisting(
  ergebnis: Ergebnis,
  result: ErgebnisAndWahlvorschlag[]
): void {
  if (ergebnis.wahlvorschlagID) {
    const wahlvorschlagForErgebnis =
      getWahlvorschlagOrUndefinedByWahlIDWahlbezirkIDAndWahlvorschlagID(
        props.wahlID,
        props.wahlbezirkID,
        ergebnis.wahlvorschlagID
      );
    if (wahlvorschlagForErgebnis) {
      result.push({
        ergebnis: ergebnis,
        wahlvorschlag: wahlvorschlagForErgebnis,
      });
    } else {
      logWarn(
        `ergebnis wahlID=${props.wahlID}, wahlbezirkID=${props.wahlbezirkID}, wahlvorschlagID=${ergebnis.wahlvorschlagID} hat keinen Wahlvorschlag`
      );
    }
  } else {
    logWarn(
      `ergebnis wahlID=${props.wahlID}, wahlbezirkID=${props.wahlbezirkID} ist ohne wahlvorschlagID`
    );
  }
}

function createErgebnisseAndWahlvorschlaege() {
  const result: ErgebnisAndWahlvorschlag[] = [];

  const ergebnisseForWahlAndStapel =
    loadErgebnisseForWahlAndStapelOrderedByNumIndex();

  ergebnisseForWahlAndStapel.forEach((ergebnis) => {
    addWahlvorschlagForErgebnisIfExisting(ergebnis, result);
  });

  return result;
}

function loadErgebnisseForWahlAndStapelOrderedByNumIndex(): Ergebnis[] {
  return (
    getErgebnisseByWahlIdAndStapelartOrUndefined(
      props.wahlID,
      STAPEL
    )?.ergebnisse.sort(orderedByNumIndexWithNullAtEnd) ?? []
  );
}
</script>
