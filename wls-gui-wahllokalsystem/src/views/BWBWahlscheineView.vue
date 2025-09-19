<template>
  <v-card>
    <v-card-title>Zählen der Wahlscheine</v-card-title>
    <v-card-text>
      <v-form v-model="isWahlscheineFormValid">
        <div class="d-flex flex-wrap justify-start">
          <template
            v-for="wahlschein in wahlscheine"
            :key="wahlschein.bezirkUndWahlID.wahlID"
          >
            <base-number-input
              v-model="wahlschein.stimmabgabevermerke"
              class="mr-4"
              :label="
                wahlenActions.getWahlNameOrBlankStringById(
                  wahlschein.bezirkUndWahlID.wahlID
                )
              "
              :rules="[required, minNumber(1), maxNumber(9999)]"
              max-width="300"
            />
          </template>
        </div>
      </v-form>
    </v-card-text>
    <v-card-actions>
      <base-button-save
        :disabled="!isWahlscheineFormValid"
        :loading="isWahlscheineSaving"
        @click="saveWahlscheine"
      />
    </v-card-actions>
  </v-card>
</template>
<script setup lang="ts">
import type { Ref } from "vue";

import { storeToRefs } from "pinia";
import { ref } from "vue";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import BaseNumberInput from "@/components/common/inputs/BaseNumberInput.vue";
import { useRules } from "@/composables/common/rules.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { useWahlscheineStore } from "@/stores/wahlscheineStore.ts";

const { wahlenActions } = useWahlenStore();
const { minNumber, maxNumber, required } = useRules();

const { saveWahlscheine } = useWahlscheineStore();
const { wahlscheine, isWahlscheineSaving } = storeToRefs(useWahlscheineStore());
const isWahlscheineFormValid: Ref<null | boolean> = ref(null);
</script>
