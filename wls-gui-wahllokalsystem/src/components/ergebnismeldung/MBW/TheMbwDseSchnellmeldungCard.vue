<template>
  <base-ergebnismeldung-cards-container
    title="Schnellmeldung"
    subtitle="Kontrolle, Übermittlung und Druck der Schnellmeldung"
    :is-sending="isSendingSchnellmeldung"
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
        <base-table-gueltige-stimmen-schnellmeldung-anzeigen
          :ergebnisse-and-wahlvorschlaege="wahlvorschlaegeErgebnisseStapelAAndB"
        />
      </v-card-text>
    </v-card>
  </base-ergebnismeldung-cards-container>
</template>

<script setup lang="ts">
import { computed } from "vue";

import BaseCardUngueltigeStimmenAnzeigen from "@/components/ergebnismeldung/common/BaseCardUngueltigeStimmenAnzeigen.vue";
import BaseErgebnismeldungCardsContainer from "@/components/ergebnismeldung/common/BaseErgebnismeldungCardsContainer.vue";
import BaseTableGueltigeStimmenSchnellmeldungAnzeigen from "@/components/ergebnismeldung/common/BaseTableGueltigeStimmenSchnellmeldungAnzeigen.vue";
import TheMBWWaehlerAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelAB/TheMBWWaehlerAnzeigenCard.vue";
import TheMBWWahlberechtigteAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelAB/TheMBWWahlberechtigteAnzeigenCard.vue";
import { useMbwSchnellmeldungViewUtils } from "@/composables/ergebnismeldung/MBW/theMbwDseSchnellmeldungViewUtils.ts";

const props = defineProps<{
  wahlbezirkID: string;
  wahlID: string;
  isSendingSchnellmeldung: boolean;
  isKorrigierenValid: boolean | null | undefined;
  isDruckenActive: boolean;
  isDruckenLoading: boolean;
  isSendenActive: boolean;
}>();

const emit = defineEmits<{
  save: [];
  edit: [];
  print: [];
}>();

const {
  stapelDUngueltig,
  stapelEUngueltig,
  wahlvorschlaegeErgebnisseStapelAAndB,
} = useMbwSchnellmeldungViewUtils(props.wahlID, props.wahlbezirkID);

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
