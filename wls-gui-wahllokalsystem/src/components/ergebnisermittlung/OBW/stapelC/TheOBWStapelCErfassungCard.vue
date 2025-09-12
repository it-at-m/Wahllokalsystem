<template>
  <v-card>
    <v-card-title>
      Stapel c - Stimmzettel, die Anlass zu Bedenken geben
    </v-card-title>
    <v-card-text>
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
            <tr
              v-for="(ergebnis, index) in stapelCErgebnisseOrdereByNumIndex"
              :key="index"
            >
              <td>{{ ergebnis.ergebnis.numIndex }}</td>
              <td>
                {{ ergebnis.stapelArt }}
                <v-checkbox-btn
                  :model-value="
                    ergebnis.stapelArt === StapelArtEnum.ObwCUngueltig
                  "
                  @update:model-value="
                    switchStapel(ergebnis.ergebnis.numIndex, ergebnis.stapelArt)
                  "
                />
              </td>
              <td>
                <v-autocomplete
                  v-model="ergebnis.ergebnis.wahlvorschlagID"
                  :items="wahlvorschlaege"
                  :item-title="getWahlvorschlagTitle"
                  item-value="identifikator"
                  :disabled="ergebnis.stapelArt === StapelArtEnum.ObwCUngueltig"
                  clearable
                  persistent-clear
                  label="Wahlvorschlag"
                />
              </td>
              <td>
                <!-- mit dem Delete Icon Button aus anderen Komponenten zusammenlegen -->
                <v-icon
                  icon="$clear"
                  title="Löschen"
                  @click="onDeleteIconClicked(ergebnis.ergebnis.numIndex)"
                />
              </td>
            </tr>
          </tbody>
        </v-table>
      </v-form>
    </v-card-text>
    <v-card-actions>
      <!-- Button mit dem Add bei Ereignisse fusionieren -->
      <v-btn
        prepend-icon="$add"
        @click="onAddClicked()"
        >Eintrag hinzufügen</v-btn
      >
      <base-button-save @click="onSaveClicked" />
    </v-card-actions>
  </v-card>
</template>

<script setup lang="ts">
import type { Ergebnis } from "@/types/ergebnismeldung/Ergebnis.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";
import type { ComputedRef } from "vue";

import { computed, ref } from "vue";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import { useLogging } from "@/composables/common/logging.ts";
import { useErgebnisUtils } from "@/composables/ergebnismeldung/ergebnisUtils.ts";
import { useWahlvorschlagUtils } from "@/composables/wahlvorschlaege/wahlvorschlagUtils.ts";
import { useErgebnismeldungStore } from "@/stores/ergebnismeldungStore.ts";
import { useWahlvorschlaegeStore } from "@/stores/wahlvorschlaegeStore.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

const { logDebug } = useLogging("TheOBWStapelCErfassungCard");

const ergebnismeldungsStore = useErgebnismeldungStore();
const wahalvorschlaegeStore = useWahlvorschlaegeStore();
const { orderedByNumIndexWithNullAtEnd, reduceToMaxOfNumIndex } =
  useErgebnisUtils();
const { getFirstKandidatNameOrEmptyString } = useWahlvorschlagUtils();

