<template>
  <v-card>
    <v-card-title> Wahlberechtigte laut Wählerverzeichnis </v-card-title>
    <v-card-text>
      <the-mbw-wahlberechtigte-anzeigen-table
        :wahlberechtigte="wahlberechtigte"
      />
    </v-card-text>
  </v-card>
</template>
<script setup lang="ts">
import type { AWerte } from "@/types/ergebnisermittlung/AWerte.ts";
import type { PropType } from "vue";

import TheMbwWahlberechtigteAnzeigenTable from "@/components/ergebnisermittlung/MBW/stapelAB/TheMBWWahlberechtigteAnzeigenTable.vue";

defineProps({
  wahlberechtigte: {
    type: Object as PropType<AWerte>,
    required: true,
  },
});
</script>
