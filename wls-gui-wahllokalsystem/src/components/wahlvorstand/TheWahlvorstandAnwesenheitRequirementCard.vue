<template>
  <base-input-feedback-card
    title="Ungültige Zusammensetzung des Wahlvorstands"
    :type="InputFeedbackTypeEnum.error"
  >
    <ul>
      <li v-if="!isMindestanwesenheitErreicht">
        Vor der Wahlschliessung müssen mindestens
        {{ MIN_WAHLVORSTAND_ANWESEND_VOR_SCHLIESSUNG }} und nach der Schliessung
        mindestens
        {{ MIN_WAHLVORSTAND_ANWESEND_NACH_SCHLIESSUNG }}
        Wahlvorstandsmitglieder anwesend sein.
      </li>
      <li v-if="!isSchriftfuehrerAnwesend">
        Die Rolle Schriftführer*in muss besetzt sein.
      </li>
      <li v-if="!isWahlvorsteherAnwesend">
        Die Rolle Wahlvorsteher*in muss besetzt sein.
      </li>
    </ul>
    <template #additionalFeedback>
      Bitte wenden Sie sich bei fehlenden Mitglieder oder getauschten Rollen an
      die Bezirksinspektion. Dort werden die Rollen im System richtig
      hinterlegt. Bis dahin bleiben Sie bitte auf dieser Seite.
    </template>
  </base-input-feedback-card>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";

import BaseInputFeedbackCard from "@/components/common/cards/BaseInputFeedbackCard.vue";
import {
  MIN_WAHLVORSTAND_ANWESEND_NACH_SCHLIESSUNG,
  MIN_WAHLVORSTAND_ANWESEND_VOR_SCHLIESSUNG,
} from "@/constants.ts";
import { useWahlvorstandStore } from "@/stores/wahlvorstandStore";
import { InputFeedbackTypeEnum } from "@/types/common/InputFeedbackTypeEnum.ts";

const {
  isSchriftfuehrerAnwesend,
  isWahlvorsteherAnwesend,
  isMindestanwesenheitErreicht,
} = storeToRefs(useWahlvorstandStore());
</script>
