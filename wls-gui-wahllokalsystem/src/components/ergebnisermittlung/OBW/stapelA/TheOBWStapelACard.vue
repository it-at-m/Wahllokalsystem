<template>
  <v-card>
    <v-card-title>Gültige Stimmabgaben</v-card-title>
    <v-card-text>
      <base-row-o-w-b-stapel-a
        v-for="(
          ergebnisWithWahlvorschlag, index
        ) in ergebnisseWithWahlvorschlag"
        :key="index"
        :model-value="ergebnisWithWahlvorschlag.ergebnis"
        :wahlvorschlag="ergebnisWithWahlvorschlag.wahlvorschlag"
      />
    </v-card-text>
    <v-card-actions>
      <base-button-save />
    </v-card-actions>
  </v-card>
</template>

<script setup lang="ts">
import type { Ergebnis } from "@/types/ergebnismeldung/Ergebnis.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";

import { computed } from "vue";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import BaseRowOWBStapelA from "@/components/ergebnisermittlung/OBW/stapelA/BaseRowOWBStapelA.vue";
import { useLogging } from "@/composables/common/logging.ts";
import { useErgebnisUtils } from "@/composables/ergebnismeldung/ergebnisUtils.ts";
import { useErgebnismeldungStore } from "@/stores/ergebnismeldungStore.ts";
import { useWahlvorschlaegeStore } from "@/stores/wahlvorschlaegeStore.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

const ergebnismeldungsStore = useErgebnismeldungStore();
const wahlvorschlaegeStore = useWahlvorschlaegeStore();

const { orderedByNumIndexWithNullAtEnd } = useErgebnisUtils();
const { logWarn } = useLogging("TheOBWStapelACard");

interface ErgebnisWithWahlvorschlag {
  ergebnis: Ergebnis;
  wahlvorschlag: Wahlvorschlag;
}

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

const ergebnisseWithWahlvorschlag = computed<ErgebnisWithWahlvorschlag[]>(
  () => {
    return createErgebnisseAndWahlvorschlaege();
  }
);

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
      .getErgebnisseByWahlIdAndStapelartOrUndefined(
        props.wahlID,
        StapelArtEnum.ObwA
      )
      ?.ergebnisse.sort(orderedByNumIndexWithNullAtEnd) ?? []
  );
}
</script>
