<template>
  <v-card>
    <v-card-title> Wahlberechtigte laut Wählerverzeichnis </v-card-title>
    <v-card-text>
      <the-m-b-w-wahlberechtigten-anzeigen-table
        :wahlberechtigte="wahlberechtigte"
      />
    </v-card-text>
  </v-card>
</template>
<script setup lang="ts">
import type { AWerte } from "@/types/ergebnisermittlung/AWerte.ts";
import type { PropType } from "vue";

import TheMBWWahlberechtigtenAnzeigenTable from "@/components/ergebnisermittlung/MBW/stapelAB/TheMBWWahlberechtigtenAnzeigenTable.vue";

defineProps({
  wahlberechtigte: {
    type: Object as PropType<AWerte>,
    required: true,
  },
});
</script>
