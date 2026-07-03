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
      <base-wls-button-save
        :loading="isSaving"
        :disabled="isSaveButtonDisabled"
        :save-text="SAVE_CONTINUE"
        @click="emit('save')"
      />
    </v-card-actions>
  </v-card>
</template>
<script setup lang="ts">
import { storeToRefs } from "pinia";
import { computed } from "vue";

import BaseTextButton from "@/components/common/buttons/BaseTextButton.vue";
import BaseWlsButtonSave from "@/components/common/buttons/BaseWlsButtonSave.vue";
import TheBeanstandeteWahlbriefeTable from "@/components/wahlhandlung/beanstandeteWahlbriefe/TheBeanstandeteWahlbriefeTable.vue";
import { SAVE_CONTINUE } from "@/constants.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

const { beanstandeteWahlbriefeState } = storeToRefs(useWahlenStore());
const { beanstandeteWahlbriefeActions } = useWahlenStore();

const props = defineProps<{
  hasNachlieferungen: boolean;
  nachtraeglichUeberbrachtValid?: boolean | null;
}>();

const emit = defineEmits<{
  save: [];
}>();

const isSaveButtonDisabled = computed(() =>
  !props.hasNachlieferungen
    ? !beanstandeteWahlbriefeState.value.isBeanstandeteWahlbriefeTableValid
    : !beanstandeteWahlbriefeState.value.isBeanstandeteWahlbriefeTableValid ||
      !props.nachtraeglichUeberbrachtValid
);
const isSaving = computed(
  () => beanstandeteWahlbriefeState.value.isBeanstandeteWahlbriefeSaving
);

function onAddBeanstandeterWahlbriefClicked() {
  beanstandeteWahlbriefeActions.addBeanstandeterWahlbriefEntry();
}
</script>
