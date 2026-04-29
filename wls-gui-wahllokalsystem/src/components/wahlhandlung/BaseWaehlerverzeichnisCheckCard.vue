<template>
  <v-card>
    <v-card-title>Auf ungültige Wahlscheine hinweisen</v-card-title>
    <v-card-text>
      <base-feedback-card
        title="Bearbeitungshinweis"
        type="information"
        class="mb-2"
        >Das Wahlamt informiert Sie, wenn Sie auf dieser Maske Änderungen
        vornehmen müssen.
      </base-feedback-card>
      <v-radio-group
        v-model="
          pflegeWaehlerverzeichnisState.pflegeWaehlerverzeichnis
            .waehlerverzeichnisUnchanged
        "
        class="mb-4"
        hide-details
      >
        <v-radio :value="true">
          <template #label>
            Ein Verzeichnis über nachträglich ausgestellte Wahlscheine lag nicht
            vor. Das Wählerverzeichnis war nicht zu berichtigen.
          </template>
        </v-radio>
        <v-radio :value="false">
          <template #label>
            Vor Beginn der Stimmabgabe berichtigten die Wahlvorsteher*innen das
            Wählerverzeichnis nach dem Verzeichnis der nachträglich erteilten
            Wahlscheine, indem sie bei den Namen der nachträglich mit
            Wahlscheinen versehenen Wahlberechtigten in der Spalte für die
            Stimmabgabe den Vermerk "Wahlschein" oder den Buchstaben "W"
            eintrugen. Sie berichtigten auch die Zahlen der Abschlussbeurkundung
            der Gemeinde, diese Berichtigung wurde von ihnen abgezeichnet.
          </template>
        </v-radio>
      </v-radio-group>
      <v-checkbox
        v-model="
          pflegeWaehlerverzeichnisState.pflegeWaehlerverzeichnis
            .nachtraeglicheBerichtigung
        "
        hide-details
      >
        <template #label>
          Die Wahlvorsteher*innen berichtigten später entsprechend das
          Wählerverzeichnis und die dazugehörige Abschlussbeurkundung unter
          Berücksichtigung der noch am Wahltag an erkrankte Wahlberechtigte
          erteilten Wahlscheine.
        </template>
      </v-checkbox>
      <v-checkbox
        v-model="
          pflegeWaehlerverzeichnisState.pflegeWaehlerverzeichnis
            .mitteilungUeberUngueltigeWahlscheineErhalten
        "
        hide-details
      >
        <template #label>
          {{ TEXT_MITTEILUNG_UEBER_UNGUELTIGE_WAHLSCHEINE }}
        </template>
      </v-checkbox>
      <base-feedback-card
        v-if="
          !pflegeWaehlerverzeichnisState.pflegeWaehlerverzeichnis
            .mitteilungUeberUngueltigeWahlscheineErhalten
        "
        title="Ungültige Eingabe"
        type="error"
        class="mt-2"
        >Bitte setzen sie einen Haken bei: "{{
          TEXT_MITTEILUNG_UEBER_UNGUELTIGE_WAHLSCHEINE
        }}"
      </base-feedback-card>
    </v-card-text>
    <v-card-actions>
      <base-wls-button-save
        :disabled="
          !pflegeWaehlerverzeichnisState.pflegeWaehlerverzeichnis
            .mitteilungUeberUngueltigeWahlscheineErhalten
        "
        :loading="
          pflegeWaehlerverzeichnisState.pflegeWaehlerverzeichnisIsSaving
        "
        :save-text="SAVE_CONTINUE"
        @click="onSavePflegeWaehlerverzeichnisClicked"
      />
    </v-card-actions>
  </v-card>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";

import BaseWlsButtonSave from "@/components/common/buttons/BaseWlsButtonSave.vue";
import BaseFeedbackCard from "@/components/common/cards/BaseFeedbackCard.vue";
import { useNavigationUtils } from "@/composables/navigation/navigationUtils.ts";
import { SAVE_CONTINUE } from "@/constants.ts";
import router from "@/plugins/router.ts";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";

const TEXT_MITTEILUNG_UEBER_UNGUELTIGE_WAHLSCHEINE =
  "Der Wahlvorstand wurde unterrichtet, dass folgende Wahlscheine für ungültig erklärt worden sind (gemäß Anlage).";

const { getNextRoute } = useNavigationUtils();

const { pflegeWaehlerverzeichnisActions } = useWahlbezirkStore();
const { pflegeWaehlerverzeichnisState } = storeToRefs(useWahlbezirkStore());

async function onSavePflegeWaehlerverzeichnisClicked() {
  await pflegeWaehlerverzeichnisActions.sendPflegeWaehlerverzeichnis();
  await router.push(getNextRoute());
}
</script>
