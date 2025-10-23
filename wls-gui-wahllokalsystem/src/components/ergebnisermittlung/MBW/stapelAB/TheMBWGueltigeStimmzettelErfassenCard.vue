<template>
  <v-card>
    <v-card-title> Gültige Stimmzettel </v-card-title>
    <v-card-text>
      <!-- todo: form ergänzen -->
      <the-m-b-w-gueltige-stimmzettel-erfassen-table
        v-if="wahlbezirkID && wahlID"
        v-model="modelValue"
        :wahl-i-d="wahlID"
        :wahlbezirk-i-d="wahlbezirkID"
      />
    </v-card-text>
    <v-card-title>
      Ungültige Stimmzettel
      <!-- todo: ergebnis importieren wenn thomas ticket fertig ist -->
    </v-card-title>
    <v-card-actions>
      <base-button-save @click="saveGueltigeErgebnisse" />
    </v-card-actions>
  </v-card>
</template>
<script setup lang="ts">
import type { MbwErgebnisseAndWahlvorschlag } from "@/types/ergebnisermittlung/MbwErgebnisseAndWahlvorschlag.ts";
import type { PropType } from "vue";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import TheMBWGueltigeStimmzettelErfassenTable from "@/components/ergebnisermittlung/MBW/stapelAB/TheMBWGueltigeStimmzettelErfassenTable.vue";

const modelValue = defineModel({
  type: Object as PropType<MbwErgebnisseAndWahlvorschlag[]>,
  required: true,
});

defineProps({
  wahlID: {
    type: String,
    required: true,
  },
  wahlbezirkID: {
    type: String,
    required: true,
  },
});

function saveGueltigeErgebnisse() {
  // todo: add functionality
}
</script>
