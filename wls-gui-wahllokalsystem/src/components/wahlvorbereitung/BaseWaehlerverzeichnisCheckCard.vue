<template>
  <v-card>
    <v-card-title>Auf ungültige Wahlscheine hinweisen </v-card-title>
    <v-card-text>
      <base-input-feedback-card
        title="Bearbeitungshinweis"
        type="information"
        class="mb-2"
        >Das Wahlamt informiert Sie, wenn Sie auf dieser Maske Änderungen
        vornehmen müssen.
      </base-input-feedback-card>
      <v-radio-group
        v-model="data.waehlerverzeichnisUnchanged"
        class="mb-4"
        hide-details
      >
        <v-radio
          label="Ein Verzeichnis über nachträglich ausgestellte Wahlscheine lag nicht
          vor. Das Wählerverzeichnis war nicht zu berichtigen."
          :value="true"
        />
        <v-radio
          label='Vor Beginn der Stimmabgabe berichtigten die Wahlvorsteher*innen das
          Wählerverzeichnis nach dem Verzeichnis der nachträglich erteilten
          Wahlscheine, indem sie bei den Namen der nachträglich mit Wahlscheinen
          versehenen Wahlberechtigten in der Spalte für die Stimmabgabe den
          Vermerk "Wahlschein" oder den Buchstaben "W" eintrugen. Sie
          berichtigten auch die Zahlen der Abschlussbeurkundung der Gemeinde;
          diese Berichtigung wurde von ihnen abgezeichnet.'
          :value="false"
        />
      </v-radio-group>
      <v-checkbox
        v-model="data.nachtraeglicheBerichtigung"
        label="Die Wahlvorsteher*innen berichtigten später entsprechend das Wählerverzeichnis und die dazugehörige Abschlussbeurkundung unter Berücksichtigung der noch am Wahltag an erkrankte Wahlberechtigte erteilten Wahlscheine."
        hide-details
      />
      <v-checkbox
        v-model="data.mitteilungUeberUngueltigeWahlscheineErhalten"
        :label="TEXT_MITTEILUNG_UEBER_UNGUELTIGE_WAHLSCHEINE"
        hide-details
      />
      <base-input-feedback-card
        v-if="!data.mitteilungUeberUngueltigeWahlscheineErhalten"
        title="Ungültige Eingabe"
        type="error"
        class="mt-2"
        >Bitte setzen sie einen Haken bei: „{{
          TEXT_MITTEILUNG_UEBER_UNGUELTIGE_WAHLSCHEINE
        }}"
      </base-input-feedback-card>
    </v-card-text>
    <v-card-actions>
      <base-button-save
        active
        :disabled="!data.mitteilungUeberUngueltigeWahlscheineErhalten"
      />
    </v-card-actions>
  </v-card>
</template>

<script setup lang="ts">
import { ref } from "vue";
import {
  VCard,
  VCardActions,
  VCardText,
  VCardTitle,
  VCheckbox,
  VRadio,
  VRadioGroup,
} from "vuetify/components";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import BaseInputFeedbackCard from "@/components/common/cards/BaseInputFeedbackCard.vue";

const TEXT_MITTEILUNG_UEBER_UNGUELTIGE_WAHLSCHEINE =
  "Der Wahlvorstand wurde unterrichtet, dass folgende Wahlscheine für ungültig erklärt worden sind (gemäß Anlage).";

const data = ref({
  waehlerverzeichnisUnchanged: true,
  mitteilungUeberUngueltigeWahlscheineErhalten: true,
  nachtraeglicheBerichtigung: false,
});
</script>
