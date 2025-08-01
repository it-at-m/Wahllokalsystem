<template>
  <v-card>
    <v-card-title>Wahlbriefe zulassen oder zurückweisen</v-card-title>
    <v-card-text>
      <v-form v-model="bedenklicheWahlbriefeFormValid">
        <the-beanstandete-wahlbriefe-table />
      </v-form>
    </v-card-text>
    <v-card-actions>
      <v-btn
        prepend-icon="$add"
        data-test="addBedenklicherWahlbriefRow"
        @click="onAddBeanstandeterWahlbriefClicked()"
      >
        Neuen Beschluss erfassen
      </v-btn>
      <base-button-save
        active
        :loading="isSaving"
        :disabled="isSaveButtonDisabled"
        @click="onSaveClicked"
      />
    </v-card-actions>
  </v-card>
</template>
<script setup lang="ts">
import { storeToRefs } from "pinia";
import { computed, ref } from "vue";
import {
  VBtn,
  VCard,
  VCardActions,
  VCardText,
  VCardTitle,
  VForm,
} from "vuetify/components";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import TheBeanstandeteWahlbriefeTable from "@/components/wahlhandlung/beanstandeteWahlbriefe/TheBeanstandeteWahlbriefeTable.vue";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

const { isBeanstandeteWahlbriefeSaving } = storeToRefs(useWahlenStore());
const { addBeanstandeterWahlbriefEntry, saveBeanstandeteWahlbriefe } =
  useWahlenStore();

const bedenklicheWahlbriefeFormValid = ref<null | boolean>(null);

const isSaveButtonDisabled = computed(
  () => !bedenklicheWahlbriefeFormValid.value
);
const isSaving = computed(() => isBeanstandeteWahlbriefeSaving.value);

function onAddBeanstandeterWahlbriefClicked() {
  addBeanstandeterWahlbriefEntry();
}

function onSaveClicked() {
  saveBeanstandeteWahlbriefe();
}
</script>
