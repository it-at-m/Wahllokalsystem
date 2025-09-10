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
            <base-row-o-w-b-stapel-a
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
              <td class="font-weight-bold text-end">{{ sumOfValidVotes }}</td>
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
import type { Ergebnis } from "@/types/ergebnismeldung/Ergebnis";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag";

import { computed, ref } from "vue";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import BaseRowOWBStapelA from "@/components/ergebnisermittlung/OBW/stapelA/BaseRowOWBStapelA.vue";
import { useLogging } from "@/composables/common/logging";
import { useErgebnisUtils } from "@/composables/ergebnismeldung/ergebnisUtils";
import { useErgebnismeldungStore } from "@/stores/ergebnismeldungStore";
import { useWahlvorschlaegeStore } from "@/stores/wahlvorschlaegeStore";
import { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum";

const ergebnismeldungsStore = useErgebnismeldungStore();
const wahlvorschlaegeStore = useWahlvorschlaegeStore();

const { orderedByNumIndexWithNullAtEnd } = useErgebnisUtils();
const { logWarn } = useLogging("TheOBWStapelACard");

interface ErgebnisWithWahlvorschlag {
  ergebnis: Ergebnis;
  wahlvorschlag: Wahlvorschlag;
}

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

const ergebnisseWithWahlvorschlag = computed<ErgebnisWithWahlvorschlag[]>(
  () => {
    return createErgebnisseAndWahlvorschlaege();
  }
);

const sumOfValidVotes = computed(() =>
  ergebnisseWithWahlvorschlag.value
    .map((item) => item.ergebnis)
    .reduce((sum, ergebnis) => {
      return sum + (ergebnis.ergebnis ?? 0);
    }, 0)
);

function onSaveClicked() {
  ergebnismeldungsStore.sendErgebnisseByStapelArt(props.wahlID, STAPEL);
}

function addWahlvorschlagForErgebnisIfExisting(
  ergebnis: Ergebnis,
  result: ErgebnisWithWahlvorschlag[]
): void {
  if (ergebnis.wahlvorschlagID) {
    const wahlvorschlagForErgebnis =
      wahlvorschlaegeStore.getWahlvorschlagOrUndefinedByWahlIDWahlbezirkIDAndWahlvorschlagID(
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
        `ergebnis wahlID=${props.wahlID}, wahlbezirkID=${props.wahlbezirkID}, wahlvorschlagID=${ergebnis.wahlvorschlagID} hat keine Wahlvorschlag`
      );
    }
  } else {
    logWarn(
      `ergebnis wahlID=${props.wahlID}, wahlbezirkID=${props.wahlbezirkID} ist ohne wahlvorschlagID`
    );
  }
}

function createErgebnisseAndWahlvorschlaege() {
  const result: ErgebnisWithWahlvorschlag[] = [];

  const ergebnisseForWahlAndStapel =
    loadErgebnisseForWahlAndStapelOrderedByNumIndex();

  ergebnisseForWahlAndStapel.forEach((ergebnis) => {
    addWahlvorschlagForErgebnisIfExisting(ergebnis, result);
  });

  return result;
}

function loadErgebnisseForWahlAndStapelOrderedByNumIndex(): Ergebnis[] {
  return (
    ergebnismeldungsStore
      .getErgebnisseByWahlIdAndStapelartOrUndefined(props.wahlID, STAPEL)
      ?.ergebnisse.sort(orderedByNumIndexWithNullAtEnd) ?? []
  );
}
</script>
