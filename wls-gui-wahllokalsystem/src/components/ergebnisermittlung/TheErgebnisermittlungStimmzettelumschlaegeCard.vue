<template>
  <v-container>
    <v-card v-if="wahl">
      <v-card-title>Wahlurne öffnen und Stimmzettel zählen</v-card-title>
      <v-card-text class="pb-0 pt-2">
        <v-form v-model="anzahlStimmzettelValidForm">
          <v-number-input
            v-model="wahl.stimmzettelumschlaege.anzahlWaehler"
            class="mr-4"
            :rules="[required, minNumber(0), maxNumber(9999)]"
            min-width="20rem"
            label="Anzahl der Stimmzettel"
            clearable
          />
        </v-form>
      </v-card-text>
      <v-card-actions>
        <base-button-save
          active
          :loading="isStimmzettelumschlaegeSaving"
          :disabled="isSaveButtonDisabled"
          @click="onSaveAnzahlStimmzettelClicked"
        />
      </v-card-actions>
    </v-card>
  </v-container>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";
import { computed, ref } from "vue";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import { useRules } from "@/composables/common/rules.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

const { maxNumber, minNumber, required } = useRules();

const props = defineProps<{
  wahlId: string;
}>();

const { getWahlOrUndefinedById, saveStimmzettelumschlaege } = useWahlenStore();
const { isStimmzettelumschlaegeSaving } = storeToRefs(useWahlenStore());

const wahl = computed(() => getWahlOrUndefinedById(props.wahlId));

const anzahlStimmzettelValidForm = ref<null | boolean>(null);

const isSaveButtonDisabled = computed(() => {
  return !anzahlStimmzettelValidForm.value;
});

function onSaveAnzahlStimmzettelClicked() {
  saveStimmzettelumschlaege(props.wahlId);
}
</script>
