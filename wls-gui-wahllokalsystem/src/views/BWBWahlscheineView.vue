<template>
  <v-card>
    <v-card-title>Zählen der Wahlscheine</v-card-title>
    <v-card-text>
      <v-form v-model="wahlscheineModel">
        <template v-for="wahl in wahlenState.wahlen" :key="wahl.wahlID">
          <div>{{wahl.name}}</div>
          <base-number-input />
        </template>
      </v-form>
    </v-card-text>
    <v-card-actions>
      <base-button-save
          :disabled="!wahlscheineModel"
          :loading="isWahlscheineSaving"
          @click="saveWahlscheine"
      />
    </v-card-actions>
  </v-card>
</template>
<script setup lang="ts">
import { useWahlscheineStore } from "@/stores/wahlscheineStore.ts";
import {storeToRefs} from "pinia";
import {ref} from "vue";
import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import BaseNumberInput from "@/components/common/inputs/BaseNumberInput.vue";

import { useWahlenStore } from "@/stores/wahlenStore.ts";

const { wahlenState } = storeToRefs(useWahlenStore());

const { saveWahlscheine } = useWahlscheineStore();
const { isWahlscheineSaving } = storeToRefs(
    useWahlscheineStore()
);
const wahlscheineModel = ref(false);



</script>
