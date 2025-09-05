<template>
  <v-container>
    <v-card v-if="wahl">
      <v-card-title>{{ title }}</v-card-title>
      <v-card-text class="pb-0 pt-2 mr-4">
        <v-form v-model="anzahlStimmzettelValidForm">
          <base-time-input
            v-if="useTime"
            v-model="wahl.stimmzettelumschlaege.urneneroeffnungsUhrzeit"
            :rules="[
              timeNotInFuture,
              timeGreaterOrEqual(fruehesteSchliessungsuhrzeit),
            ]"
            label="Uhrzeit der Öffnung der Wahlurne"
            min-width="20rem"
          />
          <v-number-input
            v-model="wahl.stimmzettelumschlaege.anzahlWaehler"
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
import BaseTimeInput from "@/components/common/inputs/BaseTimeInput.vue";
import { useRules } from "@/composables/common/rules.ts";
import { useInfomanagementStore } from "@/stores/infomanagementStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

const { maxNumber, minNumber, required, timeGreaterOrEqual, timeNotInFuture } =
  useRules();

const props = defineProps<{
  wahlId: string;
  title: string;
  useTime?: boolean;
}>();

const { getWahlOrUndefinedById, saveStimmzettelumschlaege } = useWahlenStore();
const { isStimmzettelumschlaegeSaving } = storeToRefs(useWahlenStore());
const { fruehesteSchliessungsuhrzeit } = storeToRefs(useInfomanagementStore());

const wahl = computed(() => getWahlOrUndefinedById(props.wahlId));

const anzahlStimmzettelValidForm = ref<null | boolean>(null);

const isSaveButtonDisabled = computed(() => {
  return !anzahlStimmzettelValidForm.value;
});

function onSaveAnzahlStimmzettelClicked() {
  saveStimmzettelumschlaege(props.wahlId);
}
</script>
