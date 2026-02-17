<template>
  <v-card>
    <v-card-text>
      <v-form
        v-model="beanstandeteWahlbriefeState.isBeanstandeteWahlbriefeTableValid"
      >
        <the-beanstandete-wahlbriefe-table />
      </v-form>
    </v-card-text>
    <v-card-actions>
      <base-text-button
        prepend-icon="$add"
        data-test="addBedenklicherWahlbriefRow"
        @click="onAddBeanstandeterWahlbriefClicked()"
      >
        Neuen Beschluss erfassen
      </base-text-button>
      <base-button-save
        :loading="isSaving"
        :disabled="isSaveButtonDisabled"
        save-text="Speichern und Weiter"
        @click="onSaveClicked"
      />
    </v-card-actions>
  </v-card>
</template>
<script setup lang="ts">
import { storeToRefs } from "pinia";
import { computed } from "vue";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import BaseTextButton from "@/components/common/buttons/BaseTextButton.vue";
import TheBeanstandeteWahlbriefeTable from "@/components/wahlhandlung/beanstandeteWahlbriefe/TheBeanstandeteWahlbriefeTable.vue";
import { useNavigationUtils } from "@/composables/navigation/navigationUtils.ts";
import router from "@/plugins/router.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

const { beanstandeteWahlbriefeState } = storeToRefs(useWahlenStore());
const { beanstandeteWahlbriefeActions } = useWahlenStore();
const { getNextRoute } = useNavigationUtils();

const isSaveButtonDisabled = computed(
  () => !beanstandeteWahlbriefeState.value.isBeanstandeteWahlbriefeTableValid
);
const isSaving = computed(
  () => beanstandeteWahlbriefeState.value.isBeanstandeteWahlbriefeSaving
);

function onAddBeanstandeterWahlbriefClicked() {
  beanstandeteWahlbriefeActions.addBeanstandeterWahlbriefEntry();
}

async function onSaveClicked() {
  await beanstandeteWahlbriefeActions.saveBeanstandeteWahlbriefe();
  await router.push(getNextRoute());
}
</script>
