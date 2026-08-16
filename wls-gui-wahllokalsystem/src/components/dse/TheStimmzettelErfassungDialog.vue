<template>
  <v-dialog
    v-model="isDialogVisibleModel"
    persistent
    fullscreen
  >
    <v-card>
      <v-card-title>
        Erfassung Stimmzettel Nummer {{ currentUserTeamName }}
        {{ stimmzettel.stimmzettelkennung }}
      </v-card-title>
      <v-card-text>
        <v-row>
          <v-col cols="2">
            <the-eingabehistorie-card :change-history="[]" />
            <base-stimmzettel-zusammenfassung-card
              class="mt-2"
              :listenstimmen="[]"
              :gesamtstimmen="0"
              :ungueltigestimmen="0"
              :direktstimmen="0"
              :reststimmen="0"
              :streichungen="0"
              :gueltigkeit="'VALID'"
            />
          </v-col>
          <v-col cols="10">
            <the-stimmzettel-command-processing-text-field
              :stimmzettel-manager="stimmzettelManager"
            />
            <the-stimmzettel-content
              :active-wahlvorschlag-id="null"
              :wahlvorschlaege="
                stimmzettelManager.managedStimmzettel.stimmzettel.value
                  .wahlvorschlaege
              "
            />
          </v-col>
        </v-row>
      </v-card-text>
      <v-card-actions>
        <base-text-button @click="onCancelClicked">Abbrechen</base-text-button>
        <base-wls-button-save @click="onSavedClicked" />
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import type { Stimmzettel } from "@/types/dse/persistedStimmzettel/Stimmzettel.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";
import type { PropType } from "vue";

import { storeToRefs } from "pinia";

import BaseTextButton from "@/components/common/buttons/BaseTextButton.vue";
import BaseWlsButtonSave from "@/components/common/buttons/BaseWlsButtonSave.vue";
import BaseStimmzettelZusammenfassungCard from "@/components/dse/BaseStimmzettelZusammenfassungCard.vue";
import TheEingabehistorieCard from "@/components/dse/TheEingabehistorieCard.vue";
import TheStimmzettelCommandProcessingTextField from "@/components/dse/TheStimmzettelCommandProcessingTextField.vue";
import TheStimmzettelContent from "@/components/dse/TheStimmzettelContent.vue";
import { useStimmzettelerfassungDialogUtils } from "@/composables/dse/stimmzettelerfassungDialogUtils.ts";
import { useUserStore } from "@/stores/userStore.ts";

const isDialogVisibleModel = defineModel("modelValue", {
  type: Boolean,
  required: false,
});

const props = defineProps({
  stimmzettel: {
    type: Object as PropType<Stimmzettel>,
    required: true,
  },
  wahlvorschlaege: {
    type: Array as PropType<Wahlvorschlag[]>,
    required: true,
  },
});

const emit = defineEmits<{
  cancel: [];
  confirm: [stimmzettel: Stimmzettel];
}>();

const { stimmzettelManager } = useStimmzettelerfassungDialogUtils(
  props.wahlvorschlaege
);

const { currentUserTeamName } = storeToRefs(useUserStore());

function onCancelClicked() {
  emit("cancel");
}

function onSavedClicked() {
  emit("confirm", props.stimmzettel);
}
</script>
