<template>
  <v-container>
    <v-card>
      <v-card-title>Zahl der Wahlurnen</v-card-title>
      <v-card-text class="pb-0 pt-2">
        <v-form
          ref="wahlurnenForm"
          v-model="anzahlWahlurnenValidForm"
        >
          <base-wahlumgebung-wahlurnen-div
            :wahl-vorbereitung="
              urnenwahlVorbereitungState.urnenwahlVorbereitung
            "
          />
          <v-checkbox
            v-model="
              urnenwahlVorbereitungState.urnenwahlVorbereitung.urneVersiegelt
            "
            :label="checkboxLabelText"
            data-test="checkboxAlleVersiegelt"
          />
        </v-form>
      </v-card-text>
      <v-card-title>Abstimmungsschutzvorrichtungen</v-card-title>
      <v-card-text class="pb-0 pt-2">
        <v-form
          ref="abstimmungsschutzvorrichtungenForm"
          v-model="abstimmungsschutzvorrichtungenValidForm"
        >
          <div class="d-flex flex-wrap justify-start">
            <div>
              <base-number-input
                v-model="
                  urnenwahlVorbereitungState.urnenwahlVorbereitung
                    .anzahlWahltische
                "
                class="mr-4"
                :rules="[required, minNumber(0), maxNumber(99)]"
                min-width="30rem"
                data-test="numberInputAnzahlWahltische"
                label="Anzahl der Tische mit Sichtblenden"
              />
            </div>
            <div>
              <base-number-input
                v-model="
                  urnenwahlVorbereitungState.urnenwahlVorbereitung
                    .anzahlNebenraeume
                "
                class="mr-4"
                :rules="[required, minNumber(0), maxNumber(99)]"
                data-test="numberInputAnzahlNebenraeume"
                label="Anzahl der Nebenräume im Wahlraum"
                min-width="30rem"
              />
            </div>
            <div>
              <base-number-input
                v-model="
                  urnenwahlVorbereitungState.urnenwahlVorbereitung
                    .anzahlWahlkabinen
                "
                class="mr-4"
                :rules="[required, minNumber(0), maxNumber(99)]"
                data-test="numberInputAnzahlWahlkabinen"
                label="Anzahl der Wahlkabinen"
                min-width="30rem"
              />
            </div>
          </div>
        </v-form>
        <base-input-feedback-card
          v-show="isMinimumRequired"
          title="Ungültige Eingaben"
          :type="InputFeedbackTypeEnum.error"
          class="my-2"
        >
          Die Summe der Kabinen, Tische und Nebenräume muss mindestens 1
          betragen.
        </base-input-feedback-card>
      </v-card-text>
      <v-card-actions>
        <base-button-save
          :disabled="isSaveButtonDisabled"
          :loading="urnenwahlVorbereitungState.urnenwahlVorbereitungIsSaving"
          save-text="Speichern und Weiter"
          @click="onSaveWahlumgebungUWBClicked"
        />
      </v-card-actions>
    </v-card>
  </v-container>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";
import { computed, ref } from "vue";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import BaseInputFeedbackCard from "@/components/common/cards/BaseInputFeedbackCard.vue";
import BaseNumberInput from "@/components/common/inputs/BaseNumberInput.vue";
import BaseWahlumgebungWahlurnenDiv from "@/components/wahlhandlung/BaseWahlumgebungWahlurnenDiv.vue";
import { useRules } from "@/composables/common/rules.ts";
import { useNavigationUtils } from "@/composables/navigation/navigationUtils.ts";
import router from "@/plugins/router.ts";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { InputFeedbackTypeEnum } from "@/types/common/InputFeedbackTypeEnum.ts";

const { maxNumber, minNumber, required } = useRules();
const { getNextRoute } = useNavigationUtils();

const anzahlWahlurnenValidForm = ref<null | boolean>(null);
const abstimmungsschutzvorrichtungenValidForm = ref<null | boolean>(null);
const abstimmungsschutzvorrichtungenForm = ref<HTMLFormElement>();

const { wahlenState } = storeToRefs(useWahlenStore());
const { urnenwahlVorbereitungActions } = useWahlbezirkStore();
const { urnenwahlVorbereitungState } = storeToRefs(useWahlbezirkStore());

const isSaveButtonDisabled = computed(() => {
  return (
    anzahlWahlurnenValidForm.value !== true ||
    abstimmungsschutzvorrichtungenValidForm.value !== true ||
    !urnenwahlVorbereitungState.value.urnenwahlVorbereitung.urneVersiegelt ||
    isMinimumRequired.value
  );
});

const isMinimumRequired = computed(() => {
  const tischeSichtblenden =
    Number(
      urnenwahlVorbereitungState.value.urnenwahlVorbereitung.anzahlWahltische
    ) || 0;
  const nebenraeumeWahlraum =
    Number(
      urnenwahlVorbereitungState.value.urnenwahlVorbereitung.anzahlNebenraeume
    ) || 0;
  const wahlkabinen =
    Number(
      urnenwahlVorbereitungState.value.urnenwahlVorbereitung.anzahlWahlkabinen
    ) || 0;

  return tischeSichtblenden + nebenraeumeWahlraum + wahlkabinen < 1;
});

const checkboxLabelText = computed(() => {
  if (!hasMoreThanOneWahlurnen.value) {
    return "Die Wahlurne war leer und wurde ordnungsgemäß versiegelt";
  }
  return "Die Wahlurnen waren leer und wurden ordnungsgemäß versiegelt";
});

async function onSaveWahlumgebungUWBClicked() {
  await urnenwahlVorbereitungActions.sendUrnenwahlvorbereitung();
  await router.push(getNextRoute());
}

const hasMoreThanOneWahlurnen = computed(() => {
  return (
    (wahlenState.value.wahlen && wahlenState.value.wahlen.length > 1) ||
    (urnenwahlVorbereitungState.value.urnenwahlVorbereitung.urnenAnzahl[0]
      ?.anzahl || 0) > 1
  );
});
</script>
