<template>
  <base-ergebnismeldung-cards-container
    title="Niederschrift"
    subtitle="Kontrolle, Übermittlung und Druck der Niederschrift"
    :is-sending="isSendingNiederschrift"
    :is-korrigieren-active="isKorrigierenValid"
    :is-drucken-active="isDruckenActive"
    :is-drucken-loading="isDruckenLoading"
    :is-senden-active="isSendenActive"
    @save="onSave"
    @edit="onEdit"
    @print="onPrint"
  >
    <the-m-b-w-wahlberechtigte-anzeigen-card
      :wahlbezirk-id="wahlbezirkID"
      :wahl-id="wahlID"
    />
    <the-m-b-w-waehler-anzeigen-card
      :wahlbezirk-id="wahlbezirkID"
      :wahl-id="wahlID"
    />
    <base-card-ungueltige-stimmen-anzeigen
      :ungueltige-stimmen="ungueltigeStimmen"
      :ungueltige-stimmzettel-nach-beschluss="
        ungueltigeStimmzettelNachBeschluss
      "
    />
    <v-card>
      <v-card-title> Gültige Stimmen </v-card-title>
      <v-card-text>
        <the-m-b-w-gueltige-stimmen-anzeigen-niederschrift-table
          :wahlvorschlaege-kandidaten-ergebnisse="
            wahlvorschlaegeWithKandidatenErgebnissenStapelAAndB
          "
          :ergebnisse-and-wahlvorschlaege="wahlvorschlaegeErgebnisseStapelAAndB"
        />
      </v-card-text>
    </v-card>
    <base-card-wahlvorschlaege-kandidatenstimmen-anzeigen
      :kandidatenstimmen="wahlvorschlaegeWithKandidatenErgebnissenStapelBC"
    />
    <the-vorkommnisse-requirement-card
      :type="
        hasDoneVorkommnisse(ereignisse)
          ? InputFeedbackTypeEnum.information
          : InputFeedbackTypeEnum.error
      "
    />
  </base-ergebnismeldung-cards-container>
</template>

<script setup lang="ts">
import type { WahlbezirkEreignisse } from "@/types/vorfaelleundvorkommnisse/WahlbezirkEreignisse.ts";

import { computed } from "vue";

import BaseCardUngueltigeStimmenAnzeigen from "@/components/ergebnismeldung/common/BaseCardUngueltigeStimmenAnzeigen.vue";
import BaseCardWahlvorschlaegeKandidatenstimmenAnzeigen from "@/components/ergebnismeldung/common/BaseCardWahlvorschlaegeKandidatenstimmenAnzeigen.vue";
import BaseErgebnismeldungCardsContainer from "@/components/ergebnismeldung/common/BaseErgebnismeldungCardsContainer.vue";
import TheVorkommnisseRequirementCard from "@/components/ergebnismeldung/common/TheVorkommnisseRequirementCard.vue";
import TheMBWGueltigeStimmenAnzeigenNiederschriftTable from "@/components/ergebnismeldung/MBW/stapelAB/TheMBWGueltigeStimmenAnzeigenNiederschriftTable.vue";
import TheMBWWaehlerAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelAB/TheMBWWaehlerAnzeigenCard.vue";
import TheMBWWahlberechtigteAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelAB/TheMBWWahlberechtigteAnzeigenCard.vue";
import { useStimmzettelZusammenfassungUtils } from "@/composables/dse/stimmzettelerfassung/stimmzettelZusammenfassungUtils.ts";
import { useMbwNiederschriftViewUtils } from "@/composables/ergebnismeldung/MBW/theMbwDseNiederschriftViewUtils.ts";
import { useEreignisUtils } from "@/composables/vorfaelleundvorkommnisse/ereignisUtils.ts";
import { InputFeedbackTypeEnum } from "@/types/common/InputFeedbackTypeEnum.ts";

const props = defineProps<{
  wahlbezirkID: string;
  wahlID: string;
  isSendingNiederschrift: boolean;
  isKorrigierenValid: boolean | null | undefined;
  isDruckenActive: boolean | undefined;
  isDruckenLoading: boolean;
  isSendenActive: boolean;
  ereignisse: WahlbezirkEreignisse | null;
}>();

const emit = defineEmits<{
  save: [];
  edit: [];
  print: [];
}>();

const { hasDoneVorkommnisse } = useEreignisUtils();

const {
  stapelA,
  stapelB,
  stapelBC,
  stapelDUngueltig,
  stapelEUngueltig,
  wahlvorschlaege,
  wahlvorschlaegeErgebnisseStapelAAndB,
} = useMbwNiederschriftViewUtils(props.wahlID, props.wahlbezirkID);
const {
  wahlvorschlaegeWithKandidatenErgebnissen:
    wahlvorschlaegeWithKandidatenErgebnissenStapelAAndB,
} = useStimmzettelZusammenfassungUtils(
  computed(() => [...stapelA.value, ...stapelB.value]),
  wahlvorschlaege
);
const {
  wahlvorschlaegeWithKandidatenErgebnissen:
    wahlvorschlaegeWithKandidatenErgebnissenStapelBC,
} = useStimmzettelZusammenfassungUtils(stapelBC, wahlvorschlaege);

const ungueltigeStimmen = computed(() => stapelDUngueltig.value.length);
const ungueltigeStimmzettelNachBeschluss = computed(
  () => stapelEUngueltig.value.length
);

function onSave() {
  emit("save");
}

function onEdit() {
  emit("edit");
}

function onPrint() {
  emit("print");
}
</script>