interface ErgebnisAndStapel {
  ergebnis: Ergebnis;
  stapelArt: StapelArtEnum;
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

const wahlvorschlaege = computed(() => {
  const wahlvorschlaege =
    wahalvorschlaegeStore.getWahlvorschlaegeByWahlIDAndWahlbezirkID(
      props.wahlID,
      props.wahlbezirkID
    );
  return wahlvorschlaege ? [...(wahlvorschlaege.wahlvorschlaege ?? [])] : [];
});

const stapelCUngueltigErgebnisse: ComputedRef<ErgebnisAndStapel[]> = computed(
  () =>
    ergebnismeldungsStore
      .getErgebnisseByWahlIdAndStapelartOrUndefined(
        props.wahlID,
        StapelArtEnum.ObwCUngueltig
      )
      ?.ergebnisse.map((ergebnis) => ({
        ergebnis: ergebnis,
        stapelArt: StapelArtEnum.ObwCUngueltig,
      })) ?? []
);
const stapelCGueltigErgebnisse: ComputedRef<ErgebnisAndStapel[]> = computed(
  () =>
    ergebnismeldungsStore
      .getErgebnisseByWahlIdAndStapelartOrUndefined(
        props.wahlID,
        StapelArtEnum.ObwCGueltig
      )
      ?.ergebnisse.map((ergebnis) => ({
        ergebnis: ergebnis,
        stapelArt: StapelArtEnum.ObwCGueltig,
      })) ?? []
);

const stapelCErgebnisseOrdereByNumIndex = computed(() => {
  return [
    ...stapelCUngueltigErgebnisse.value,
    ...stapelCGueltigErgebnisse.value,
  ].sort((a, b) => orderedByNumIndexWithNullAtEnd(a.ergebnis, b.ergebnis));
});

const isFormValid = ref<boolean | null>(null);

function onAddClicked() {
  const maxNumIndexOfUngueltig = stapelCUngueltigErgebnisse.value
    .map((value) => value.ergebnis)
    .reduce(reduceToMaxOfNumIndex, null);
  const maxNumIndexOfGueltig = stapelCGueltigErgebnisse.value
    .map((value) => value.ergebnis)
    .reduce(reduceToMaxOfNumIndex, null);

  const currentMaxNumIndex = maxOfOptionalNumbers([
    maxNumIndexOfUngueltig,
    maxNumIndexOfGueltig,
  ]);
  const nextNumIndex = currentMaxNumIndex === null ? 1 : currentMaxNumIndex + 1;

  const newStapelCErgebnis: Ergebnis = {
    ergebnis: 1,
    numIndex: nextNumIndex,
    wahlvorschlagID: null,
    kandidatID: null,
    wahlvorschlagsOrdnungszahl: null,
  };
  ergebnismeldungsStore.addErgebnis(
    {
      wahlID: props.wahlID,
      wahlbezirkID: props.wahlbezirkID,
      stapelArt: StapelArtEnum.ObwCGueltig,
    },
    newStapelCErgebnis
  );
}

function onDeleteIconClicked(index: number | null) {
  if (index === null) {
    return;
  }

  if (
    !ergebnismeldungsStore.deleteByNumIndexIfExists(
      {
        wahlID: props.wahlID,
        wahlbezirkID: props.wahlbezirkID,
        stapelArt: StapelArtEnum.ObwCUngueltig,
      },
      index
    )
  ) {
    ergebnismeldungsStore.deleteByNumIndexIfExists(
      {
        wahlID: props.wahlID,
        wahlbezirkID: props.wahlbezirkID,
        stapelArt: StapelArtEnum.ObwCGueltig,
      },
      index
    );
  }
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

function getWahlvorschlagTitle(wahlvorschlag: Wahlvorschlag) {
  return [
    wahlvorschlag.kurzname,
    getFirstKandidatNameOrEmptyString(wahlvorschlag),
  ].join(", ");
}

function maxOfOptionalNumbers(numbers: (number | null)[]): number | null {
  const nonNullNumbers = numbers.filter((n) => n !== null) as number[];
  if (nonNullNumbers.length === 0) {
    return null;
  }

  return Math.max(...nonNullNumbers);
}

function switchStapel(numIndex: number, currentStapelArt: StapelArtEnum) {
  logDebug(
    `switchStapel - numIndex: ${numIndex}, currentStapelArt: ${currentStapelArt}`
  );
  const newStapelArt =
    currentStapelArt === StapelArtEnum.ObwCGueltig
      ? StapelArtEnum.ObwCUngueltig
      : StapelArtEnum.ObwCGueltig;
  ergebnismeldungsStore.switchStapelOfErgebnis(
    {
      wahlID: props.wahlID,
      wahlbezirkID: props.wahlbezirkID,
      stapelArt: currentStapelArt,
    },
    numIndex,
    newStapelArt
  );
}
</script>
