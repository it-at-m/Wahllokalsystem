<template>
  <v-card class="bg-grey-lighten-3">
    <v-card-text>
      <v-form v-model="isFormValid">
        <base-table-wahlvorschlag-kandidaten-stimmen-erfassen
          :wahlvorschlag-nummer="wahlvorschlagModel.ordnungszahl"
          :model-value="wahlvorschlagModel.kandidatenErgebnisse"
        />
      </v-form>
    </v-card-text>
    <v-card-actions>
      <base-button-save
        :disabled="!isFormValid"
        :loading="isSaving"
        @click="onSaveButtonClicked"
      />
    </v-card-actions>
  </v-card>
</template>

<script setup lang="ts">
import type { WahlvorschlagWithKandidatenErgebnissen } from "@/types/ergebnisermittlung/WahlvorschlagWithKandidatenErgebnissen.ts";
import type { PropType } from "vue";

import { ref } from "vue";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import BaseTableWahlvorschlagKandidatenStimmenErfassen from "@/components/ergebnisermittlung/MBW/stapelBC/BaseTableWahlvorschlagKandidatenStimmenErfassen.vue";

const wahlvorschlagModel = defineModel("modelValue", {
  type: Object as PropType<WahlvorschlagWithKandidatenErgebnissen>,
  required: true,
});

defineProps({
  isSaving: {
    type: Boolean,
    required: true,
  },
});

const emit = defineEmits<{
  doSave: [];
}>();

const isFormValid = ref<boolean | null>(null);

function onSaveButtonClicked() {
  emit("doSave");
}
</script>
