<template>
  <div>
    <v-card-title> Wahlberechtigte laut Wählerverzeichnis </v-card-title>
    <v-card-text>
      <the-mbw-wahlberechtigte-anzeigen-table
        :wahlberechtigte="wahlberechtigte"
      />
    </v-card-text>
  </div>
</template>
<script setup lang="ts">
import type { AWerte } from "@/types/ergebnisermittlung/AWerte.ts";

import { onMounted, ref } from "vue";

import TheMbwWahlberechtigteAnzeigenTable from "@/components/ergebnisermittlung/MBW/stapelAB/TheMBWWahlberechtigteAnzeigenTable.vue";
import { useMbwUtils } from "@/composables/ergebnisermittlung/mbwUtils.ts";

const props = defineProps<{
  wahlbezirkId: string;
  wahlId: string;
}>();

const { getAWerteForWahlbezirkAndWahl } = useMbwUtils(
  props.wahlId,
  props.wahlbezirkId
);

const wahlberechtigte = ref<AWerte>({
  bezirkUndWahlID: { wahlID: "", wahlbezirkID: "" },
  a1: 0,
  a2: 0,
});

onMounted(async () => {
  wahlberechtigte.value = await getAWerteForWahlbezirkAndWahl();
});
</script>
