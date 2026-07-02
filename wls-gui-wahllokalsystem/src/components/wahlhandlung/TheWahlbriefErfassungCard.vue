<template>
  <v-card>
    <v-card-title>
      Anzahl der Wahlbriefe (aus Wahlurne und Wahlbriefe, die vor
      {{
        toTimeWithHoursAndOptionalMinutes(
          createTodayWithTime(fruehesteSchliessungsuhrzeit)
        )
      }}
      Uhr übergeben wurden)
    </v-card-title>
    <v-card-text class="pb-0 pt-2">
      <v-form v-model="anzahlWahlbriefeValid">
        <base-number-input
          v-model="wahlbriefDatenState.wahlbriefDaten.wahlbriefe"
          class="mr-4"
          :min-valid="1"
          :rules="[required]"
          data-test="textFieldWahlbriefeAnzahl"
          label="Anzahl Wahlbriefe"
          :max-width="WIDTH"
        />
      </v-form>
    </v-card-text>
    <v-card-title
      >Anzahl der Verzeichnisse der für ungültig erklärten
      Wahlscheine</v-card-title
    >
    <v-card-text class="pb-0 pt-2">
      <v-form v-model="anzahlVerzeichnisseValid">
        <base-number-input
          v-model="wahlbriefDatenState.wahlbriefDaten.verzeichnisseUngueltige"
          class="mr-4"
          :rules="[required]"
          data-test="textFieldVerzeichnisseAnzahl"
          label="Anzahl Verzeichnisse"
          :max-width="WIDTH"
        />
      </v-form>
    </v-card-text>
    <v-card-title>Anzahl der Nachträge zu den Verzeichnissen</v-card-title>
    <v-card-text class="pb-0 pt-2">
      <v-form v-model="anzahlNachtraegeValid">
        <base-number-input
          v-model="wahlbriefDatenState.wahlbriefDaten.nachtraege"
          class="mr-4"
          :rules="[required]"
          data-test="textFieldNachtraegeAnzahl"
          label="Anzahl Nachträge"
          :max-width="WIDTH"
        />
      </v-form>
    </v-card-text>
    <v-card-actions>
      <base-wls-button-save
        data-test="button-save"
        :disabled="isSaveButtonDisabled"
        :loading="wahlbriefDatenState.wahlbriefDatenIsSaving"
        :save-text="SAVE_CONTINUE"
        @click="onSaveBriefwahldatenClicked"
      />
    </v-card-actions>
  </v-card>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";
import { computed, ref } from "vue";

import BaseWlsButtonSave from "@/components/common/buttons/BaseWlsButtonSave.vue";
import BaseNumberInput from "@/components/common/inputs/BaseNumberInput.vue";
import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";
import { useDateTimeUtils } from "@/composables/common/dateTimeUtils.ts";
import { useRules } from "@/composables/common/rules.ts";
import { useNavigationService } from "@/composables/navigation/navigationService.ts";
import { SAVE_CONTINUE } from "@/constants.ts";
import router from "@/plugins/router.ts";
import { useInfomanagementStore } from "@/stores/infomanagementStore.ts";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";

const { required } = useRules();

const { wahlbriefDatenActions } = useWahlbezirkStore();
const { wahlbriefDatenState } = storeToRefs(useWahlbezirkStore());
const { fruehesteSchliessungsuhrzeit } = storeToRefs(useInfomanagementStore());
const { toTimeWithHoursAndOptionalMinutes } = useDateTimeFormatter();
const { createTodayWithTime } = useDateTimeUtils();
const { getNextRoute } = useNavigationService();

const anzahlWahlbriefeValid = ref<null | boolean>(null);
const anzahlVerzeichnisseValid = ref<null | boolean>(null);
const anzahlNachtraegeValid = ref<null | boolean>(null);

const WIDTH = 300;

const isSaveButtonDisabled = computed(() => {
  return (
    anzahlWahlbriefeValid.value !== true ||
    anzahlVerzeichnisseValid.value !== true ||
    anzahlNachtraegeValid.value !== true
  );
});

async function onSaveBriefwahldatenClicked() {
  await wahlbriefDatenActions.sendWahlbriefdaten();
  await router.push(getNextRoute());
}
</script>
