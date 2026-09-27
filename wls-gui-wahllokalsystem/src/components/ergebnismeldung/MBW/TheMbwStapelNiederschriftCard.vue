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
    <the-m-b-w-ungueltige-stimmen-anzeigen-card
      :wahlbezirk-id="wahlbezirkID"
      :wahl-id="wahlID"
    />
    <the-m-b-w-gueltige-stimmen-anzeigen-card
      :is-schnellmeldung="false"
      :wahlbezirk-id="wahlbezirkID"
      :wahl-id="wahlID"
    />
    <base-card-wahlvorschlaege-kandidatenstimmen-anzeigen
      :kandidatenstimmen="wahlvorschlaegeWithKandidatenErgebnissen"
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
import type { Status } from "@/types/ergebnismeldung/common/Status.ts";
import type { WahlbezirkEreignisse } from "@/types/vorfaelleundvorkommnisse/WahlbezirkEreignisse.ts";

import { onActivated, ref } from "vue";

import BaseCardWahlvorschlaegeKandidatenstimmenAnzeigen from "@/components/ergebnismeldung/common/BaseCardWahlvorschlaegeKandidatenstimmenAnzeigen.vue";
import BaseErgebnismeldungCardsContainer from "@/components/ergebnismeldung/common/BaseErgebnismeldungCardsContainer.vue";
import TheVorkommnisseRequirementCard from "@/components/ergebnismeldung/common/TheVorkommnisseRequirementCard.vue";
import TheMBWGueltigeStimmenAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelAB/TheMBWGueltigeStimmenAnzeigenCard.vue";
import TheMBWWaehlerAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelAB/TheMBWWaehlerAnzeigenCard.vue";
import TheMBWWahlberechtigteAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelAB/TheMBWWahlberechtigteAnzeigenCard.vue";
import TheMBWUngueltigeStimmenAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelC/TheMBWUngueltigeStimmenAnzeigenCard.vue";
import { useStatusUtils } from "@/composables/ergebnismeldung/common/statusUtils.ts";
import { useMwbStapelBCUtils } from "@/composables/ergebnismeldung/MBW/mwbStapelBCUtils.ts";
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
const { loadStatusByWahlIdAndWahlbezirkId } = useStatusUtils();
const {
  wahlvorschlaegeWithKandidatenErgebnissen,
  loadWahlvorschlaegeAndErgebnisse,
} = useMwbStapelBCUtils(props.wahlbezirkID, props.wahlID);

const status = ref<Status | null>(null);

onActivated(async () => {
  await loadWahlvorschlaegeAndErgebnisse();
  status.value = await loadStatusByWahlIdAndWahlbezirkId(
    props.wahlID,
    props.wahlbezirkID
  );
});

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
