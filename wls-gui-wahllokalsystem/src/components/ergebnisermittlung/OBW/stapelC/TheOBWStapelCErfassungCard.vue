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
            <base-row-stapel-c
              v-for="(
                ergebnisAndStapel, index
              ) in stapelCErgebnisseOrdereByNumIndex"
              :key="index"
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
      <!-- Button mit dem Add bei Ereignisse fusionieren -->
      <v-btn
        prepend-icon="$add"
        @click="onAddClicked()"
        >Eintrag hinzufügen</v-btn
      >
      <base-button-save
        :disabled="!areStapelCGueltigeErgebnisseValid"
        @click="onSaveClicked"
      />
    </v-card-actions>
  </v-card>
</template>

<script setup lang="ts">
import type { Ergebnis } from "@/types/ergebnismeldung/Ergebnis.ts";
import type { ComputedRef } from "vue";

import { computed, ref } from "vue";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import BaseRowStapelC from "@/components/ergebnisermittlung/OBW/stapelC/BaseRowStapelC.vue";
import { useMathUtils } from "@/composables/common/mathUtils.ts";
import { useErgebnisUtils } from "@/composables/ergebnismeldung/ergebnisUtils.ts";
import { useErgebnismeldungStore } from "@/stores/ergebnismeldungStore.ts";
import { useWahlvorschlaegeStore } from "@/stores/wahlvorschlaegeStore.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

const ergebnismeldungsStore = useErgebnismeldungStore();
const wahalvorschlaegeStore = useWahlvorschlaegeStore();
const { orderedByNumIndexWithNullAtEnd, reduceToMaxOfNumIndex } =
  useErgebnisUtils();
const { maxOfOptionalNumbers } = useMathUtils();

interface ErgebnisAndStapelArt {
  ergebnis: Ergebnis;
  stapelArt: StapelArtEnum;
}
interface ErgebnisWithNumIndexAndStapel extends ErgebnisAndStapelArt {
  ergebnis: ErgebnisWithNumIndex;
}

interface ErgebnisWithNumIndex extends Ergebnis {
  numIndex: number;
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

const stapelCUngueltigErgebnisse: ComputedRef<ErgebnisAndStapelArt[]> =
  computed(
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
const stapelCGueltigErgebnisse: ComputedRef<ErgebnisAndStapelArt[]> = computed(
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

const areStapelCGueltigeErgebnisseValid = computed(() =>
  stapelCGueltigErgebnisse.value.every(
    (value) => value.ergebnis.wahlvorschlagID !== null
  )
);

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

function switchStapelCOfErgebnis(
  currentErgebnisAndStapel: {
    stapelArt: StapelArtEnum;
    ergebnis: {
      numIndex: number;
    };
  },
  shouldSetStapelUngueltig: boolean
) {
  const newStapelArt = shouldSetStapelUngueltig
    ? StapelArtEnum.ObwCUngueltig
    : StapelArtEnum.ObwCGueltig;
  ergebnismeldungsStore.switchStapelOfErgebnis(
    {
      wahlID: props.wahlID,
      wahlbezirkID: props.wahlbezirkID,
      stapelArt: currentErgebnisAndStapel.stapelArt,
    },
    currentErgebnisAndStapel.ergebnis.numIndex,
    newStapelArt
  );
}
</script>
