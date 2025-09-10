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
              briefwahlVorbereitungState.briefwahlVorbereitung
            "
          />
          <v-checkbox
            v-model="
              briefwahlVorbereitungState.briefwahlVorbereitung.urneVersiegelt
            "
            :label="checkboxLabelText"
            data-test="checkboxAlleVersiegelt"
          />
          <v-card-actions>
            <base-button-save
              :disabled="isSaveButtonDisabled"
              :loading="
                briefwahlVorbereitungState.briefWahlVorbereitungIsSaving
              "
              @click="onSaveWahlumgebungBWBClicked"
            />
          </v-card-actions>
        </v-form>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";
import { computed, ref } from "vue";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import BaseWahlumgebungWahlurnenDiv from "@/components/wahlhandlung/BaseWahlumgebungWahlurnenDiv.vue";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

const anzahlWahlurnenValidForm = ref<null | boolean>(null);

const { wahlenState } = storeToRefs(useWahlenStore());
const { briefwahlVorbereitungActions } = useWahlbezirkStore();
const { briefwahlVorbereitungState } = storeToRefs(useWahlbezirkStore());

const isSaveButtonDisabled = computed(() => {
  return (
    anzahlWahlurnenValidForm.value !== true ||
    !briefwahlVorbereitungState.value.briefwahlVorbereitung.urneVersiegelt
  );
});

const checkboxLabelText = computed(() => {
  if (wahlenState.value.wahlen && wahlenState.value.wahlen?.length > 1) {
    return "Die Wahlurnen waren leer und wurden ordnungsgemäß versiegelt";
  }
  return "Die Wahlurne war leer und wurde ordnungsgemäß versiegelt";
});

function onSaveWahlumgebungBWBClicked() {
  briefwahlVorbereitungActions.sendBriefwahlvorbereitung();
}
</script>
