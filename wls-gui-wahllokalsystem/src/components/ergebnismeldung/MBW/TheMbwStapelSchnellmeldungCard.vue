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
    <the-m-b-w-ungueltige-stimmen-anzeigen-card
      :wahlbezirk-id="wahlbezirkID"
      :wahl-id="wahlID"
    />
    <the-m-b-w-gueltige-stimmen-anzeigen-card
      :is-schnellmeldung="true"
      :wahlbezirk-id="wahlbezirkID"
      :wahl-id="wahlID"
    />
  </base-ergebnismeldung-cards-container>
</template>

<script setup lang="ts">
import BaseErgebnismeldungCardsContainer from "@/components/ergebnismeldung/common/BaseErgebnismeldungCardsContainer.vue";
import TheMBWGueltigeStimmenAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelAB/TheMBWGueltigeStimmenAnzeigenCard.vue";
import TheMBWWaehlerAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelAB/TheMBWWaehlerAnzeigenCard.vue";
import TheMBWWahlberechtigteAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelAB/TheMBWWahlberechtigteAnzeigenCard.vue";
import TheMBWUngueltigeStimmenAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelC/TheMBWUngueltigeStimmenAnzeigenCard.vue";

defineProps<{
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
