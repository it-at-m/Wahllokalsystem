<template>
  <div>
    <v-card-title>Uhrzeit Wahlende</v-card-title>
    <v-card-text class="pb-0">
      Bitte geben Sie hier die Uhrzeit ein, zu der die Wahl für geschlossen
      erklärt wurde.
      <v-form v-model="schliessungsuhrzeitValidForm">
        <base-time-input
          v-model="schliessungsuhrzeitState.schliessungsuhrzeit"
          class="mt-5"
          max-width="300"
          :rules="[
            required,
            timeNotInFuture,
            timeGreaterOrEqual(fruehesteSchliessungsuhrzeit),
          ]"
        />
      </v-form>
    </v-card-text>
    <v-card-actions>
      <base-button-save
        :loading="schliessungsuhrzeitState.schliessungsuhrzeitIsSaving"
        :disabled="isSaveButtonDisabled"
        save-text="Speichern und Weiter"
        @click="onSaveSchliessungsuhrzeitClicked"
      />
    </v-card-actions>
  </div>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";
import { computed, ref } from "vue";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import BaseTimeInput from "@/components/common/inputs/BaseTimeInput.vue";
import { useRules } from "@/composables/common/rules.ts";
import { useNavigationUtils } from "@/composables/navigation/navigationUtils.ts";
import router from "@/plugins/router.ts";
import { useInfomanagementStore } from "@/stores/infomanagementStore.ts";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";

const { required, timeGreaterOrEqual, timeNotInFuture } = useRules();
const { getNextRoute } = useNavigationUtils();

const { schliessungsuhrzeitActions } = useWahlbezirkStore();
const { schliessungsuhrzeitState } = storeToRefs(useWahlbezirkStore());
const { fruehesteSchliessungsuhrzeit } = storeToRefs(useInfomanagementStore());

const schliessungsuhrzeitValidForm = ref<null | boolean>(null);

const isSaveButtonDisabled = computed(
  () => schliessungsuhrzeitValidForm.value !== true
);

async function onSaveSchliessungsuhrzeitClicked() {
  await schliessungsuhrzeitActions.sendSchliessungsuhrzeit();
  await router.push(getNextRoute());
}
</script>
