<template>
  <v-card>
    <v-card-title> Gültige Stimmzettel </v-card-title>
    <v-card-text>
      <v-form v-model="isGueltigeStimmzettelErfassenTableValid">
        <the-m-b-w-gueltige-stimmzettel-erfassen-table
          v-if="wahlbezirkID && wahlID"
          v-model="modelValue"
        />
      </v-form>
    </v-card-text>
    <v-card-actions>
      <base-button-save
        :disabled="!isGueltigeStimmzettelErfassenTableValid"
        :loading="isErgebnisseSaving"
        :tabindex="modelValue.length * 2 + 1"
        @click="onSaveClicked"
      />
    </v-card-actions>
  </v-card>
</template>
<script setup lang="ts">
import type { MbwErgebnisseAndWahlvorschlag } from "@/types/ergebnisermittlung/MbwErgebnisseAndWahlvorschlag.ts";
import type { PropType } from "vue";

import { ref } from "vue";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import TheMBWGueltigeStimmzettelErfassenTable from "@/components/ergebnismeldung/MBW/stapelAB/TheMBWGueltigeStimmzettelErfassenTable.vue";
import { useMbwUtils } from "@/composables/ergebnismeldung/MBW/mbwUtils.ts";

const modelValue = defineModel({
  type: Object as PropType<MbwErgebnisseAndWahlvorschlag[]>,
  required: true,
});

const props = defineProps({
  wahlID: {
    type: String,
    required: true,
  },
  wahlbezirkID: {
    type: String,
    required: true,
  },
});

const { isErgebnisseSaving, saveGueltigeErgebnisse } = useMbwUtils(
  props.wahlID,
  props.wahlbezirkID
);

const isGueltigeStimmzettelErfassenTableValid = ref<boolean | null>(null);

async function onSaveClicked() {
  await saveGueltigeErgebnisse(modelValue.value);
}
</script>
