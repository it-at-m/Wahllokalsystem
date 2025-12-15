<template>
  <v-card v-if="isUWB">
    <v-card-title> Wahlberechtigte laut Wählerverzeichnis </v-card-title>
    <v-card-text>
      <the-mbw-wahlberechtigte-anzeigen-table
        :wahlberechtigte="wahlberechtigte"
      />
    </v-card-text>
  </v-card>
</template>
<script setup lang="ts">
import type { AWerte } from "@/types/ergebnismeldung/common/AWerte.ts";

import { storeToRefs } from "pinia";
import { onActivated, ref } from "vue";

import TheMbwWahlberechtigteAnzeigenTable from "@/components/ergebnismeldung/MBW/stapelAB/TheMBWWahlberechtigteAnzeigenTable.vue";
import { useMbwUtils } from "@/composables/ergebnismeldung/MBW/mbwUtils.ts";
import { useUserStore } from "@/stores/userStore.ts";

const props = defineProps<{
  wahlbezirkId: string;
  wahlId: string;
}>();

const { isUWB } = storeToRefs(useUserStore());

const { getAWerteForWahlbezirkAndWahl } = useMbwUtils(
  props.wahlId,
  props.wahlbezirkId
);

const wahlberechtigte = ref<AWerte>({
  bezirkUndWahlID: { wahlID: "", wahlbezirkID: "" },
  a1: 0,
  a2: 0,
});

onActivated(async () => {
  wahlberechtigte.value = await getAWerteForWahlbezirkAndWahl();
});
</script>
